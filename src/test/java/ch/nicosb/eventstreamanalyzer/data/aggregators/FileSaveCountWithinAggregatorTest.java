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
import cc.kave.commons.model.events.visualstudio.DocumentAction;
import cc.kave.commons.model.events.visualstudio.DocumentEvent;
import ch.nicosb.eventstreamanalyzer.testutils.TestEvent;
import org.junit.Before;
import org.junit.Test;

import java.time.ZonedDateTime;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class FileSaveCountWithinAggregatorTest {

    private FileSaveCountWithinAggregator aggregator;
    private static final int WINDOW = 300;

    @Before
    public void setUp() {
        aggregator = new FileSaveCountWithinAggregator(WINDOW);
    }

    @Test
    public void returnsCorrectTitles() {
        // given
        int window2 = WINDOW * 2;
        aggregator = new FileSaveCountWithinAggregator(WINDOW, window2);

        String expected1 = String.format(FileSaveCountWithinAggregator.TITLE_BLUEPRINT, WINDOW);
        String expected2 = String.format(FileSaveCountWithinAggregator.TITLE_BLUEPRINT, window2);

        // when
        Set<String> actual = aggregator.getTitles();

        // then
        assertEquals(2, actual.size());
        assertTrue(actual.contains(expected1));
        assertTrue(actual.contains(expected2));
    }

    @Test
    public void whenIsFirstEvent_returnsZero() {
        // given
        IIDEEvent event = new TestEvent(ZonedDateTime.now());

        String title = String.format(FileSaveCountWithinAggregator.TITLE_BLUEPRINT, WINDOW);
        String expected = "0";

        // when
        Map<String, String> result = aggregator.aggregateValue(event);

        // then
        assertEquals(expected, result.get(title));
    }

    @Test
    public void whenIsFileSaveEvent_returnsOne() {
        // given
        DocumentEvent event = new DocumentEvent();
        event.Action = DocumentAction.Saved;
        event.TriggeredAt = ZonedDateTime.now();

        String title = String.format(FileSaveCountWithinAggregator.TITLE_BLUEPRINT, WINDOW);
        String expected = "1";

        // when
        Map<String, String> result = aggregator.aggregateValue(event);

        // then
        assertEquals(expected, result.get(title));
    }

    @Test
    public void whenFileSaveEventPreceded_returnsOne() {
        // given
        ZonedDateTime now = ZonedDateTime.now();

        DocumentEvent documentEvent = new DocumentEvent();
        documentEvent.Action = DocumentAction.Saved;
        documentEvent.TriggeredAt = now.minusSeconds(WINDOW - 1);

        IIDEEvent event = new TestEvent(now);

        String title = String.format(FileSaveCountWithinAggregator.TITLE_BLUEPRINT, WINDOW);
        String expected = "1";

        // when
        aggregator.aggregateValue(documentEvent);
        Map<String, String> result = aggregator.aggregateValue(event);

        // then
        assertEquals(expected, result.get(title));
    }

    @Test
    public void whenSaveEventPrecededBeforeWindow_returnsOne() {
        // given
        ZonedDateTime now = ZonedDateTime.now();

        DocumentEvent documentEvent = new DocumentEvent();
        documentEvent.Action = DocumentAction.Saved;
        documentEvent.TriggeredAt = now.minusSeconds(WINDOW + 1);

        IIDEEvent event = new TestEvent(now);

        String title = String.format(FileSaveCountWithinAggregator.TITLE_BLUEPRINT, WINDOW);
        String expected = "0";

        // when
        aggregator.aggregateValue(documentEvent);
        Map<String, String> result = aggregator.aggregateValue(event);

        // then
        assertEquals(expected, result.get(title));
    }
}