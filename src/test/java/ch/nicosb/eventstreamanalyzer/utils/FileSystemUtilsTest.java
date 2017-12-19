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

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.Assert.assertEquals;


public class FileSystemUtilsTest {

    private final String DIRECTORY = "tmp";

    @Before
    public void setUp() throws Exception {
        new File(DIRECTORY).mkdir();
    }

    @After
    public void tearDown() throws Exception {
        FileUtils.deleteDirectory(new File(DIRECTORY));
    }

    @Test
    public void getAllFilePathsWithEnding_getsAllFilesWithEnding() throws Exception {
        // given
        String file1Uri = "file1.match";
        String file2Uri = "file2.match";
        String notMatchingFileUri = "file.nomatch";

        Path matchingFile1 = Files.createFile(Paths.get(DIRECTORY, file1Uri));
        Path matchingFile2 = Files.createFile(Paths.get(DIRECTORY, file2Uri));
        Path notMatchingFile = Files.createFile(Paths.get(DIRECTORY, notMatchingFileUri));

        // when
        List<Path> paths = FileSystemUtils.getAllFilePathsWithEnding(DIRECTORY, ".match");

        // then
        assertEquals(2, paths.size());
        assertEquals(matchingFile1, paths.get(0));
        assertEquals(matchingFile2, paths.get(1));
    }

    @Test
    public void getAllFolders_getsAllFolders() throws IOException {
        // given
        String folder1 = "folder1";
        String folder2 = "folder2";
        Path folder1Path = Paths.get(DIRECTORY, folder1);
        Path folder2Path = Paths.get(DIRECTORY, folder2);

        Files.createDirectory(folder1Path);
        Files.createDirectory(folder2Path);

        // when
        List<Path> folders = FileSystemUtils.getAllFolders(DIRECTORY);

        // then
        assertEquals(2, folders.size());
        assertEquals(folder1Path, folders.get(0));
        assertEquals(folder2Path, folders.get(1));
    }

    @Test
    public void whenFoldersAndFilesAreAvailable_getsAllPaths() throws IOException {
        // given
        String folder1 = "folder1";
        String folder2 = "folder2";
        String file1 = "file1.txt";
        Path folder1Path = Paths.get(DIRECTORY, folder1);
        Path folder2Path = Paths.get(DIRECTORY, folder2);
        Path file1Path = Paths.get(DIRECTORY, file1);

        Files.createDirectory(folder1Path);
        Files.createDirectory(folder2Path);
        Files.createFile(file1Path);

        // when
        List<Path> folders = FileSystemUtils.getChildPaths(DIRECTORY);

        // then
        assertEquals(3, folders.size());
        assertEquals(file1Path, folders.get(0));
        assertEquals(folder1Path, folders.get(1));
        assertEquals(folder2Path, folders.get(2));
    }
}