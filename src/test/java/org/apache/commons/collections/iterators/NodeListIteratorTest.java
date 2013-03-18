/*
 * Copyright 2013 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.collections.iterators;

import java.util.Iterator;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;

/**
 * Tests the NodeListIterator.
 */
public class NodeListIteratorTest extends AbstractIteratorTest<Node> {

    // Node array to be filled with mocked Node instances 
    private Node[] nodes;
    
    // NodeListIterator supports two constructors. This flag allows to
    // control, which constructor to use in makeObject() and makeEmtpyIterator
    private boolean createIteratorWithStandardConstr = true;
    
    /**
     * Constructor 
     * @param testName 
     */
    public NodeListIteratorTest(final String testName) {
        super(testName);
    }
    
    @Override
    protected void setUp() throws Exception {
        super.setUp(); 

        // Default: use standard constr.
        createIteratorWithStandardConstr = true;
        
        
        // create mocked Node Instances and fill Node[] to be used by test cases
        final Node node1 = createMock(Element.class);
        final Node node2 = createMock(Element.class);
        final Node node3 = createMock(Text.class);
        final Node node4 = createMock(Element.class);
        nodes = new Node[] {node1, node2, node3, node4};
        
        replay(node1);
        replay(node2);
        replay(node3);
        replay(node4);
    }

    @Override
    public Iterator<Node> makeEmptyIterator() {
        final NodeList emptyNodeList = new NodeList() {
            public Node item(final int index) {
                throw new IndexOutOfBoundsException();
            }
            public int getLength() {
                return 0;
            }
        };
        
        if (createIteratorWithStandardConstr) {
            return new NodeListIterator(emptyNodeList);
        } else {
            final Node parentNode = createMock(Node.class);
            expect(parentNode.getChildNodes()).andStubReturn(emptyNodeList);
            replay(parentNode);
            
            return new NodeListIterator(parentNode);
        }
    }

    @Override
    public Iterator<Node> makeObject() {
        final NodeList nodeList = new NodeList() {
            public Node item(final int index) {
                return nodes[index];
            }
            public int getLength() {
                return nodes.length;
            }
        };

        return new NodeListIterator(nodeList);
    }

    @Override
    public boolean supportsRemove() {
        return false;
    }
    
    //-----------------------------------------------------------------------
    public void testNullConstructor(){
        try{
            @SuppressWarnings("unused")
            final NodeListIterator iter = new NodeListIterator((Node) null);
            fail("IllegalArgumentException expected!");
        }catch(final IllegalArgumentException e){
            // expected.
        }
    }

    /**
     * tests the convenience Constructor with parameter type org.w3c.Node
     */
    public void testEmptyIteratorWithNodeConstructor(){
        createIteratorWithStandardConstr = false;
        testEmptyIterator();
    }

    /**
     * tests the convenience Constructor with parameter type org.w3c.Node
     */
    public void testFullIteratorWithNodeConstructor(){
        createIteratorWithStandardConstr = false;
        testFullIterator();
    }
}
