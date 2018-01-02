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

import cc.kave.commons.model.events.IIDEEvent;
import ch.nicosb.eventstreamanalyzer.data.aggregators.Aggregator;
import ch.nicosb.eventstreamanalyzer.parser.ListeningEventQueue;
import ch.nicosb.eventstreamanalyzer.stream.util.StatusProvider;

import java.io.IOException;
import java.util.*;

public class QueueProcessor implements Runnable, StatusProvider {
    private final String fileName;
    private Set<Aggregator> aggregators = new HashSet<>();
    private ArffWriter arffWriter;
    private ListeningEventQueue queue;
    private boolean cancelled;
    private int counter = 0;
    private int failures = 0;

    public QueueProcessor(ListeningEventQueue queue) {
        cancelled = false;
        this.fileName = queue.getTitle();
        this.queue = queue;
        arffWriter = new ArffWriter(aggregators, fileName);
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
            try {
                processNextEvent();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            arffWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private synchronized void processNextEvent() throws IOException {
        if (queue.size() > 0) {
            IIDEEvent event = queue.poll();
            if (event != null)
                tryExtractAndWriteValues(event);
        }
    }

    private void tryExtractAndWriteValues(IIDEEvent event) {
        try {
            Map<String, String> combinedMap = new HashMap<>();
            aggregators.forEach(aggregator -> combinedMap.putAll(aggregator.aggregateValue(event)));
            tryMapWrite(combinedMap);
        } catch (Exception e) {
            System.err.println("Failed to write event: " + event.toString());
        }
    }

    private void tryMapWrite(Map<String, String> map) {
        try {
            arffWriter.writeData(map);
            counter++;
        } catch (IOException e) {
            e.printStackTrace();
            failures++;
        }
    }

    private boolean isRunning() {
        return !cancelled || queue.size() > 0;
    }

    @Override
    public String getStatus() {
        return String.format("%d Events written. %d errors.", counter, failures);
    }
}
