/**
 * Copyright 2017 Nico Strebel
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package ch.nicosb.eventstreamanalyzer.stream.interval;

import cc.kave.commons.model.events.visualstudio.BuildEvent;
import ch.nicosb.eventstreamanalyzer.Execution;
import ch.nicosb.eventstreamanalyzer.data.*;
import ch.nicosb.eventstreamanalyzer.data.aggregators.*;
import ch.nicosb.eventstreamanalyzer.data.aggregators.entryaggregators.*;
import ch.nicosb.eventstreamanalyzer.parser.ListeningEventQueue;
import ch.nicosb.eventstreamanalyzer.parser.NotifyingZipParser;
import ch.nicosb.eventstreamanalyzer.parser.ZipUtils;
import ch.nicosb.eventstreamanalyzer.stream.EventStream;
import ch.nicosb.eventstreamanalyzer.stream.Session;
import ch.nicosb.eventstreamanalyzer.stream.Sessionizer;
import ch.nicosb.eventstreamanalyzer.weka.MapToArffConverter;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

public class Intervalling implements Execution {

    private List<IntervalPostProcessor> postProcessors;
    private List<EntryAggregator> entryAggregators;
    private MapToArffConverter converter;

    public Intervalling() {
        this.postProcessors = new ArrayList<>();
        entryAggregators = new ArrayList<>();
    }

    @Override
    public void execute(String[] args) {
        try {
            long millis = System.currentTimeMillis();
            String folder = args[1];
            List<Path> zips = ZipUtils.getAllZips(folder);

            zips.parallelStream().forEach(this::processZip);
            System.out.println(System.currentTimeMillis() - millis);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void processZip(Path path) {
        NotifyingZipParser parser = new NotifyingZipParser(path);
        ListeningEventQueue queue = new ListeningEventQueue(path.getFileName().toString());
        QueueProcessor processor = new QueueProcessor(queue);

        registerAggregators(processor);
        parser.subscribe(queue);

        processor.start();
        parser.parse();
        processor.stop();
    }

    private void registerAggregators(QueueProcessor processor) {
        int fiveMinutes = 5*60;
        Aggregator eventCountAggregator = new EventCountAggregator("EventCount", fiveMinutes);
        processor.registerAggregator(eventCountAggregator);

        int twoMinutes = 2*60;
        Aggregator timeSinceLastBuildAggregator = new LastBuildAggregator(twoMinutes);
        processor.registerAggregator(timeSinceLastBuildAggregator);

        Aggregator timeSinceLastCommitAggregator = new LastCommitAggregator(twoMinutes);
        processor.registerAggregator(timeSinceLastCommitAggregator);

        Aggregator lastSuccessfulTestWithinAggregator = new LastSuccessfulTestWithinAggregator(twoMinutes);
        processor.registerAggregator(lastSuccessfulTestWithinAggregator);
    }

    private List<Entry> aggregateStream(EventStream stream) {
        Traverser traverser = new TraverserImpl(stream.getEvents());

        registerAggregator(traverser);
        return traverser.traverse();
    }

    private void registerPostProcessors() {
        FirstAndLastRemover remover = new FirstAndLastRemover(10*60);
        postProcessors.add(remover);
    }

    private void registerAggregator(Traverser traverser) {
        int fiveMinutes = 5*60;
        Aggregator eventCountAggregator = new EventCountAggregator("EventCount", fiveMinutes);
        traverser.register(eventCountAggregator);

        int twoMinutes = 2*60;
        Aggregator timeSinceLastBuildAggregator = new LastBuildAggregator(twoMinutes);
        traverser.register(timeSinceLastBuildAggregator);

        Aggregator timeSinceLastCommitAggregator = new LastCommitAggregator(twoMinutes);
        traverser.register(timeSinceLastCommitAggregator);
    }

    private void initConverter(List<Entry> entries) {
        converter = new MapToArffConverter("events", "test.arff");
        entries.forEach(entry -> converter.add(entry.getFields()));
    }

    private void registerEntryAggregators() {
        EntryAggregator hasBuildEventAggregator = new HasEventAggregator(BuildEvent.class);
        entryAggregators.add(hasBuildEventAggregator);

        EntryAggregator totalTestAggregator = new TotalTestCompletionRatioAggregator();
        entryAggregators.add(totalTestAggregator);

        EntryAggregator runTestAggregator = new RunTestCompletionRatioAggregator();
        entryAggregators.add(runTestAggregator);

        EntryAggregator commitEventAggregator = new HasCommitEventAggregator();
        entryAggregators.add(commitEventAggregator);
    }

    private void processStream(EventStream stream) {
        List<Entry> entries = aggregateStream(stream);
        List<Session> sessions = getSessions(entries);

        List<Interval> intervals = getIntervals(sessions);
        List<Interval> relevantIntervals = getRelevantIntervals(intervals);

        List<Entry> averagedEntries = averageEntries(relevantIntervals);

        writeEntriesToFile(averagedEntries);
    }

    private void writeEntriesToFile(List<Entry> averagedEntries) {
        initConverter(averagedEntries);
        converter.writeFile();
    }

    private List<Interval> getRelevantIntervals(List<Interval> intervals) {
        List<Interval> relevantIntervals = new ArrayList<>();

        for(IntervalPostProcessor processor : postProcessors) {
            relevantIntervals = processor.process(intervals);
        }
        return relevantIntervals;
    }

    private List<Entry> averageEntries(List<Interval> relevantIntervals) {
        List<Entry> averagedEntries = new ArrayList<>();

        relevantIntervals.forEach(interval -> {
            Entry averaged = averageValues(interval.getEntries());
            entryAggregators.forEach(agg ->
                    averaged.put(agg.getTitle(), agg.aggregateValue(interval.getEntries())));
            averagedEntries.add(averaged);
        });

        return averagedEntries;
    }

    private Entry averageValues(List<Entry> entries) {
        Entry entry = new Entry();
        for(String key : entries.get(0).getFields().keySet()) {
            entry.put(key, averageValue(entries, key));
        }

        return entry;
    }

    private String averageValue(List<Entry> entries, String key) {
        double sum = 0d;

        for (Entry entry : entries) {
            sum += Double.valueOf(entry.getFields().get(key));
        }

        return String.valueOf(sum / entries.size());
    }

    private List<Session> getSessions(List<Entry> events) {
        Sessionizer sessionizer = new Sessionizer(events, 10*60);
        return sessionizer.extractSessions();
    }

    private List<Interval> getIntervals(List<Session> sessions) {
        IntervalSplitter splitter = new IntervalSplitter(sessions, 15);
        return splitter.split();
    }
}
