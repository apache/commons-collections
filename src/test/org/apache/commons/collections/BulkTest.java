/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/test/org/apache/commons/collections/BulkTest.java,v 1.5 2003/10/02 22:14:29 scolebourne Exp $
 * ====================================================================
 *
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
package org.apache.commons.collections;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * A {@link TestCase} that can define both simple and bulk test methods.
 * <p>
 * A <I>simple test method</I> is the type of test traditionally 
 * supplied by by {@link TestCase}.  To define a simple test, create a public 
 * no-argument method whose name starts with "test".  You can specify the
 * the name of simple test in the constructor of <code>BulkTest</code>;
 * a subsequent call to {@link TestCase#run} will run that simple test.
 * <p>
 * A <I>bulk test method</I>, on the other hand, returns a new instance
 * of <code>BulkTest</code>, which can itself define new simple and bulk
 * test methods.  By using the {@link #makeSuite} method, you can 
 * automatically create a hierarchal suite of tests and child bulk tests.
 * <p>
 * For instance, consider the following two classes:
 *
 * <Pre>
 *  public class TestSet extends BulkTest {
 *
 *      private Set set;
 *
 *      public TestSet(Set set) {
 *          this.set = set;
 *      }
 *
 *      public void testContains() {
 *          boolean r = set.contains(set.iterator().next()));
 *          assertTrue("Set should contain first element, r);
 *      }
 *
 *      public void testClear() {
 *          set.clear();
 *          assertTrue("Set should be empty after clear", set.isEmpty());
 *      }
 *  }
 *
 *
 *  public class TestHashMap extends BulkTest {
 *
 *      private Map makeFullMap() {
 *          HashMap result = new HashMap();
 *          result.put("1", "One");
 *          result.put("2", "Two");
 *          return result;
 *      }
 *
 *      public void testClear() {
 *          Map map = makeFullMap();
 *          map.clear();
 *          assertTrue("Map empty after clear", map.isEmpty());
 *      }
 *
 *      public BulkTest bulkTestKeySet() {
 *          return new TestSet(makeFullMap().keySet());
 *      }
 *
 *      public BulkTest bulkTestEntrySet() {
 *          return new TestSet(makeFullMap().entrySet());
 *      }
 *  }
 *  </Pre>
 *
 *  In the above examples, <code>TestSet</code> defines two
 *  simple test methods and no bulk test methods; <code>TestHashMap</code>
 *  defines one simple test method and two bulk test methods.  When
 *  <code>makeSuite(TestHashMap.class).run</code> is executed, 
 *  <I>five</I> simple test methods will be run, in this order:<P>
 *
 *  <Ol>
 *  <Li>TestHashMap.testClear()
 *  <Li>TestHashMap.bulkTestKeySet().testContains();
 *  <Li>TestHashMap.bulkTestKeySet().testClear();
 *  <Li>TestHashMap.bulkTestEntrySet().testContains();
 *  <Li>TestHashMap.bulkTestEntrySet().testClear();
 *  </Ol>
 *
 *  In the graphical junit test runners, the tests would be displayed in
 *  the following tree:<P>
 *
 *  <UL>
 *  <LI>TestHashMap</LI>
 *      <UL>
 *      <LI>testClear
 *      <LI>bulkTestKeySet
 *          <UL>
 *          <LI>testContains
 *          <LI>testClear
 *          </UL>
 *      <LI>bulkTestEntrySet
 *          <UL>
 *          <LI>testContains
 *          <LI>testClear
 *          </UL>
 *      </UL>
 *  </UL>
 *
 *  A subclass can override a superclass's bulk test by
 *  returning <code>null</code> from the bulk test method.  If you only
 *  want to override specific simple tests within a bulk test, use the
 *  {@link #ignoredSimpleTests} method.<P>
 *
 *  Note that if you want to use the bulk test methods, you <I>must</I>
 *  define your <code>suite()</code> method to use {@link #makeSuite}.
 *  The ordinary {@link TestSuite} constructor doesn't know how to 
 *  interpret bulk test methods.
 *
 *  @author Paul Jack
 *  @version $Id: BulkTest.java,v 1.5 2003/10/02 22:14:29 scolebourne Exp $
 */
public class BulkTest extends TestCase implements Cloneable {


    // Note:  BulkTest is Cloneable to make it easier to construct 
    // BulkTest instances for simple test methods that are defined in 
    // anonymous inner classes.  Basically we don't have to worry about
    // finding weird constructors.  (And even if we found them, technically
    // it'd be illegal for anyone but the outer class to invoke them).  
    // Given one BulkTest instance, we can just clone it and reset the 
    // method name for every simple test it defines.  


    /**
     *  The full name of this bulk test instance.  This is the full name
     *  that is compared to {@link #ignoredSimpleTests} to see if this
     *  test should be ignored.  It's also displayed in the text runner
     *  to ease debugging.
     */
    String verboseName;


    /**
     *  Constructs a new <code>BulkTest</code> instance that will run the
     *  specified simple test.
     *
     *  @param name  the name of the simple test method to run
     */
    public BulkTest(String name) {
        super(name);
        this.verboseName = getClass().getName();
    }


    /**
     *  Creates a clone of this <code>BulkTest</code>.<P>
     *
     *  @return  a clone of this <code>BulkTest</code>
     */
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            throw new Error(); // should never happen
        }
    }


    /**
     *  Returns an array of simple test names to ignore.<P>
     *
     *  If a simple test that's defined by this <code>BulkTest</code> or
     *  by one of its bulk test methods has a name that's in the returned
     *  array, then that simple test will not be executed.<P>
     *
     *  A simple test's name is formed by taking the class name of the
     *  root <code>BulkTest</code>, eliminating the package name, then
     *  appending the names of any bulk test methods that were invoked
     *  to get to the simple test, and then appending the simple test
     *  method name.  The method names are delimited by periods:
     *
     *  <Pre>
     *  TestHashMap.bulkTestEntrySet.testClear
     *  </Pre>
     *
     *  is the name of one of the simple tests defined in the sample classes
     *  described above.  If the sample <code>TestHashMap</code> class
     *  included this method:
     *
     *  <Pre>
     *  public String[] ignoredSimpleTests() {
     *      return new String[] { "TestHashMap.bulkTestEntrySet.testClear" };
     *  }
     *  </Pre>
     *
     *  then the entry set's clear method wouldn't be tested, but the key
     *  set's clear method would.
     *
     *  @return an array of the names of simple tests to ignore, or null if
     *   no tests should be ignored
     */
    public String[] ignoredSimpleTests() {
        return null;
    }


    /**
     *  Returns the display name of this <code>BulkTest</code>.
     *
     *  @return the display name of this <code>BulkTest</code>
     */
    public String toString() {
        return getName() + "(" + verboseName + ") ";
    }


    /**
     *  Returns a {@link TestSuite} for testing all of the simple tests
     *  <I>and</I> all the bulk tests defined by the given class.<P>
     *
     *  The class is examined for simple and bulk test methods; any child
     *  bulk tests are also examined recursively; and the results are stored
     *  in a hierarchal {@link TestSuite}.<P>
     *
     *  The given class must be a subclass of <code>BulkTest</code> and must
     *  not be abstract.<P>
     *
     *  @param c  the class to examine for simple and bulk tests
     *  @return  a {@link TestSuite} containing all the simple and bulk tests
     *    defined by that class
     */
    public static TestSuite makeSuite(Class c) {
        if (Modifier.isAbstract(c.getModifiers())) {
            throw new IllegalArgumentException("Class must not be abstract.");
        }
        if (!BulkTest.class.isAssignableFrom(c)) {
            throw new IllegalArgumentException("Class must extend BulkTest.");
        }
        return new BulkTestSuiteMaker(c).make();
    }

}


// It was easier to use a separate class to do all the reflection stuff
// for making the TestSuite instances.  Having permanent state around makes
// it easier to handle the recursion.
class BulkTestSuiteMaker {

    /** The class that defines simple and bulk tests methods. */
    private Class startingClass;

    /** List of ignored simple test names. */
    private List ignored;
   
    /** The TestSuite we're currently populating.  Can change over time. */
    private TestSuite result;

    /** 
     *  The prefix for simple test methods.  Used to check if a test is in 
     *  the ignored list.
     */ 
    private String prefix;

    /** 
     *  Constructor.
     *
     *  @param startingClass  the starting class
     */     
    public BulkTestSuiteMaker(Class startingClass) {
        this.startingClass = startingClass;
    }

    /**
     *  Makes a hierarchal TestSuite based on the starting class.
     *
     *  @return  the hierarchal TestSuite for startingClass
     */
    public TestSuite make() {
         this.result = new TestSuite();
         this.prefix = getBaseName(startingClass);
         result.setName(prefix);

         BulkTest bulk = makeFirstTestCase(startingClass);
         ignored = new ArrayList();
         String[] s = bulk.ignoredSimpleTests();
         if (s != null) {
             ignored.addAll(Arrays.asList(s));
         }
         make(bulk);
         return result;
    }

    /**
     *  Appends all the simple tests and bulk tests defined by the given
     *  instance's class to the current TestSuite.
     *
     *  @param bulk  An instance of the class that defines simple and bulk
     *    tests for us to append
     */
    void make(BulkTest bulk) {
        Class c = bulk.getClass();
        Method[] all = c.getMethods();
        for (int i = 0; i < all.length; i++) {
            if (isTest(all[i])) addTest(bulk, all[i]);
            if (isBulk(all[i])) addBulk(bulk, all[i]);
        }
    }

    /**
     *  Adds the simple test defined by the given method to the TestSuite.
     *
     *  @param bulk  The instance of the class that defined the method
     *   (I know it's weird.  But the point is, we can clone the instance
     *   and not have to worry about constructors.)
     *  @param m  The simple test method
     */
    void addTest(BulkTest bulk, Method m) {
        BulkTest bulk2 = (BulkTest)bulk.clone();
        bulk2.setName(m.getName());
        bulk2.verboseName = prefix + "." + m.getName();
        if (ignored.contains(bulk2.verboseName)) return;
        result.addTest(bulk2);
    }

    /**
     *  Adds a whole new suite of tests that are defined by the result of
     *  the given bulk test method.  In other words, the given bulk test
     *  method is invoked, and the resulting BulkTest instance is examined
     *  for yet more simple and bulk tests.
     *
     *  @param bulk  The instance of the class that defined the method
     *  @param m  The bulk test method
     */
    void addBulk(BulkTest bulk, Method m) {
        BulkTest bulk2;
        try {
            bulk2 = (BulkTest)m.invoke(bulk, null);
            if (bulk2 == null) return;
        } catch (InvocationTargetException e) {
            throw new Error(); // FIXME;
        } catch (IllegalAccessException e) {
            throw new Error(); // FIXME;
        }

        // Save current state on the stack.
        String oldPrefix = prefix;
        TestSuite oldResult = result;

        prefix = prefix + "." + m.getName();
        result = new TestSuite();
        result.setName(m.getName());

        make(bulk2);

        oldResult.addTest(result);

        // Restore the old state
        prefix = oldPrefix;
        result = oldResult;
    }

    /**
     *  Returns the base name of the given class.
     *
     *  @param c  the class
     *  @return the name of that class, minus any package names
     */
    private static String getBaseName(Class c) {
        String name = c.getName();
        int p = name.lastIndexOf('.');
        if (p > 0) {
            name = name.substring(p + 1);
        }
        return name;
    }


    // These three methods are used to create a valid BulkTest instance
    // from a class.

    private static Constructor getTestCaseConstructor(Class c) {
        try {
            return c.getConstructor(new Class[] { String.class });
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException(c + " must provide " +
             "a (String) constructor");
        }
    }

    private static BulkTest makeTestCase(Class c, Method m) {
        Constructor con = getTestCaseConstructor(c);
        try {
            return (BulkTest)con.newInstance(new String[] { m.getName() });
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            throw new RuntimeException(); // FIXME;
        } catch (IllegalAccessException e) {
            throw new Error(); // should never occur
        } catch (InstantiationException e) {
            throw new RuntimeException(); // FIXME;
        }
    }

    private static BulkTest makeFirstTestCase(Class c) {
        Method[] all = c.getMethods();
        for (int i = 0; i < all.length; i++) {
            if (isTest(all[i])) return makeTestCase(c, all[i]);
        }
        throw new IllegalArgumentException(c.getName() + " must provide " 
          + " at least one test method.");
    }

    /**
     *  Returns true if the given method is a simple test method.
     */
    private static boolean isTest(Method m) {
        if (!m.getName().startsWith("test")) return false;
        if (m.getReturnType() != Void.TYPE) return false;
        if (m.getParameterTypes().length != 0) return false;
        int mods = m.getModifiers();
        if (Modifier.isStatic(mods)) return false;
        if (Modifier.isAbstract(mods)) return false;
        return true;
    }

    /**
     *  Returns true if the given method is a bulk test method.
     */
    private static boolean isBulk(Method m) {
        if (!m.getName().startsWith("bulkTest")) return false;
        if (m.getReturnType() != BulkTest.class) return false;
        if (m.getParameterTypes().length != 0) return false;
        int mods = m.getModifiers();
        if (Modifier.isStatic(mods)) return false;
        if (Modifier.isAbstract(mods)) return false;
        return true;
    }

}
