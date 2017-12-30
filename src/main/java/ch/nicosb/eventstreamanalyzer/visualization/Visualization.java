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
import ch.nicosb.eventstreamanalyzer.parser.ZipUtils;
import ch.nicosb.eventstreamanalyzer.stream.EventStream;
import ch.nicosb.eventstreamanalyzer.stream.CompactEvent;
import ch.nicosb.eventstreamanalyzer.stream.EventListTransformer;
import ch.nicosb.eventstreamanalyzer.visualization.jfreechart.JFreeChartDrawer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Visualization implements Execution {
    private static final String DEFAULT_FOLDER_URI = "tmp";
    public static final String PNG = ".png";

    @Override
    public void execute(String[] args) {
        String folderUri = args.length == 3 ? args[2] : DEFAULT_FOLDER_URI;

//        String folder = args[1];
//        List<EventStream> streams = ZipUtils.parseDirectory(folder);
//
//        int i = 0;
//        for (EventStream stream : streams) {
//            String fileName = i++ + "_" + stream.getTitle() + PNG;
//            ensureFolderExists(folderUri);
//            drawImage(folderUri, fileName, stream.getEvents());
//        }
    }

    private void ensureFolderExists(String folderUri) {
        try {
            Files.createDirectory(Paths.get(folderUri));
        } catch (IOException e) { }
    }

    private void drawImage(String folderUri, String fileName, List<IIDEEvent> events) {
        List<CompactEvent> compactEvents = EventListTransformer.fromEventList(events);

        String fileUri = Paths.get(folderUri, fileName).toAbsolutePath().toString();

        Visualizer visualizer = new Visualizer(compactEvents, new JFreeChartDrawer(fileUri));
        visualizer.drawImage();
        System.out.printf("Created file at %s.\n", fileUri);
    }
}
