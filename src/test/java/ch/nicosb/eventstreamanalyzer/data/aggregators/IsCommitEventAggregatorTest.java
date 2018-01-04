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

import cc.kave.commons.model.events.versioncontrolevents.VersionControlAction;
import cc.kave.commons.model.events.versioncontrolevents.VersionControlActionType;
import cc.kave.commons.model.events.versioncontrolevents.VersionControlEvent;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.*;

public class IsCommitEventAggregatorTest {

    public static final String EXPECTED_TITLE = "IsCommitEvent";
    private IsCommitEventAggregator aggregator;

    @Before
    public void setUp() {
        aggregator = new IsCommitEventAggregator();
    }

    @Test
    public void returnsCorrectTitles() {
        // given
        String expected = "IsCommitEvent";

        // when
        Set<String> actual = aggregator.getTitles();

        // then
        assertEquals(1, actual.size());
        assertTrue(actual.contains(expected));
    }
    @Test
    public void whenIsCommitEvent_returnsT() {
        // given
        VersionControlAction action = new VersionControlAction();
        action.ActionType = VersionControlActionType.Commit;

        VersionControlEvent event = new VersionControlEvent();
        event.Actions = new ArrayList<>();
        event.Actions.add(action);

        String expected = IsCommitEventAggregator.TRUE;

        // when
        Map<String, String> result = aggregator.aggregateValue(event);

        // then
        assertEquals(expected, result.get(EXPECTED_TITLE));
    }

    @Test
    public void whenIsNotCommitEvent_returnsF() {
        // given
        VersionControlAction action = new VersionControlAction();
        action.ActionType = VersionControlActionType.Rebase;

        VersionControlEvent event = new VersionControlEvent();
        event.Actions = new ArrayList<>();
        event.Actions.add(action);

        String expected = IsCommitEventAggregator.FALSE;

        // when
        Map<String, String> result = aggregator.aggregateValue(event);

        // then
        assertEquals(expected, result.get(EXPECTED_TITLE));
    }

    @Test
    public void getPossibleValue_returnsTF() {
        // given
        String[] expected = {IsCommitEventAggregator.TRUE, IsCommitEventAggregator.FALSE};

        // when
        //String[] actual = aggregator.getPossibleValues();

        // then
        //assertArrayEquals(expected, actual);
    }
}