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
import cc.kave.commons.model.events.versioncontrolevents.VersionControlAction;
import cc.kave.commons.model.events.versioncontrolevents.VersionControlActionType;
import cc.kave.commons.model.events.versioncontrolevents.VersionControlEvent;
import ch.nicosb.eventstreamanalyzer.testutils.TestEvent;
import org.junit.Before;
import org.junit.Test;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.*;

public class LastCommitAggregatorTest {

    private int timeout = 10;
    private LastCommitAggregator aggregator;

    @Before
    public void setUp() {
        aggregator = new LastCommitAggregator(timeout);
    }

    @Test
    public void returnsCorrectTitles() {
        // given
        String expected = LastCommitAggregator.TITLE;

        // when
        Set<String> actual = aggregator.getTitles();

        // then
        assertEquals(1, actual.size());
        assertTrue(actual.contains(expected));
    }

    @Test
    public void whenFirstEventIsGiven_returnsZero() {
        // given
        IIDEEvent event = new TestEvent(ZonedDateTime.now());

        String expected = "0.0";

        // when
        Map<String, String> result = aggregator.aggregateValue(event);

        // then
        assertEquals(expected, result.get(LastCommitAggregator.TITLE));
    }

    @Test
    public void whenCommitEventIsGiven_returnsZeroDotZero() {
        // given
        VersionControlAction action = new VersionControlAction();
        action.ActionType = VersionControlActionType.Commit;

        List<VersionControlAction> actions = new ArrayList<>();
        actions.add(action);

        VersionControlEvent commitEvent = new VersionControlEvent();
        commitEvent.Actions = actions;
        commitEvent.TriggeredAt = ZonedDateTime.now();

        String expected = "0.0";

        // when
        Map<String, String> result = aggregator.aggregateValue(commitEvent);

        // then
        assertEquals(1, result.size());
        assertEquals(expected, result.get(LastCommitAggregator.TITLE));
    }

    @Test
    public void whenEventAfterCommitEventIsGiven_returnsDifference() {
        // given
        int difference = 10;
        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime later = now.plusSeconds(difference);

        VersionControlAction action = new VersionControlAction();
        action.ActionType = VersionControlActionType.Commit;

        List<VersionControlAction> actions = new ArrayList<>();
        actions.add(action);

        VersionControlEvent commitEvent = new VersionControlEvent();
        commitEvent.Actions = actions;
        commitEvent.TriggeredAt = now;

        IIDEEvent laterEvent = new TestEvent(later);

        String expected = "10.0";

        // when
        aggregator.aggregateValue(commitEvent);
        Map<String, String> result = aggregator.aggregateValue(laterEvent);

        // then
        assertEquals("10.0", result.get(LastCommitAggregator.TITLE));
    }



    @Test
    public void whenEventsAreWithinTimeOut_doesAddDifference() {
        // given
        ZonedDateTime now = ZonedDateTime.now();
        IIDEEvent event = new TestEvent(now);
        ZonedDateTime later = now.plusSeconds(timeout);

        IIDEEvent laterEvent = new TestEvent(later);

        String expected = "10.0";

        // when
        aggregator.aggregateValue(event);
        Map<String, String> result = aggregator.aggregateValue(laterEvent);

        // then
        assertEquals(expected, result.get(LastCommitAggregator.TITLE));
    }

    @Test
    public void whenEventsAreNotWithinTimeOut_doesNotAddDifference() {
        // given
        ZonedDateTime now = ZonedDateTime.now();
        IIDEEvent event = new TestEvent(now);
        ZonedDateTime later = now.plusSeconds(timeout + 1);

        IIDEEvent laterEvent = new TestEvent(later);

        String expected = "0.0";

        // when
        aggregator.aggregateValue(event);
        Map<String, String> result = aggregator.aggregateValue(laterEvent);

        // then
        assertEquals(expected, result.get(LastCommitAggregator.TITLE));
    }
}