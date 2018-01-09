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
import sun.misc.CEFormatException;

import javax.print.Doc;
import java.time.ZonedDateTime;
import java.util.*;

public class EventUtils {

    private static final Set<String> BUILD_EVENT_TITLES = new HashSet<>();

    static {
        BUILD_EVENT_TITLES.add("SolutionBuilderTres:1:BuildSolution");
        BUILD_EVENT_TITLES.add("SolBuilderDuo.Build");
        BUILD_EVENT_TITLES.add("VsAction:1:Build.BuildSolution");
        BUILD_EVENT_TITLES.add("5EFC7975-14BC-11CF-9B2B-00AA00573819}:882:Build.BuildSolution");
    }

    public static boolean isCommitEvent(IIDEEvent event) {
        if(!(event instanceof VersionControlEvent))
            return false;

        VersionControlEvent vcEvent = (VersionControlEvent) event;
        List<VersionControlAction> actions = vcEvent.Actions;

        for (VersionControlAction action : actions) {
            if (action.ActionType == VersionControlActionType.Commit) {
                return true;
            }
        }

        return false;
    }

    public static ZonedDateTime getEnd(IIDEEvent event) {
        try {
            return event.getTerminatedAt() != null ? event.getTerminatedAt() : event.getTriggeredAt();
        } catch (Exception e) {
            return event.getTriggeredAt();
        }
    }

    public static boolean isSuccessfulBuildEvent(IIDEEvent event) {
        if (!(event instanceof BuildEvent) || ((BuildEvent) event).Targets.isEmpty())
            return false;

        for(BuildTarget target : ((BuildEvent) event).Targets) {
            if (!target.Successful)
                return false;
        }

        return true;
    }

    public static boolean isFileClosingEvent(IIDEEvent event) {
        return isFileActionEvent(event, DocumentAction.Closing);
    }

    public static boolean isFileActionEvent(IIDEEvent event, DocumentAction action) {
        if (!(event instanceof DocumentEvent))
            return false;

        DocumentEvent documentEvent = (DocumentEvent) event;

        return documentEvent.Action == action;
    }

    public static ZonedDateTime getVersionControlActionDate(VersionControlEvent event, VersionControlActionType type) {
        Optional<ZonedDateTime> timeOptional = event.Actions.stream()
                .filter(action -> action.ActionType == type)
                .map(action -> action.ExecutedAt)
                .sorted(Comparator.reverseOrder())
                .findFirst();

        return timeOptional.orElseThrow(() -> new IllegalArgumentException("No datetime for type: '" + type + "' was found!"));
    }

    public static boolean isBuildEvent(IIDEEvent event) {
        if (!(event instanceof BuildEvent) && !(event instanceof CommandEvent))
            return false;

        if (event instanceof BuildEvent)
            return true;

        CommandEvent commandEvent = (CommandEvent) event;

        return BUILD_EVENT_TITLES.contains(commandEvent.CommandId);

    }
}
