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

import weka.classifiers.Evaluation;

public class ClassificationResult {
    public String fileUri;
    public double precisionT;
    public double recallT;
    public double precisionF;
    public double recallF;

    private ClassificationResult(String fileUri, double precisionT, double recallT, double precisionF, double recallF) {
        this.fileUri = fileUri;
        this.precisionT = precisionT;
        this.recallT = recallT;
        this.precisionF = precisionF;
        this.recallF = recallF;
    }

    public static ClassificationResult fromEvaluation(String fileUri, Evaluation evaluation) {
        double precisionT = evaluation.precision(0);
        double recallT = evaluation.recall(0);
        double precisionF = evaluation.precision(1);
        double recallF = evaluation.recall(1);

        precisionT = Double.isNaN(precisionT) ? 0.0 : precisionT;
        recallT = Double.isNaN(recallT) ? 0.0 : recallT;
        precisionF = Double.isNaN(precisionF) ? 0.0 : precisionF;
        recallF = Double.isNaN(recallF) ? 0.0 : recallF;

        if (recallF == 1.0 && precisionF == 1.0)
            System.out.printf("%s does not contain any positive cases!\n", fileUri);

        return new ClassificationResult(fileUri, precisionT, recallT, precisionF, recallF);
    }

    public String toCsvString() {
        return fileUri + ";" + precisionT + ";" + recallT + ";" + precisionF + ";" + recallF;
    }
}
