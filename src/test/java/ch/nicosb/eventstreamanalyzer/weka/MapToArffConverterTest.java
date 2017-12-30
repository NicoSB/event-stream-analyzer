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
package ch.nicosb.eventstreamanalyzer.weka;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertTrue;

public class MapToArffConverterTest {

    private static final String NUMERIC_KEY = "NUMERIC";
    private static final String ATTR_1 = "Attr1";
    private static final String ATTR_2 = "Attr2";
    private static final String ATTR_3 = "Attr3";
    private static final String ATTR_4 = "Attr4";
    private static final String ATTRIBUTE_KEY = "@ATTRIBUTE ";
    private static final String RELATION_KEY = "@RELATION ";
    private static final String DATA_KEY = "@DATA";
    private static final String FILE_URI = "MapTest.arff";
    private static final String RELATION = "relation";

    private MapToArffConverter converter;
    private HashMap<String, String> map;

    @Before
    public void setUp() {
        converter = new MapToArffConverter(RELATION, FILE_URI);
        map = new HashMap<>();
        initMap();
    }

    private void initMap() {
        map.put(ATTR_1, "1.0");
        map.put(ATTR_2, "2.0");
        map.put(ATTR_3, "3.0");
        map.put(ATTR_4, "4.0");
    }

    @Test
    public void whenObjectIsWritten_fileStartsWithRelation() throws IOException {
        // given
        String expected = RELATION_KEY + RELATION;

        // when
        converter.add(map);
        converter.writeFile();

        List<String> lines = Files.readAllLines(Paths.get(FILE_URI));

        // then
        assertEquals(expected, lines.get(0));
    }

    @Test
    public void whenObjectIsWritten_fileContainsAttributes() throws IOException {
        // given
        String expected1 = ATTRIBUTE_KEY + ATTR_1 + " " + NUMERIC_KEY;
        String expected2 = ATTRIBUTE_KEY + ATTR_2 + " " + NUMERIC_KEY;
        String expected3 = ATTRIBUTE_KEY + ATTR_3 + " " + NUMERIC_KEY;
        String expected4 = ATTRIBUTE_KEY + ATTR_4 + " " + NUMERIC_KEY;

        // when
        converter.add(map);
        converter.writeFile();

        List<String> attributeLines = Files.readAllLines(Paths.get(FILE_URI))
                .stream()
                .filter(line -> line.startsWith(ATTRIBUTE_KEY))
                .collect(Collectors.toList());

        // then
        assertEquals(4, attributeLines.size());
        assertTrue(attributeLines.contains(expected1));
        assertTrue(attributeLines.contains(expected2));
        assertTrue(attributeLines.contains(expected3));
        assertTrue(attributeLines.contains(expected4));
    }

    @Test
    public void whenObjectIsWritten_fileContainsDataRows() throws IOException {
        // given
        String expected = map.values().toString()
                .replace(" ", "")
                .replace("[", "")
                .replace("]", "");

        // when
        converter.add(map);
        converter.add(map);
        converter.writeFile();

        List<String> lines = Files.readAllLines(Paths.get(FILE_URI));
        int startIndex = getDataRowIndex(lines) + 2;

        List<String> dataLines = lines.subList(startIndex, lines.size());

        // then
        assertEquals(2, dataLines.size());
        assertEquals(expected, dataLines.get(0));
        assertEquals(expected, dataLines.get(1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void whenMapWithDifferentKeysIsAdded_throwsIllegalArgumentException() {
        // given
        HashMap<String, String> map2 = new HashMap<>();

        // when
        converter.add(map);
        converter.add(map2);
        converter.writeFile();

        // then throws
    }

    private int getDataRowIndex(List<String> attributeLines) {
        return attributeLines.indexOf(DATA_KEY);
    }

    @After
    public void cleanUp() throws IOException {
        Path path = Paths.get(FILE_URI);
        if (Files.exists(path))
            Files.delete(path);
    }
}