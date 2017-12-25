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
package ch.nicosb.eventstreamanalyzer.stream;

import ch.nicosb.eventstreamanalyzer.testutils.TestEvent;
import org.junit.Before;
import org.junit.Test;

import java.time.ZonedDateTime;

import static org.junit.Assert.*;

public class TriggeredAtComparatorTest {

    private TriggeredAtComparator comparator;

    @Before
    public void setUp() {
        comparator = new TriggeredAtComparator();
    }

    @Test
    public void whenObjectsAreEqual_returnsZero() {
        // given
        TestEvent event = new TestEvent(ZonedDateTime.now());

        // when
        int actual = comparator.compare(event, event);

        // then
        assertEquals(0, actual);
    }

    @Test
    public void whenFirstEventWasTriggeredEarlier_returnsMinusOne() {
        // given
        TestEvent first = new TestEvent(ZonedDateTime.now());
        TestEvent second = new TestEvent(ZonedDateTime.now().plusDays(1));

        // when
        int actual = comparator.compare(first, second);

        // then
        assertEquals(-1, actual);
    }

    @Test
    public void whenSecondEventWasTriggeredEarlier_returnsOne() {
        // given
        TestEvent first = new TestEvent(ZonedDateTime.now());
        TestEvent second = new TestEvent(ZonedDateTime.now().minusDays(1));

        // when
        int actual = comparator.compare(first, second);

        // then
        assertEquals(1, actual);
    }

}