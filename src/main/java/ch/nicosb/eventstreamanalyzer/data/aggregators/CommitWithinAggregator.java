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
import cc.kave.commons.model.events.versioncontrolevents.VersionControlActionType;
import cc.kave.commons.model.events.versioncontrolevents.VersionControlEvent;
import ch.nicosb.eventstreamanalyzer.utils.EventUtils;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CommitWithinAggregator extends NominalAggregator {
    static final String TRUE = "t";
    static final String FALSE = "f";
    final static String TITLE_BLUEPRINT = "Within%dsOfCommit";

    private ZonedDateTime lastCommitDate;
    private Set<String> titles;
    private String title;
    private int seconds;

    public CommitWithinAggregator(int seconds) {
        titles = new HashSet<>();
        title = String.format(TITLE_BLUEPRINT, seconds);
        titles.add(title);

        this.seconds =  seconds;
        possibleValues = new String[]{TRUE, FALSE};

        Instant minDate = Instant.ofEpochMilli(Long.MIN_VALUE);
        lastCommitDate = minDate.atZone(ZoneOffset.UTC);
    }

    @Override
    public Map<String, String> aggregateValue(IIDEEvent event) {
        String result;
        Map<String, String> map = new HashMap<>();

        if (EventUtils.isCommitEvent(event)) {
            VersionControlEvent vcEvent = (VersionControlEvent) event;
            lastCommitDate = EventUtils.getVersionControlActionDate(vcEvent, VersionControlActionType.Commit);
            result = TRUE;
        } else {
            long difference = Duration.between(lastCommitDate, event.getTriggeredAt()).getSeconds();
            result = difference > 0 && difference <= seconds ? TRUE : FALSE;
        }

        map.put(title, result);

        return map;
    }

    @Override
    public Set<String> getTitles() {
        return titles;
    }
}
