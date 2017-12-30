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
package ch.nicosb.eventstreamanalyzer.parser;

import cc.kave.commons.model.events.IIDEEvent;
import ch.nicosb.eventstreamanalyzer.testutils.TestEvent;
import org.junit.Before;
import org.junit.Test;

import java.time.ZonedDateTime;

import static org.junit.Assert.*;

public class ListeningEventQueueTest {

    private static final String TITLE = "Title";
    private ListeningEventQueue queue;

    @Before
    public void setUp() {
        queue = new ListeningEventQueue(TITLE);
    }

    @Test
    public void whenEventIsAdded_eventIsAdded() {
        // given
        IIDEEvent event = new TestEvent(ZonedDateTime.now());

        // when
        queue.add(event);

        // then
        assertEquals(event, queue.poll());
    }

    @Test
    public void whenEventIsPolled_eventIsRemoved() {
        // given
        IIDEEvent event = new TestEvent(ZonedDateTime.now());

        // when
        queue.add(event);
        IIDEEvent polled = queue.poll();

        // then
        assertEquals(event, polled);
        assertEquals(0, queue.size());
    }

    @Test
    public void whenEventIsParsed_eventIsAdded() {
        // given
        IIDEEvent event = new TestEvent(ZonedDateTime.now());

        // when
        queue.onEventParsed(event);

        // then
        assertEquals(1, queue.size());
        assertEquals(event, queue.poll());
    }
}