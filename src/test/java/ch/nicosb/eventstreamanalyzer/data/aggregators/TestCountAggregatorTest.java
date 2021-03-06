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
package ch.nicosb.eventstreamanalyzer.data.aggregators;

import cc.kave.commons.model.events.IIDEEvent;
import cc.kave.commons.model.events.testrunevents.TestRunEvent;
import ch.nicosb.eventstreamanalyzer.testutils.TestEvent;
import org.junit.Before;
import org.junit.Test;

import java.time.ZonedDateTime;
import java.util.Map;
import java.util.Set;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

public class TestCountAggregatorTest {

    private TestCountAggregator aggregator;

    @Before
    public void setUp() {
        aggregator = new TestCountAggregator();
    }

    @Test
    public void returnsCorrectTitles() {
        // when
        Set<String> titles = aggregator.getTitles();

        // then
        assertEquals(1, titles.size());
        assertTrue(titles.contains(TestCountAggregator.title));
    }

    @Test
    public void whenNoTestEventIsGiven_doesNotIncreaseCounter() {
        // given
        IIDEEvent event = new TestEvent(ZonedDateTime.now());
        String expected = String.valueOf(0);

        // when
        Map<String, String> results = aggregator.aggregateValue(event);

        // then
        assertEquals(1, results.size());
        assertEquals(expected, results.get(TestCountAggregator.title));
    }

    @Test
    public void whenTestEventIsGiven_increasesCounter() {
        // given
        IIDEEvent event = new TestRunEvent();

        String expected = String.valueOf(1);

        // when
        Map<String, String> results = aggregator.aggregateValue(event);

        // then
        assertEquals(1, results.size());
        assertEquals(expected, results.get(TestCountAggregator.title));
    }
}