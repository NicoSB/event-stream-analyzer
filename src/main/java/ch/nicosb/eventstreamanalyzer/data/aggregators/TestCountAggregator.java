package ch.nicosb.eventstreamanalyzer.data.aggregators;

import cc.kave.commons.model.events.IIDEEvent;
import cc.kave.commons.model.events.testrunevents.TestRunEvent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class TestEventCountAggregator implements Aggregator {

    private Set<String> titles = new HashSet<>();
    private int testEventsCount = 0;

    public TestEventCountAggregator() {
        titles.add("registeredTestEvents");
    }


    @Override
    public Map<String, String> aggregateValue(IIDEEvent event) {
        Map<String, String> map = new HashMap<>();
        if (event instanceof TestRunEvent)
            ++testEventsCount;

        map.put(titles.iterator().next(), String.valueOf(testEventsCount));

        return map;
    }

    @Override
    public Set<String> getTitles() {
        ;
    }
}
