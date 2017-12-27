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

import cc.kave.commons.model.events.IIDEEvent;
import cc.kave.commons.model.events.testrunevents.TestCaseResult;
import cc.kave.commons.model.events.testrunevents.TestResult;
import cc.kave.commons.model.events.testrunevents.TestRunEvent;
import ch.nicosb.eventstreamanalyzer.data.Entry;
import ch.nicosb.eventstreamanalyzer.testutils.TestEvent;
import org.junit.Before;
import org.junit.Test;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.junit.Assert.*;

public class TotalTestCompletionRatioAggregatorTest {

    TotalTestCompletionRatioAggregator aggregator;

    @Before
    public void setUp() {
        aggregator = new TotalTestCompletionRatioAggregator();
    }

    @Test
    public void whenHasNoTestEvents_returnsZero()  {
        // given
        IIDEEvent event = new TestEvent(ZonedDateTime.now());
        List<Entry> entries = new ArrayList<>();
        entries.add(new Entry(event));

        double expected = 0.0d;

        // when
        double actual = aggregator.aggregateValue(entries);

        // then
        assertEquals(expected, actual, 0);
    }

    @Test
    public void whenHasTestEvents_returnsRatio()  {
        // given
        TestCaseResult positiveResult = new TestCaseResult();
        positiveResult.Result = TestResult.Success;

        TestCaseResult negativeResult = new TestCaseResult();
        negativeResult.Result = TestResult.Failed;

        TestRunEvent event = new TestRunEvent();
        event.Tests = new HashSet<>();
        event.Tests.add(positiveResult);
        event.Tests.add(negativeResult);

        List<Entry> entries = new ArrayList<>();
        entries.add(new Entry(event));

        // when
        double actual = aggregator.aggregateValue(entries);

        // then
        assertEquals(0.5d, actual, 0);
    }

    @Test
    public void whenHasMultipleTestEvents_evaluatesLastEvent()  {
        // given
        TestCaseResult positiveResult = new TestCaseResult();
        positiveResult.Result = TestResult.Success;

        TestCaseResult negativeResult = new TestCaseResult();
        negativeResult.Result = TestResult.Failed;

        TestRunEvent negativeEvent = new TestRunEvent();
        negativeEvent.Tests = new HashSet<>();
        negativeEvent.Tests.add(negativeResult);
        negativeEvent.TriggeredAt = ZonedDateTime.now();

        TestRunEvent positiveEvent = new TestRunEvent();
        positiveEvent.Tests = new HashSet<>();
        positiveEvent.Tests.add(positiveResult);
        positiveEvent.TriggeredAt = ZonedDateTime.now().plusSeconds(1);

        List<Entry> entries = new ArrayList<>();
        entries.add(new Entry(negativeEvent));
        entries.add(new Entry(positiveEvent));

        // when
        double actual = aggregator.aggregateValue(entries);

        // then
        assertEquals(1.0d, actual, 0);
    }
}