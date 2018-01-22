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
import cc.kave.commons.model.events.visualstudio.BuildTarget;
import ch.nicosb.eventstreamanalyzer.testutils.TestEvent;
import org.junit.Before;
import org.junit.Test;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class LastBuildWasSuccessfulAggregatorTest {

    private LastBuildWasSuccessfulAggregator aggregator;

    @Before
    public void setUp() {
        aggregator = new LastBuildWasSuccessfulAggregator();
    }

    @Test
    public void returnsCorrectTitles() {
        // when
        Set<String> titles = aggregator.getTitles();

        // then
        assertEquals(1, titles.size());
        assertTrue(titles.contains(LastBuildWasSuccessfulAggregator.TITLE));
    }

    @Test
    public void whenNoTestEventPreceded_returnsFalse() {
        // given
        IIDEEvent event = new TestEvent(ZonedDateTime.now());

        // when
        Map<String, String> results = aggregator.aggregateValue(event);

        // then
        assertEquals(1, results.size());
        assertEquals(LastBuildWasSuccessfulAggregator.FALSE, results.get(LastBuildWasSuccessfulAggregator.TITLE));
    }

    @Test
    public void whenFailingTestPreceded_returnsFalse() {
        // given
        BuildEvent successfulBuild = createSuccessfulBuild();
        BuildEvent failingBuild = createFailingBuild();

        // when
        aggregator.aggregateValue(successfulBuild);
        Map<String, String> results = aggregator.aggregateValue(failingBuild);

        // then
        assertEquals(1, results.size());
        assertEquals(LastBuildWasSuccessfulAggregator.FALSE, results.get(LastBuildWasSuccessfulAggregator.TITLE));
    }

    @Test
    public void whenIsSuccessfullEvent_returnsTrue() {
        // given
        BuildEvent successfulBuild = createSuccessfulBuild();

        // when
        Map<String, String> results = aggregator.aggregateValue(successfulBuild);

        // then
        assertEquals(1, results.size());
        assertEquals(LastBuildWasSuccessfulAggregator.TRUE, results.get(LastBuildWasSuccessfulAggregator.TITLE));
    }

    @Test
    public void whenSuccessfullEventPreceded_returnsTrue() {
        // given
        BuildEvent successfulBuild = createSuccessfulBuild();
        IIDEEvent event = new TestEvent(ZonedDateTime.now());

        // when
        aggregator.aggregateValue(successfulBuild);
        Map<String, String> results = aggregator.aggregateValue(event);

        // then
        assertEquals(1, results.size());
        assertEquals(LastBuildWasSuccessfulAggregator.TRUE, results.get(LastBuildWasSuccessfulAggregator.TITLE));
    }

    private BuildEvent createSuccessfulBuild() {
        BuildEvent buildEvent = new BuildEvent();
        buildEvent.Targets = new ArrayList<>();

        BuildTarget target = new BuildTarget();
        target.Successful = true;

        buildEvent.Targets.add(target);

        return buildEvent;
    }

    private BuildEvent createFailingBuild() {
        BuildEvent buildEvent = new BuildEvent();
        buildEvent.Targets = new ArrayList<>();

        BuildTarget target = new BuildTarget();
        target.Successful = false;

        buildEvent.Targets.add(target);

        return buildEvent;
    }
}