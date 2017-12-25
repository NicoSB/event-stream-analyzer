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
package ch.nicosb.eventstreamanalyzer.stream;

import cc.kave.commons.model.events.IIDEEvent;
import ch.nicosb.eventstreamanalyzer.data.Entry;
import ch.nicosb.eventstreamanalyzer.stream.interval.FirstAndLastRemover;
import ch.nicosb.eventstreamanalyzer.stream.interval.Interval;
import ch.nicosb.eventstreamanalyzer.testutils.TestEvent;
import org.junit.Before;
import org.junit.Test;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class FirstAndLastRemoverTest {

    public static final int TIMEOUT = 10;
    private FirstAndLastRemover remover;

    @Before
    public void setUp() {
        remover = new FirstAndLastRemover(TIMEOUT);
    }

    @Test
    public void removesFirstSet() {
        // given
        List<Interval> intervals = createIntervals();

        // when
        List<Interval> actual = remover.process(intervals);

        // then
        assertFalse(actual.contains(intervals.get(0)));
    }

    @Test
    public void removesLastSet() {
        // given
        List<Interval> intervals = createIntervals();

        // when
        List<Interval> actual = remover.process(intervals);

        // then
        assertFalse(actual.contains(intervals.get(intervals.size() - 1)));
    }

    @Test
    public void whenHasThreeIntervals_doesOnlyRemoveEdgeSets() {
        // given
        List<Interval> intervals = createIntervals();

        // when
        List<Interval> actual = remover.process(intervals);

        // then
        assertEquals(1, actual.size());
        assertTrue(actual.contains(intervals.get(1)));
    }

    private List<Interval> createIntervals() {
        List<Interval> intervals = new ArrayList<>();

        Interval first = new Interval(TIMEOUT);
        Interval middle = new Interval(TIMEOUT);
        Interval last = new Interval(TIMEOUT);

        IIDEEvent event = new TestEvent(ZonedDateTime.now());
        IIDEEvent event2 = new TestEvent(ZonedDateTime.now().plusSeconds(TIMEOUT));
        IIDEEvent event3 = new TestEvent(ZonedDateTime.now().plusSeconds(TIMEOUT + 1));

        first.add(new Entry(event));
        middle.add(new Entry(event2));
        last.add(new Entry(event3));

        intervals.add(first);
        intervals.add(middle);
        intervals.add(last);
        return intervals;
    }

    @Test
    public void whenHasMiddleSetsWithinInterval_doesNotRemoveTheseSets() {
        // given
        List<Interval> intervals = new ArrayList<>();

        Interval first = new Interval(TIMEOUT);
        Interval middle = new Interval(TIMEOUT);
        Interval middle2 = new Interval(TIMEOUT);
        Interval last = new Interval(TIMEOUT);

        IIDEEvent event = new TestEvent(ZonedDateTime.now());
        IIDEEvent event2 = new TestEvent(ZonedDateTime.now().plusSeconds(TIMEOUT));
        IIDEEvent event3 = new TestEvent(ZonedDateTime.now().plusSeconds(TIMEOUT + 1));
        IIDEEvent event4 = new TestEvent(ZonedDateTime.now().plusSeconds(TIMEOUT + 2));

        first.add(new Entry(event));
        middle.add(new Entry(event2));
        middle2.add(new Entry(event3));
        last.add(new Entry(event4));

        intervals.add(first);
        intervals.add(middle);
        intervals.add(middle2);
        intervals.add(last);

        // when
        List<Interval> actual = remover.process(intervals);

        // then
        assertEquals(2, actual.size());
        assertTrue(actual.contains(intervals.get(1)));
        assertTrue(actual.contains(middle2));
    }

    @Test
    public void whenHasMiddleSetsExceedingTimeout_removesSets() {
        // given
        List<Interval> intervals = new ArrayList<>();

        Interval first = new Interval(TIMEOUT);
        Interval middle = new Interval(TIMEOUT);
        Interval middle2 = new Interval(TIMEOUT);
        Interval last = new Interval(TIMEOUT);

        IIDEEvent event = new TestEvent(ZonedDateTime.now());
        IIDEEvent event2 = new TestEvent(ZonedDateTime.now().plusSeconds(TIMEOUT));
        IIDEEvent event3 = new TestEvent(ZonedDateTime.now().plusSeconds(2 * TIMEOUT + 1));
        IIDEEvent event4 = new TestEvent(ZonedDateTime.now().plusSeconds(3 * TIMEOUT));

        first.add(new Entry(event));
        middle.add(new Entry(event2));
        middle2.add(new Entry(event3));
        last.add(new Entry(event4));

        intervals.add(first);
        intervals.add(middle);
        intervals.add(middle2);
        intervals.add(last);

        // when
        List<Interval> actual = remover.process(intervals);

        // then
        assertTrue(actual.isEmpty());
    }

    @Test
    public void whenSetIsEmpty_removesSet() {
        // given
        List<Interval> intervals = createIntervals();

        Interval interval = new Interval(TIMEOUT);
        intervals.add(interval);

        // when
        List<Interval> actual = remover.process(intervals);

        // then
        assertFalse(actual.contains(interval));
    }
}