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

import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.stream.Collectors;

public class ArffWriter implements Closeable{
    static final String ARFF = ".arff";
    static final String RELATION_KEY = "@RELATION";
    static final String ATTRIBUTE_KEY = "@ATTRIBUTE";
    static final String NUMERIC_KEY = "NUMERIC";
    static final String DATA_KEY = "@DATA";

    private Set<Aggregator> aggregators;
    private String fileName;
    private BufferedWriter writer;
    private int counter = 0;

    public ArffWriter(TreeSet<Aggregator> aggregators, String fileName) {
        this.aggregators = aggregators;
        this.fileName = fileName;
        if (!fileName.endsWith(ARFF)) {
            this.fileName = fileName + ARFF;
        }
    }

    void createNewFile() {
        try {
            writer = Files.newBufferedWriter(Paths.get(fileName));
            writeHeader(fileName.replace(ARFF, ""), writer);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    private void writeHeader(String relation, BufferedWriter writer) throws IOException {
        writeRelation(relation, writer);
        writeAttributes(writer);
        writeDataHeader(writer);
    }

    private void writeRelation(String relation, BufferedWriter writer) throws IOException {
        writer.write(RELATION_KEY + " " + relation);
        writer.newLine();
        writer.newLine();
    }

    private void writeAttributes(BufferedWriter writer) throws IOException {
        List<Aggregator> sorted = aggregators.stream()
                .sorted(Comparator.comparing(Aggregator::getTitle))
                .collect(Collectors.toList());

        for (Aggregator agg : sorted) {
            writeAttribute(agg, writer);
        }
        writer.newLine();
    }

    private void writeAttribute(Aggregator aggregator, BufferedWriter writer) throws IOException {
        if (aggregator instanceof NominalAggregator) {
            writeNominalAttribute((NominalAggregator) aggregator, writer);
        } else {
            writeNumericalAttribute(aggregator, writer);
        }
    }

    private void writeNominalAttribute(NominalAggregator aggregator, BufferedWriter writer) throws IOException {
        String valueString = buildValueString(aggregator);

        writer.write(ATTRIBUTE_KEY + " " + aggregator.getTitle() + " " + valueString);
        writer.newLine();
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

    private void writeNumericalAttribute(Aggregator aggregator, BufferedWriter writer) throws IOException {
        writer.write(ATTRIBUTE_KEY + " " + aggregator.getTitle() + " " + NUMERIC_KEY);
        writer.newLine();
    }

    private void writeDataHeader(BufferedWriter writer) throws IOException {
        writer.write(DATA_KEY);
        writer.newLine();
    }

    public void writeData(Map<String, String> map) throws IOException {
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
        map.keySet().stream().sorted(Comparator.naturalOrder())
                .forEach(key -> builder.append(map.get(key) + ","));

        builder.deleteCharAt(builder.length() - 1);
        return builder.toString();
    }

    @Override
    public void close() throws IOException {
        writer.close();
    }
}