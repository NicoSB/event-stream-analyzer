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
import ch.nicosb.eventstreamanalyzer.stream.EventStream;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ZipUtilsTest {

    private final String RESOURCES_URI = "src/test/resources";
    private final String FILE1_URI = "test1.zip";
    private final String FILE2_URI = "test2.zip";
    private final String FILE3_URI = "test3.zip";

    @Test
    public void returnsAllZips() throws IOException {
        // when
        List<Path> paths = ZipUtils.getAllZips(RESOURCES_URI);

        // then
        assertEquals(3, paths.size());
        assertTrue(paths.get(0).endsWith(FILE1_URI));
        assertTrue(paths.get(1).endsWith(FILE2_URI));
        assertTrue(paths.get(2).endsWith(FILE3_URI));
    }
}
