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
import ch.nicosb.eventstreamanalyzer.stream.EventStream;
import ch.nicosb.eventstreamanalyzer.stream.TriggeredAtComparator;
import ch.nicosb.eventstreamanalyzer.utils.FileSystemUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

public class ZipUtils {

    private static final String ZIP_ENDING = ".zip";

    public static List<Path> getAllZips(String uri) throws IOException {
        System.out.printf("Gathering ZIPs from %s.\n", uri);

        List<Path> paths = new ArrayList<>();
        List<Path> zipFiles = FileSystemUtils.getAllFilePathsWithEnding(uri, ZIP_ENDING);

        paths.addAll(zipFiles);

        return paths;
    }
}
