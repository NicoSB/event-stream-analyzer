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
package ch.nicosb.eventstreamanalyzer;

import cc.kave.commons.model.events.IIDEEvent;
import ch.nicosb.eventstreamanalyzer.parser.EventParser;
import ch.nicosb.eventstreamanalyzer.stream.CompactEvent;
import ch.nicosb.eventstreamanalyzer.stream.EventListTransformer;
import ch.nicosb.eventstreamanalyzer.visualization.Visualizer;
import ch.nicosb.eventstreamanalyzer.visualization.jfreechart.JFreeChartDrawer;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class Main {
    private static final String DEFAULT_FILE_URI = "test.png";

    public static void main(String[] args) {
        if (args.length == 0) {
            printErrorMessage();
        }

        String fileUri = args.length == 2 ? args[1] : DEFAULT_FILE_URI;

        String folder = args[0];
        List<IIDEEvent> events = parseZips(folder);
        List<CompactEvent> compactEvents = EventListTransformer.fromEventList(events);

        Visualizer visualizer = new Visualizer(compactEvents, new JFreeChartDrawer(fileUri));
        visualizer.drawImage();
        Path image = Paths.get(fileUri);
        System.out.printf("Created file at %s.", image.toAbsolutePath().toString());
    }

    private static void printErrorMessage() {
        System.err.println("Must have at least a directory as input");
    }

    private static List<IIDEEvent> parseZips(String uri) {
        return EventParser.parseDirectory(uri);
    }
}
