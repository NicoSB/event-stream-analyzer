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
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.time.ZonedDateTime;

public class IntervalEventWindowTest {

    private IntervalEventWindow window;
    private static final int INTERVAL = 300;

    @Before
    public void setUp() {
        window = new IntervalEventWindow(INTERVAL);
    }

    @Test
    public void whenEventIsAdded_eventIsAddedToList() {
        // given
        IIDEEvent event = new TestEvent(ZonedDateTime.now());

        // when
        window.add(event);

        // then
        Assert.assertEquals(1, window.size());
        Assert.assertEquals(event, window.get(0));
    }

    @Test
    public void whenEventIsAdded_removesOldEvents() {
        // given
        IIDEEvent old = new TestEvent(ZonedDateTime.now().minusDays(1));
        IIDEEvent event = new TestEvent(ZonedDateTime.now());

        // when
        window.add(old);
        window.add(event);

        // then
        Assert.assertEquals(1, window.size());
        Assert.assertEquals(event, window.get(0));
    }

    @Test
    public void whenEventIsAdded_sizeReturnsCorrectNumber() {
        // given
        IIDEEvent old = new TestEvent(ZonedDateTime.now());
        IIDEEvent event = new TestEvent(ZonedDateTime.now());

        // when
        window.add(old);
        window.add(event);

        // then
        Assert.assertEquals(2, window.size());
    }

    @Test
    public void whenEventIsAdded_getReturnsCorrectElement() {
        // given
        IIDEEvent first = new TestEvent(ZonedDateTime.now());
        IIDEEvent second = new TestEvent(ZonedDateTime.now().plusSeconds(1));

        // when
        window.add(first);
        window.add(second);

        // then
        Assert.assertEquals(first, window.get(0));
        Assert.assertEquals(second, window.get(1));
    }
}
