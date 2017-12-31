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

import static org.junit.Assert.assertEquals;


public class EventCountAggregatorTest {

    private EventCountAggregator aggregator;

    @Before
    public void setUp() {
        aggregator = new EventCountAggregator("Title", 300);
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
        aggregator.aggregateValue(events, events.get(0));
        String actual = aggregator.aggregateValue(events, events.get(events.size() - 1));

        // then
        assertEquals(expected, actual);
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
        aggregator.aggregateValue(events, events.get(0));
        String actual = aggregator.aggregateValue(events, events.get(events.size() - 1));

        // then
        assertEquals(expected, actual);
    }
}

