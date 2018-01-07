package ch.nicosb.eventstreamanalyzer.data.sampling;

import cc.kave.commons.model.events.IIDEEvent;
import ch.nicosb.eventstreamanalyzer.testutils.TestEvent;
import org.junit.Before;
import org.junit.Test;

import java.time.ZonedDateTime;

import static org.junit.Assert.*;

public class IntervalPickerTest {

    private IntervalPicker picker;
    private final static int INTERVAL = 10;

    @Before
    public void startUp() {
        picker = new IntervalPicker(INTERVAL);
    }

    @Test
    public void whenFirstEventIsGiven_returnsTrue() {
        // given
        IIDEEvent event = new TestEvent(ZonedDateTime.now());

        // when
        boolean actual = picker.shouldSample(event);

        // then
        assertTrue(actual);
    }

    @Test
    public void whenEventIsWithinInterval_returnsFalse() {
        // given
        IIDEEvent event = new TestEvent(ZonedDateTime.now());
        IIDEEvent laterEvent = new TestEvent(event.getTriggeredAt().plusSeconds(INTERVAL - 1));

        // when
        picker.shouldSample(event);
        boolean actual = picker.shouldSample(laterEvent);

        // then
        assertFalse(actual);
    }

    @Test
    public void whenEventIsAfterIntervalEnd_returnsTrue() {
        // given
        IIDEEvent event = new TestEvent(ZonedDateTime.now());
        IIDEEvent laterEvent = new TestEvent(event.getTriggeredAt().plusSeconds(INTERVAL + 1));

        // when
        picker.shouldSample(event);
        boolean actual = picker.shouldSample(laterEvent);

        // then
        assertTrue(actual);
    }
}