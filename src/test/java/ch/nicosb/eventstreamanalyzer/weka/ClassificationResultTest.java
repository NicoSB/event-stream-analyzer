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

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import weka.classifiers.Evaluation;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ClassificationResultTest {

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private
    Evaluation evaluation;

    @Test
    @Ignore
    public void whenFromEvaluation_buildsCorrectClassificationResult() {
        // given
        double precisionT = 1.0;
        double precisionF = 0.9;
        double recallT = 0.8;
        double recallF = 0.7;

        when(evaluation.precision(0)).thenReturn(precisionT);
        when(evaluation.precision(1)).thenReturn(precisionF);
        when(evaluation.recall(0)).thenReturn(recallT);
        when(evaluation.recall(1)).thenReturn(recallF);
        when(evaluation.numInstances()).thenReturn(1.0);

        String fileUri = "test";

        // when
        ClassificationResult result = ClassificationResult.fromEvaluation(fileUri, evaluation);

        // then
        assertEquals(fileUri, result.fileUri);
        assertEquals(precisionT, result.precisionT, 0);
        assertEquals(precisionF, result.precisionF, 0);
        assertEquals(recallT, result.recallT, 0);
        assertEquals(recallF, result.recallF, 0);
    }

    @Test
    @Ignore
    public void convertsCorrectlyToCsvString() {
        // given
        double precisionT = 1.0;
        double precisionF = 0.9;
        double recallT = 0.8;
        double recallF = 0.7;
        double truePositive = 1;
        double trueNegative = 1;
        double total = 2;
        double accuracy =  (truePositive + trueNegative) / total;

        when(evaluation.precision(0)).thenReturn(precisionT);
        when(evaluation.precision(1)).thenReturn(precisionF);
        when(evaluation.recall(0)).thenReturn(recallT);
        when(evaluation.recall(1)).thenReturn(recallF);
        when(evaluation.numInstances()).thenReturn(total);

        String fileUri = "test";
        String expected = fileUri + ";" + precisionT + ";" + recallT + ";" + precisionF + ";" + recallF + ";" + accuracy;

        // when
        ClassificationResult result = ClassificationResult.fromEvaluation(fileUri, evaluation);
        String actual = result.toCsvString();

        // then
        assertEquals(expected, actual);
    }

}