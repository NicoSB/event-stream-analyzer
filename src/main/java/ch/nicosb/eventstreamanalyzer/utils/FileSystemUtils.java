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
package ch.nicosb.eventstreamanalyzer.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class FileSystemUtils {
    public static List<Path> getAllFilePathsWithEnding(String directory, final String fileEnding) throws IOException {
        return Files.walk(Paths.get(directory))
                .filter(path -> path.toString().endsWith(fileEnding))
                .distinct()
                .collect(Collectors.toList());
    }

    public static List<Path> getAllFolders(String directory) throws IOException {
        Path currentFolder = Paths.get(directory);
        return Files.walk(Paths.get(directory))
                .filter(path -> Files.isDirectory(path))
                .filter(path -> !path.equals(currentFolder))
                .distinct()
                .collect(Collectors.toList());
    }

    public static List<Path> getChildPaths(String directory) throws IOException {
        Path currentFolder = Paths.get(directory);

        return Files.walk(currentFolder)
                .filter(path -> !path.equals(currentFolder))
                .distinct()
                .collect(Collectors.toList());
    }
}