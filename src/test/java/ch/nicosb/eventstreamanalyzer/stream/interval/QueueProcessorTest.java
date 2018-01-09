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
import ch.nicosb.eventstreamanalyzer.parser.NotifyingZipParser;
import ch.nicosb.eventstreamanalyzer.testutils.TestEvent;
import ch.nicosb.eventstreamanalyzer.testutils.TestFileUtils;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class QueueProcessorTest {

    public static final int INTERVAL = 15;
    public static final String VALUE = "1.0";
    public static final String TITLE = "Test";
    public static final String ARFF = ".arff";

    private String fileName;
    private QueueProcessor processor;
    private boolean notified;
    private ListeningEventQueue queue;

    @Before
    public void setUp() throws IOException {
        fileName = TestFileUtils.getRandomFilePath(ARFF).toString();
        queue = new ListeningEventQueue(fileName);
        processor = new  QueueProcessor(queue, INTERVAL);
        notified = false;
    }

    @Test
    public void whenEventIsParsed_AggregatorsAreNotified() throws InterruptedException {
        // given
        Aggregator aggregator = createAggregator();
        processor.registerAggregator(aggregator);
        queue.add(new TestEvent(ZonedDateTime.now()));

        // when
        processor.start();
        processor.stop();

        Thread.sleep(10);

        // then
        assertTrue(notified);
    }

    @Test
    public void whenFirstEventIsParsed_createsFileWithFilename() throws InterruptedException {
        // given
        Aggregator aggregator = createAggregator();
        processor.registerAggregator(aggregator);

        // when
        processor.start();
        processor.stop();

        Thread.sleep(10);

        // then
        assertTrue(Files.exists(Paths.get(fileName)));
    }

    private Aggregator createAggregator() {
        return new Aggregator() {
            @Override
            public Map<String, String> aggregateValue(IIDEEvent event) {
                notified = true;
                Map<String, String> map = new HashMap<>();
                map.put(TITLE, VALUE);
                return map;
            }

            @Override
            public Set<String> getTitles() {
                Set<String> titles = new TreeSet<>();
                titles.add(TITLE);

                return titles;
            }
        };
    }

    @Test
    public void whenEventsAreTooFarApart_createsDummyEventAndEndEvent() throws IOException, InterruptedException {
        // given
        IIDEEvent event = new TestEvent(ZonedDateTime.now());
        IIDEEvent later = new TestEvent(event.getTriggeredAt().plusSeconds(INTERVAL * 3));
        Aggregator aggregator = createAggregator();

        processor.registerAggregator(aggregator);

        // when
        queue.add(event);
        queue.add(later);

        processor.start();
        processor.stop();

        Thread.sleep(50);

        List<String> lines = Files.readAllLines(Paths.get(fileName));
        List<String> dataLines = lines.stream()
                .filter(line -> line.contains(VALUE))
                .collect(Collectors.toList());

        // then
        assertEquals(4, dataLines.size());
    }

    @Test
    public void whenEventsWithinInterval_OnlyCreatesEndEvent() throws IOException, InterruptedException {
        // given
        IIDEEvent event = new TestEvent(ZonedDateTime.now());
        IIDEEvent later = new TestEvent(event.getTriggeredAt().plusSeconds(INTERVAL * 2));
        Aggregator aggregator = createAggregator();

        processor.registerAggregator(aggregator);

        // when
        queue.add(event);
        queue.add(later);

        processor.start();
        processor.stop();

        Thread.sleep(50);

        List<String> lines = Files.readAllLines(Paths.get(fileName));
        List<String> dataLines = lines.stream()
                .filter(line -> line.contains(VALUE))
                .collect(Collectors.toList());

        // then
        assertEquals(3, dataLines.size());
    }

    @After
    public void cleanUp() throws IOException {
        if (Files.exists(Paths.get(fileName)))
            Files.delete(Paths.get(fileName));
    }
}