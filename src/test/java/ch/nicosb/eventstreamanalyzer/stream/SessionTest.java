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
import ch.nicosb.eventstreamanalyzer.testutils.TestEvent;
import org.junit.Before;
import org.junit.Test;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class SessionTest {

    private Session session;

    @Before
    public void setUp() {
        session = new Session();
    }

    @Test
    public void whenEventIsAdded_eventIsAdded() {
        // given
        IIDEEvent event = new TestEvent(ZonedDateTime.now());

        // when
        session.add(event);

        // then
        assertEquals(1, session.size());
        assertTrue(session.contains(event));
    }

    @Test
    public void whenEventsAreAdded_eventsAreAdded() {
        // given
        IIDEEvent event1 = new TestEvent(ZonedDateTime.now());
        IIDEEvent event2 = new TestEvent(ZonedDateTime.now().minusDays(1));

        List<IIDEEvent> events = new ArrayList<>();
        events.add(event1);
        events.add(event2);

        // when
        session.addAll(events);

        // then
        assertEquals(2, events.size());
        assertEquals(event1, session.first());
        assertEquals(event2, session.last());
    }

    @Test
    public void getStart_returnsTriggeredAtOfFirst() {
        // given
        ZonedDateTime begin = ZonedDateTime.now();
        IIDEEvent event1 = new TestEvent(begin);
        IIDEEvent event2 = new TestEvent(begin.plusSeconds(1));

        // when
        session.add(event1);
        session.add(event2);

        // then
        assertEquals(begin, session.getStart());
    }

    @Test
    public void getEnd_returnsTriggeredAtOfLast() {
        // given
        ZonedDateTime end = ZonedDateTime.now();
        IIDEEvent event1 = new TestEvent(ZonedDateTime.now().minusDays(1));
        IIDEEvent event2 = new TestEvent(end);

        // when
        session.add(event1);
        session.add(event2);

        // then
        assertEquals(end, session.getEnd());
    }
}