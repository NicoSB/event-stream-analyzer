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
import java.util.List;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class QueueProcessorTest {

    private final String fileName = "Test.arff";
    private QueueProcessor processor;
    private boolean notified;
    private ListeningEventQueue queue;

    @Mock
    private NotifyingZipParser parser;

    @Before
    public void setUp() {
        queue = new ListeningEventQueue(fileName);
        processor = new QueueProcessor(queue);
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

        Thread.sleep(10);

        processor.stop();
        // then
        assertTrue(notified);
    }

    @Test
    public void whenFirstEventIsParsed_createsFileWithFilename() {
        // given
        Aggregator aggregator = createAggregator();
        processor.registerAggregator(aggregator);

        // when
        processor.start();
        processor.stop();

        // then
        assertTrue(Files.exists(Paths.get(fileName)));
    }

    private Aggregator createAggregator() {
        return new Aggregator("Test") {
                @Override
                public String aggregateValue(List<IIDEEvent> events, IIDEEvent event) {
                    notified = true;
                    return "";
                }
            };
    }

    @After
    public void cleanUp() throws IOException {
        Files.delete(Paths.get(fileName));
    }
}