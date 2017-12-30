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
package ch.nicosb.eventstreamanalyzer.data;

import ch.nicosb.eventstreamanalyzer.Execution;
import ch.nicosb.eventstreamanalyzer.data.aggregators.Aggregator;
import ch.nicosb.eventstreamanalyzer.data.aggregators.EventCountAggregator;
import ch.nicosb.eventstreamanalyzer.data.aggregators.LastBuildAggregator;
import ch.nicosb.eventstreamanalyzer.parser.ZipUtils;
import ch.nicosb.eventstreamanalyzer.stream.EventStream;
import ch.nicosb.eventstreamanalyzer.weka.MapToArffConverter;

import java.util.ArrayList;
import java.util.List;

public class DataAggregation implements Execution {

    private static MapToArffConverter converter;

    @Override
    public void execute(String[] args) {
        aggregateInput(args);
    }

    private void aggregateInput(String[] args) {
        String folder = args[1];
//        List<EventStream> streams = ZipUtils.parseDirectory(folder);
//
//        List<Entry> entries = new ArrayList<>();
//
//        streams.forEach(stream -> entries.addAll(aggregateStream(stream)));
//        initConverter(entries);
//        converter.writeFile();
    }

    private List<Entry> aggregateStream(EventStream stream) {
        Traverser traverser = new TraverserImpl(stream.getEvents());

        registerAggregators(traverser);
        return traverser.traverse();
    }

    private void registerAggregators(Traverser traverser) {
        int fiveMinutes = 5*60;
        Aggregator eventCountAggregator = new EventCountAggregator("EventCount", fiveMinutes);
        traverser.register(eventCountAggregator);

        int twoMinutes = 2*60;
        Aggregator timeSinceLastBuildAggregator = new LastBuildAggregator(twoMinutes);
        traverser.register(timeSinceLastBuildAggregator);
    }

    private void initConverter(List<Entry> entries) {
        converter = new MapToArffConverter("events", "test.arff");
        entries.forEach(entry -> converter.add(entry.getFields()));
    }
}
