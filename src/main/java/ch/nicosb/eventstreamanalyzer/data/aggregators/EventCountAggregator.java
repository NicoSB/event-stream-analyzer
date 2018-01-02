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

import java.util.*;

public class EventCountAggregator extends Aggregator {
    private HashMap<String, IntervalEventWindow> eventWindows;
    private Set<String> titles;

    public EventCountAggregator(int... windowSizes) {
        titles = new HashSet<>();
        eventWindows = new HashMap<>();
        init(windowSizes);
    }

    private void init(int[] windowSizes) {
        for(int size : windowSizes) {
            String title = "EventsInLast" + size + "s";
            titles.add(title);
            eventWindows.put(title, new IntervalEventWindow(size));
        }
    }

    @Override
    public Map<String, String> aggregateValue(IIDEEvent event) {
        Map<String, String> map = new HashMap<>();
        titles.stream()
                .sorted()
                .forEach(title -> {
                    eventWindows.get(title).add(event);
                    map.put(title, String.valueOf(eventWindows.get(title).size()));
                });

        return map;
    }

    @Override
    public Set<String> getTitles() {
        return titles;
    }
}
