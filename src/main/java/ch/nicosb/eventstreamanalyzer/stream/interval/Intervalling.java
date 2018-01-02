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

import ch.nicosb.eventstreamanalyzer.Execution;
import ch.nicosb.eventstreamanalyzer.data.aggregators.*;
import ch.nicosb.eventstreamanalyzer.parser.ListeningEventQueue;
import ch.nicosb.eventstreamanalyzer.parser.NotifyingZipParser;
import ch.nicosb.eventstreamanalyzer.parser.ZipUtils;
import ch.nicosb.eventstreamanalyzer.utils.PeriodicLogger;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

public class Intervalling implements Execution {

    private QueueProcessor processor;

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
        processor = new QueueProcessor(queue);

        PeriodicLogger logger = initLogger(parser);

        registerAggregators();
        parser.subscribe(queue);

        processor.start();
        parser.parse();
        processor.stop();
        logger.stop();
    }

    private PeriodicLogger initLogger(NotifyingZipParser parser) {
        PeriodicLogger logger = new PeriodicLogger(5);
        logger.registerProvider(parser);
        logger.registerProvider(processor);
        return logger;
    }

    private void registerAggregators() {
        int fiveMinutes = 5*60;
        Aggregator eventCountAggregator = new EventCountAggregator(fiveMinutes);
        processor.registerAggregator(eventCountAggregator);

        int twoMinutes = 2*60;
        Aggregator timeSinceLastBuildAggregator = new LastBuildAggregator(twoMinutes);
        processor.registerAggregator(timeSinceLastBuildAggregator);

        Aggregator timeSinceLastCommitAggregator = new LastCommitAggregator(twoMinutes);
        processor.registerAggregator(timeSinceLastCommitAggregator);

        Aggregator lastSuccessfulTestWithinAggregator = new LastSuccessfulTestWithinAggregator(twoMinutes);
        processor.registerAggregator(lastSuccessfulTestWithinAggregator);

        Aggregator isCommitEventAggregator = new IsCommitEventAggregator();
        processor.registerAggregator(isCommitEventAggregator);
    }
}
