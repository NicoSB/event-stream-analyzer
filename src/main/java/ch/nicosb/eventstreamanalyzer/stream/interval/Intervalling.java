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
import ch.nicosb.eventstreamanalyzer.parser.NotifyingEventParser;
import ch.nicosb.eventstreamanalyzer.parser.ZipUtils;
import ch.nicosb.eventstreamanalyzer.utils.PeriodicLogger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class Intervalling implements Execution {

    public static final int LOGGING_INTERVAL = 5;
    private static int INTERVAL = 60;
    private String outputFolder;

    @Override
    public void execute(String[] args) {
        try {
            String folder = args[1];
            outputFolder = args[2];

            ensureOutputFolderExists();
            if (args.length >= 4)
                INTERVAL = Integer.valueOf(args[3]);

            List<Path> zips = ZipUtils.getAllZips(folder);

            zips.forEach(this::processZip);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void ensureOutputFolderExists() throws IOException {
        Path path = Paths.get(outputFolder);
        if(!Files.exists(path))
            Files.createDirectory(path);
    }

    private void processZip(Path path) {
        NotifyingEventParser parser = new NotifyingEventParser(path);

        String filename = path.getFileName().toString();
        String parentFolder = path.getParent().getFileName().toString();
        String name = INTERVAL + "_" + parentFolder + "_" + filename;

        String fileUri = Paths.get(outputFolder, name).toAbsolutePath().toString();

        ListeningEventQueue queue = new ListeningEventQueue(fileUri);
        QueueProcessor processor = new QueueProcessor(queue, INTERVAL);

        PeriodicLogger logger = createLogger(parser, processor);

        StatisticsAggregator statisticsAggregator = new StatisticsAggregator(fileUri + ".stat");

        registerAggregators(processor);
        processor.registerAggregator(statisticsAggregator);

        parser.subscribe(queue);

        processor.start();
        parser.parse();

        processor.stop();
        logger.stop();

        try {
            statisticsAggregator.close();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    private PeriodicLogger createLogger(NotifyingEventParser parser, QueueProcessor processor) {
        PeriodicLogger logger = new PeriodicLogger(LOGGING_INTERVAL);
        logger.registerProvider(parser);
        logger.registerProvider(processor);
        return logger;
    }

    private void registerAggregators(QueueProcessor processor) {
        Aggregator eventCountAggregator = new EventCountAggregator(INTERVAL);
        processor.registerAggregator(eventCountAggregator);

        Aggregator timeSinceLastBuildAggregator = new LastBuildAggregator(INTERVAL);
        processor.registerAggregator(timeSinceLastBuildAggregator);
        
        Aggregator lastSuccessfulTestWithinAggregator = new LastSuccessfulTestWithinAggregator(INTERVAL);
        processor.registerAggregator(lastSuccessfulTestWithinAggregator);

        Aggregator lastSuccessfulBuildWithinAggregator = new LastBuildWithinAggregator(INTERVAL);
        processor.registerAggregator(lastSuccessfulBuildWithinAggregator);

        Aggregator fileCloseCountAggregator = new FileCloseCountWithinAggregator(INTERVAL);
        processor.registerAggregator(fileCloseCountAggregator);

        Aggregator fileSaveCountAggregator = new FileSaveCountWithinAggregator(INTERVAL);
        processor.registerAggregator(fileSaveCountAggregator);

        Aggregator activeTimeAggregator = new ActiveTimeAggregator(INTERVAL, 5);
        processor.registerAggregator(activeTimeAggregator);

        Aggregator lastTestWasSuccessfulAggregator = new LastTestWasSuccessfulAggregator();
        processor.registerAggregator(lastTestWasSuccessfulAggregator);

        Aggregator lastBuildWasSuccessful = new LastBuildWasSuccessfulAggregator();
        processor.registerAggregator(lastBuildWasSuccessful);

        Aggregator sinceLastLabel = new LastCommitAggregator(5);
        processor.registerAggregator(sinceLastLabel);

        Aggregator label = new CommitWithinAggregator(INTERVAL);
        processor.registerAggregator(label);
    }
}
