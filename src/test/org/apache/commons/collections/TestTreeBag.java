/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/test/org/apache/commons/collections/TestTreeBag.java,v 1.5 2003/10/02 22:35:31 scolebourne Exp $
 * $Revision: 1.5 $
 * $Date: 2003/10/02 22:35:31 $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1999-2001 The Apache Software Foundation.  All rights
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
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowledgement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgement may appear in the software itself,
 *    if and wherever such third-party acknowledgements normally appear.
 *
 * 4. The names "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Group.
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
 *
 */
package org.apache.commons.collections;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Extension of {@link TestBag} for exercising the {@link TreeBag}
 * implementation.
 *
 * @author Chuck Burdick
 * @version $Id: TestTreeBag.java,v 1.5 2003/10/02 22:35:31 scolebourne Exp $
 */
public class TestTreeBag extends AbstractTestBag {
    
   public TestTreeBag(String testName) {
      super(testName);
   }

   public static Test suite() {
      return new TestSuite(TestTreeBag.class);
   }

   public static void main(String args[]) {
      String[] testCaseName = { TestTreeBag.class.getName() };
      junit.textui.TestRunner.main(testCaseName);
   }

   protected Bag makeBag() {
      return new TreeBag();
   }

   public SortedBag setupBag() {
      SortedBag bag = (SortedBag)makeBag();
      bag.add("C");
      bag.add("A");
      bag.add("B");
      bag.add("D");
      return bag;
   }

   public void testOrdering() {
      Bag bag = setupBag();
      assertEquals("Should get elements in correct order",
                   "A", bag.toArray()[0]);
      assertEquals("Should get elements in correct order",
                   "B", bag.toArray()[1]);
      assertEquals("Should get elements in correct order",
                   "C", bag.toArray()[2]);
      assertEquals("Should get first key",
                   "A", ((SortedBag)bag).first());
      assertEquals("Should get last key",
                   "D", ((SortedBag)bag).last());
   }
}
