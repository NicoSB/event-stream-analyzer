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
package ch.nicosb.eventstreamanalyzer.data.aggregators.entryaggregators;

import cc.kave.commons.model.events.IIDEEvent;
import cc.kave.commons.model.events.versioncontrolevents.VersionControlAction;
import cc.kave.commons.model.events.versioncontrolevents.VersionControlActionType;
import cc.kave.commons.model.events.versioncontrolevents.VersionControlEvent;
import ch.nicosb.eventstreamanalyzer.data.Entry;
import ch.nicosb.eventstreamanalyzer.testutils.TestEvent;
import org.junit.Before;
import org.junit.Test;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class HasCommitEventTest {

    HasCommitEventAggregator aggregator;

    @Before
    public void setUp() {
        aggregator = new HasCommitEventAggregator();
    }

    @Test
    public void whenNoVersionControlEventIsGiven_returnsZeroDotZero() {
        // given
        IIDEEvent event = new TestEvent(ZonedDateTime.now());
        List<Entry> entries = new ArrayList<>();
        entries.add(new Entry(event));

        // when
        double actual = aggregator.aggregateValue(entries);

        // then
        assertEquals(0.0, actual, 0);
    }

    @Test
    public void whenNoCommitEventIsGiven_returnsZeroDotZero() {
        // given
        VersionControlEvent event = new VersionControlEvent();

        VersionControlAction action = new VersionControlAction();
        action.ActionType = VersionControlActionType.Unknown;
        event.Actions = new ArrayList<>();
        event.Actions.add(action);

        List<Entry> entries = new ArrayList<>();
        entries.add(new Entry(event));

        // when
        double actual = aggregator.aggregateValue(entries);

        // then
        assertEquals(0.0, actual, 0);
    }

    @Test
    public void whenCommitEventIsGiven_returnsOneDotZero() {
        // given
        VersionControlEvent event = new VersionControlEvent();

        VersionControlAction action = new VersionControlAction();
        action.ActionType = VersionControlActionType.Commit;
        event.Actions = new ArrayList<>();
        event.Actions.add(action);

        List<Entry> entries = new ArrayList<>();
        entries.add(new Entry(event));

        // when
        double actual = aggregator.aggregateValue(entries);

        // then
        assertEquals(1.0, actual, 0);
    }
}