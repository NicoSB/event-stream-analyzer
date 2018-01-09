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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ActiveTimeAggregator implements Aggregator {
    private Set<String> titles;
    static final String TITLE_BLUEPRINT = "ActiveTimeInLast%ds";
    private IntervalActiveTimeWindow window;
    private int seconds;
    private String title;

    public ActiveTimeAggregator(int seconds, int timeout) {
        this.seconds = seconds;

        title = String.format(TITLE_BLUEPRINT, seconds);
        titles = new HashSet<>();
        titles.add(title);

        window = new IntervalActiveTimeWindow(seconds, timeout);
    }

    @Override
    public Map<String, String> aggregateValue(IIDEEvent event) {
        Map<String, String> map = new HashMap<>();

        Interval interval = getEventInterval(event);
        window.addInterval(interval);

        String activeTimeRatio = calculateActiveTimeRatio();
        map.put(title, activeTimeRatio);

        return map;
    }

    private Interval getEventInterval(IIDEEvent event) {
        return new Interval(event.getTriggeredAt(), EventUtils.getEnd(event));
    }

    private String calculateActiveTimeRatio() {
        long totalTimeInMillis = seconds * 1000;

        return String.valueOf((float) window.getActiveTimeInMillis() / totalTimeInMillis);
    }

    @Override
    public Set<String> getTitles() {
        return titles;
    }
}
