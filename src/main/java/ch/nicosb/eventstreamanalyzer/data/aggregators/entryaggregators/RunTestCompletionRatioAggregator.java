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
package ch.nicosb.eventstreamanalyzer.data.aggregators.entryaggregators;

import cc.kave.commons.model.events.testrunevents.TestCaseResult;
import cc.kave.commons.model.events.testrunevents.TestResult;
import cc.kave.commons.model.events.testrunevents.TestRunEvent;
import ch.nicosb.eventstreamanalyzer.data.Entry;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class RunTestCompletionRatioAggregator extends EntryAggregator {

    public RunTestCompletionRatioAggregator() {
        super("RunTestRatio");
    }

    @Override
    public double aggregateValue(List<Entry> events) {
        List<Entry> testEvents = events.stream()
                .filter(evt -> evt.getEvent() instanceof TestRunEvent)
                .collect(Collectors.toList());

        return getLastTestFulfillment(testEvents);
    }

    private double getLastTestFulfillment(List<Entry> testEvents) {
        Optional<Entry> lastEvent = testEvents
                .stream()
                .sorted(Comparator.comparing(Entry::getTriggeredAt).reversed())
                .findFirst();

        if (!lastEvent.isPresent() || !(lastEvent.get().getEvent() instanceof TestRunEvent))
            return 0.0d;

        TestRunEvent testEvent = (TestRunEvent) lastEvent.get().getEvent();
        return getTestCompletionRatio(testEvent.Tests);
    }

    private double getTestCompletionRatio(Set<TestCaseResult> testResults) {
        double successfulTestCount = 0;
        double runTestCount = 0;

        for(TestCaseResult result : testResults) {
            if(testWasRun(result)) {
                runTestCount += 1.0d;
                successfulTestCount += result.Result == TestResult.Success ? 1.0 : 0.0;
            }
        }

        return successfulTestCount / runTestCount;
    }

    private boolean testWasRun(TestCaseResult result) {
        return result.Result != TestResult.Ignored && result.Result != TestResult.Unknown;
    }
}
