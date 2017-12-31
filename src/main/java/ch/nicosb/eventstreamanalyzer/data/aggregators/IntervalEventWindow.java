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

public class IntervalEventWindow {
    private TreeSet<IIDEEvent> events;
    private int intervalInSeconds;

    public IntervalEventWindow(int intervalInSeconds) {
        events = new TreeSet<>(Comparator.comparing(IIDEEvent::getTriggeredAt));
        this.intervalInSeconds = intervalInSeconds;
    }

    public void add(IIDEEvent event) {
        events.add(event);
        removeOldEvents();
    }

    private synchronized void removeOldEvents() {
        IIDEEvent last = events.last();
        ZonedDateTime minTime = EventUtils.getEnd(last).minusSeconds(intervalInSeconds);

        for (Iterator<IIDEEvent> iterator = events.iterator(); iterator.hasNext();) {
            IIDEEvent event = iterator.next();
            if (event.getTriggeredAt().isAfter(minTime))
                break;

            iterator.remove();
        }
    }

    public int size() {
        return events.size();
    }

    public IIDEEvent get(int index) {
        Iterator it = events.iterator();
        for (int i = 0; i < index && it.hasNext();i++) {
            it.next();
        }
        return (IIDEEvent) it.next();
    }
}
