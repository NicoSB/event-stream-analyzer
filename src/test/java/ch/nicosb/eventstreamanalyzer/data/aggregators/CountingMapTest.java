package ch.nicosb.eventstreamanalyzer.data.aggregators;

import org.junit.Before;
import org.junit.Test;

import java.util.Map;

import static cc.kave.commons.assertions.Asserts.assertEquals;

public class CountingMapTest {

    private CountingMap map;

    @Before
    public void setUp() {
        map = new CountingMap();
    }

    @Test
    public void whenFirstElementIsAdded_containsOne() {
        // given
        String key = "KEY";

        // when
        map.put(key);
        Map<String, Integer> result = map.getMap();

        // then
        assertEquals(1, result.size());
        assertEquals(1, result.get(key));
    }

    @Test
    public void whenSecondElementIsAdded_increasesCounter() {
        // given
        String key = "KEY";

        // when
        map.put(key);
        map.put(key);
        Map<String, Integer> result = map.getMap();

        // then
        assertEquals(1, result.size());
        assertEquals(2, result.get(key));
    }

    @Test
    public void whenDifferentStringIsAdded_CreatesNewEntry() {
        // given
        String key1 = "KEY1";
        String key2 = "KEY2";

        // when
        map.put(key1);
        map.put(key2);
        Map<String, Integer> result = map.getMap();

        // then
        assertEquals(2, result.size());
        assertEquals(1, result.get(key1));
        assertEquals(1, result.get(key2));
    }

}