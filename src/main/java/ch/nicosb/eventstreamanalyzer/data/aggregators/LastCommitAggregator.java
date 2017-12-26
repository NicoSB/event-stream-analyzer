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

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.List;

public class LastCommitAggregator extends Aggregator {
    private ZonedDateTime lastCommitTime;

    public LastCommitAggregator() {
        super("SecsSinceLastCommit");
        lastCommitTime = null;
    }

    @Override
    public double aggregateValue(List<IIDEEvent> events, IIDEEvent event) {
        if (EventUtils.isCommitEvent(event)) {
            this.lastCommitTime = event.getTriggeredAt();
            return 0.0d;
        }

        if (lastCommitTime == null)
            return -1.0d;

        return calculateSecondsSinceLastCommit(event);
    }

    private double calculateSecondsSinceLastCommit(IIDEEvent event) {
        return Duration.between(lastCommitTime, event.getTriggeredAt()).getSeconds();
    }
}