package ch.nicosb.eventstreamanalyzer.data.sampling;

import cc.kave.commons.model.events.IIDEEvent;

public interface SamplePicker {
    boolean shouldSample(IIDEEvent event);
}
