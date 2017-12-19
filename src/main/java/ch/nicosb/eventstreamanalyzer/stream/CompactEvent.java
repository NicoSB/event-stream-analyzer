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

public class CompactEvent {
    public ZonedDateTime dateTime;
    public String eventType;

    public CompactEvent(IIDEEvent event) {
        this.dateTime = event.getTerminatedAt();
        this.eventType = event.getClass().getName();
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + dateTime.hashCode();
        result = 31 * result + eventType.hashCode();

        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;

        if (!(obj instanceof CompactEvent))
            return false;

        CompactEvent event = (CompactEvent) obj;

        return event.eventType.equals(this.eventType)
                && event.dateTime.equals(this.dateTime);
    }

    @Override
    public String toString() {
        return "CompactEvent{" +
                "dateTime=" + dateTime.toString() +
                ", eventType='" + eventType + '\'' +
                '}';
    }
}
