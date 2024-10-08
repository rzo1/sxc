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

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.JAXBIntrospector;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.SchemaOutputResolver;
import jakarta.xml.bind.Unmarshaller;
import jakarta.xml.bind.annotation.XmlRegistry;
import org.glassfish.jaxb.runtime.v2.ContextFactory;
import org.glassfish.jaxb.runtime.v2.model.runtime.RuntimeTypeInfoSet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.logging.Logger;

public class JAXBContextImpl extends JAXBContext {
    private static final Logger logger = Logger.getLogger(JAXBContextImpl.class.getName());

    public static synchronized JAXBContextImpl newSxcInstance(Class... classes) throws JAXBException {
        JAXBContextImpl jaxbContext = createContext(classes, Collections.<String, Object>emptyMap());
        return jaxbContext;
    }

    public static synchronized JAXBContextImpl createContext(Class[] classes, Map<String, ?> properties) throws JAXBException {
        JAXBContextImpl jaxbContext = new JAXBContextImpl(properties, classes);
        return jaxbContext;
    }

    public static JAXBContext newSxcInstance(String contextPath) throws JAXBException {
        JAXBContextImpl jaxbContext = createContext(contextPath, Thread.currentThread().getContextClassLoader(), Collections.<String, Object>emptyMap());
        return jaxbContext;
    }

    public static JAXBContext newSxcInstance(String contextPath, ClassLoader classLoader) throws JAXBException {
        JAXBContextImpl jaxbContext = createContext(contextPath, classLoader, Collections.<String, Object>emptyMap());
        return jaxbContext;
    }

    public static synchronized JAXBContextImpl newSxcInstance(String contextPath, ClassLoader classLoader, Map<String, ?> properties) throws JAXBException {
        JAXBContextImpl jaxbContext = createContext(contextPath, classLoader, properties);
        return jaxbContext;
    }

    public static synchronized JAXBContextImpl createContext(String contextPath, ClassLoader classLoader, Map<String, ?> properties) throws JAXBException {
        Class[] classes = loadPackageClasses(contextPath, classLoader);
        JAXBContextImpl jaxbContext = createContext(classes, properties);
        return jaxbContext;
    }

    private final JAXBIntrospectorImpl introspector = new JAXBIntrospectorImpl();
    private final Callable<JAXBContext> schemaGenerator;

    public JAXBContextImpl(Class... classes) throws JAXBException {
        this(null, classes);
    }
    
    public JAXBContextImpl(final Map<String, ?> properties, final Class... classes) throws JAXBException {
        String generateProperty = properties != null ? (String) properties.get("org.metatype.sxc.generate") : null;
        boolean generate = true;
        if (generateProperty != null) {
            generate = Boolean.parseBoolean(generateProperty);
        }

        // Check if there is a generted marshaller for the specified types
        //
        // It is important that this simple check be performed without
        // checking for annotations on the class, becuase even checking an
        // annotation is present is an expensive operation
        LinkedList<Class> unknownTypes = new LinkedList<Class>();

        for (Class xmlType : classes) {

            // loadJAXBClass isn't smart enough to load all the classes associated with the ObjectFactory
            if (xmlType.getSimpleName().equals("ObjectFactory") && xmlType.isAnnotationPresent(XmlRegistry.class)) {
//                loadJAXBObjectFactory(properties, classes);
                unknownTypes.add(xmlType);
                continue;
            }

            JAXBClass jaxbClass = JAXBIntrospectorImpl.loadJAXBClass(xmlType, null);
            if (jaxbClass != null) {
                introspector.addJAXBClass(jaxbClass);
            } else {
                unknownTypes.add(xmlType);
            }
        }

        if (unknownTypes.isEmpty()) {
            schemaGenerator = new Callable<JAXBContext>() {
                public JAXBContext call() throws Exception {
                    // use the ri to generate the schema
                    //noinspection unchecked
                    JAXBContext context = ContextFactory.createContext(classes, (Map<String, Object>) properties);
                    return context;
                }
            };
        } else if(generate) {
            // generate missing classes
            BuilderContext builder = new BuilderContext(properties, classes);
            schemaGenerator = builder.getSchemaGenerator();
            for (JAXBClass jaxbClass : builder.compile()) {
                introspector.addJAXBClass(jaxbClass);
            }
        } else {
            throw new JAXBException("Generation is disabled but no JaxB parser is available for the classes " + unknownTypes);
        }

        logger.info("Created SXC JAXB Context.");
    }

