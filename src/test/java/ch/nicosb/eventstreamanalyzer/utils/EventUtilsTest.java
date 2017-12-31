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
package ch.nicosb.eventstreamanalyzer.utils;

import cc.kave.commons.model.events.IIDEEvent;
import cc.kave.commons.model.events.versioncontrolevents.VersionControlAction;
import cc.kave.commons.model.events.versioncontrolevents.VersionControlActionType;
import cc.kave.commons.model.events.versioncontrolevents.VersionControlEvent;
import ch.nicosb.eventstreamanalyzer.testutils.TestEvent;
import org.junit.Test;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

public class EventUtilsTest {

    @Test
    public void whenCommitEventIsGiven_returnsTrue() {
        // given
        VersionControlAction action = new VersionControlAction();
        action.ActionType = VersionControlActionType.Commit;

        List<VersionControlAction> actions = new ArrayList<>();
        actions.add(action);

        VersionControlEvent commitEvent = new VersionControlEvent();
        commitEvent.Actions = actions;

        // when
        boolean actual = EventUtils.isCommitEvent(commitEvent);

        // then
        assertTrue(actual);
    }

    @Test
    public void whenNoCommitEventIsGiven_returnsFalse() {
        // given
        VersionControlAction action = new VersionControlAction();
        action.ActionType = VersionControlActionType.Unknown;
        List<VersionControlAction> actions = new ArrayList<>();

        VersionControlEvent commitEvent = new VersionControlEvent();
        commitEvent.Actions = actions;

        // when
        boolean actual = EventUtils.isCommitEvent(commitEvent);

        // then
        assertFalse(actual);
    }

    @Test
    public void whenNoVersionControlEventIsGiven_returnsFalse() {
        // given
        IIDEEvent event = new TestEvent(ZonedDateTime.now());

        // when
        boolean actual = EventUtils.isCommitEvent(event);

        // then
        assertFalse(actual);
    }


    @Test
    public void whenGetTerminatedAtSucceeds_returnsTerminatedAt() {
        // given
        ZonedDateTime now = ZonedDateTime.now();
        IIDEEvent event = new TestEvent(now);

        // when
        ZonedDateTime actual = EventUtils.getEnd(event);

        // then
        assertEquals(now, actual);
    }

    @Test
    public void whenGetTerminatedAtThrows_returnsTriggeredAt() {
        // given
        ZonedDateTime now = ZonedDateTime.now();
        IIDEEvent event = createFailingEvent(now);

        // when
        ZonedDateTime actual = EventUtils.getEnd(event);

        // then
        assertEquals(now, actual);
    }

    private IIDEEvent createFailingEvent(ZonedDateTime now) {
        return new IIDEEvent() {
                @Override
                public ZonedDateTime getTriggeredAt() {
                    return now;
                }

                @Override
                public ZonedDateTime getTerminatedAt() {
                    return now.plus(null);
                }
            };
    }
}