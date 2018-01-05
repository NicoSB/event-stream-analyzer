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
package ch.nicosb.eventstreamanalyzer.stream.interval;

import ch.nicosb.eventstreamanalyzer.data.aggregators.Aggregator;
import ch.nicosb.eventstreamanalyzer.data.aggregators.NominalAggregator;

import javax.swing.event.DocumentEvent;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class ArffWriter implements Closeable{
    private static final String ARFF = ".arff";
    private static final String RELATION_KEY = "@RELATION";
    private static final String ATTRIBUTE_KEY = "@ATTRIBUTE";
    private static final String NUMERIC_KEY = "NUMERIC";
    private static final String DATA_KEY = "@DATA";

    private Set<Aggregator> aggregators;
    private String fileName;
    private BufferedWriter writer;
    private List<String> keys;

    public ArffWriter(Set<Aggregator> aggregators, String fileName) {
        this.aggregators = aggregators;
        this.fileName = fileName;
        keys = new ArrayList<>();

        if (!fileName.endsWith(ARFF)) {
            this.fileName = fileName + ARFF;
        }
    }

    void createNewFile() {
        try {
            writer = Files.newBufferedWriter(Paths.get(fileName));
            writeHeader(fileName.replace(ARFF, ""));
        } catch (IOException ioe) {
            fileName = fileName + "(1)";
            createNewFile();
        }
    }

    private void writeHeader(String relation) throws IOException {
        writeRelation(relation);
        writeAttributes();
        writeDataHeader();
    }

    private void writeRelation(String relation) throws IOException {
        writer.write(RELATION_KEY + " " + relation);
        writer.newLine();
        writer.newLine();
    }

    private void writeAttributes() throws IOException {

        List<Aggregator> sorted = aggregators.stream()
                .sorted(Comparator.comparing(aggregator -> aggregator.getClass().toString()))
                .collect(Collectors.toList());

        for (Aggregator agg : sorted) {
            writeAttributes(agg);
        }
        writer.newLine();
    }

    private void writeAttributes(Aggregator aggregator) {
        if (aggregator instanceof NominalAggregator) {
            writeNominalAttributes((NominalAggregator) aggregator);
        } else {
            writeNumericalAttributes(aggregator);
        }
    }

    private void writeNominalAttributes(NominalAggregator aggregator) {
        aggregator.getTitles().stream()
            .sorted()
            .forEach(title -> writeNominalAttribute(aggregator, title));
    }

    private void writeNominalAttribute(NominalAggregator aggregator, String title){
        try {
            String valueString = buildValueString(aggregator);

            writer.write(ATTRIBUTE_KEY + " " + title + " " + valueString);
            writer.newLine();

            keys.add(title);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String buildValueString(NominalAggregator aggregator) {
        String[] values = aggregator.getPossibleValues();
        StringBuilder builder = new StringBuilder();
        builder.append("{");
        for (String value : values) {
            builder.append(value);
            builder.append(",");
        }

        builder.replace(builder.length() - 1, builder.length(), "}");
        return builder.toString();
    }

    private void writeNumericalAttributes(Aggregator aggregator) {
        aggregator.getTitles().stream()
                .sorted()
                .forEach(this::writeNumericalAttribute);
    }

    private void writeNumericalAttribute(String title) {
        try {
            writer.write(ATTRIBUTE_KEY + " " + title + " " + NUMERIC_KEY);
            writer.newLine();
            keys.add(title);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeDataHeader() throws IOException {
        writer.write(DATA_KEY);
        writer.newLine();
    }

    public void writeData(Map<String, String> map) {
        try {
            String dataRow = buildDataRow(map);

            writer.write(dataRow);
            writer.newLine();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    private String buildDataRow(Map<String, String> map) {
        StringBuilder builder = new StringBuilder();
        keys.forEach(key -> builder.append(map.get(key)).append(","));
        builder.deleteCharAt(builder.length() - 1);
        return builder.toString();
    }

    @Override
    public void close() throws IOException {
        writer.close();
    }
}