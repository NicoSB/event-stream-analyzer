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
package ch.nicosb.eventstreamanalyzer.data.sampling;

import cc.kave.commons.model.events.IIDEEvent;
import ch.nicosb.eventstreamanalyzer.testutils.TestEvent;
import org.junit.Before;
import org.junit.Test;

import java.time.ZonedDateTime;

import static org.junit.Assert.*;

public class IntervalPickerTest {

    private IntervalPicker picker;
    private final static int INTERVAL = 10;

    @Before
    public void startUp() {
        picker = new IntervalPicker(INTERVAL);
    }

    @Test
    public void whenFirstEventIsGiven_returnsTrue() {
        // given
        IIDEEvent event = new TestEvent(ZonedDateTime.now());

        // when
        boolean actual = picker.shouldSample(event);

        // then
        assertTrue(actual);
    }

    @Test
    public void whenEventIsWithinInterval_returnsFalse() {
        // given
        IIDEEvent event = new TestEvent(ZonedDateTime.now());
        IIDEEvent laterEvent = new TestEvent(event.getTriggeredAt().plusSeconds(INTERVAL - 1));

        // when
        picker.shouldSample(event);
        boolean actual = picker.shouldSample(laterEvent);

        // then
        assertFalse(actual);
    }

    @Test
    public void whenEventIsAfterIntervalEnd_returnsTrue() {
        // given
        IIDEEvent event = new TestEvent(ZonedDateTime.now());
        IIDEEvent laterEvent = new TestEvent(event.getTriggeredAt().plusSeconds(INTERVAL + 1));

        // when
        picker.shouldSample(event);
        boolean actual = picker.shouldSample(laterEvent);

        // then
        assertTrue(actual);
    }
}