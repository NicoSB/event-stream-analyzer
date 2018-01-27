package ch.nicosb.eventstreamanalyzer.data.aggregators;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class CountingMap {
    private Map<String, Integer> map = new HashMap<>();

    public void put(String key) {
        if (map.containsKey(key)) {
            map.replace(key, map.get(key) + 1);
        } else {
            map.put(key, 1);
        }
    }

    public int get(String key) {
        return map.get(key);
    }

    public Map<String, Integer> getMap() {
        return map;
    }

    public Set<Map.Entry<String, Integer>> entrySet() {
        return map.entrySet();
    }
}
