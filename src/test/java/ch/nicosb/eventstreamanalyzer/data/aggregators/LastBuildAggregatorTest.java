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
import ch.nicosb.eventstreamanalyzer.stream.TriggeredAtComparator;
import ch.nicosb.eventstreamanalyzer.testutils.TestEvent;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class LastBuildAggregatorTest {

    private LastBuildAggregator aggregator;
    private ZonedDateTime now;
    private int timeout = 10;

    @Mock
    private BuildEvent buildEvent;

    @Before
    public void setUp() throws Exception {
        this.aggregator = new LastBuildAggregator(10);

        now = ZonedDateTime.now();
        when(buildEvent.getTriggeredAt()).thenReturn(now);
    }

    @Test
    public void whenEventIsBuildEvent_returnsZero() {
        // given
        List<IIDEEvent> events = new ArrayList<>();
        events.add(buildEvent);

        double expected = 0.0d;

        // when
        double actual = aggregator.aggregateValue(events, buildEvent);

        // then
        assertEquals(expected, actual, 0);
    }

    @Test
    public void whenNoBuildEventPreceded_returnsZero() {
        // given
        IIDEEvent buildEvent = new TestEvent(ZonedDateTime.now());
        List<IIDEEvent> events = new ArrayList<>();
        events.add(buildEvent);

        double expected = 0.0d;

        // when
        double actual = aggregator.aggregateValue(events, buildEvent);

        // then
        assertEquals(expected, actual, 0);
    }

    @Test
    public void whenEventsAreWithinTimeOut_doesAddDifference() {
        // given
        ZonedDateTime later = now.plusSeconds(timeout);

        IIDEEvent laterEvent = new TestEvent(later);

        List<IIDEEvent> events = new ArrayList<>();
        events.add(buildEvent);
        events.add(laterEvent);

        double expected = timeout;

        // when
        aggregator.aggregateValue(events, buildEvent);
        double actual = aggregator.aggregateValue(events, laterEvent);

        // then
        assertEquals(expected, actual, 0);
    }

    @Test
    public void whenEventsAreNotWithinTimeOut_doesNotAddDifference() {
        // given
        ZonedDateTime later = now.plusSeconds(timeout + 1);

        IIDEEvent laterEvent = new TestEvent(later);

        List<IIDEEvent> events = new ArrayList<>();
        events.add(buildEvent);
        events.add(laterEvent);

        double expected = 0.0d;

        // when
        aggregator.aggregateValue(events, buildEvent);
        double actual = aggregator.aggregateValue(events, laterEvent);

        // then
        assertEquals(expected, actual, 0);
    }
}