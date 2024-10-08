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
package org.metatype.sxc.jaxb.idref;

import jakarta.xml.bind.annotation.XmlAccessOrder;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorOrder;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlID;
import jakarta.xml.bind.annotation.XmlIDREF;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

import java.util.Set;

@XmlType
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlAccessorOrder(XmlAccessOrder.ALPHABETICAL)
@SuppressWarnings({"UnusedDeclaration"})
public class IdRefNode {
    @XmlID
    @XmlAttribute
    private String id;

    @XmlIDREF
    @XmlAttribute
    private IdRefNode self;

    @XmlIDREF
    @XmlAttribute
    private IdRefNode next;

    @XmlIDREF
    private Set<IdRefNode> all;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public IdRefNode getSelf() {
        return self;
    }

    public void setSelf(IdRefNode self) {
        this.self = self;
    }

    public IdRefNode getNext() {
        return next;
    }

    public void setNext(IdRefNode next) {
        this.next = next;
    }

    public Set<IdRefNode> getAll() {
        return all;
    }

    public String toString() {
        return id;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        IdRefNode idRefNode = (IdRefNode) o;

        return id.equals(idRefNode.id);
    }

    public int hashCode() {
        return id.hashCode();
    }
}
