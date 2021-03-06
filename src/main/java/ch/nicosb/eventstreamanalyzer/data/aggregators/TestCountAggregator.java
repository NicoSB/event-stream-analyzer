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
import cc.kave.commons.model.events.testrunevents.TestRunEvent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class TestCountAggregator implements Aggregator {

    private Set<String> titles = new HashSet<>();
    private int testEventsCount = 0;
    final static String title = "registeredTestEvents";

    public TestCountAggregator() {
        titles.add(title);
    }


    @Override
    public Map<String, String> aggregateValue(IIDEEvent event) {
        Map<String, String> map = new HashMap<>();
        if (event instanceof TestRunEvent)
            ++testEventsCount;

        map.put(title, String.valueOf(testEventsCount));

        return map;
    }

    @Override
    public Set<String> getTitles() {
        return titles;
    }
}
