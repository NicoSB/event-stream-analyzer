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

import cc.kave.commons.model.events.testrunevents.TestCaseResult;
import cc.kave.commons.model.events.testrunevents.TestResult;
import cc.kave.commons.model.events.testrunevents.TestRunEvent;
import org.junit.Before;
import org.junit.Test;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

public class LastSuccessfulTestWithinAggregatorTest {

    public static final int SECONDS = 15;
    private LastSuccessfulTestWithinAggregator aggregator;

    @Before
    public void setUp() {
        aggregator = new LastSuccessfulTestWithinAggregator(SECONDS);
    }

    @Test
    public void returnsCorrectTitles() {
        // given
        int seconds2 = SECONDS + 10;
        aggregator = new LastSuccessfulTestWithinAggregator(SECONDS, seconds2);

        String title1 = String.format("Within%dsOfSuccessfulTest", SECONDS);
        String title2 = String.format("Within%dsOfSuccessfulTest", seconds2);

        // when
        Set<String> actual = aggregator.getTitles();

        // then
        assertEquals(2, actual.size());
        assertTrue(actual.contains(title1));
        assertTrue(actual.contains(title2));
    }

    @Test
    public void whenEventIsSuccessfulTestEvent_returnsT() {
        // given
        TestRunEvent event = createSuccessfulEvent(ZonedDateTime.now());
        String title = String.format("Within%dsOfSuccessfulTest", SECONDS);
        String expected = LastSuccessfulTestWithinAggregator.TRUE;

        // when
        Map<String, String> result = aggregator.aggregateValue(event);

        // then
        assertEquals(expected, result.get(title));
    }

    @Test
    public void whenEventIsNotWithinXSecondsOfSuccessfulTest_returnsF() {
        // given
        String title = String.format("Within%dsOfSuccessfulTest", SECONDS);
        TestRunEvent event = createSuccessfulEvent(ZonedDateTime.now().minusDays(1));
        TestRunEvent event2 = createUnSuccessfulEvent(ZonedDateTime.now());

        String expected = LastSuccessfulTestWithinAggregator.FALSE;

        // when
        aggregator.aggregateValue(event);
        Map<String, String> result = aggregator.aggregateValue(event2);

        // then
        assertEquals(expected, result.get(title));
    }

    @Test
    public void whenEventIsWithinXSecondsOfSuccessfulTest_returnsT() {
        // given
        String title = String.format("Within%dsOfSuccessfulTest", SECONDS);
        TestRunEvent event = createSuccessfulEvent(ZonedDateTime.now().minusSeconds(1));
        TestRunEvent event2 = createUnSuccessfulEvent(ZonedDateTime.now());

        String expected = "t";

        // when
        aggregator.aggregateValue(event);
        Map<String, String> result = aggregator.aggregateValue(event2);

        // then
        assertEquals(expected, result.get(title));
    }

    private TestRunEvent createSuccessfulEvent(ZonedDateTime dateTime) {
        Set<TestCaseResult> tests = new HashSet<>();
        TestCaseResult result = new TestCaseResult();
        result.Result = TestResult.Success;
        tests.add(result);

        TestRunEvent event = new TestRunEvent();
        event.Tests = tests;
        event.TriggeredAt = dateTime;
        event.Duration = Duration.ZERO;
        return event;
    }

    private TestRunEvent createUnSuccessfulEvent(ZonedDateTime dateTime) {
        Set<TestCaseResult> tests = new HashSet<>();
        TestCaseResult result = new TestCaseResult();
        result.Result = TestResult.Failed;
        tests.add(result);

        TestRunEvent event = new TestRunEvent();
        event.Tests = tests;
        event.TriggeredAt = dateTime;
        event.Duration = Duration.ZERO;
        return event;
    }
}