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
import cc.kave.commons.model.events.visualstudio.BuildEvent;
import ch.nicosb.eventstreamanalyzer.utils.EventUtils;

import java.util.*;

public class LastBuildAggregator implements Aggregator{
    static final String TITLE = "SecsSinceLastBuild";

    private Set<String> titles;
    private int timeout;
    private double activeTimeSinceLastBuild = 0;
    private long lastEventEnd = -1;

    public LastBuildAggregator(int timeout) {
        this.timeout = timeout;
        titles = new HashSet<>();
        titles.add(TITLE);
    }

    @Override
    public Map<String, String> aggregateValue(IIDEEvent event) {
        Map<String, String> map = new HashMap<>();

        if (event instanceof BuildEvent || lastEventEnd == -1) {
            setActiveTimeToZero(event);
        } else {
            calculateActiveTime(event);
        }

        map.put(TITLE, String.valueOf(activeTimeSinceLastBuild));

        return map;
    }

    private void calculateActiveTime(IIDEEvent event) {
        long eventStart = event.getTriggeredAt().toEpochSecond();
        long eventEnd = EventUtils.getEnd(event).toEpochSecond();

        long differenceInSeconds = eventStart - lastEventEnd;

        if (differenceInSeconds <= timeout) {
            activeTimeSinceLastBuild += eventEnd - lastEventEnd;
        }

        lastEventEnd = eventEnd;
    }

    private void setActiveTimeToZero(IIDEEvent event) {
        lastEventEnd = EventUtils.getEnd(event).toEpochSecond();
        activeTimeSinceLastBuild = 0;
    }

    @Override
    public Set<String> getTitles() {
        return titles;
    }
}
