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
import cc.kave.commons.model.events.visualstudio.BuildEvent;
import ch.nicosb.eventstreamanalyzer.utils.EventUtils;

import java.util.List;

public class LastBuildAggregator extends Aggregator{
    public static final String TITLE = "SecsSinceLastBuild";
    private int timeout;

    private double activeTimeSinceLastBuild = 0;
    private long lastEventEnd = -1;

    public LastBuildAggregator(int timeout) {
        super(TITLE);
        this.timeout = timeout;
    }

    @Override
    public String aggregateValue(List<IIDEEvent> events, IIDEEvent event) {
        if (event instanceof BuildEvent || lastEventEnd == -1) {
            lastEventEnd = EventUtils.getEnd(event).toEpochSecond();
            activeTimeSinceLastBuild = 0;
            return String.valueOf(activeTimeSinceLastBuild);
        }

        long eventStart = event.getTriggeredAt().toEpochSecond();
        long eventEnd = EventUtils.getEnd(event).toEpochSecond();

        long differenceInSeconds = eventStart - lastEventEnd;

        if (differenceInSeconds <= timeout) {
            activeTimeSinceLastBuild += eventEnd - lastEventEnd;
        }

        lastEventEnd = eventEnd;

        return String.valueOf(activeTimeSinceLastBuild);
    }
}
