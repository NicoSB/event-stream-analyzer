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
import ch.nicosb.eventstreamanalyzer.testutils.TestEvent;
import org.junit.Before;
import org.junit.Test;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;


public class EventCountAggregatorTest {

    public static final int WINDOW_SIZE = 300;
    private EventCountAggregator aggregator;

    @Before
    public void setUp() {
        aggregator = new EventCountAggregator(WINDOW_SIZE);
    }

    @Test
    public void whenEventIsAggregated_returnsCorrectValue() {
        // given
        IIDEEvent event1 = new TestEvent(ZonedDateTime.now());
        IIDEEvent event2 = new TestEvent(ZonedDateTime.now().plusSeconds(1));

        ArrayList<IIDEEvent> events = new ArrayList<>();
        events.add(event1);
        events.add(event2);

        String expected = "2";

        // when
        aggregator.aggregateValue(events.get(0));
        Map actual = aggregator.aggregateValue(events.get(events.size() - 1));

        // then
        assertEquals(1, actual.size());
        assertTrue(actual.containsValue(expected));
    }

    @Test
    public void whenEventIsRemoved_returnsCorrectValue() {
        // given
        IIDEEvent event1 = new TestEvent(ZonedDateTime.now().minusDays(1));
        IIDEEvent event2 = new TestEvent(ZonedDateTime.now());

        ArrayList<IIDEEvent> events = new ArrayList<>();
        events.add(event1);
        events.add(event2);

        String expected = "1";

        // when
        aggregator.aggregateValue(events.get(0));
        Map result = aggregator.aggregateValue(events.get(events.size() - 1));

        // then
        assertEquals(1, result.size());
        assertTrue(result.containsValue(expected));
    }

    @Test
    public void whenHasMultipleWindows_returnsMultipleValues() {
        // given
        int longerWindowSize = WINDOW_SIZE + 10;
        aggregator = new EventCountAggregator(WINDOW_SIZE, longerWindowSize);
        IIDEEvent event1 = new TestEvent(ZonedDateTime.now().minusSeconds(WINDOW_SIZE + 1));
        IIDEEvent event2 = new TestEvent(ZonedDateTime.now());

        ArrayList<IIDEEvent> events = new ArrayList<>();
        events.add(event1);
        events.add(event2);

        String title1 = "EventsInLast" + WINDOW_SIZE + "s";
        String title2 = "EventsInLast" + longerWindowSize + "s";

        String expected1 = "1";
        String expected2 = "2";

        // when
        aggregator.aggregateValue(events.get(0));
        Map<String, String> result = aggregator.aggregateValue(events.get(events.size() - 1));

        // then
        assertEquals(2, result.size());
        assertEquals(expected1, result.get(title1));
        assertEquals(expected2, result.get(title2));
    }

    @Test
    public void returnsCorrectTitles() {
        // given
        int longerWindowSize = WINDOW_SIZE + 10;
        aggregator = new EventCountAggregator(WINDOW_SIZE, longerWindowSize);

        String title1 = "EventsInLast" + WINDOW_SIZE + "s";
        String title2 = "EventsInLast" + longerWindowSize + "s";

        // when
        Set<String> actual = aggregator.getTitles();

        // then
        assertTrue(actual.contains(title1));
        assertTrue(actual.contains(title2));
    }

}

