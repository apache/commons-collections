/* 
 * $Id: TestComparatorChain.java,v 1.6 2003/08/31 17:28:46 scolebourne Exp $
 * ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001-2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgement may appear in the software itself,
 *    if and wherever such third-party acknowledgements normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *    "Apache Commons" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Apache Turbine", nor may "Apache" appear in their name, without
 *    prior written permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

package org.apache.commons.collections.comparators;

import java.io.Serializable;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;

public class TestComparatorChain extends TestComparator {

    public TestComparatorChain(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(TestComparatorChain.class);
    }

    public Comparator makeComparator() {
        ComparatorChain chain = new ComparatorChain(new ColumnComparator(0));
        chain.addComparator(new ColumnComparator(1),true); // reverse the second column
        chain.addComparator(new ColumnComparator(2),false);
        return chain;
    }

    public void testNoopComparatorChain() {
        ComparatorChain chain = new ComparatorChain();
        Integer i1 = new Integer(4);
        Integer i2 = new Integer(6);
        chain.addComparator(new ComparableComparator());

        int correctValue = i1.compareTo(i2);
        assertTrue("Comparison returns the right order",chain.compare(i1,i2) == correctValue);
    }

    public void testBadNoopComparatorChain() {
        ComparatorChain chain = new ComparatorChain();
        Integer i1 = new Integer(4);
        Integer i2 = new Integer(6);
        try {
            chain.compare(i1,i2);
            fail("An exception should be thrown when a chain contains zero comparators.");
        } catch (UnsupportedOperationException e) {

        }
    }

    public void testListComparatorChain() {
        List list = new LinkedList();
        list.add(new ComparableComparator());
        ComparatorChain chain = new ComparatorChain(list);
        Integer i1 = new Integer(4);
        Integer i2 = new Integer(6);

        int correctValue = i1.compareTo(i2);
        assertTrue("Comparison returns the right order",chain.compare(i1,i2) == correctValue);
    }

    public void testBadListComparatorChain() {
        List list = new LinkedList();
        ComparatorChain chain = new ComparatorChain(list);
        Integer i1 = new Integer(4);
        Integer i2 = new Integer(6);
        try {
            chain.compare(i1,i2);
            fail("An exception should be thrown when a chain contains zero comparators.");
        } catch (UnsupportedOperationException e) {

        }
    }


    public void testComparatorChainOnMinvaluedCompatator() {
        // -1 * Integer.MIN_VALUE is less than 0,
        // test that ComparatorChain handles this edge case correctly
        ComparatorChain chain = new ComparatorChain();
        chain.addComparator(
            new Comparator() {
                public int compare(Object a, Object b) {
                    int result = ((Comparable)a).compareTo(b);
                    if(result < 0) {
                        return Integer.MIN_VALUE;
                    } else if(result > 0) {
                        return Integer.MAX_VALUE;
                    } else {
                        return 0;
                    }
                }
            }, true);

        assertTrue(chain.compare(new Integer(4), new Integer(5)) > 0);            
        assertTrue(chain.compare(new Integer(5), new Integer(4)) < 0);            
        assertTrue(chain.compare(new Integer(4), new Integer(4)) == 0);            
    }

    public List getComparableObjectsOrdered() {
        List list = new LinkedList();
        // this is the correct order assuming a
        // "0th forward, 1st reverse, 2nd forward" sort
        list.add(new PseudoRow(1,2,3));
        list.add(new PseudoRow(2,3,5));
        list.add(new PseudoRow(2,2,4));
        list.add(new PseudoRow(2,2,8));
        list.add(new PseudoRow(3,1,0));
        list.add(new PseudoRow(4,4,4));
        list.add(new PseudoRow(4,4,7));
        return list;
    }

    public static class PseudoRow implements Serializable {

        public int cols[] = new int[3];

        public PseudoRow(int col1, int col2, int col3) {
            cols[0] = col1;
            cols[1] = col2;
            cols[2] = col3;
        }

        public int getColumn(int colIndex) {
            return cols[colIndex];
        }

        public String toString() {
            StringBuffer buf = new StringBuffer();
            buf.append("[");
            buf.append(cols[0]);
            buf.append(",");
            buf.append(cols[1]);
            buf.append(",");
            buf.append(cols[2]);
            buf.append("]");
            return buf.toString();
        }

        public boolean equals(Object o) {
            if (!(o instanceof PseudoRow)) {
                return false;
            }

            PseudoRow row = (PseudoRow) o;
            if (getColumn(0) != row.getColumn(0)) {
                return false;
            }

            if (getColumn(1) != row.getColumn(1)) {
                return false;
            }            
            
            if (getColumn(2) != row.getColumn(2)) {
                return false;
            }

            return true;
        }

    }

    public static class ColumnComparator implements Comparator,Serializable {

        protected int colIndex = 0;

        public ColumnComparator(int colIndex) {
            this.colIndex = colIndex;
        }

        public int compare(Object o1, Object o2) {

            int col1 = ( (PseudoRow) o1).getColumn(colIndex);
            int col2 = ( (PseudoRow) o2).getColumn(colIndex);

            if (col1 > col2) {
                return 1;
            } else if (col1 < col2) {
                return -1;
            }

            return 0;
        }
        
        public int hashCode() {
            return colIndex;
        }
        
        public boolean equals(Object that) {
            if(that instanceof ColumnComparator) {
                return colIndex == ((ColumnComparator)that).colIndex;
            } else {
                return false;
            }
        }
        
        private static final long serialVersionUID = -2284880866328872105L;
    }
}
