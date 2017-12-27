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

import static org.junit.Assert.*;

public class LastCommitAggregatorTest {

    LastCommitAggregator aggregator;

    @Before
    public void setUp() {
        aggregator = new LastCommitAggregator();
    }

    @Test
    public void whenFirstEventIsGiven_returnsZero() {
        // given
        IIDEEvent event = new TestEvent(ZonedDateTime.now());
        List<IIDEEvent> events = new ArrayList<>();
        events.add(event);

        double expected = 0.0d;

        // when
        double actual = aggregator.aggregateValue(events, event);

        // then
        assertEquals(expected, actual, 0);
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

        List<IIDEEvent> events = new ArrayList<>();
        events.add(commitEvent);

        double expected = 0.0d;

        // when
        double actual = aggregator.aggregateValue(events, commitEvent);

        // then
        assertEquals(expected, actual, 0);
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

        List<IIDEEvent> events = new ArrayList<>();
        events.add(commitEvent);
        events.add(laterEvent);

        // when
        aggregator.aggregateValue(events, commitEvent);
        double actual = aggregator.aggregateValue(events, laterEvent);

        // then
        assertEquals(difference, actual, 0);
    }

}