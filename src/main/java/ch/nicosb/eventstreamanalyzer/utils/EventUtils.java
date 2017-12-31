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

import java.time.ZonedDateTime;
import java.util.List;

public class EventUtils {

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
}
