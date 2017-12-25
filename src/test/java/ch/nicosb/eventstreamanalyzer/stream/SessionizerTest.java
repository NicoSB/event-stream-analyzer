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
import ch.nicosb.eventstreamanalyzer.testutils.TestEvent;
import org.junit.Test;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SessionizerTest {

    private Sessionizer sessionizer;

    @Test
    public void whenAllEventsAreWithinTimeout_onlyCreatesOneSession() {
        // given
        int timeout = 1;

        IIDEEvent event1 = new TestEvent(ZonedDateTime.now());
        IIDEEvent event2 = new TestEvent(ZonedDateTime.now());

        List<Entry> entries = new ArrayList<>();
        entries.add(new Entry(event1));
        entries.add(new Entry(event2));
        sessionizer = new Sessionizer(entries, timeout);

        // when
        List<Session> actual = sessionizer.extractSessions();

        // then
        assertEquals(1, actual.size());
    }

    @Test
    public void whenEventsExceedingWithinTimeout_createsMultipleSessions() {
        // given
        int timeout = 1;

        IIDEEvent event1 = new TestEvent(ZonedDateTime.now());
        IIDEEvent event2 = new TestEvent(ZonedDateTime.now().plusSeconds(timeout + 1));

        List<Entry> entries = new ArrayList<>();
        entries.add(new Entry(event1));
        entries.add(new Entry(event2));
        sessionizer = new Sessionizer(entries, timeout);

        // when
        List<Session> actual = sessionizer.extractSessions();

        // then
        assertEquals(2, actual.size());
    }

    @Test
    public void whenEventsExceedingWithinTimeout_sessionsAreSorted() {
        // given
        int timeout = 1;

        IIDEEvent event1 = new TestEvent(ZonedDateTime.now());
        IIDEEvent event2 = new TestEvent(ZonedDateTime.now().plusSeconds(timeout + 1));

        List<Entry> entries = new ArrayList<>();
        entries.add(new Entry(event1));
        entries.add(new Entry(event2));
        sessionizer = new Sessionizer(entries, timeout);

        // when
        ArrayList<Session> actual = (ArrayList<Session>) sessionizer.extractSessions();

        // then
        assertTrue(actual.get(0).getEnd().isBefore(actual.get(actual.size() - 1).getStart()));
    }
}