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
package ch.nicosb.eventstreamanalyzer.visualization;

import cc.kave.commons.model.events.IIDEEvent;
import ch.nicosb.eventstreamanalyzer.Execution;
import ch.nicosb.eventstreamanalyzer.parser.EventParser;
import ch.nicosb.eventstreamanalyzer.stream.CompactEvent;
import ch.nicosb.eventstreamanalyzer.stream.EventListTransformer;
import ch.nicosb.eventstreamanalyzer.visualization.jfreechart.JFreeChartDrawer;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class Visualization implements Execution {
    private static final String DEFAULT_FILE_URI = "test.png";

    @Override
    public void execute(String[] args) {
        String fileUri = args.length == 3 ? args[2] : DEFAULT_FILE_URI;

        String folder = args[1];
        List<IIDEEvent> events = EventParser.parseDirectory(folder);
        List<CompactEvent> compactEvents = EventListTransformer.fromEventList(events);

        Visualizer visualizer = new Visualizer(compactEvents, new JFreeChartDrawer(fileUri));
        visualizer.drawImage();
        Path image = Paths.get(fileUri);
        System.out.printf("Created file at %s.\n", image.toAbsolutePath().toString());
    }
}
