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
package ch.nicosb.eventstreamanalyzer.utils;

import ch.nicosb.eventstreamanalyzer.stream.util.StatusProvider;

import java.util.ArrayList;
import java.util.List;

public class PeriodicLogger {

    private List<StatusProvider> providers = new ArrayList<>();
    private int seconds;
    private boolean stopped = false;

    public PeriodicLogger(int seconds) {
        this.seconds = seconds;
    }

    public void registerProvider(StatusProvider provider) {
        if (providers.isEmpty())
            new Thread(this::start).start();

        providers.add(provider);
    }

    public void stop() {
        stopped = true;
    }

    private void start() {
        if (!stopped) {
            delayLog();
            start();
        }
    }

    private void delayLog() {
        try {
            Thread.sleep(seconds * 1000);
            printCollectedLogs();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void printCollectedLogs() {
        StringBuilder builder = new StringBuilder();

        providers.forEach(provider -> builder.append(provider.getStatus()).append("\n"));

        System.out.println(builder.toString());
    }
}
