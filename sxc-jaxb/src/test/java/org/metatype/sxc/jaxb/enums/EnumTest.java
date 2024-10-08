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
package org.metatype.sxc.jaxb.enums;

import jakarta.xml.bind.Marshaller;
import org.metatype.sxc.jaxb.JAXBContextImpl;
import org.metatype.sxc.util.XoTestCase;
import org.w3c.dom.Document;

import java.io.ByteArrayOutputStream;

public class EnumTest extends XoTestCase {
    public void testEnum() throws Exception {
        System.setProperty("org.metatype.sxc.output.directory", "target/tmp-jaxb");
        JAXBContextImpl ctx = new JAXBContextImpl(Enums.class);

        Enums enums = (Enums) ctx.createUnmarshaller().unmarshal(getClass().getResourceAsStream("enum.xml"));

        assertNotNull(enums);

        assertEquals(AnnotatedEnum.TWO, enums.getAnnotatedEnumAttribute());
        assertEquals(NotAnnotatedEnum.TWO, enums.getNotAnnotatedEnumAttribute());
        assertEquals(GeneratedEnum.SILVER, enums.getGeneratedEnumAttribute());

        assertEquals(AnnotatedEnum.TWO, enums.getAnnotatedEnum());
        assertEquals(NotAnnotatedEnum.TWO, enums.getNotAnnotatedEnum());
        assertEquals(GeneratedEnum.SILVER, enums.getGeneratedEnum());

        Marshaller marshaller = ctx.createMarshaller();
        assertNotNull(marshaller);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        marshaller.marshal(enums, bos);

        Document d = readDocument(bos.toByteArray());
        assertValid("/enums[@annotatedEnumAttribute='dos']", d);
        assertValid("/enums[@notAnnotatedEnumAttribute='TWO']", d);
        assertValid("/enums[@generatedEnumAttribute='Silver']", d);

        assertValid("/enums/annotatedEnum[text()='dos']", d);
        assertValid("/enums/notAnnotatedEnum[text()='TWO']", d);
        assertValid("/enums/generatedEnum[text()='Silver']", d);
    }
}
