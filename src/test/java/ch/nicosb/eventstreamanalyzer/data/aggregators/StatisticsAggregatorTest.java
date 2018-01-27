package ch.nicosb.eventstreamanalyzer.data.aggregators;

import cc.kave.commons.model.events.IIDEEvent;
import ch.nicosb.eventstreamanalyzer.testutils.TestEvent;
import ch.nicosb.eventstreamanalyzer.testutils.TestFileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.*;

public class StatisticsAggregatorTest {

    private StatisticsAggregator aggregator;
    private Path filePath;

    @Before
    public void setUp() throws IOException {
        filePath = TestFileUtils.getRandomFilePath(".txt");
        aggregator = new StatisticsAggregator(filePath.toAbsolutePath().toString());
    }

    @After
    public void cleanUp() throws IOException {
        if (Files.exists(filePath))
            Files.delete(filePath);
    }

    @Test
    public void returnsEmptyTitleSet() {
        // when
        Set<String> result = aggregator.getTitles();

        // then
        assertEquals(0, result.size());
    }

    @Test
    public void whenEventIsAggregated_returnsEmptyMap() {
        // given
        IIDEEvent testEvent = new TestEvent(ZonedDateTime.now());

        // when
        Map<String, String> result = aggregator.aggregateValue(testEvent);

        // then
        assertEquals(0, result.size());
    }

    @Test
    public void whenIsClosed_writesFile() throws IOException {
        // given
        IIDEEvent testEvent = new TestEvent(ZonedDateTime.now());
        String expected = testEvent.getClass().getName() + ";1";

        // when
        aggregator.aggregateValue(testEvent);
        aggregator.close();
        List<String> lines = Files.readAllLines(filePath);

        // then
        assertEquals(1, lines.size());
        assertEquals(expected, lines.get(0));
    }

}