    // TODO Finish
    private void loadJAXBObjectFactory(Map<String, ?> properties, Class[] classes) throws JAXBException {
        // The obvious optimization is to know that because the ObjectFactory JAXBClass can be loaded
        // it means we've already generated and can directly load the related types from the RuntimeTypeInfoSet
        Map<String, Object> riProperties = new LinkedHashMap<String, Object>(properties);
        for (Iterator<String> iterator = riProperties.keySet().iterator(); iterator.hasNext();) {
            String key =  iterator.next();
            if (key.startsWith("org.metatype")) {
                iterator.remove();
            }

        }

        final org.glassfish.jaxb.runtime.v2.runtime.JAXBContextImpl context = (org.glassfish.jaxb.runtime.v2.runtime.JAXBContextImpl) ContextFactory.createContext(classes, riProperties);
        RuntimeTypeInfoSet runtimeTypeInfoSet = JAXBModelFactory.create(context, classes);
    }

    public Marshaller createMarshaller() throws JAXBException {
        return new MarshallerImpl(introspector);
    }

    public Unmarshaller createUnmarshaller() throws JAXBException {
        return new UnmarshallerImpl(introspector);
    }

    public JAXBIntrospector createJAXBIntrospector() {
        return introspector;
    }

    public void generateSchema(SchemaOutputResolver outputResolver) throws IOException {
        JAXBContext jaxbContext = null;
        try {
            jaxbContext = schemaGenerator.call();
        } catch (Exception e) {
        }

        if (jaxbContext == null) {
            throw new UnsupportedOperationException("Schema generation is not supported");
        }
        jaxbContext.generateSchema(outputResolver);
    }

    public static Class[] loadPackageClasses(String contextPath, ClassLoader classLoader) throws JAXBException {
        Set<Class> classes = new HashSet<Class>();
        for (String pkg : contextPath.split(":")) {
            // look for ObjectFactory and load it
            Class objectFactoryClass = loadObjectFactory(pkg, classLoader);
            if (objectFactoryClass != null) classes.add(objectFactoryClass);

            // look for jaxb.index and load the list of classes
            List<Class> indexedClasses = loadIndexedClasses(pkg, classLoader);
            if (indexedClasses != null) classes.addAll(indexedClasses);

            if (objectFactoryClass == null && indexedClasses == null) {
                throw new JAXBException("Package must contain a jaxb.index file or ObjectFactory class: " + pkg);
            }
        }

        return classes.toArray(new Class[classes.size()]);
    }

    /**
     * Look for jaxb.index file in the specified package and load it's contents
     *
     * @param pkg package name to search
     * @param classLoader class loader used to find the jaxb.index resouce
     * @return a list of JAXB types in the package
     * @throws JAXBException if there are any errors in the index file
     */
    private static List<Class> loadIndexedClasses(String pkg, ClassLoader classLoader) throws JAXBException {
        String resource = pkg.replace('.', '/') + "/jaxb.index";
        InputStream resourceAsStream = classLoader.getResourceAsStream(resource);

        if (resourceAsStream == null) {
            return null;
        }

        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(resourceAsStream, "UTF-8"));
            try {
                ArrayList<Class> classes = new ArrayList<Class>();
                String className = in.readLine();
                while (className != null) {
                    className = className.trim();
                    if (className.startsWith("#") || (className.length() == 0)) {
                        className = in.readLine();
                        continue;
                    }

                    if (className.endsWith(".class")) {
                        throw new JAXBException("Illegal entry: " + className);
                    }

                    try {
                        classes.add(classLoader.loadClass(pkg + '.' + className));
                    } catch (ClassNotFoundException e) {
                        throw new JAXBException("Error loading class: " + className, e);
                    }

                    className = in.readLine();
                }
                return classes;
            } finally {
                in.close();
            }
        } catch (IOException e) {
            throw new JAXBException("Error loading jaxb.index file for " + pkg, e);
        }
    }

    private static Class loadObjectFactory(String pkg, ClassLoader classLoader) throws JAXBException {
        try {
            Class<?> objectFactory = classLoader.loadClass(pkg + ".ObjectFactory");
            return objectFactory;
        } catch (ClassNotFoundException e) {
            // not necessarily an error
            return null;
        }
    }
}
