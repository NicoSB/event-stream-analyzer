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

import ch.nicosb.eventstreamanalyzer.stream.CompactEvent;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.time.temporal.Temporal;
import java.util.List;

public class TimelineDrawer implements ImageDrawer {

    private static final float DOT_DIAMETER = 5.0f;
    private final String PNG = "PNG";
    private final int HEIGHT = 100;
    private final int PADDING = 5;
    private final int width;
    private final String outputUri;
    private List<CompactEvent> events;

    public TimelineDrawer(String outputUri, int width) {
        this.outputUri = outputUri;
        this.width = width;
    }

    @Override
    public void drawImage() {
        try {
            draw();
        } catch (IOException e) {
            System.err.println("Image could not be drawn!");
        }
    }

    @Override
    public void setEvents(List<CompactEvent> events) {
        this.events = events;
    }

    private void draw() throws IOException {
        BufferedImage image = new BufferedImage(width, HEIGHT, BufferedImage.TYPE_INT_ARGB);

        Graphics2D graphics = image.createGraphics();
        drawBackground(graphics);
        drawLine(graphics);
        drawEvents(graphics);
        writeImage(image);
    }

    private void drawBackground(Graphics2D graphics) {
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, width, HEIGHT);
    }

    private void drawLine(Graphics2D graphics) {
        int verticalCenter = HEIGHT / 2;
        graphics.setColor(Color.black);
        graphics.drawLine(PADDING, verticalCenter, width - PADDING, verticalCenter);
    }

    private void drawEvents(Graphics2D graphics) {
        events.forEach(evt -> drawEvent(evt, graphics));
    }

    private void drawEvent(CompactEvent evt, Graphics2D graphics) {
        Point point = calculatePoint(evt.dateTime);
        Ellipse2D dot = new Ellipse2D.Float(point.x, point.y, DOT_DIAMETER, DOT_DIAMETER);

        graphics.setColor(Color.BLACK);
        graphics.fill(dot);
    }

    private Point calculatePoint(ZonedDateTime dateTime) {
        int verticalCenter = HEIGHT / 2;
        int minX = PADDING;
        int maxX = width - PADDING;
        int availableWidth = maxX - minX;

        long timeSpanInMillis = calculateTimeDifference(events.get(0).dateTime, events.get(events.size() - 1).dateTime);
        long offset = calculateTimeDifference(events.get(0).dateTime, dateTime);

        long xPosition = (long)(PADDING + DOT_DIAMETER / 2 + ((float)offset / timeSpanInMillis) * availableWidth);
        return new Point((int)xPosition, verticalCenter);
    }

    private long calculateTimeDifference(Temporal temp1, Temporal temp2) {
        return Duration.between(temp1, temp2).toMillis();
    }

    private void writeImage(BufferedImage image) throws IOException {
        ImageIO.write(image, PNG, new File(outputUri));
    }
}
