/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/test/org/apache/commons/collections/primitives/Attic/TestAbstractIntArrayList.java,v 1.7 2003/04/09 06:44:34 rwaldhoff Exp $
 * ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2002-2003 The Apache Software Foundation.  All rights
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
 *    any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Software Foundation.
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

package org.apache.commons.collections.primitives;

import java.util.List;

import org.apache.commons.collections.TestList;

/**
 * @version $Revision: 1.7 $ $Date: 2003/04/09 06:44:34 $
 * @author Rodney Waldhoff
 * @deprecated as the tested classes are deprecated also
 */
public abstract class TestAbstractIntArrayList extends TestList {

    //------------------------------------------------------------ Conventional

    public TestAbstractIntArrayList(String testName) {
        super(testName);
    }

    //---------------------------------------------------------------- Abstract

    abstract protected AbstractIntList createList();

    //------------------------------------------------------- TestList interface

    public List makeEmptyList() {
        return createList();
    }

    //------------------------------------------------------------------- Tests

    public void testAddGet() {
        AbstractIntList list = createList();
        for(int i=0;i<1000;i++) {
            list.addInt(i);
        }
        for(int i=0;i<1000;i++) {
            assertEquals(i,list.getInt(i));
        }
    }

    public void testAddGetLargeValues() {
        AbstractIntList list = createList();
        for(int i=0;i<1000;i++) {
            int value = ((int)(Short.MAX_VALUE));
            value += i;
            list.addInt(value);
        }
        for(int i=0;i<1000;i++) {
            int value = ((int)(Short.MAX_VALUE));
            value += i;
            assertEquals(value,list.getInt(i));
        }
    }

   public void testAddAndShift() {
      AbstractIntList list = createList();
      list.addInt(0, 1);
      assertEquals("Should have one entry", 1, list.size());
      list.addInt(3);
      list.addInt(4);
      list.addInt(1, 2);
      for (int i = 0; i < 4; i++) {
         assertEquals("Should get entry back", i + 1, list.getInt(i));
      }
      list.addInt(0, 0);
      for (int i = 0; i < 5; i++) {
         assertEquals("Should get entry back", i, list.getInt(i));
      }
   }


   /**
    *  Returns small Integer objects for testing.
    */
   protected Object[] getFullElements() {
       Integer[] result = new Integer[19];
       for (int i = 0; i < result.length; i++) {
           result[i] = new Integer(i + 19);
       }
       return result;
   }


   /**
    *  Returns small Integer objects for testing.
    */
   protected Object[] getOtherElements() {
       Integer[] result = new Integer[16];
       for (int i = 0; i < result.length; i++) {
           result[i] = new Integer(i + 48);
       }
       return result;
   }

   public void testCanonicalEmptyCollectionExists() {
   }


   public void testCanonicalFullCollectionExists() {
   }

   public void testEmptyListCompatibility() {
   }

   public void testFullListCompatibility() {
   }

   public void testCollectionIteratorFailFast() {
   }

   public void testListSubListFailFastOnAdd() {
   }

   public void testListSubListFailFastOnRemove() {
   }

}

