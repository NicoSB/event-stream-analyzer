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

import ch.nicosb.eventstreamanalyzer.Execution;
import ch.nicosb.eventstreamanalyzer.utils.FileSystemUtils;
import ch.nicosb.eventstreamanalyzer.weka.Filter.FilterFactory;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.meta.ThresholdSelector;
import weka.core.Instances;
import weka.core.converters.ConverterUtils;
import weka.filters.Filter;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class Classification implements Execution {

    static final String SUFFIX_ARFF = ".arff";
    public static final double THRESHOLD = 0.5;
    private Classifier classifier;
    private Filter filter;
    private List<ClassificationResult> results = new ArrayList<>();
    private String outputFile;

    public Classification(ClassifierFactory classifierFactory, FilterFactory filterFactory) {
        classifier = classifierFactory.createClassifier();
        filter = filterFactory.createFilter();
    }

    @Override
    public void execute(String[] args) {
        String directory = args[1];
        this.outputFile = args[2];
        try {
            evaluateArffFilesInDirectory(directory);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void evaluateArffFilesInDirectory(String directory) throws IOException {
        List<Path> arffFiles = FileSystemUtils.getAllFilePathsWithEnding(directory, SUFFIX_ARFF);
        arffFiles.forEach(this::applyClassifier);
        writeCsv();
    }

    private void writeCsv() throws IOException {
        BufferedWriter writer = Files.newBufferedWriter(Paths.get(outputFile));
        results.stream()
            .filter(ClassificationResult::containsPositiveCases)
            .forEach(result -> writeLine(writer, result));
        writer.close();
        System.out.println(outputFile + " was written.");
    }

    private void writeLine(BufferedWriter writer, ClassificationResult result) {
        try {
            writer.write(result.toCsvString());
            writer.newLine();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private void applyClassifier(Path path) {
        try {
            Instances data = getData(path);
            Instances filtered = filterData(data);
            classifier.buildClassifier(filtered);
            System.out.println(classifier.toString());

            Evaluation evaluation = evaluate(filtered);

            addResult(path, evaluation);
        } catch (IllegalArgumentException e) {
            System.out.printf("%s does not contain enough instances.\n", path.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addResult(Path path, Evaluation evaluation) {
        ClassificationResult result = ClassificationResult.fromEvaluation(path.toAbsolutePath().toString(), evaluation);
        results.add(result);
    }

    private Instances getData(Path path) throws Exception {
        ConverterUtils.DataSource dataSource = new ConverterUtils.DataSource(path.toAbsolutePath().toString());
        Instances data = dataSource.getDataSet();
        data.setClassIndex(1);
        return data;
    }

    private Instances filterData(Instances data) {
        try {
            filter.setInputFormat(data);
            Instances filteredData = Filter.useFilter(data, filter);
            filteredData.setClassIndex(1);
            return filteredData;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private Evaluation evaluate(Instances data) throws Exception {
        ThresholdSelector thresholdSelector = buildThresholdSelector(THRESHOLD);

        Evaluation evaluation = new Evaluation(data);
        evaluation.crossValidateModel(thresholdSelector, data, 10, new Random(1));
        return evaluation;
    }

    private ThresholdSelector buildThresholdSelector(double threshold) throws Exception {
        ThresholdSelector thresholdSelector = new ThresholdSelector();
        thresholdSelector.setManualThresholdValue(threshold);
        thresholdSelector.setClassifier(classifier);
        return thresholdSelector;
    }
}
