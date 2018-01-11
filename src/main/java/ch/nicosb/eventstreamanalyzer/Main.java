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
package ch.nicosb.eventstreamanalyzer;

import ch.nicosb.eventstreamanalyzer.stream.interval.Intervalling;
import ch.nicosb.eventstreamanalyzer.weka.Classification;
import ch.nicosb.eventstreamanalyzer.weka.LogisticRegression;

public class Main {

    public static void main(String[] args) {
        if (args.length == 0) {
            printErrorMessage();
        }

        switch (args[0]) {
            case "i":
                intervalizeInput(args);
                break;
            case "c":
                classificateInput(args);
        }
    }

    private static void intervalizeInput(String[] args) {
        Execution intervalling = new Intervalling();
        intervalling.execute(args);
    }

    private static void classificateInput(String[] args) {
        Execution classification = new Classification(new LogisticRegression());
        classification.execute(args);
    }

    private static void printErrorMessage() {
        System.err.println("Must have at least a directory as input");
    }
}
