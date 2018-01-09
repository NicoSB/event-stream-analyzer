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

import cc.kave.commons.model.events.CommandEvent;
import cc.kave.commons.model.events.IIDEEvent;
import cc.kave.commons.model.events.visualstudio.BuildEvent;
import cc.kave.commons.model.events.visualstudio.BuildTarget;
import ch.nicosb.eventstreamanalyzer.testutils.TestEvent;
import org.junit.Before;
import org.junit.Test;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.*;

public class LastBuildWithinAggregatorTest {

    private LastBuildWithinAggregator aggregator;
    private final static int WINDOW_SIZE = 300;

    @Before
    public void setUp() {
        aggregator = new LastBuildWithinAggregator(WINDOW_SIZE);
    }

    @Test
    public void returnsCorrectTitles() {
        // given
        int windowSize2 = WINDOW_SIZE + 10;
        aggregator = new LastBuildWithinAggregator(WINDOW_SIZE, windowSize2);

        String title1 = String.format(LastBuildWithinAggregator.TITLE_BLUEPRINT, WINDOW_SIZE);
        String title2 = String.format(LastBuildWithinAggregator.TITLE_BLUEPRINT, windowSize2);

        // when
        Set<String> titles = aggregator.getTitles();

        // then
        assertEquals(2, titles.size());
        assertTrue(titles.contains(title1));
        assertTrue(titles.contains(title2));
    }

    @Test
    public void whenNoBuildPreceded_returnsFalse() {
        // given
        IIDEEvent event = new TestEvent(ZonedDateTime.now());

        String title = String.format(LastBuildWithinAggregator.TITLE_BLUEPRINT, WINDOW_SIZE);
        String expected = LastBuildWithinAggregator.FALSE;

        // when
        Map<String, String> result = aggregator.aggregateValue(event);

        // then
        assertEquals(1, result.size());
        assertEquals(expected, result.get(title));
    }

    @Test
    public void whenBuildEventIsGiven_returnsTrue() {
        // given
        BuildEvent event = createBuildEvent();
        event.TriggeredAt = ZonedDateTime.now();

        String title = String.format(LastBuildWithinAggregator.TITLE_BLUEPRINT, WINDOW_SIZE);
        String expected = LastBuildWithinAggregator.TRUE;

        // when
        Map<String, String> result = aggregator.aggregateValue(event);

        // then
        assertEquals(1, result.size());
        assertEquals(expected, result.get(title));
    }

    @Test
    public void whenBuildEventPrecededWithinTimeWindow_returnsTrue() {
        // given
        ZonedDateTime now = ZonedDateTime.now();
        BuildEvent buildEvent = createBuildEvent();

        buildEvent.TriggeredAt = now.minusSeconds(WINDOW_SIZE - 1);
        IIDEEvent event = new TestEvent(now);

        String title = String.format(LastBuildWithinAggregator.TITLE_BLUEPRINT, WINDOW_SIZE);
        String expected = LastBuildWithinAggregator.TRUE;

        // when
        aggregator.aggregateValue(buildEvent);
        Map<String, String> result = aggregator.aggregateValue(event);

        // then
        assertEquals(1, result.size());
        assertEquals(expected, result.get(title));
    }

    @Test
    public void whenBuildEventPrecededOutsideTimeWindow_returnsFalse() {
        // given
        ZonedDateTime now = ZonedDateTime.now();
        BuildEvent buildEvent = createBuildEvent();

        buildEvent.TriggeredAt = now.minusSeconds(WINDOW_SIZE + 1);
        IIDEEvent event = new TestEvent(now);

        String title = String.format(LastBuildWithinAggregator.TITLE_BLUEPRINT, WINDOW_SIZE);
        String expected = LastBuildWithinAggregator.FALSE;

        // when
        aggregator.aggregateValue(buildEvent);
        Map<String, String> result = aggregator.aggregateValue(event);

        // then
        assertEquals(1, result.size());
        assertEquals(expected, result.get(title));
    }

    @Test
    public void whenBuildCommandEventIsGiven_returnsTrue() {
        // given
        CommandEvent commandEvent = new CommandEvent();
        commandEvent.CommandId = "5EFC7975-14BC-11CF-9B2B-00AA00573819}:882:Build.BuildSolution";
        commandEvent.TriggeredAt = ZonedDateTime.now();

        String title = String.format(LastBuildWithinAggregator.TITLE_BLUEPRINT, WINDOW_SIZE);
        String expected = LastBuildWithinAggregator.TRUE;

        // when
        Map<String, String> result = aggregator.aggregateValue(commandEvent);

        // then
        assertEquals(1, result.size());
        assertEquals(expected, result.get(title));
    }

    @Test
    public void whenMultipleWindowsAreGiven_returnsCorrectValues() {
        // given
        int windowSize2 = WINDOW_SIZE * 2;
        aggregator = new LastBuildWithinAggregator(WINDOW_SIZE, windowSize2);

        ZonedDateTime now = ZonedDateTime.now();
        BuildEvent buildEvent = createBuildEvent();

        buildEvent.TriggeredAt = now.minusSeconds(WINDOW_SIZE + 1);
        IIDEEvent event = new TestEvent(now);

        String title1 = String.format(LastBuildWithinAggregator.TITLE_BLUEPRINT, WINDOW_SIZE);
        String title2 = String.format(LastBuildWithinAggregator.TITLE_BLUEPRINT, windowSize2);
        String expected1 = LastBuildWithinAggregator.FALSE;
        String expected2 = LastBuildWithinAggregator.TRUE;

        // when
        aggregator.aggregateValue(buildEvent);
        Map<String, String> result = aggregator.aggregateValue(event);

        // then
        assertEquals(2, result.size());
        assertEquals(expected1, result.get(title1));
        assertEquals(expected2, result.get(title2));
    }

    private BuildEvent createBuildEvent() {
        BuildEvent buildEvent = new BuildEvent();
        buildEvent.Targets = new ArrayList<>();

        BuildTarget target = new BuildTarget();
        target.Successful = true;
        buildEvent.Targets.add(target);

        return buildEvent;
    }
}