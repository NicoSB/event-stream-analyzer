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
package ch.nicosb.eventstreamanalyzer.weka;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class MapToArffConverter {
    private static final String RELATION_KEY = "@RELATION";
    private static final String ATTRIBUTE_KEY = "@ATTRIBUTE";
    private static final String NUMERIC_KEY = "NUMERIC";
    private static final String DATA_KEY = "@DATA";

    private String fileUri;
    private String relation;
    private List<HashMap<String, String>> maps;

    public MapToArffConverter(String relation, String fileUri) {
        this.relation = relation;
        this.fileUri = fileUri;
        this.maps = new ArrayList<>();
    }

    public void writeFile() {
        System.out.printf("Converting %d entries to ARFF file: %s\n", maps.size(), fileUri);
        try (BufferedWriter writer =
                     Files.newBufferedWriter(Paths.get(fileUri))) {
            writeRelation(writer);
            writeAttributes(writer);
            writeData(writer);

            System.out.println("Successfully created file: " + Paths.get(fileUri).toAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeRelation(BufferedWriter writer) throws IOException {
        writer.write(RELATION_KEY + " " + relation);
        writer.newLine();
        writer.newLine();
    }

    private void writeAttributes(BufferedWriter writer) throws IOException {
        Set<String> keys = maps.get(0).keySet();

        for (String key : keys) {
            writeAttribute(key, writer);
        }
    }

    private void writeAttribute(String key, BufferedWriter writer) throws IOException {
        writer.write(ATTRIBUTE_KEY + " " + key + " " + NUMERIC_KEY);
        writer.newLine();
    }

    private void writeData(BufferedWriter writer) throws IOException {
        Set<String> keys = maps.get(0).keySet();
        writer.write(DATA_KEY);
        writer.newLine();
        writer.newLine();

        for (HashMap<String, String> map : maps) {
            writeDataRow(map, keys, writer);
        }
    }

    private void writeDataRow(HashMap<String, String> map, Set<String> keys, BufferedWriter writer) throws IOException {
        for (Iterator<String> it = keys.iterator(); it.hasNext(); ) {
            String key = it.next();
            String value = String.valueOf(map.get(key));
            writer.write(value);

            if (it.hasNext())
                writer.write(",");
        }

        writer.newLine();
    }

    public void add(HashMap<String, String> map) throws IllegalArgumentException {
        validateMap(map);
        maps.add(map);
    }

    private void validateMap(HashMap<String, String> map) throws IllegalArgumentException {
        if (maps.isEmpty())
            return;

        if (!doKeysMatch(maps.get(0), map))
            throw new IllegalArgumentException(createErrorMessage(map));
    }

    private boolean doKeysMatch(HashMap<String, String> map1, HashMap<String, String> map2) {
        return map1.keySet().equals(map2.keySet());
    }

    private String createErrorMessage(HashMap<String, String> map) {
        return String.format("All maps must have the same keys! Expected: %s - Actual: %s",
                maps.get(0).keySet(), map.keySet());
    }
}
