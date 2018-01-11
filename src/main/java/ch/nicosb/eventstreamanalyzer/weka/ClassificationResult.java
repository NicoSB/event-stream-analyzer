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
        return new ClassificationResult(fileUri, evaluation.precision(0), evaluation.recall(0),
                evaluation.precision(1), evaluation.recall(1));
    }

    public String toCsvString() {
        return fileUri + ";" + precisionT + ";" + recallT + ";" + precisionF + ";" + recallF;
    }
}
