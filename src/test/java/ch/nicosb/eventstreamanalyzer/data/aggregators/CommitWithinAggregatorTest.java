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
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.*;

public class CommitWithinAggregatorTest {

    private CommitWithinAggregator aggregator;
    private final int SECONDS = 15;

    @Before
    public void setUp() {
        aggregator = new CommitWithinAggregator(SECONDS);
    }

    @Test
    public void returnsCorrectTitles() {
        // given
        String expected = String.format(CommitWithinAggregator.TITLE_BLUEPRINT, SECONDS);

        // when
        Set<String> actual = aggregator.getTitles();

        // then
        assertEquals(1, actual.size());
        assertTrue(actual.contains(expected));
    }

    @Test
    public void whenNoCommitEventPreceded_returnsFalse() {
        // given
        IIDEEvent event = new TestEvent(ZonedDateTime.now());
        String expected = CommitWithinAggregator.FALSE;
        String title = String.format(CommitWithinAggregator.TITLE_BLUEPRINT, SECONDS);

        // when
        Map<String, String> result = aggregator.aggregateValue(event);

        // then
        assertEquals(1, result.size());
        assertEquals(expected, result.get(title));
    }

    @Test
    public void whenIsCommitEvent_returnsTrue() {
        // given
        IIDEEvent event = createCommitEvent(ZonedDateTime.now());
        String expected = CommitWithinAggregator.TRUE;
        String title = String.format(CommitWithinAggregator.TITLE_BLUEPRINT, SECONDS);

        // when
        Map<String, String> result = aggregator.aggregateValue(event);

        // then
        assertEquals(1, result.size());
        assertEquals(expected, result.get(title));
    }

    @Test
    public void whenEventIsWithinSecondsOfCommit_returnsTrue() {
        // given
        ZonedDateTime now = ZonedDateTime.now();

        IIDEEvent commitEvent = createCommitEvent(now);
        IIDEEvent testEvent = new TestEvent(now.plusSeconds(SECONDS - 1));

        String expected = CommitWithinAggregator.TRUE;
        String title = String.format(CommitWithinAggregator.TITLE_BLUEPRINT, SECONDS);

        // when
        aggregator.aggregateValue(commitEvent);
        Map<String, String> result = aggregator.aggregateValue(testEvent);

        // then
        assertEquals(1, result.size());
        assertEquals(expected, result.get(title));
    }

    @Test
    public void whenEventIsNotWithinSecondsOfCommit_returnsFalse() {
        // given
        ZonedDateTime now = ZonedDateTime.now();

        IIDEEvent commitEvent = createCommitEvent(now);
        IIDEEvent testEvent = new TestEvent(now.plusSeconds(SECONDS + 1));

        String expected = CommitWithinAggregator.FALSE;
        String title = String.format(CommitWithinAggregator.TITLE_BLUEPRINT, SECONDS);

        // when
        aggregator.aggregateValue(commitEvent);
        Map<String, String> result = aggregator.aggregateValue(testEvent);

        // then
        assertEquals(1, result.size());
        assertEquals(expected, result.get(title));
    }

    private IIDEEvent createCommitEvent(ZonedDateTime dateTime) {
        VersionControlEvent event = new VersionControlEvent();
        event.Actions = new ArrayList<>();

        VersionControlAction action = new VersionControlAction();
        action.ExecutedAt = dateTime;
        action.ActionType = VersionControlActionType.Commit;

        event.Actions.add(action);

        return event;
    }

}