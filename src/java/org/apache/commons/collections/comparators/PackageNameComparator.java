package org.apache.commons.collections.comparators;

/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001 The Apache Software Foundation.  All rights
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
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
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

import java.util.Comparator;

/**
 * Sorts java package names.
 * Packages are grouped into java, javax, and other.
 * Inside each one they are alphabetical.
 *
 * @author bayard@generationjava.com
 * @version $Id: PackageNameComparator.java,v 1.1 2002/02/26 22:42:31 morgand Exp $
 */
public class PackageNameComparator implements Comparator {

    static private int JAVA = 1;
    static private int JAVAX = 2;
    static private int OTHER = 3;

    public int compare(Object obj1, Object obj2) {
        if( (obj1 instanceof String) && (obj2 instanceof String) ) {
            String str1 = (String)obj1;
            String str2 = (String)obj2;
            int type1 = getType(str1);
            int type2 = getType(str2);
            
            if(type1 == JAVA) {
                if(type2 == JAVA) {
                    str1 = str1.substring(4);
                    str2 = str2.substring(4);
                } else {
                    return -1;
                }
            } else
            if(type2 == JAVA) {
                return 1;
            } else
            if(type1 == JAVAX) {
                if(type2 == JAVAX) {
                    str1 = str1.substring(5);
                    str2 = str2.substring(5);
                } else {
                    return -1;
                }
            } else
            if(type2 == JAVAX) {
                return 1;
            }

            return str1.compareTo(str2);
        } else {
            return 0;
        }
    }

    private static int getType(String str) {
        if(str.startsWith("java")) {
            if(str.charAt(4) == '.') {
                return JAVA;
            } else 
            if( (str.charAt(4) == 'x') && (str.charAt(5) == '.') ) {
                return JAVAX;
            }            
        }
        return OTHER;
    }

}
