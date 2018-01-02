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
import ch.nicosb.eventstreamanalyzer.testutils.TestEvent;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class LastBuildAggregatorTest {

    private LastBuildAggregator aggregator;
    private ZonedDateTime buildTime;
    private int timeout = 10;

    @Mock
    private BuildEvent buildEvent;

    @Before
    public void setUp() throws Exception {
        this.aggregator = new LastBuildAggregator(10);

        buildTime = ZonedDateTime.now();
        when(buildEvent.getTriggeredAt()).thenReturn(buildTime);
    }

    @Test
    public void whenEventIsBuildEvent_returnsZero() {
        // given
        List<IIDEEvent> events = new ArrayList<>();
        events.add(buildEvent);

        String expected = "0.0";

        // when
        String actual = aggregator.aggregateValue(events, buildEvent);

        // then
        assertEquals(expected, actual);
    }

    @Test
    public void whenNoBuildEventPreceded_returnsZero() {
        // given
        IIDEEvent buildEvent = new TestEvent(ZonedDateTime.now());
        List<IIDEEvent> events = new ArrayList<>();
        events.add(buildEvent);

        String expected = "0.0";

        // when
        String actual = aggregator.aggregateValue(events, buildEvent);

        // then
        assertEquals(expected, actual);
    }

    @Test
    public void whenEventsAreWithinTimeOut_doesAddDifference() {
        // given
        ZonedDateTime later = buildTime.plusSeconds(timeout);

        IIDEEvent laterEvent = new TestEvent(later);

        List<IIDEEvent> events = new ArrayList<>();
        events.add(buildEvent);
        events.add(laterEvent);

        String expected = "10.0";

        // when
        aggregator.aggregateValue(events, buildEvent);
        String actual = aggregator.aggregateValue(events, laterEvent);

        // then
        assertEquals(expected, actual);
    }

    @Test
    public void whenEventsAreNotWithinTimeOut_doesNotAddDifference() {
        // given
        ZonedDateTime later = buildTime.plusSeconds(timeout + 1);

        IIDEEvent laterEvent = new TestEvent(later);

        List<IIDEEvent> events = new ArrayList<>();
        events.add(buildEvent);
        events.add(laterEvent);

        String expected = "0.0";

        // when
        aggregator.aggregateValue(events, buildEvent);
        String actual = aggregator.aggregateValue(events, laterEvent);

        // then
        assertEquals(expected, actual);
    }
}