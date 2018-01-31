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
package ch.nicosb.eventstreamanalyzer.parser;

import cc.kave.commons.model.events.IIDEEvent;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class NotifyingEventParserTest {

    private NotifyingEventParser parser;
    private static final String RESOURCES_URI = "src/test/resources/folder1/test1.zip";

    @Before
    public void setUp() {
        parser = new NotifyingEventParser(Paths.get(RESOURCES_URI));
    }


    @Test
    public void whenEventIsParsed_notifiesListeners() throws IOException {
        // given
        List<IIDEEvent> parsedEvents = new ArrayList<>();
        EventParsedListener eventListener = parsedEvents::add;
        parser.subscribe(eventListener);

        // when
        parser.parse();

        // then
        assertEquals(4, parsedEvents.size());
    }

    @Test
    public void whenListenerUnsubscribes_listenerIsNotNotified() throws IOException {
        // given
        List<IIDEEvent> events = new ArrayList<>();
        EventParsedListener eventListener = events::add;
        parser.subscribe(eventListener);

        // when
        parser.unsubscribe(eventListener);
        parser.parse();

        // then
        assertEquals(0, events.size());
    }

}