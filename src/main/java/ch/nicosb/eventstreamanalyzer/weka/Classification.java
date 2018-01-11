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
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Instances;
import weka.core.converters.ConverterUtils;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Classification implements Execution {

    static final String SUFFIX_ARFF = ".arff";
    private Classifier classifier;
    private List<ClassificationResult> results = new ArrayList<>();
    private String outputFile;

    public Classification(ClassifierFactory classifierFactory) {
        classifier = classifierFactory.createClassifier();
    }

    @Override
    public void execute(String[] args) {
        String directory = args[1];
        this.outputFile = args[2];
        try {
            List<Path> arffFiles = FileSystemUtils.getAllFilePathsWithEnding(directory, SUFFIX_ARFF);
            arffFiles.forEach(this::applyClassifier);
            writeCsv();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeCsv() throws IOException {
        BufferedWriter writer = Files.newBufferedWriter(Paths.get(outputFile));
        results.forEach(result -> writeLine(writer, result));
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
            classifier.buildClassifier(data);

            Evaluation evaluation = evaluate(data);

            addResult(path, evaluation);
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

    private Evaluation evaluate(Instances data) throws Exception {
        Evaluation evaluation = new Evaluation(data);
        evaluation.crossValidateModel(classifier, data, 10, new Random(1));
        return evaluation;
    }
}
