package ch.nicosb.eventstreamanalyzer.data.aggregators;

import cc.kave.commons.model.events.IIDEEvent;
import cc.kave.commons.model.events.testrunevents.TestCaseResult;
import cc.kave.commons.model.events.testrunevents.TestResult;
import cc.kave.commons.model.events.testrunevents.TestRunEvent;
import cc.kave.commons.model.events.versioncontrolevents.VersionControlAction;
import cc.kave.commons.model.events.versioncontrolevents.VersionControlEvent;
import cc.kave.commons.model.events.visualstudio.BuildEvent;
import cc.kave.commons.model.events.visualstudio.IDEStateEvent;
import ch.nicosb.eventstreamanalyzer.utils.EventUtils;

import java.io.Closeable;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.ZonedDateTime;
import java.util.*;

public class StatisticsAggregator implements Aggregator, Closeable {

    private String fileUri;
    private CountingMap map;
    private ZonedDateTime lastVCExecutedAt;

    public StatisticsAggregator(String fileUri) {
        this.fileUri = fileUri;
        map = new CountingMap();
    }

    @Override
    public Map<String, String> aggregateValue(IIDEEvent event) {

        if (event instanceof TestRunEvent) {
            map.put(EventUtils.isSuccessfulTestEvent(event) + "Test");
            map.put(event.getClass().getName());
        } else if (event instanceof VersionControlEvent) {
            VersionControlEvent vcEvent = (VersionControlEvent) event;
            if (!vcEvent.TriggeredAt.equals(lastVCExecutedAt)) {
                vcEvent.Actions.forEach(action -> map.put("VC" + action.ActionType.name()));
                map.put(event.getClass().getName());
                lastVCExecutedAt = vcEvent.TriggeredAt;
            }
        } else if (event instanceof IDEStateEvent) {
            IDEStateEvent ideStateEvent = (IDEStateEvent) event;
            map.put("IDE" + ideStateEvent.IDELifecyclePhase);
        } else if (EventUtils.isBuildEvent(event)) {
            if (event instanceof BuildEvent) {
                map.put("Build" + EventUtils.isSuccessfulBuildEvent(event));
            } else {
                map.put("buildtrue");
            }
        } else {
            map.put(event.getClass().getName());
        }

        return new HashMap<>();
    }

    @Override
    public Set<String> getTitles() {
        return new HashSet<>();
    }

    @Override
    public void close() throws IOException {
        List<String> lines = buildLines();
        Path filePath = Paths.get(fileUri);

        Files.write(filePath, lines);
    }

    private List<String> buildLines() {
        List<String> lines = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            String line = entry.getKey() + ";" + String.valueOf(entry.getValue());
            lines.add(line);
        }

        return lines;
    }
}
