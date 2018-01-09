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
package ch.nicosb.eventstreamanalyzer.parser;

import cc.kave.commons.model.events.IDEEvent;
import cc.kave.commons.model.events.IIDEEvent;
import cc.kave.commons.utils.io.IReadingArchive;
import cc.kave.commons.utils.io.ReadingArchive;
import ch.nicosb.eventstreamanalyzer.stream.util.StatusProvider;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class NotifyingZipParser implements Publisher, StatusProvider{

    private List<EventParsedListener> listeners;
    private Path filePath;
    private IReadingArchive readingArchive;
    private int counter = 1;

    public NotifyingZipParser(Path filePath) {
        if (!filePath.toString().toLowerCase().endsWith(".zip"))
            throw new IllegalArgumentException("File must be a Zip!");

        listeners = new ArrayList<>();
        this.filePath = filePath;
    }

    public void parse() {
        parseEvents(filePath);
    }

    private void parseEvents(Path file) {
        System.out.printf("Extracting events from %s.", file.toString());

        readingArchive = new ReadingArchive(file.toFile());

        while (readingArchive.hasNext()) {
            try {
                IDEEvent event = readingArchive.getNext(IIDEEvent.class);
                onEventParsed(event, file.toString());
                counter++;
            } catch(Exception e) {
                System.out.println("Failed To Parse Event");
            }
        }
    }

    private void onEventParsed(IDEEvent event, String name) {
        listeners.forEach(listener -> listener.onEventParsed(event));
    }

    @Override
    public void subscribe(EventParsedListener listener) {
        listeners.add(listener);
    }

    @Override
    public void unsubscribe(EventParsedListener listener) {
        listeners.remove(listener);
    }

    @Override
    public String getStatus() {
        return String.format("%d\\%d Events parsed.", counter, readingArchive.getNumberOfEntries());
    }
}
