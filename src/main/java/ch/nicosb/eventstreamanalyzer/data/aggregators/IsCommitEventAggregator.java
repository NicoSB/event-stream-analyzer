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

public class IsCommitEventAggregator extends NominalAggregator {

    static final String TRUE = "t";
    static final String FALSE = "f";
    private Set<String> titles;

    public IsCommitEventAggregator() {
        this.possibleValues = new String[]{TRUE, FALSE};
        titles = new HashSet<>();
        titles.add("IsCommitEvent");
    }

    @Override
    public Map<String, String> aggregateValue(IIDEEvent event) {
        Map<String, String> map = new HashMap<>();
        map.put((String)titles.toArray()[0], EventUtils.isCommitEvent(event) ? TRUE : FALSE);

        return map;
    }

    @Override
    public Set<String> getTitles() {
        return titles;
    }
}
