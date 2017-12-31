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
import cc.kave.commons.model.events.testrunevents.TestCaseResult;
import cc.kave.commons.model.events.testrunevents.TestResult;
import cc.kave.commons.model.events.testrunevents.TestRunEvent;
import ch.nicosb.eventstreamanalyzer.utils.EventUtils;

import java.time.ZonedDateTime;
import java.util.List;

public class LastSuccessfulTestWithinAggregator extends NominalAggregator {
    public static final String TRUE = "t";
    public static final String FALSE = "f";
    int seconds;
    private long lastTestEventEpoch;

    public LastSuccessfulTestWithinAggregator(int seconds) {
        super("Within" + seconds + "sOfLastSuccessfulTest");
        this.seconds = seconds;
        this.possibleValues = new String[]{TRUE, FALSE};
        lastTestEventEpoch = Long.MIN_VALUE;
    }

    @Override
    public String aggregateValue(List<IIDEEvent> events, IIDEEvent event) {
        long eventEpoch = EventUtils.getEnd(event).toEpochSecond();

        if (isSuccessfulTest(event)) {
            lastTestEventEpoch = eventEpoch;
            return TRUE;
        }

        return eventEpoch - lastTestEventEpoch <= seconds ? TRUE : FALSE;
    }

    private boolean isSuccessfulTest(IIDEEvent event) {
        if (!(event instanceof TestRunEvent))
            return false;

        TestRunEvent testEvent = (TestRunEvent) event;
        for (TestCaseResult result : testEvent.Tests) {
            if (result.Result != TestResult.Success)
                return false;
        }

        return true;
    }
}
