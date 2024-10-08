/**
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.metatype.sxc.jaxb.mixed;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import jakarta.xml.bind.helpers.DefaultValidationEventHandler;
import org.metatype.sxc.jaxb.JAXBContextImpl;
import org.metatype.sxc.util.XoTestCase;
import org.w3c.dom.Document;

import java.io.ByteArrayOutputStream;
import java.util.List;

public class MixedTest extends XoTestCase {
    public void testMixedElement() throws Exception {
        System.setProperty("org.metatype.sxc.output.directory", "target/tmp-jaxb");

        JAXBContext ctx = JAXBContextImpl.newSxcInstance(MixedElement.class);

        Unmarshaller unmarshaller = ctx.createUnmarshaller();
        unmarshaller.setEventHandler(new DefaultValidationEventHandler());
        MixedElement mixedElement = (MixedElement) unmarshaller.unmarshal(getClass().getResourceAsStream("mixedElement.xml"));

        assertNotNull("mixedElement is null", mixedElement);
        assertNotNull("mixedElement.getElement() is null", mixedElement.content);
        List<Object> content = mixedElement.content;

        assertEquals(5, content.size());

        assertEquals("before", content.get(0));
        assertEquals(new Data("content"), content.get(1));
        assertEquals("middle", content.get(2));
        assertEquals(new Data("content2"), content.get(3));
        assertEquals("after", content.get(4));

        assertEquals(new Data("element"), mixedElement.element);

        Marshaller marshaller = ctx.createMarshaller();
        assertNotNull(marshaller);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        marshaller.marshal(mixedElement, bos);

        Document d = readDocument(bos.toByteArray());
        assertValid("/mixedElement", d);
        assertValid("/mixedElement[text()='before']", d);
        assertValid("/mixedElement/data", d);
        assertValid("/mixedElement/data[text()='content']", d);
        assertValid("/mixedElement/element", d);
        assertValid("/mixedElement/element[text()='element']", d);
    }
}