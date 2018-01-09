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
import cc.kave.commons.model.events.testrunevents.TestCaseResult;
import cc.kave.commons.model.events.testrunevents.TestResult;
import cc.kave.commons.model.events.testrunevents.TestRunEvent;
import cc.kave.commons.model.events.visualstudio.BuildEvent;
import ch.nicosb.eventstreamanalyzer.utils.EventUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class LastBuildWithinAggregator extends NominalAggregator {
    static final String TRUE = "t";
    static final String FALSE = "f";
    static final String TITLE_BLUEPRINT = "Within%dsOfBuild";

    private Map<Integer, String> titles;
    private Set<Integer> windows;
    private long lastTestEventEpoch;

    public LastBuildWithinAggregator(int... seconds) {
        titles = new HashMap<>();
        windows = new TreeSet<>();
        init(seconds);

        this.possibleValues = new String[]{TRUE, FALSE};
        lastTestEventEpoch = 0;
    }

    private void init(int[] seconds) {
        for (int s : seconds) {
            windows.add(s);
            titles.put(s, String.format(TITLE_BLUEPRINT, s));
        }
    }

    private boolean isWithinXSeconds(IIDEEvent event, int seconds) {
        long eventEpoch = EventUtils.getEnd(event).toEpochSecond();

        if (EventUtils.isBuildEvent(event)) {
            lastTestEventEpoch = eventEpoch;
            return true;
        }

        return eventEpoch - lastTestEventEpoch <= seconds;
    }

    @Override
    public Map<String, String> aggregateValue(IIDEEvent event) {
        Map<String, String> map = new HashMap<>();
        windows.forEach(window -> map.put(titles.get(window), isWithinXSeconds(event, window) ? TRUE : FALSE));

        return map;
    }

    @Override
    public Set<String> getTitles() {
        return titles.keySet()
                .stream()
                .map(key -> titles.get(key))
                .collect(Collectors.toSet());
    }
}
