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

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

public class IntervalEventWindow {
    private List<IIDEEvent> events;
    private int intervalInSeconds;

    public IntervalEventWindow(int intervalInSeconds) {
        events = new ArrayList<>();
        this.intervalInSeconds = intervalInSeconds;
    }

    public void add(IIDEEvent event) {
        events.add(event);
        removeOldEvents();
    }

    private void removeOldEvents() {
        IIDEEvent last = events.get(events.size() - 1);
        ZonedDateTime minTime = last.getTriggeredAt().minusSeconds(intervalInSeconds);

        for (IIDEEvent event : events) {
            if (event.getTriggeredAt().compareTo(minTime) > 0)
                break;

            events.remove(event);
        }
    }

    public int size() {
        return events.size();
    }

    public IIDEEvent get(int index) {
        return events.get(index);
    }
}
