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
package ch.nicosb.eventstreamanalyzer.data;

import cc.kave.commons.model.events.IDEEvent;
import cc.kave.commons.model.events.IIDEEvent;

import java.time.ZonedDateTime;
import java.util.HashMap;

public class Entry implements IIDEEvent {
    private IIDEEvent event;
    private HashMap<String, String> fields;

    public Entry() {
        this.fields = new HashMap<>();
    }

    public Entry(IIDEEvent event) {
        this();
        this.event = event;
    }

    public void put(String title, String value) {
        fields.put(title, value);
    }

    public void remove(String title) {
        fields.remove(title);
    }

    public HashMap<String, String> getFields() {
        return fields;
    }

    public ZonedDateTime getDateTime() {
        return event.getTriggeredAt();
    }

    public IIDEEvent getEvent() {
        return event;
    }

    @Override
    public String toString() {
        return "Entry{" +
                "eventType='" + event.getClass().getName() + '\'' +
                ", dateTime=" + event.getTriggeredAt() +
                ", fields=" + fields.toString() +
                '}';
    }

    @Override
    public ZonedDateTime getTriggeredAt() {
        return event.getTriggeredAt();
    }

    @Override
    public ZonedDateTime getTerminatedAt() {
        return event.getTerminatedAt();
    }
}
