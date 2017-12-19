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

import cc.kave.commons.model.events.IDEEvent;
import cc.kave.commons.model.events.IIDEEvent;
import ch.nicosb.eventstreamanalyzer.testutils.TestEvent;
import org.junit.Test;

import java.time.ZonedDateTime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class CompactEventTest {

    @Test
    public void whenIIDEEventIsGiven_CorrectlyInstantiates() {
        // given
        final ZonedDateTime now = ZonedDateTime.now();
        IIDEEvent TestEvent = new TestEvent(now);

        CompactEvent event = new CompactEvent(TestEvent);

        // then
        assertEquals(now, event.dateTime);
        assertEquals(TestEvent.getClass().getName(), event.eventType);
    }

    @Test
    public void whenObjectsAreEqual_equalsReturnsTrue() {
        // given
        final ZonedDateTime now = ZonedDateTime.now();
        IIDEEvent TestEvent = new TestEvent(now);

        CompactEvent event1 = new CompactEvent(TestEvent);
        CompactEvent event2 = new CompactEvent(TestEvent);

        // then
        assertEquals(event1, event2);
    }

    @Test
    public void whenObjectsAreEqual_hashCodeIsEqual() {
        // given
        final ZonedDateTime now = ZonedDateTime.now();
        IIDEEvent TestEvent = new TestEvent(now);

        CompactEvent event1 = new CompactEvent(TestEvent);
        CompactEvent event2 = new CompactEvent(TestEvent);

        // then
        assertEquals(event1.hashCode(), event2.hashCode());
    }

    @Test
    public void whenDateTimeIsDifferent_equalsReturnsFalse() {
        // given
        final ZonedDateTime now = ZonedDateTime.now();
        final ZonedDateTime now2 = ZonedDateTime.now();
        IIDEEvent TestEvent = new TestEvent(now);
        IIDEEvent TestEvent2 = new TestEvent(now2);

        CompactEvent event1 = new CompactEvent(TestEvent);
        CompactEvent event2 = new CompactEvent(TestEvent2);

        // then
        assertNotEquals(event1, event2);
    }

    @Test
    public void whenDateTimeIsDifferent_hashCodeIsDifferent() {
        // given
        final ZonedDateTime now = ZonedDateTime.now();
        IIDEEvent TestEvent = new TestEvent(now);
        IIDEEvent TestEvent2 = new TestEvent(now.minusDays(1));

        CompactEvent event1 = new CompactEvent(TestEvent);
        CompactEvent event2 = new CompactEvent(TestEvent2);

        // then
        assertNotEquals(event1.hashCode(), event2.hashCode());
    }

    @Test
    public void whenEventTypeIsDifferent_equalsReturnsFalse() {
        // given
        final ZonedDateTime now = ZonedDateTime.now();
        IIDEEvent TestEvent = new TestEvent(now);
        IIDEEvent iideEvent = new IDEEvent() {
            @Override
            public ZonedDateTime getTerminatedAt() {
                return now;
            }
        };

        CompactEvent event1 = new CompactEvent(TestEvent);
        CompactEvent event2 = new CompactEvent(iideEvent);

        // then
        assertNotEquals(event1, event2);
    }

    @Test
    public void whenEventTypeIsDifferent_hashCodeIsDifferent() {
        // given
        final ZonedDateTime now = ZonedDateTime.now();
        IIDEEvent TestEvent = new TestEvent(now);
        IIDEEvent iideEvent = new IDEEvent() {
            @Override
            public ZonedDateTime getTerminatedAt() {
                return now;
            }
        };

        CompactEvent event1 = new CompactEvent(TestEvent);
        CompactEvent event2 = new CompactEvent(iideEvent);

        // then
        assertNotEquals(event1.hashCode(), event2.hashCode());
    }
}
