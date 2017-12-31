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

import java.io.IOException;
import java.util.*;

public class QueueProcessor implements Runnable {
    private final String fileName;
    private TreeSet<Aggregator> aggregators = new TreeSet<>(Comparator.comparing(Aggregator::getTitle));
    private ArffWriter arffWriter;
    private ListeningEventQueue queue;
    private boolean cancelled;

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

    private void processNextEvent() throws IOException {
        Map<String, String> map = new HashMap<>();
        IIDEEvent event = queue.poll();
        if (event != null) {
            tryExtractAndWriteValues(map, event);
        }
    }

    private void tryExtractAndWriteValues(Map<String, String> map, IIDEEvent event) {
        try {
            aggregators.forEach(aggregator -> map.put(aggregator.getTitle(), aggregator.aggregateValue(null, event)));
            arffWriter.writeData(map);
        } catch (Exception e) {
            System.err.println("Failed to write event: " + event.toString());
        }
    }

    private boolean isRunning() {
        return !cancelled || queue.size() > 0;
    }
}
