/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/java/org/apache/commons/collections/Attic/ClassMap.java,v 1.1 2002/10/23 03:35:23 bayard Exp $
 * $Revision: 1.1 $
 * $Date: 2002/10/23 03:35:23 $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1999-2002 The Apache Software Foundation.  All rights
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

import java.util.Map;
import java.util.HashMap;

/**
 * A map which stores objects by a key Class.
 * When obtaining the object, it will check inheritence and 
 * interface trees to see if the Class matches.
  *
  * @author <a href="mailto:bayard@apache.org">Henri Yandell</a>
 */
public class ClassMap extends ProxyMap {

    /**
     * Create a ClassMap around a passed in Map.
     */
    static public Map wrap(Map m) {
        return new ClassMap(m);
    }

    /**
     * Construct a ClassMap using a default internal Map of a 
     * HashMap.
     */
    public ClassMap() {
        this(new HashMap());
    }

    private ClassMap(Map m) {
        super(m);
    }

    /**
     * Get the object from the map. If the key is not 
     * a Class object, then it uses the Class of the object.
     * Inheritence is used to decide which value to return, so 
     * if a value is not in the map for the Class of the passed 
     * in key, then it checks the inheritence tree of the Class 
     * by first checking the interface tree and then checking 
     * the superclass.
     */
    public Object get(Object key) {
        if(key == null) {
            return null;
        }
        Class clss = null;

        if(key instanceof Class) {
            clss = (Class)key;
        } else {
            clss = key.getClass();
        }

        Object obj = super.get(clss);

        if(obj == null) {
            
            // if this is null, let's go up the inheritence tree
            obj = getInterfaces(clss);

            if(obj == null) {
                obj = getSuperclass(clss);
            }
        }

        return obj;
    }

    private Object getInterfaces(Class clss) {
        if(clss == null) {
            return null;
        }
        Object obj = null;
        Class[] interfaces = clss.getInterfaces();
        for(int i=0; i<interfaces.length; i++) {
            obj = (Object)super.get(interfaces[i]);
            if(obj != null) {
                return obj; 
            }
            obj = getInterfaces(interfaces[i]);
            if(obj != null) {
                return obj; 
            }
            obj = getSuperclass(interfaces[i]);
            if(obj != null) {
                return obj; 
            }
        }
        return null;
    }

    private Object getSuperclass(Class clss) {
        if(clss == null) {
            return null;
        }
        Object obj = null;
        Class superclass = clss.getSuperclass();
        obj = (Object)super.get(superclass);
        if(obj != null) {
            return obj; 
        }
        obj = getInterfaces(superclass);
        if(obj != null) {
            return obj; 
        }
        obj = getSuperclass(superclass);
        if(obj != null) {
            return obj; 
        }
        return null;
    }

}
