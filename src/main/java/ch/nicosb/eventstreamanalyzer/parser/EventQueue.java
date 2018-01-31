package ch.nicosb.eventstreamanalyzer.parser;

import cc.kave.commons.model.events.IIDEEvent;

public interface EventQueue {
    void add(IIDEEvent event);

    IIDEEvent poll();

    int size();
}
