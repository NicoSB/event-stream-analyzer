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
import ch.nicosb.eventstreamanalyzer.data.aggregators.NominalAggregator;
import ch.nicosb.eventstreamanalyzer.testutils.TestEvent;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.ZonedDateTime;
import java.util.*;

import static org.junit.Assert.*;

public class ArffWriterTest {

    private static final String NOMINAL_AGGREGATOR_TITLE = "NominalTest";
    private static final String NUMERICAL_AGGREGATOR_TITLE = "NumericalTest";
    private static String fileName;
    private ArffWriter writer;
    private TreeSet<Aggregator> aggregators;

    @Before
    public void setUp() {
        fileName = "Test.arff";
        aggregators = new TreeSet<>(Comparator.comparing(agg -> agg.getClass().getName()));
        writer = new ArffWriter(aggregators, fileName);
    }

    @Test
    public void whenCreateNewFileIsCalled_CreatesFile() throws IOException {
        // when
        writer.createNewFile();
        writer.close();

        // then
        assertTrue(Files.exists(Paths.get(fileName)));
    }

    @Test
    public void whenFileNameLacksArffEnding_CreatesFileWithEnding() throws IOException {
        // given
        fileName = "Test";
        writer = new ArffWriter(aggregators, fileName);

        fileName = fileName + ".arff";

        // when
        writer.createNewFile();
        writer.close();

        // then
        assertTrue(Files.exists(Paths.get(fileName)));
    }

    @Test
    public void whenNewFileIsCreated_writesHeader() throws IOException {
        // given
        String expected = "@RELATION Test";

        // when
        writer.createNewFile();
        writer.close();
        List<String> lines = Files.readAllLines(Paths.get(fileName));

        // then
        assertEquals(expected, lines.get(0));
        assertEquals("", lines.get(1));
    }

    @Test
    public void whenNumericalAggregatorIsGiven_writesNumericalAttribute() throws IOException {
        // given
        Aggregator aggregator = createNumericalAggregator();
        aggregators.add(aggregator);
        String expected = "@ATTRIBUTE " + NUMERICAL_AGGREGATOR_TITLE + " NUMERIC";

        // when
        writer.createNewFile();
        writer.close();
        List<String> lines = Files.readAllLines(Paths.get(fileName));

        // then
        assertEquals(expected, lines.get(2));
    }

    @Test
    public void whenNominalAggregatorIsGiven_writesNominalAttributeWithValues() throws IOException {
        // given
        Aggregator aggregator = createNominalAggregator();
        aggregators.add(aggregator);
        String expected = "@ATTRIBUTE " + NOMINAL_AGGREGATOR_TITLE + " {A,B}";

        // when
        writer.createNewFile();
        writer.close();
        List<String> lines = Files.readAllLines(Paths.get(fileName));

        // then
        assertEquals(expected, lines.get(2));
    }

    @Test
    public void whenFileIsCreated_writesDataHeader() throws IOException {
        // given
        String expected = "@DATA";

        // when
        writer.createNewFile();
        writer.close();
        List<String> lines = Files.readAllLines(Paths.get(fileName));

        // then
        assertEquals(expected, lines.get(3));
    }

    @Test
    public void whenNominalAggregatorIsGiven_writesNominalDataRow() throws IOException {
        // given
        Aggregator aggregator = createNominalAggregator();
        aggregators.add(aggregator);
        String expected = "A";

        Map<String, String> map = new HashMap<>();
        map.putAll(aggregator.aggregateValue(new TestEvent(ZonedDateTime.now())));

        // when
        writer.createNewFile();
        writer.writeData(map);
        writer.close();
        List<String> lines = Files.readAllLines(Paths.get(fileName));

        // then
        assertEquals(expected, lines.get(5));
    }

    @Test
    public void whenNumericalAggregatorIsGiven_writesNumericalDataRow() throws IOException {
        // given
        Aggregator aggregator = createNumericalAggregator();
        aggregators.add(aggregator);
        String expected = "0.0";

        Map<String, String> map = new HashMap<>();
        map.putAll(aggregator.aggregateValue(new TestEvent(ZonedDateTime.now())));

        // when
        writer.createNewFile();
        writer.writeData(map);
        writer.close();
        List<String> lines = Files.readAllLines(Paths.get(fileName));

        // then
        assertEquals(expected, lines.get(5));
    }

    @Test
    public void whenMultipleAggregatorsAreGiven_separatesEntriesWithCommas() throws IOException {
        // given
        Aggregator numericalAggregator = createNumericalAggregator();
        Aggregator nominalAggregator = createNominalAggregator();
        aggregators.add(nominalAggregator);
        aggregators.add(numericalAggregator);

        Map<String, String> map = new HashMap<>();
        map.putAll(numericalAggregator.aggregateValue(new TestEvent(ZonedDateTime.now())));
        map.putAll(nominalAggregator.aggregateValue(new TestEvent(ZonedDateTime.now())));

        String expected = "0.0,A";

        // when
        writer.createNewFile();
        writer.writeData(map);
        writer.close();
        List<String> lines = Files.readAllLines(Paths.get(fileName));

        // then
        assertEquals(expected, lines.get(6));
    }

    private Aggregator createNumericalAggregator() {
        return new Aggregator() {
            @Override
            public Map<String, String> aggregateValue(IIDEEvent event) {
                Map<String, String> map = new HashMap<>();
                map.put(NUMERICAL_AGGREGATOR_TITLE, "0.0");
                return map;
            }

            @Override
            public Set<String> getTitles() {
                Set<String> set = new HashSet<>();
                set.add(NUMERICAL_AGGREGATOR_TITLE);

                return set;
            }
        };
    }

    private Aggregator createNominalAggregator() {
        return new NominalAggregator("A", "B") {
            @Override
            public Map<String, String> aggregateValue(IIDEEvent event) {
                Map<String, String> map = new HashMap<>();
                map.put(NOMINAL_AGGREGATOR_TITLE, "A");

                return map;
            }

            @Override
            public Set<String> getTitles() {
                Set<String> set = new HashSet<>();
                set.add(NOMINAL_AGGREGATOR_TITLE);
                return set;
            }
        };
    }

    @After
    public void cleanUp() throws IOException {
        Files.delete(Paths.get(fileName));
    }
}