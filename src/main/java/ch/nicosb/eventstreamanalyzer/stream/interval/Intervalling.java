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

    private static final int INTERVAL = 60;

    @Override
    public void execute(String[] args) {
        try {
            String folder = args[1];
            List<Path> zips = ZipUtils.getAllZips(folder);

            zips.forEach(this::processZip);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void processZip(Path path) {
        NotifyingZipParser parser = new NotifyingZipParser(path);

        String filename = path.getFileName().toString();
        String parentFolder = path.getParent().getFileName().toString();
        String name = INTERVAL + parentFolder + "_" + filename;

        ListeningEventQueue queue = new ListeningEventQueue(name);
        QueueProcessor processor = new QueueProcessor(queue, INTERVAL);

        PeriodicLogger logger = createLogger(parser, processor);

        registerAggregators(processor);
        parser.subscribe(queue);

        processor.start();
        parser.parse();

        processor.stop();
        logger.stop();
    }

    private PeriodicLogger createLogger(NotifyingZipParser parser, QueueProcessor processor) {
        PeriodicLogger logger = new PeriodicLogger(5);
        logger.registerProvider(parser);
        logger.registerProvider(processor);
        return logger;
    }

    private void registerAggregators(QueueProcessor processor) {
        int thirtySeconds = 30;
        int fiveMinutes = 5*60;
        int twoMinutes = 2*60;

        Aggregator eventCountAggregator = new EventCountAggregator(fiveMinutes);
        processor.registerAggregator(eventCountAggregator);

        Aggregator timeSinceLastBuildAggregator = new LastBuildAggregator(twoMinutes);
        processor.registerAggregator(timeSinceLastBuildAggregator);

        Aggregator lastSuccessfulTestWithinAggregator = new LastSuccessfulTestWithinAggregator(thirtySeconds, twoMinutes, fiveMinutes);
        processor.registerAggregator(lastSuccessfulTestWithinAggregator);

        Aggregator lastSuccessfulBuildWithinAggregator = new LastBuildWithinAggregator(thirtySeconds, twoMinutes, fiveMinutes);
        processor.registerAggregator(lastSuccessfulBuildWithinAggregator);

        Aggregator fileCloseCountAggregator = new FileCloseCountWithinAggregator(thirtySeconds, twoMinutes, fiveMinutes);
        processor.registerAggregator(fileCloseCountAggregator);

        Aggregator fileSaveCountAggregator = new FileSaveCountWithinAggregator(thirtySeconds, twoMinutes, fiveMinutes);
        processor.registerAggregator(fileSaveCountAggregator);

        Aggregator activeTimeAggregator = new ActiveTimeAggregator(fiveMinutes, 5);
        processor.registerAggregator(activeTimeAggregator);

        Aggregator label = new CommitWithinAggregator(INTERVAL);
        processor.registerAggregator(label);
    }
}
