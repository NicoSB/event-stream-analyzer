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

import ch.nicosb.eventstreamanalyzer.stream.interval.Interval;
import ch.nicosb.eventstreamanalyzer.stream.interval.IntervalSplitter;
import ch.nicosb.eventstreamanalyzer.testutils.TestEvent;
import org.junit.Test;

import java.time.ZonedDateTime;
import java.util.*;

import static org.junit.Assert.*;

public class IntervalSplitterTest {

    private IntervalSplitter splitter;

    @Test
    public void whenEventsAreWithinInterval_addsEventsToSameInterval() {
        // given
        Session session = new Session();
        session.add(new TestEvent(ZonedDateTime.now()));
        session.add(new TestEvent(ZonedDateTime.now().plusSeconds(1)));

        List<Session> sessions = new ArrayList<>();
        sessions.add(session);

        splitter = new IntervalSplitter(sessions, 10);

        // when
        List<Interval> intervals = new ArrayList<>(splitter.split());

        // then
        assertEquals(1, intervals.size());
        assertEquals(2, intervals.get(0).size());
    }

    @Test
    public void whenEventsAreNotWithinInterval_addsEventsToDifferentIntervals() {
        // given
        Session session = new Session();
        session.add(new TestEvent(ZonedDateTime.now()));
        session.add(new TestEvent(ZonedDateTime.now().plusSeconds(11)));

        List<Session> sessions = new ArrayList<>();
        sessions.add(session);

        splitter = new IntervalSplitter(sessions, 10);

        // when
        List<Interval> intervals = new ArrayList<>(splitter.split());

        // then
        assertEquals(2, intervals.size());
        assertEquals(1, intervals.get(0).size());
        assertEquals(1, intervals.get(1).size());
    }
}