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
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.*;

public class ActiveTimeAggregatorTest {

    private static int WINDOW_SIZE = 10;
    private static int TIMEOUT = 6;

    private ActiveTimeAggregator aggregator;
    private String expectedTitle;

    @Before
    public void setUp() {
        aggregator = new ActiveTimeAggregator(WINDOW_SIZE, TIMEOUT);
        expectedTitle = String.format(ActiveTimeAggregator.TITLE_BLUEPRINT, WINDOW_SIZE);
    }

    @Test
    public void returnsCorrectTitles() {
        // when
        Set<String> titles = aggregator.getTitles();

        // then
        assertEquals(1, titles.size());
        assertTrue(titles.contains(expectedTitle));
    }

    @Test
    public void whenFirstEventIsAdded_returnsRatio() {
        // given
        int difference = 1;
        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime plusDifference = now.plusSeconds(difference);

        IIDEEvent event = new TestEvent(now, plusDifference);

        String expected = String.valueOf((float)(difference * 1000) / (WINDOW_SIZE * 1000));

        // when
        Map<String, String> result = aggregator.aggregateValue(event);

        // then
        assertEquals(expected, result.get(expectedTitle));
    }

    @Test
    public void whenMultipleEventsAreAdded_returnsRatio() {
        // given
        int difference = 1;
        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime plusDifference = ZonedDateTime.now().plusSeconds(difference);

        IIDEEvent event = new TestEvent(now, now);
        IIDEEvent laterEvent = new TestEvent(plusDifference, plusDifference);

        String expected = String.valueOf((float)(difference * 1000) / (WINDOW_SIZE * 1000));

        // when
        aggregator.aggregateValue(event);
        Map<String, String> result = aggregator.aggregateValue(laterEvent);

        // then
        assertEquals(expected, result.get(expectedTitle));
    }

    @Test
    public void whenEventOutsideOfWindowIsAddedEventsAreAdded_ignoresOldEventsRatio() {
        // given
        int difference = 1;
        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime later = ZonedDateTime.now().plusSeconds(difference);
        ZonedDateTime last = now.plusDays(WINDOW_SIZE);

        IIDEEvent event = new TestEvent(now, now);
        IIDEEvent laterEvent = new TestEvent(later, later);
        IIDEEvent lastEvent = new TestEvent(last, last.plusSeconds(difference));

        String expected = String.valueOf((float)(difference * 1000) / (WINDOW_SIZE * 1000));

        // when
        aggregator.aggregateValue(event);
        aggregator.aggregateValue(laterEvent);
        Map<String, String> result = aggregator.aggregateValue(lastEvent);

        // then
        assertEquals(expected, result.get(expectedTitle));
    }

}