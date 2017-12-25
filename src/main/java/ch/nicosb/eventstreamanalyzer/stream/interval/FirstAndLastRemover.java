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
package ch.nicosb.eventstreamanalyzer.stream.interval;

import java.time.ZonedDateTime;
import java.util.*;

public class FirstAndLastRemover implements IntervalPostProcessor {

    private int timeout;

    public FirstAndLastRemover(int timeout) {
        this.timeout = timeout;
    }

    @Override
    public List<Interval> process(List<Interval> intervals) {
        List<Interval> copy = new ArrayList<>(intervals);
        ListIterator iterator = copy.listIterator();
        boolean checkDate = false;
        ZonedDateTime lastEnd = ZonedDateTime.now().minusSeconds(timeout + 1);

        iterator.next();
        iterator.remove();

        while (iterator.hasNext()) {
            Interval interval = (Interval) iterator.next();

            if(interval.isEmpty()) {
                iterator.remove();
            } else {
                ZonedDateTime intervalBegin = interval.getStart();

                if (checkDate && intervalBegin.isAfter(lastEnd)) {
                    removeLastTwoElements(iterator);
                    checkDate = false;
                } else {
                    lastEnd = interval.getEnd();
                    checkDate = true;
                }
            }
        }

        if (iterator.hasPrevious()) {
            iterator.previous();
            iterator.remove();
        }

        return copy;
    }

    private void removeLastTwoElements(ListIterator iterator) {
        iterator.previous();
        iterator.remove();
        iterator.next();
        iterator.remove();
    }
}
