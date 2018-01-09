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

import org.junit.Before;
import org.junit.Test;

import java.time.ZonedDateTime;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class IntervalActiveTimeWindowTest {

    IntervalActiveTimeWindow window;
    private final int WINDOW_SIZE = 10;
    private final int TIMEOUT = 6;

    @Before
    public void setUp() {
        window = new IntervalActiveTimeWindow(WINDOW_SIZE, TIMEOUT);
    }

    @Test
    public void whenIntervalIsAdded() {
        // given
        ZonedDateTime now = ZonedDateTime.now();
        Interval interval = new Interval(now, now);

        // when
        window.addInterval(interval);
        // then
        assertEquals(1, window.size());
        assertEquals(now, window.get(0).start);
        assertEquals(now, window.get(0).end);
    }

    @Test
    public void whenTimePointWithinTimeoutIsAdded_updatesInterval() {
        // given
        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime later = now.plusNanos(TIMEOUT);

        Interval interval1 = new Interval(now, now);
        Interval interval2 = new Interval(later, later);

        // when
        window.addInterval(interval1);
        window.addInterval(interval2);

        // then
        assertEquals(1, window.size());
        assertEquals(now, window.get(0).start);
        assertEquals(later, window.get(0).end);
    }

    @Test
    public void whenTimePointOutsideTimeoutIsAdded_createsNewInterval() {
        // given
        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime later = now.plusSeconds(TIMEOUT + 1);

        Interval interval1 = new Interval(now, now);
        Interval interval2 = new Interval(later, later);

        // when
        window.addInterval(interval1);
        window.addInterval(interval2);

        // then
        assertEquals(2, window.size());
        assertEquals(now, window.get(0).start);
        assertEquals(now, window.get(0).end);
        assertEquals(later, window.get(1).start);
        assertEquals(later, window.get(1).end);
    }

    @Test
    public void whenWindowIsExceededAndEndIsWithinTimeoutOfNewPoint_updatesOldInterval() {
        // given
        ZonedDateTime outsideWindow = ZonedDateTime.now();
        ZonedDateTime insideWindow = outsideWindow.plusSeconds(TIMEOUT);
        ZonedDateTime date = insideWindow.plusSeconds(TIMEOUT);

        ZonedDateTime newWindowStart = date.minusSeconds(WINDOW_SIZE);

        Interval interval1 = new Interval(outsideWindow, outsideWindow);
        Interval interval2 = new Interval(insideWindow, insideWindow);
        Interval interval3 = new Interval(date, date);

        // when
        window.addInterval(interval1);
        window.addInterval(interval2);
        window.addInterval(interval3);

        // then
        assertEquals(1, window.size());
        assertEquals(newWindowStart, window.get(0).start);
        assertEquals(date, window.get(0).end);
    }

    @Test
    public void whenWindowIsExceededAndEndIsOutsideOfTimeoutOfNewPoint_createsNewInterval() {
        // given
        ZonedDateTime outsideWindow = ZonedDateTime.now();
        ZonedDateTime insideWindow = outsideWindow.plusSeconds(TIMEOUT);
        ZonedDateTime date = insideWindow.plusSeconds(TIMEOUT + 1);

        ZonedDateTime newWindowStart = date.minusSeconds(WINDOW_SIZE);

        Interval interval1 = new Interval(outsideWindow, outsideWindow);
        Interval interval2 = new Interval(insideWindow, insideWindow);
        Interval interval3 = new Interval(date, date);

        // when
        window.addInterval(interval1);
        window.addInterval(interval2);
        window.addInterval(interval3);

        // then
        assertEquals(2, window.size());
        assertEquals(newWindowStart, window.get(0).start);
        assertEquals(insideWindow, window.get(0).end);
        assertEquals(date, window.get(1).start);
        assertEquals(date, window.get(1).end);
    }

    @Test
    public void whenWindowIsExceededAndIntervalIsOutsideOfWindow_removesOldInterval() {
        // given
        ZonedDateTime outsideWindow = ZonedDateTime.now();
        ZonedDateTime insideWindow = outsideWindow.plusSeconds(TIMEOUT);
        ZonedDateTime date = insideWindow.plusDays(TIMEOUT);

        Interval interval1 = new Interval(outsideWindow, outsideWindow);
        Interval interval2 = new Interval(insideWindow, insideWindow);
        Interval interval3 = new Interval(date, date);

        // when
        window.addInterval(interval1);
        window.addInterval(interval2);
        window.addInterval(interval3);

        // then
        assertEquals(1, window.size());
        assertEquals(date, window.get(0).start);
        assertEquals(date, window.get(0).end);
    }

    @Test
    public void returnsSumOfIntervals() {
        // given
        int differenceInSeconds = TIMEOUT;
        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime later = now.plusSeconds(differenceInSeconds);

        Interval interval1 = new Interval(now, now);
        Interval interval2 = new Interval(later, later);

        // when
        window.addInterval(interval1);
        window.addInterval(interval2);

        long expected = differenceInSeconds * 1000;

        // when
        window.addInterval(interval1);
        window.addInterval(interval2);

        long actual = window.getActiveTimeInMillis();

        // then
        assertEquals(expected, actual);
    }

    @Test
    public void whenIsEmpty_returnsIsEmpty() {
        // when
        boolean actual = window.isEmpty();

        // then
        assertTrue(actual);
    }

    @Test
    public void whenIsNotEmpty_returnsNotEmpty() {
        // given
        ZonedDateTime now = ZonedDateTime.now();
        window.addInterval(new Interval(now, now));

        // when
        boolean actual = window.isEmpty();

        // then
        assertFalse(actual);
    }
}