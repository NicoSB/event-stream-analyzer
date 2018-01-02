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
import org.junit.Test;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class EventListTransformerTest {

    @Test
    public void whenValidListIsGiven_transformsCorrectly() {
        // given
        IIDEEvent event1 = new TestEvent(ZonedDateTime.now());
        IIDEEvent event2 = new TestEvent(ZonedDateTime.now());
        IIDEEvent event3 = new TestEvent(ZonedDateTime.now());

        CompactEvent expectedEvent1 = new CompactEvent(event1);
        CompactEvent expectedEvent2 = new CompactEvent(event2);
        CompactEvent expectedEvent3 = new CompactEvent(event3);

        List<IIDEEvent> events = new ArrayList<>();
        events.add(event1);
        events.add(event2);
        events.add(event3);

        // when
        List<CompactEvent> compactEvents = EventListTransformer.fromEventList(events);

        // then
        assertEquals(3, compactEvents.size());
        assertEquals(expectedEvent1, compactEvents.get(0));
        assertEquals(expectedEvent2, compactEvents.get(1));
        assertEquals(expectedEvent3, compactEvents.get(2));
    }

    @Test
    public void whenListIsUnsorted_sortsListByDateTime() {
        // given
        IIDEEvent event1 = new TestEvent(ZonedDateTime.now());
        IIDEEvent event2 = new TestEvent(ZonedDateTime.now().plusDays(1));
        IIDEEvent event3 = new TestEvent(ZonedDateTime.now().plusDays(2));

        CompactEvent earliest = new CompactEvent(event1);
        CompactEvent middle = new CompactEvent(event2);
        CompactEvent latest = new CompactEvent(event3);

        List<IIDEEvent> events = new ArrayList<>();
        events.add(event3);
        events.add(event1);
        events.add(event2);

        // when
        List<CompactEvent> compactEvents = EventListTransformer.fromEventList(events);

        // then
        assertEquals(3, compactEvents.size());
        assertEquals(earliest, compactEvents.get(0));
        assertEquals(middle, compactEvents.get(1));
        assertEquals(latest, compactEvents.get(2));
    }
}
