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
package ch.nicosb.eventstreamanalyzer.visualization.jfreechart;

import ch.nicosb.eventstreamanalyzer.stream.CompactEvent;
import ch.nicosb.eventstreamanalyzer.visualization.ImageDrawer;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.xy.AbstractXYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class JFreeChartDrawer implements ImageDrawer {

    private static final String PNG = "png";
    private final String outputUri;
    private List<CompactEvent> events;

    public JFreeChartDrawer(String outputUri) {
        this.outputUri = outputUri;
    }

    @Override
    public void drawImage() {
        try {
            draw();
        } catch (IOException e) {
            System.err.println("Could not draw the image!");
        }
    }

    private void draw() throws IOException {
        JFreeChart chart = createChart();
        BufferedImage image = chart.createBufferedImage(2000, 400);
        ImageIO.write(image, PNG, new File(outputUri));
    }

    private JFreeChart createChart() {
        JFreeChart chart = ChartFactory.createXYLineChart("events", "Time", "Events",
                createDataset(), PlotOrientation.VERTICAL,
                false, true, false);
        AbstractXYItemRenderer renderer = createRenderer();
        chart.getXYPlot().setRenderer(renderer);
        return chart;
    }

    private AbstractXYItemRenderer createRenderer() {
        AbstractXYItemRenderer renderer = new XYLineAndShapeRenderer(false, true);
        renderer.setDefaultItemLabelsVisible(true);
        return renderer;
    }

    private XYDataset createDataset() {
        return ListToDataSetConverter.convert(events);
    }

    @Override
    public void setEvents(List<CompactEvent> events) {
        this.events = events;
    }
}
