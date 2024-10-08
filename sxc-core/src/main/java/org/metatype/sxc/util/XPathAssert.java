/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.metatype.sxc.util;

import junit.framework.Assert;
import junit.framework.AssertionFailedError;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.namespace.NamespaceContext;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * XPath test assertions.
 * 
 * @author <a href="mailto:dan@metatype.org">Dan Diephouse</a>
 */
public final class XPathAssert {
    private XPathAssert() {
    }

    /**
     * Assert that the following XPath query selects one or more nodes.
     * 
     * @param xpath
     */
    public static NodeList assertValid(String xpath, Node node, Map<String, String> namespaces)
        throws Exception {
        if (node == null) {
            throw new NullPointerException("Node cannot be null.");
        }

        NodeList nodes = (NodeList)createXPath(namespaces).evaluate(xpath, node, XPathConstants.NODESET);

        if (nodes.getLength() == 0) {
            throw new AssertionFailedError("Failed to select any nodes for expression:\n" + xpath
                                           + " from document:\n" + writeNodeToString(node));
        }

        return nodes;
    }

    private static String writeNodeToString(Node node) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            writeXml(node, bos);
        } catch (TransformerException e) {
            throw new RuntimeException(e);
        }
        return bos.toString();
    }

    /**
     * Assert that the following XPath query selects no nodes.
     * 
     * @param xpath
     */
    public static NodeList assertInvalid(String xpath, Node node, Map<String, String> namespaces)
        throws Exception {
        if (node == null) {
            throw new NullPointerException("Node cannot be null.");
        }

        NodeList nodes = (NodeList)createXPath(namespaces).evaluate(xpath, node, XPathConstants.NODESET);

        if (nodes.getLength() > 0) {
            String value = writeNodeToString(node);

            throw new AssertionFailedError("Found multiple nodes for expression:\n" + xpath + "\n" + value);
        }

        return nodes;
    }

    /**
     * Asser that the text of the xpath node retrieved is equal to the value
     * specified.
     * 
     * @param xpath
     * @param value
     * @param node
     */
    public static void assertXPathEquals(String xpath, 
                                         String value, 
                                         Node node, 
                                         Map<String, String> namespaces)
        throws Exception {
        Node result = (Node)createXPath(namespaces).evaluate(xpath, node, XPathConstants.NODE);
        if (result == null) {
            throw new AssertionFailedError("No nodes were found for expression: " + xpath);
        }

        String value2 = getContent(result).trim();

        Assert.assertEquals(value, value2);
    }

    public static void assertNoFault(Node node) throws Exception {
        Map<String, String> namespaces = new HashMap<String, String>();
        namespaces.put("s", "http://schemas.xmlsoap.org/soap/envelope/");
        namespaces.put("s12", "http://www.w3.org/2003/05/soap-envelope");

        assertInvalid("/s:Envelope/s:Body/s:Fault", node, namespaces);
        assertInvalid("/s12:Envelope/s12:Body/s12:Fault", node, namespaces);
    }

    public static void assertFault(Node node) throws Exception {
        Map<String, String> namespaces = new HashMap<String, String>();
        namespaces.put("s", "http://schemas.xmlsoap.org/soap/envelope/");
        namespaces.put("s12", "http://www.w3.org/2003/05/soap-envelope");

        assertValid("/s:Envelope/s:Body/s:Fault", node, namespaces);
        assertValid("/s12:Envelope/s12:Body/s12:Fault", node, namespaces);
    }

    /**
     * Create the specified XPath expression with the namespaces added via
     * addNamespace().
     */
    public static XPath createXPath(Map<String, String> namespaces) throws Exception {
        XPath xpath = XPathFactory.newInstance().newXPath();

        if (namespaces != null) {
            xpath.setNamespaceContext(new MapNamespaceContext(namespaces));
        }

        return xpath;
    }

    public static void writeXml(Node n, OutputStream os) throws TransformerException {
        TransformerFactory tf = TransformerFactory.newInstance();
        // identity
        Transformer t = tf.newTransformer();
        t.setOutputProperty(OutputKeys.INDENT, "yes");
        t.transform(new DOMSource(n), new StreamResult(os));
    }
    
    /**
     * Get the trimed text content of a node or null if there is no text
     */
    public static String getContent(Node n) {
        if (n == null) {
            return null;
        }
        
        Node n1 = getChild(n, Node.TEXT_NODE);

        if (n1 == null) {
            return null;
        }
        
        return n1.getNodeValue().trim();
    }


    /**
     * Get the first direct child with a given type
     */
    public static Node getChild(Node parent, int type) {
        Node n = parent.getFirstChild();
        while (n != null && type != n.getNodeType()) {
            n = n.getNextSibling();
        }
        if (n == null) {
            return null;
        }
        return n;
    }
    
    static class MapNamespaceContext implements NamespaceContext {
        private Map<String, String> namespaces;

        public MapNamespaceContext(Map<String, String> namespaces) {
            super();
            this.namespaces = namespaces;
        }

        public String getNamespaceURI(String prefix) {
            return namespaces.get(prefix);
        }

        public String getPrefix(String namespaceURI) {
            for (Map.Entry<String, String> e : namespaces.entrySet()) {
                if (e.getValue().equals(namespaceURI)) {
                    return e.getKey();
                }
            }
            return null;
        }

        public Iterator getPrefixes(String namespaceURI) {
            return null;
        }

    }
}
