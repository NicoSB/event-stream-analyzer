package ch.nicosb.eventstreamanalyzer.data.sampling;

import cc.kave.commons.model.events.IIDEEvent;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

public class IntervalPicker implements SamplePicker {

    private int intervalInSeconds;
    private ZonedDateTime intervalEnd;

    public IntervalPicker(int intervalInSeconds) {
        this.intervalInSeconds = intervalInSeconds;

        Instant minDate = Instant.ofEpochMilli(Long.MIN_VALUE);
        intervalEnd = minDate.atZone(ZoneOffset.UTC);
    }

    @Override
    public boolean shouldSample(IIDEEvent event) {
        return !isWithinInterval(event);
    }

    private boolean isWithinInterval(IIDEEvent event) {
        if (event.getTriggeredAt().isAfter(intervalEnd)) {
            intervalEnd = event.getTriggeredAt().plusSeconds(intervalInSeconds);
            return false;
        }

        return true;
    }
}
