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
import ch.nicosb.eventstreamanalyzer.utils.EventUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class LastTestWasSuccessfulAggregator extends NominalAggregator {

    protected static final String TRUE = "t";
    protected static final String FALSE = "f";
    protected static final String TITLE = "lastTestWasSuccessful";
    private Set<String> titles = new HashSet<>();

    private boolean lastTestWasSuccessful = false;

    public LastTestWasSuccessfulAggregator() {
        possibleValues = new String[]{TRUE, FALSE};
        titles.add(TITLE);
    }

    @Override
    public Map<String, String> aggregateValue(IIDEEvent event) {
        Map<String, String> map = new HashMap<>();

        if (EventUtils.isSuccessfulTestEvent(event))
            lastTestWasSuccessful = true;
        else if (event instanceof TestRunEvent && !EventUtils.isSuccessfulTestEvent(event))
            lastTestWasSuccessful = false;

        map.put(TITLE, lastTestWasSuccessful ? TRUE : FALSE);

        resetIfLabelled(event);

        return map;
    }

    private void resetIfLabelled(IIDEEvent event) {
        if (EventUtils.isCommitEvent(event))
            lastTestWasSuccessful = false;
    }

    @Override
    public Set<String> getTitles() {
        return titles;
    }
}
