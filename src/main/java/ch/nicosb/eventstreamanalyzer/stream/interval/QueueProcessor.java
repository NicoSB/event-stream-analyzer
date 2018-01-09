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

import cc.kave.commons.model.events.CommandEvent;
import cc.kave.commons.model.events.IIDEEvent;
import ch.nicosb.eventstreamanalyzer.data.aggregators.Aggregator;
import ch.nicosb.eventstreamanalyzer.data.sampling.IntervalPicker;
import ch.nicosb.eventstreamanalyzer.data.sampling.SamplePicker;
import ch.nicosb.eventstreamanalyzer.parser.ListeningEventQueue;
import ch.nicosb.eventstreamanalyzer.stream.util.StatusProvider;
import ch.nicosb.eventstreamanalyzer.utils.EventUtils;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.*;

public class QueueProcessor implements Runnable, StatusProvider {
    private Set<Aggregator> aggregators = new HashSet<>();
    private ArffWriter arffWriter;
    private SamplePicker samplePicker;
    private ListeningEventQueue queue;
    private boolean cancelled;
    private int eventsWritten = 0;
    private int eventsSkipped = 0;
    private int interval;
    private ZonedDateTime lastEventEnd;

    public QueueProcessor(ListeningEventQueue queue, int interval) {
        cancelled = false;
        this.interval = interval;

        String fileName = queue.getTitle();
        this.queue = queue;
        arffWriter = new ArffWriter(aggregators, fileName);

        samplePicker = new IntervalPicker(interval);
    }

    public void start() {
        arffWriter.createNewFile();
        new Thread(this).start();
    }

    public void stop() {
        cancelled = true;
    }

    public void registerAggregator(Aggregator aggregator) {
        aggregators.add(aggregator);
    }

    @Override
    public void run() {
        while(isRunning()) {
            processNextEvent();
        }

        evaluateEnd();
        try {
            arffWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private synchronized void processNextEvent() {
        if (queue.size() > 0) {
            IIDEEvent event = queue.poll();
            if (event != null)
                tryExtractAndWriteValues(event);
        }
    }

    private void tryExtractAndWriteValues(IIDEEvent event) {
        try {
            if (!isWithinIntervalOfLastEvent(event)) {
                IIDEEvent dummyEvent = createDummyEvent();
                extractAndWriteValues(dummyEvent);
            }

            extractAndWriteValues(event);
            lastEventEnd = EventUtils.getEnd(event);
        } catch (Exception e) {
            System.err.println("Failed to write event: " + event.toString());
        }
    }

    private boolean isWithinIntervalOfLastEvent(IIDEEvent event) {
        if (lastEventEnd == null)
            return true;

        ZonedDateTime maxTime = lastEventEnd.plusSeconds(2 * interval);
        return !event.getTriggeredAt().isAfter(maxTime);
    }

    private IIDEEvent createDummyEvent() {
        CommandEvent event = new CommandEvent();
        event.CommandId = "DummyEvent";
        event.TriggeredAt = lastEventEnd.plusSeconds(interval + 1);

        return event;
    }

    private void extractAndWriteValues(IIDEEvent event) {
        Map<String, String> combinedMap = extractValues(event);
        if (shouldSample(event)) {
            tryMapWrite(combinedMap);
        } else {
            eventsSkipped++;
        }
    }

    private Map<String, String> extractValues(IIDEEvent event) {
        Map<String, String> combinedMap = new HashMap<>();
        aggregators.forEach(aggregator -> combinedMap.putAll(aggregator.aggregateValue(event)));
        return combinedMap;
    }

    private boolean shouldSample(IIDEEvent event) {
        return samplePicker.shouldSample(event);
    }

    private void tryMapWrite(Map<String, String> map) {
        arffWriter.writeData(map);
        eventsWritten++;
    }

    private boolean isRunning() {
        return !cancelled || queue.size() > 0;
    }


    private void evaluateEnd() {
        IIDEEvent endEvent = createDummyEvent();
        tryExtractAndWriteValues(endEvent);
    }

    @Override
    public String getStatus() {
        return String.format("%d Events written. %d Events skipped.", eventsWritten, eventsSkipped);
    }
}
