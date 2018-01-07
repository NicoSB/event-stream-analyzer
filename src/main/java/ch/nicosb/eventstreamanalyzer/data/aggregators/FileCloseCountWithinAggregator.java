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

import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class FileCloseCountWithinAggregator implements Aggregator {

    final static String TITLE_BLUEPRINT = "FilesClosedInLast%ds";
    private Set<Integer> windows;
    private Map<Integer, IntervalEventWindow> intervalWindows;

    public FileCloseCountWithinAggregator(int... seconds) {
        windows = new TreeSet<>();
        intervalWindows = new HashMap<>();
        init(seconds);
    }

    private void init(int[] seconds) {
        for (int s : seconds)
            windows.add(s);

        windows.forEach(window -> intervalWindows.put(window, new IntervalEventWindow(window)));
    }

    @Override
    public Map<String, String> aggregateValue(IIDEEvent event) {
        if (EventUtils.isFileClosingEvent(event)) {
            windows.forEach(window -> intervalWindows.get(window).add(event));
        } else {
            ZonedDateTime end = EventUtils.getEnd(event);
            windows.forEach(window -> intervalWindows.get(window).setWindowEnd(end));
        }

        return collectResults();
    }

    private Map<String,String> collectResults() {
        Map<String, String> map = new HashMap<>();

        windows.forEach(window -> {
            String eventCount = String.valueOf(intervalWindows.get(window).size());
            String title = String.format(TITLE_BLUEPRINT, window);
            map.put(title, eventCount);
        });

        return map;
    }

    @Override
    public Set<String> getTitles() {
        return windows.
                stream()
                .map(secs -> String.format(TITLE_BLUEPRINT, secs))
                .collect(Collectors.toSet());
    }
}
