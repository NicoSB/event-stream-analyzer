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
package ch.nicosb.eventstreamanalyzer.stream;

import cc.kave.commons.model.events.IIDEEvent;

import java.time.ZonedDateTime;
import java.util.*;

public class Session {
    private ArrayList<IIDEEvent> events;

    public Session() {
        events = new ArrayList<>();
    }

    public void add(IIDEEvent event) {
        events.add(event);
    }

    public void addAll(Collection collection) {
        events.addAll(collection);
    }

    public int size() {
        return events.size();
    }

    public boolean contains(IIDEEvent event) {
        return events.contains(event);
    }

    public ZonedDateTime getStart() {
        return first().getTriggeredAt();
    }

    public ZonedDateTime getEnd() {
        return last().getTriggeredAt();
    }

    public Iterator iterator() {
        return events.iterator();
    }

    public IIDEEvent first() {
        return events.get(0);
    }

    public IIDEEvent last() {
        return events.get(events.size() - 1);
    }

    public ArrayList<IIDEEvent> getEvents() {
        return events;
    }

    public boolean isEmpty() {
        return events.isEmpty();
    }
}
