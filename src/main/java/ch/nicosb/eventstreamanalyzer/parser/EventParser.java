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
import ch.nicosb.eventstreamanalyzer.utils.FileSystemUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class EventParser {

    public static final String ZIP_ENDING = ".zip";

    public static ArrayList<IIDEEvent> parseDirectory(String directory) {
        try {
            List<Path> zips = getAllZips(directory);
            return parseZips(zips);
        } catch (IOException e) {
            throw new IllegalArgumentException("The uri '" + directory + "' does not exist!", e);
        }
    }

    private static List<Path> getAllZips(String uri) throws IOException {
        List<Path> paths = new ArrayList<>();
        List<Path> zipFiles = FileSystemUtils.getAllFilePathsWithEnding(uri, ZIP_ENDING);

        paths.addAll(zipFiles);

        return paths;
    }

    public static ArrayList<IIDEEvent> parseZips(List<Path> files) {
        ArrayList<IIDEEvent> events = new ArrayList<>();

        for (Path file : files) {
            events.addAll(extractEvents(file));
        }

        return events;
    }

    public static ArrayList<IIDEEvent> extractEvents(Path file) {
        ArrayList<IIDEEvent> events = new ArrayList<>();

        IReadingArchive readingArchive = new ReadingArchive(file.toFile());

        while (readingArchive.hasNext()) {
            IDEEvent event = readingArchive.getNext(IIDEEvent.class);
            events.add(event);
        }

        return events;
    }
}