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

import cc.kave.commons.model.events.IIDEEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class TraverserImpl extends Traverser {
    private List<Entry> entries;

    public TraverserImpl(List<IIDEEvent> events) {
        super(events);
        entries = new ArrayList<>();
    }

    @Override
    public List<Entry> traverse() {
        events.forEach(this::applyAggregators);
        return entries;
    }

    private void applyAggregators(IIDEEvent event) {
        System.out.printf("Applying aggregators to event of type '%s'.\n", event.getClass().getName());

        Entry entry = new Entry(event);
        aggregators.forEach(ag -> entry.put(ag.getTitle(), ag.aggregateValue(events, event)));

        entries.add(entry);
    }
}
