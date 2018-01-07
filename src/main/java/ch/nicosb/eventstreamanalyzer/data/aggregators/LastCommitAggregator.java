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
import ch.nicosb.eventstreamanalyzer.utils.EventUtils;

import java.util.*;

public class LastCommitAggregator implements Aggregator {
    static final String TITLE = "SecsSinceLastCommit";

    private Set<String> titles;
    private int timeout;
    private double activeTimeSinceLastCommit = 0;
    private long lastEventEnd = -1;

    public LastCommitAggregator(int timeout) {
        this.timeout = timeout;
        titles = new HashSet<>();
        titles.add(TITLE);
    }

    private void setActiveTimeToZero(IIDEEvent event) {
        lastEventEnd = EventUtils.getEnd(event).toEpochSecond();
        activeTimeSinceLastCommit = 0;
    }

    private void calculateActiveTime(IIDEEvent event) {
        long eventStart = event.getTriggeredAt().toEpochSecond();
        long eventEnd = EventUtils.getEnd(event).toEpochSecond();
        long differenceInSeconds = eventStart - lastEventEnd;

        if (differenceInSeconds <= timeout) {
            activeTimeSinceLastCommit += eventEnd - lastEventEnd;
        }

        lastEventEnd = eventEnd;
    }

    @Override
    public Map<String, String> aggregateValue(IIDEEvent event) {
        Map<String, String> map = new HashMap<>();
        if (EventUtils.isCommitEvent(event) || lastEventEnd == -1) {
            setActiveTimeToZero(event);
        } else {
            calculateActiveTime(event);
        }

        map.put(TITLE, String.valueOf(activeTimeSinceLastCommit));

        return map;
    }

    @Override
    public Set<String> getTitles() {
        return titles;
    }
}
