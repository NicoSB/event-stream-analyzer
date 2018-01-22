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
import ch.nicosb.eventstreamanalyzer.testutils.TestEvent;
import org.junit.Before;
import org.junit.Test;

import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.*;

public class LastTestWasSuccessfulAggregatorTest {

    private LastTestWasSuccessfulAggregator aggregator;

    @Before
    public void setUp() {
        aggregator = new LastTestWasSuccessfulAggregator();
    }

    @Test
    public void returnsCorrectTitles() {
        // when
        Set<String> titles = aggregator.getTitles();

        // then
        assertEquals(1, titles.size());
        assertTrue(titles.contains(LastTestWasSuccessfulAggregator.TITLE));
    }

    @Test
    public void whenNoTestEventPreceded_returnsFalse() {
        // given
        IIDEEvent event = new TestEvent(ZonedDateTime.now());

        // when
        Map<String, String> results = aggregator.aggregateValue(event);

        // then
        assertEquals(1, results.size());
        assertEquals(LastTestWasSuccessfulAggregator.FALSE, results.get(LastTestWasSuccessfulAggregator.TITLE));
    }

    @Test
    public void whenFailingTestPreceded_returnsFalse() {
        // given
        TestRunEvent successfullTest = createSuccessfullTest();
        TestRunEvent failingTest = createFailingTest();

        // when
        aggregator.aggregateValue(successfullTest);
        Map<String, String> results = aggregator.aggregateValue(failingTest);

        // then
        assertEquals(1, results.size());
        assertEquals(LastTestWasSuccessfulAggregator.FALSE, results.get(LastTestWasSuccessfulAggregator.TITLE));
    }

    @Test
    public void whenIsSuccessfullEvent_returnsTrue() {
        // given
        TestRunEvent successfullTest = createSuccessfullTest();

        // when
        Map<String, String> results = aggregator.aggregateValue(successfullTest);

        // then
        assertEquals(1, results.size());
        assertEquals(LastTestWasSuccessfulAggregator.TRUE, results.get(LastTestWasSuccessfulAggregator.TITLE));
    }

    @Test
    public void whenSuccessfullEventPreceded_returnsTrue() {
        // given
        TestRunEvent successfullTest = createSuccessfullTest();
        IIDEEvent event = new TestEvent(ZonedDateTime.now());

        // when
        aggregator.aggregateValue(successfullTest);
        Map<String, String> results = aggregator.aggregateValue(event);

        // then
        assertEquals(1, results.size());
        assertEquals(LastTestWasSuccessfulAggregator.TRUE, results.get(LastTestWasSuccessfulAggregator.TITLE));
    }

    private TestRunEvent createSuccessfullTest() {
        TestRunEvent testEvent = new TestRunEvent();
        testEvent.Tests = new HashSet<>();

        TestCaseResult result = new TestCaseResult();
        result.Result = TestResult.Success;

        testEvent.Tests.add(result);

        return testEvent;
    }

    private TestRunEvent createFailingTest() {
        TestRunEvent testEvent = new TestRunEvent();
        testEvent.Tests = new HashSet<>();

        TestCaseResult result = new TestCaseResult();
        result.Result = TestResult.Failed;

        testEvent.Tests.add(result);

        return testEvent;
    }
}