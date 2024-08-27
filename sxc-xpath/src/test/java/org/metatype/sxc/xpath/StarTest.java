package org.metatype.sxc.xpath;

import junit.framework.TestCase;

import javax.xml.stream.XMLStreamException;

public class StarTest extends TestCase {
    boolean match;
    
    public void testNonMatchedText() throws Exception {

        XPathEventHandler eventHandler = new XPathEventHandler() {

            public void onMatch(XPathEvent event) throws XMLStreamException {
                match = true;
            }
            
        };
        
        XPathBuilder builder = new XPathBuilder();
        builder.listen("/*/global", eventHandler);
        
        XPathEvaluator evaluator = builder.compile();
        evaluator.evaluate(getClass().getResourceAsStream("global.xml"));
        
        assertTrue(match);
    }
}
