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
import ch.nicosb.eventstreamanalyzer.data.Entry;

import java.time.ZonedDateTime;
import java.util.*;

public class Sessionizer {

    private int timeout;
    private List<Entry> events;

    public Sessionizer(List<Entry> events, int timeout) {
        this.timeout = timeout;
        this.events = events;
    }

    public List<Session> extractSessions() {
        List<Session> sessions = new ArrayList<>();
        Session currentSession = new Session();

        for (IIDEEvent event : events) {
            if (shouldBeAddedTo(event, currentSession)) {
                currentSession.add(event);
            } else {
                sessions.add(currentSession);
                currentSession = new Session();
                currentSession.add(event);
            }
        }

        sessions.add(currentSession);
        return sessions;
    }

    private boolean shouldBeAddedTo(IIDEEvent event, Session currentSession) {
        if (currentSession.isEmpty())
            return true;

        ZonedDateTime max = currentSession.getEnd().plusSeconds(timeout);
        return !max.isBefore(event.getTriggeredAt());
    }
}
