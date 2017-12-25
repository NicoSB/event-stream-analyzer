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
import cc.kave.commons.model.events.visualstudio.BuildEvent;
import ch.nicosb.eventstreamanalyzer.data.Entry;
import ch.nicosb.eventstreamanalyzer.data.HasEventAggregator;
import ch.nicosb.eventstreamanalyzer.testutils.TestEvent;
import org.junit.Before;
import org.junit.Test;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class HasEventAggregatorTest {

    private HasEventAggregator aggregator;

    @Before
    public void setUp() {
        aggregator = new HasEventAggregator(TestEvent.class);
    }

    @Test
    public void whenListContainsEvent_returnsOneDotZero() {
        // given
        List<Entry> entries = new ArrayList<>();
        IIDEEvent event = new TestEvent(ZonedDateTime.now());
        entries.add(new Entry(event));

        // when
        double actual = aggregator.aggregateValue(entries);

        // then
        assertEquals(1.0, actual, 0);
    }

    @Test
    public void whenListDoesNotContainEvent_returnsZeroDotZero() {
        // given
        List<Entry> entries = new ArrayList<>();
        IIDEEvent event = new BuildEvent();
        entries.add(new Entry(event));

        // when
        double actual = aggregator.aggregateValue(entries);

        // then
        assertEquals(0.0, actual, 0);
    }

}