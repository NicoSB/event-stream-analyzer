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

import cc.kave.commons.model.events.IIDEEvent;
import cc.kave.commons.model.events.visualstudio.IDEStateEvent;
import cc.kave.commons.model.events.visualstudio.WindowEvent;
import org.junit.Before;
import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class EventParserTest {

    private Path testResources;
    private final String RESOURCES_URI = "src/test/resources";
    private final String FOLDER1_URI = "folder1";
    private final String FOLDER2_URI = "folder2";
    private final String FILE1_URI = "test1.zip";
    private final String FILE2_URI = "test2.zip";
    private final String FILE3_URI = "test3.zip";

    @Before
    public void setUp() {
        testResources = Paths.get(RESOURCES_URI);
    }

    @Test
    public void whenZipIsNotEmpty_extractsEvents() {
        // when
        Path testZip = Paths.get(RESOURCES_URI, FOLDER1_URI, FILE1_URI);
        ArrayList<IIDEEvent> events = EventParser.extractEvents(testZip);

        // then
        assertEquals(4, events.size());
        assertTrue(events.get(0) instanceof IDEStateEvent);
        assertTrue(events.get(1) instanceof WindowEvent);
        assertTrue(events.get(2) instanceof WindowEvent);
        assertTrue(events.get(3) instanceof WindowEvent);
    }

    @Test
    public void whenMultipleZipsAreAvailable_parsesAllZips() {
        // when
        Path file1 = Paths.get(RESOURCES_URI, FOLDER1_URI, FILE1_URI);
        Path file2 = Paths.get(RESOURCES_URI, FOLDER1_URI, FILE2_URI);
        ArrayList<Path> paths = new ArrayList<>();
        paths.add(file1);
        paths.add(file2);

        ArrayList<EventStream> streams = EventParser.parseZips(paths);

        // then
        assertEquals(2, streams.size());
        assertEquals(4, streams.get(0).size());
        assertEquals(FILE1_URI, streams.get(0).getTitle());
        assertEquals(2, streams.get(1).size());
        assertEquals(FILE2_URI, streams.get(1).getTitle());

    }

    @Test
    public void whenMultipleFoldersArePassed_parsesAllZips() {
        // when
        ArrayList<EventStream> streams = EventParser.parseDirectory(RESOURCES_URI);

        // then

        assertEquals(3, streams.size());
        assertEquals(4, streams.get(0).size());
        assertEquals(FILE1_URI, streams.get(0).getTitle());
        assertEquals(2, streams.get(1).size());
        assertEquals(FILE2_URI, streams.get(1).getTitle());
        assertEquals(2, streams.get(2).size());
        assertEquals(FILE3_URI, streams.get(2).getTitle());
    }

    @Test(expected = IllegalArgumentException.class)
    public void whenUriIsInvalid_throwsIllegalArgumentException() {
        // when
        String invalidUri = "invalid";
        ArrayList<EventStream> events = EventParser.parseDirectory(invalidUri);

        // then throws
    }
}
