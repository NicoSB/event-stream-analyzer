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
import ch.nicosb.eventstreamanalyzer.data.aggregators.Aggregator;
import ch.nicosb.eventstreamanalyzer.testutils.TestEvent;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TraverserImplTest {

    public static final String AGGREGATOR_TITLE = "AGG";
    public static final double VALUE = 19.34;
    private TraverserImpl traverser;
    private List<IIDEEvent> events;
    private boolean visited;

    @Mock
    private Aggregator aggregator;

    @Before
    public void setUp() {
        initEvents();
        when(aggregator.getTitle()).thenReturn(AGGREGATOR_TITLE);
        when(aggregator.aggregateValue(any(), any())).thenReturn(VALUE);

        visited = false;

        traverser = new TraverserImpl(events);
        traverser.register(aggregator);
    }

    @Test
    public void whenAggregatorRegisters_isCalledInTraverse() {
        // given
        Aggregator visitedAggregator = createVisitedAggregator();
        traverser.register(visitedAggregator);

        // when
        traverser.traverse();

        // then
        assertTrue(visited);
    }

    @Test
    public void whenAggregatorUnregisters_isNotCalledInTraverse() {
        // given
        Aggregator visitedAggregator = createVisitedAggregator();
        traverser.register(visitedAggregator);
        traverser.unregister(visitedAggregator);

        // when
        traverser.traverse();

        // then
        assertFalse(visited);
    }

    private Aggregator createVisitedAggregator() {
        return new Aggregator("VisitedAggregator") {
            @Override
            public double aggregateValue(List<IIDEEvent> events, IIDEEvent event) {
                visited = true;
                return 1.0d;
            }
        };
    }

    @Test
    public void whenListIsTraversed_returnsCorrectValues() {
        // when
        List<Entry> entries = traverser.traverse();

        // then
        assertEquals(4, entries.size());
        assertEquals(1, entries.get(0).getFields().size());
        assertEquals(1, entries.get(1).getFields().size());
        assertEquals(1, entries.get(2).getFields().size());
        assertEquals(1, entries.get(3).getFields().size());
        assertEquals(VALUE, (double)entries.get(0).getFields().get(AGGREGATOR_TITLE), 0);
        assertEquals(VALUE, (double)entries.get(1).getFields().get(AGGREGATOR_TITLE), 0);
        assertEquals(VALUE, (double)entries.get(2).getFields().get(AGGREGATOR_TITLE), 0);
        assertEquals(VALUE, (double)entries.get(3).getFields().get(AGGREGATOR_TITLE), 0);

    }

    private void initEvents() {
        events = new ArrayList<>();

        events.add(new TestEvent(ZonedDateTime.now()));
        events.add(new TestEvent(ZonedDateTime.now()));
        events.add(new TestEvent(ZonedDateTime.now()));
        events.add(new TestEvent(ZonedDateTime.now()));
    }
}
