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
package ch.nicosb.eventstreamanalyzer.parser;

import cc.kave.commons.model.events.IIDEEvent;

import java.util.ArrayDeque;
import java.util.Queue;

public class ListeningEventQueue implements EventParsedListener, EventQueue {
    private Queue<IIDEEvent> queue;
    private String title;

    public ListeningEventQueue(String title) {
        queue = new ArrayDeque<>();
        this.title = title;
    }

    @Override
    public void add(IIDEEvent event) {
        queue.add(event);
    }

    @Override
    public IIDEEvent poll() {
        return queue.poll();
    }

    @Override
    public void onEventParsed(IIDEEvent event) {
        add(event);
    }

    public String getTitle() {
        return title;
    }

    @Override
    public int size() {
        return queue.size();
    }
}
