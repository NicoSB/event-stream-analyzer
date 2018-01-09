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

import cc.kave.commons.model.events.CommandEvent;
import cc.kave.commons.model.events.IIDEEvent;
import cc.kave.commons.model.events.versioncontrolevents.VersionControlAction;
import cc.kave.commons.model.events.versioncontrolevents.VersionControlActionType;
import cc.kave.commons.model.events.versioncontrolevents.VersionControlEvent;
import cc.kave.commons.model.events.visualstudio.BuildEvent;
import cc.kave.commons.model.events.visualstudio.BuildTarget;
import cc.kave.commons.model.events.visualstudio.DocumentAction;
import cc.kave.commons.model.events.visualstudio.DocumentEvent;
import ch.nicosb.eventstreamanalyzer.data.aggregators.CommitWithinAggregator;
import ch.nicosb.eventstreamanalyzer.testutils.TestEvent;
import org.junit.Test;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

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
        IIDEEvent event = createThrowingEvent(now);

        // when
        ZonedDateTime actual = EventUtils.getEnd(event);

        // then
        assertEquals(now, actual);
    }

    private IIDEEvent createThrowingEvent(ZonedDateTime now) {
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

    @Test
    public void whenGetTerminatedReturnsNull_returnsTriggeredAt() {
        // given
        ZonedDateTime now = ZonedDateTime.now();
        IIDEEvent event = createNullEvent(now);

        // when
        ZonedDateTime actual = EventUtils.getEnd(event);

        // then
        assertEquals(now, actual);
    }

    private IIDEEvent createNullEvent(ZonedDateTime now) {
        return new IIDEEvent() {
            @Override
            public ZonedDateTime getTriggeredAt() {
                return now;
            }

            @Override
            public ZonedDateTime getTerminatedAt() {
                return null;
            }
        };
    }

    @Test
    public void whenIsNotABuildEvent_returnsFalse() {
        // given
        IIDEEvent event = new TestEvent(ZonedDateTime.now());

        // when
        boolean actual = EventUtils.isSuccessfulBuildEvent(event);

        // then
        assertFalse(actual);
    }

    @Test
    public void whenNotAllTargetsWereSuccessful_returnsFalse() {
        // given
        BuildEvent event = new BuildEvent();
        event.Targets = new ArrayList<>();
        BuildTarget successful = new BuildTarget();
        successful.Successful = true;
        BuildTarget failed = new BuildTarget();
        failed.Successful = false;

        event.Targets.add(successful);
        event.Targets.add(failed);

        // when
        boolean actual = EventUtils.isSuccessfulBuildEvent(event);

        // then
        assertFalse(actual);
    }

    @Test
    public void whenAllTargetsWereSuccessful_returnsTrue() {
        // given
        BuildEvent event = new BuildEvent();
        event.Targets = new ArrayList<>();

        BuildTarget successful = new BuildTarget();
        successful.Successful = true;
        event.Targets.add(successful);

        // when
        boolean actual = EventUtils.isSuccessfulBuildEvent(event);

        // then
        assertTrue(actual);
    }

    @Test
    public void whenNoTargetsAreAvailable_returnsFalse() {
        // given
        BuildEvent event = new BuildEvent();
        event.Targets = new ArrayList<>();

        // when
        boolean actual = EventUtils.isSuccessfulBuildEvent(event);

        // then
        assertFalse(actual);
    }

    @Test
    public void whenIsFileCloseEvent_returnsTrue() {
        // given
        DocumentEvent event = new DocumentEvent();
        event.Action = DocumentAction.Closing;

        // when
        boolean actual = EventUtils.isFileClosingEvent(event);

        // then
        assertTrue(actual);
    }

    @Test
    public void whenIsNotAFileCloseEvent_returnsTrue() {
        // given
        DocumentEvent event = new DocumentEvent();
        event.Action = DocumentAction.Opened;

        // when
        boolean actual = EventUtils.isFileClosingEvent(event);

        // then
        assertFalse(actual);
    }

    @Test
    public void whenIsNotADocumentEvent_returnsFalse() {
        // given
        IIDEEvent event = new TestEvent(ZonedDateTime.now());

        // when
        boolean actual = EventUtils.isFileActionEvent(event, DocumentAction.Closing);

        // then
        assertFalse(actual);
    }

    @Test
    public void whenDocumentActionMatches_returnsTrue() {
        // given
        DocumentAction save = DocumentAction.Saved;

        DocumentEvent event = new DocumentEvent();
        event.Action = save;

        // when
        boolean actual = EventUtils.isFileActionEvent(event, save);

        // then
        assertTrue(actual);
    }

    @Test
    public void whenDocumentActionDoesNotMatch_returnsFalse() {
        // given
        DocumentAction save = DocumentAction.Saved;

        DocumentEvent event = new DocumentEvent();
        event.Action = DocumentAction.Closing;

        // when
        boolean actual = EventUtils.isFileActionEvent(event, save);

        // then
        assertFalse(actual);
    }

    @Test
    public void whenVcEventOfTypeExists_returnsExecutedAtTime() {
        // given
        VersionControlEvent event = new VersionControlEvent();
        event.Actions = new ArrayList<>();

        VersionControlAction action = new VersionControlAction();
        action.ExecutedAt = ZonedDateTime.now();
        action.ActionType = VersionControlActionType.Commit;

        event.Actions.add(action);

        ZonedDateTime expected = action.ExecutedAt;

        // when
        ZonedDateTime actual = EventUtils.getVersionControlActionDate(event, VersionControlActionType.Commit);

        // then
        assertEquals(expected, actual);
    }

    @Test
    public void whenMultipleEventsExist_returnsLastExecutedAtTime() {
        // given
        VersionControlEvent event = new VersionControlEvent();
        event.Actions = new ArrayList<>();

        VersionControlAction action = new VersionControlAction();
        action.ExecutedAt = ZonedDateTime.now();
        action.ActionType = VersionControlActionType.Commit;

        VersionControlAction laterAction = new VersionControlAction();
        laterAction.ExecutedAt = action.ExecutedAt.plusSeconds(1);
        laterAction.ActionType = VersionControlActionType.Commit;

        event.Actions.add(action);
        event.Actions.add(laterAction);

        ZonedDateTime expected = laterAction.ExecutedAt;

        // when
        ZonedDateTime actual = EventUtils.getVersionControlActionDate(event, VersionControlActionType.Commit);

        // then
        assertEquals(expected, actual);
    }

    @Test(expected = IllegalArgumentException.class)
    public void whenNoVcEventOfTypeExists_throws() {
        // given
        VersionControlEvent event = new VersionControlEvent();
        event.Actions = new ArrayList<>();

        VersionControlAction action = new VersionControlAction();
        action.ExecutedAt = ZonedDateTime.now();
        action.ActionType = VersionControlActionType.Rebase;

        event.Actions.add(action);

        ZonedDateTime expected = action.ExecutedAt;

        // when
        ZonedDateTime actual = EventUtils.getVersionControlActionDate(event, VersionControlActionType.Commit);

        // then throws
    }

    @Test
    public void whenEventIsNotBuildEvent_returnsFalse() {
        // given
        IIDEEvent event = new TestEvent(ZonedDateTime.now());

        // when
        boolean actual = EventUtils.isBuildEvent(event);

        // then
        assertFalse(actual);
    }

    @Test
    public void whenEventIsRegularBuildEvent_returnsTrue() {
        // given
        IIDEEvent event = new BuildEvent();

        // when
        boolean actual = EventUtils.isBuildEvent(event);

        // then
        assertTrue(actual);

    }

    @Test
    public void whenEventIsCommandRSSolutionBuildEvent_returnsTrue() {
        // given
        CommandEvent event = new CommandEvent();
        event.CommandId = "SolutionBuilderTres:1:BuildSolution";

        // when
        boolean actual = EventUtils.isBuildEvent(event);

        // then
        assertTrue(actual);

    }

    @Test
    public void whenEventIsCommandRSBuildEvent_returnsTrue() {
        // given
        CommandEvent event = new CommandEvent();
        event.CommandId = "SolBuilderDuo.Build";

        // when
        boolean actual = EventUtils.isBuildEvent(event);

        // then
        assertTrue(actual);

    }

    @Test
    public void whenEventIsCommandVSBuildEvent_returnsTrue() {
        // given
        CommandEvent event = new CommandEvent();
        event.CommandId = "VsAction:1:Build.BuildSolution";

        // when
        boolean actual = EventUtils.isBuildEvent(event);

        // then
        assertTrue(actual);

    }

    @Test
    public void whenEventIsCommandVSSolutionBuildEvent_returnsTrue() {
        // given
        CommandEvent event = new CommandEvent();
        event.CommandId = "5EFC7975-14BC-11CF-9B2B-00AA00573819}:882:Build.BuildSolution";

        // when
        boolean actual = EventUtils.isBuildEvent(event);

        // then
        assertTrue(actual);

    }
}