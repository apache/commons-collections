/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/java/org/apache/commons/collections/FactoryUtils.java,v 1.1 2002/05/29 02:57:41 arron Exp $
 * $Revision: 1.1 $
 * $Date: 2002/05/29 02:57:41 $
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
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "The Jakarta Project", "Struts", and "Apache Software
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

import java.util.*;
import java.lang.reflect.*;

/**
 * A Factory for the creation of Factories. This is more of the factory
 * "pattern" by definition, but what it creates, is objects which subscribe to
 * a factory interface which other systems can rely on them to manufacture
 * objects.
 *
 * @author Arron Bates
 * @version $Revision: 1.1 $
 */
public class FactoryUtils {
  
  /** Creates a SimpleObjectFactory whith a class definition, which will be
   * used to create a new object from an empty constructor.
   *
   * @param inClass class definition which will be ued to create the new object
   * @return the simple object factory.
   */
  public static SimpleObjectFactory createStandardFactory(Class inClass) {
    return new StandardFactory(inClass);
  }
  
  /** Creates a SimpleObjectFactory whith the class definition and argument
   * details, which can create a new object from a constructor which requires
   * arguments.
   *
   * @param inClass class definition which will be ued to create the new object
   * @param argTypes argument class types for the constructor
   * @param argObjects the objects for the arguments themselves
   * @return the simple object factory.
   */
  public static SimpleObjectFactory createStandardFactory(Class inClass,
                                                          Class[] argTypes,
                                                          Object[] argObjects) {
    return new StandardFactory(inClass, argTypes, argObjects);
  }
  
  
  
  /* A simple factory, which takes the bare bones of object creation to do just
   * that, create new objects.
   */
  private static class StandardFactory implements SimpleObjectFactory {
    
    /* builds the object factory. The class definition can creat objects which
     * have no-argument constructors.
     */
    public StandardFactory(Class inClass) {
      this.classDefinition = inClass;
    }
    
    /* builds the object factory taking all the options needed to provide
     * arguments to a constructor.
     */
    public StandardFactory(Class inClass, Class[] argTypes, Object[] argObjects) {
      this(inClass);
      this.argTypes = argTypes;
      this.argObjects = argObjects;
    }
  
  
  
    /* This method is the beast that creates the new objects. Problem faced is that
     * the Exceptions thrown are all RuntimeExceptions, meaning that for this class
     * to be used as a java.util.Map implementation itself, it has to guide the
     * exceptions as the runtime excpetions commonly thrown by these objects.
     *
     * Thinly disguising the error as a null pointer, with a modified message for
     * debugging.
     */
    public Object createObject() {
      
      Object obj = null;
      /* for catching error specifics */
      String fubar = null;
      
      try {
        if ((argTypes == null) || (argObjects == null)) {
          /* no arguments, make object with empty constructor */
          obj = this.classDefinition.newInstance();
        } else {
          /* construct object with argument details */
          Constructor constructor = this.classDefinition.getConstructor(argTypes);
          obj = constructor.newInstance(argObjects);
        }
      } catch (InstantiationException ex) {
        fubar = ex.getMessage();
      } catch (IllegalAccessException ex) {
        fubar = ex.getMessage();
      } catch (IllegalArgumentException ex) {
        fubar = ex.getMessage();
      } catch (NoSuchMethodException ex) {
        fubar = ex.getMessage();
      } catch (InvocationTargetException ex) {
        fubar = ex.getMessage();
      }
      
      /* fake our Exception if required */
      if (fubar != null) {
        /* guise the error as a more typical error */
  	    throw new NullPointerException("Failed object creation :: "+ fubar +"\n");
      }
      
      return obj;
    }
  
    /* class definition for new object creation */
    private Class classDefinition;
  
    /* construcor details, optional */
    private Class[] argTypes;
    private Object[] argObjects;
  }
}