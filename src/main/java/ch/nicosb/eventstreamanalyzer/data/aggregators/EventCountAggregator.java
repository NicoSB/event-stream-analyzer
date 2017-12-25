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

import java.util.List;
import java.util.Set;

public class EventCountAggregator extends Aggregator {
    private IntervalEventWindow eventWindow;

    public EventCountAggregator(String title, int windowSizeInSeconds) {
        super(title);
        eventWindow = new IntervalEventWindow(windowSizeInSeconds);
    }

    @Override
    public double aggregateValue(List<IIDEEvent> events, IIDEEvent event) {
        eventWindow.add(event);
        return eventWindow.size();
    }
}
