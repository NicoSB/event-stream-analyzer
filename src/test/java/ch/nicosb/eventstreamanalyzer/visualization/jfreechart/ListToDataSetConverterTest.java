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
package ch.nicosb.eventstreamanalyzer.visualization.jfreechart;

import cc.kave.commons.model.events.IIDEEvent;
import ch.nicosb.eventstreamanalyzer.stream.CompactEvent;
import ch.nicosb.eventstreamanalyzer.testutils.TestEvent;
import org.jfree.data.xy.XYDataset;
import org.junit.Test;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class ListToDataSetConverterTest {

    @Test
    public void convertsCompactEventCorrectly() throws Exception {
        // given
        ZonedDateTime date1 = ZonedDateTime.now();
        ZonedDateTime date2 = ZonedDateTime.now();

        IIDEEvent event = new TestEvent(date1);
        IIDEEvent event2 = new TestEvent(date2);

        List<CompactEvent> events = new ArrayList<>();
        events.add(new CompactEvent((event)));
        events.add(new CompactEvent(event2));

        // when
        XYDataset dataset = ListToDataSetConverter.convert(events);

        // then
        assertEquals(1, dataset.getSeriesCount());
        assertEquals(2, dataset.getItemCount(0));
        assertEquals((double)date1.toEpochSecond(), dataset.getX(0, 0));
        assertEquals((double)date2.toEpochSecond(), dataset.getX(0, 1));
    }

}