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
package org.metatype.sxc.jaxb.fields;

import jakarta.xml.bind.annotation.XmlAccessOrder;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorOrder;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import junit.framework.Assert;

@XmlType
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlAccessorOrder(XmlAccessOrder.ALPHABETICAL)
@SuppressWarnings({"UnusedDeclaration"})
public class Fields {
    @XmlElement(name = "public-field")
    public String publicField;
    @XmlElement(name = "package-field")
    String packageField;
    @XmlElement(name = "protected-field")
    protected String protectedField;
    @XmlElement(name = "private-field")
    private String privateField;

    private boolean booleanField;
    private byte byteField;
    private short shortField;
    private int intField;
    private long longField;
    private float floatField;
    private double doubleField;

    public void assertPublicField(String expected) {
        Assert.assertEquals(expected, publicField);
    }

    public void assertPackageField(String expected) {
        Assert.assertEquals(expected, packageField);
    }

    public void assertProtectedField(String expected) {
        Assert.assertEquals(expected, protectedField);
    }

    public void assertPrivateField(String expected) {
        Assert.assertEquals(expected, privateField);
    }

    public void assertBooleanField(boolean expected) {
        Assert.assertEquals(expected, booleanField);
    }

    public void assertByteField(byte expected) {
        Assert.assertEquals(expected, byteField);
    }

    public void assertShortField(short expected) {
        Assert.assertEquals(expected, shortField);
    }

    public void assertIntField(int expected) {
        Assert.assertEquals(expected, intField);
    }

    public void assertLongField(long expected) {
        Assert.assertEquals(expected, longField);
    }

    public void assertFloatField(float expected) {
        Assert.assertEquals(expected, floatField);
    }

    public void assertDoubleField(double expected) {
        Assert.assertEquals(expected, doubleField);
    }
}
