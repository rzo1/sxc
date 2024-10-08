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
package org.metatype.sxc.jaxb;

import com.sun.codemodel.JClass;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import org.metatype.sxc.builder.BuildException;

import javax.xml.namespace.QName;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class JAXBObjectFactoryBuilder {
    private final BuilderContext builderContext;
    private final Class type;
    private final JDefinedClass jaxbObjectFactoryClass;
    private JMethod constructor;
    private JInvocation superInvocation;
    private Set<String> dependencies = new TreeSet<String>();
    private JFieldVar rootElements;

    public JAXBObjectFactoryBuilder(BuilderContext builderContext, Class type) {
        this.builderContext = builderContext;
        this.type = type;

        String className = "" + type.getName() + "$JAXB";

        try {
            jaxbObjectFactoryClass = builderContext.getCodeModel()._class(className);
            jaxbObjectFactoryClass._extends(builderContext.getCodeModel().ref(JAXBObjectFactory.class).narrow(type));
        } catch (JClassAlreadyExistsException e) {
            throw new BuildException(e);
        }

        // INSTANCE variable
        jaxbObjectFactoryClass.field(JMod.PUBLIC | JMod.STATIC | JMod.FINAL, jaxbObjectFactoryClass, "INSTANCE", JExpr._new(jaxbObjectFactoryClass));

        // constructor
        constructor = jaxbObjectFactoryClass.constructor(JMod.PUBLIC);
        superInvocation = constructor.body().invoke("super").arg(JExpr.dotclass(builderContext.toJClass(type)));

        // Map<QName, JAXBObject> rootElements = new HashMap<QName, JAXBObject>();
        JClass qnameJClass = builderContext.toJClass(QName.class);
        JClass extendsJAXBObjectClass = builderContext.toJClass(Class.class).narrow(builderContext.toJClass(JAXBObject.class).wildcard());
        JClass rootElementsType = builderContext.toJClass(Map.class).narrow(qnameJClass, extendsJAXBObjectClass);
        rootElements = jaxbObjectFactoryClass.field(JMod.PRIVATE | JMod.FINAL, rootElementsType, "rootElements");
        rootElements.init(JExpr._new(builderContext.toJClass(HashMap.class).narrow(qnameJClass, extendsJAXBObjectClass)));

        // public Map<QName, JAXBObject>() getRootElements() { return rootElements; }
        JMethod method = jaxbObjectFactoryClass.method(JMod.PUBLIC, rootElementsType, "getRootElements");
        method.body()._return(rootElements);
    }

    private JExpression newQName(QName xmlRootElement) {
        if (xmlRootElement == null) {
            return JExpr._null();
        }
        return JExpr._new(builderContext.toJClass(QName.class))
                .arg(JExpr.lit(xmlRootElement.getNamespaceURI()).invoke("intern"))
                .arg(JExpr.lit(xmlRootElement.getLocalPart()).invoke("intern"));
    }

    public Class getType() {
        return type;
    }

    public JDefinedClass getJAXBObjectFactoryClass() {
        return jaxbObjectFactoryClass;
    }

    public void addDependency(Class type) {
        if (!type.isEnum()) {
            JAXBObjectBuilder objectBuilder = builderContext.getJAXBObjectBuilder(type);
            if (objectBuilder != null) {
                addDependency(objectBuilder.getJAXBObjectClass());
            }
        } else {
            JAXBEnumBuilder enumBuilder = builderContext.getJAXBEnumBuilder(type);
            if (enumBuilder != null) {
                addDependency(enumBuilder.getJAXBEnumClass());
            }
        }
    }

    public void addDependency(JClass dependency) {
        if (jaxbObjectFactoryClass.fullName().equals(dependency.fullName())) return;

        if (dependencies.add(dependency.fullName())) {
            superInvocation.arg(dependency.dotclass());
        }
    }

    public void addRootElement(QName name, Class type) {
        if (!type.isEnum()) {
            JAXBObjectBuilder objectBuilder = builderContext.getJAXBObjectBuilder(type);
            if (objectBuilder != null) {
                constructor.body().invoke(rootElements, "put")
                        .arg(newQName(name))
                        .arg(objectBuilder.getJAXBObjectClass().dotclass());
            } else {
                JAXBObject jaxbObject = StandardJAXBObjects.jaxbObjectByClass.get(type);
                if (jaxbObject != null) {
                    constructor.body().invoke(rootElements, "put")
                            .arg(newQName(name))
                            .arg(builderContext.dotclass(jaxbObject.getClass()));
                }

            }
        } else {
            JAXBEnumBuilder enumBuilder = builderContext.getJAXBEnumBuilder(type);
            if (enumBuilder != null) {
                constructor.body().invoke(rootElements, "put")
                        .arg(newQName(name))
                        .arg(enumBuilder.getJAXBEnumClass().dotclass());
            }
        }
    }
}