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

import cc.kave.commons.model.events.versioncontrolevents.VersionControlAction;
import cc.kave.commons.model.events.versioncontrolevents.VersionControlActionType;
import cc.kave.commons.model.events.versioncontrolevents.VersionControlEvent;
import ch.nicosb.eventstreamanalyzer.data.Entry;

import java.util.List;
import java.util.Optional;

public class HasCommitEventAggregator extends EntryAggregator {

    public HasCommitEventAggregator() {
        super("HasCommitEvent");
    }

    @Override
    public double aggregateValue(List<Entry> events) {
        Optional<Entry> commitEvent = events.stream()
                .filter(this::isAddOrCommitEvent)
                .findFirst();

        return commitEvent.isPresent() ? 1.0 : 0.0;
    }

    private boolean isAddOrCommitEvent(Entry entry) {
        if(!(entry.getEvent() instanceof VersionControlEvent))
            return false;

        VersionControlEvent vcEvent = (VersionControlEvent) entry.getEvent();
        List<VersionControlAction> actions = vcEvent.Actions;

        for (VersionControlAction action : actions) {
            if (action.ActionType == VersionControlActionType.Commit) {
                return true;
            }
        }

        return false;
    }
}
