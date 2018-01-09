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


import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class IntervalActiveTimeWindow {
    private List<Interval> intervals = new ArrayList<>();
    private int sizeInSeconds;
    private int timeoutInSeconds;

    public IntervalActiveTimeWindow(int sizeInSeconds, int timeout) {
        this.sizeInSeconds = sizeInSeconds;
        this.timeoutInSeconds = timeout;
    }

    public void addInterval(Interval interval) {
        try {
            if (!isEmpty() && isWithinTimeoutOfLast(interval.start))
                prolongLastInterval(interval.end);
            else {
                intervals.add(interval);
            }

            removeOldIntervals();
        } catch (IllegalArgumentException e) {

        }
    }

    private boolean isWithinTimeoutOfLast(ZonedDateTime point) throws IllegalArgumentException {
        Interval last = intervals.get(intervals.size() - 1);
        long difference = Duration.between(last.end, point).toMillis();

        if (difference < 0) {
            throw new IllegalArgumentException("Wrong ordering of Intervals");
        }
        return difference <= timeoutInSeconds * 1000;
    }

    private void prolongLastInterval(ZonedDateTime point) {
        Interval last = intervals.get(intervals.size() - 1);
        last.end = point;
    }

    private  void removeOldIntervals() {
        Interval last = intervals.get(intervals.size() - 1);
        ZonedDateTime minTime = last.end.minusSeconds(sizeInSeconds);

        Iterator it = intervals.iterator();
        while (it.hasNext()) {
            shortenOrRemoveInterval(minTime, it);
        }
    }

    private void shortenOrRemoveInterval(ZonedDateTime minTime, Iterator it) {
        Interval interval = (Interval) it.next();
        if (interval.end.isBefore(minTime)) {
            it.remove();
        } else if(interval.start.isBefore(minTime)) {
            interval.start = minTime;
        }
    }

    public long getActiveTimeInMillis() {
        return intervals.stream()
                .mapToLong(Interval::lengthInMillis)
                .sum();
    }

    public int size() {
        return intervals.size();
    }

    public Interval get(int index) {
        return intervals.get(index);
    }

    public boolean isEmpty() {
        return intervals.isEmpty();
    }
}
