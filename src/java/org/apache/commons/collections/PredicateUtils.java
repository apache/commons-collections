/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/java/org/apache/commons/collections/PredicateUtils.java,v 1.3 2002/08/13 00:26:51 pjack Exp $
 * $Revision: 1.3 $
 * $Date: 2002/08/13 00:26:51 $
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

import java.io.Serializable;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.List;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Comparator;
import java.util.Set;
import java.util.SortedSet;
import java.util.Map;
import java.util.SortedMap;
/**
 * PredicateUtils provides access to common predicate functionality.
 * <p>
 * Included are collections wrapper that support validation.
 * Only elements that pass a predicate (validation test) can 
 * be added to the collection. An <tt>IllegalArgumentException</tt> is
 * thrown if the validation fails.
 * <p>
 * The collections can be accessed by static factory methods. A wrapper
 * is provided for all the java and commons collections.
 * <p>
 * Also included are predicate implementations for True, False, Not,
 * And, Or and instanceof.
 * 
 * @author Stephen Colebourne
 */
public class PredicateUtils {
    
	/**
	 * A predicate that always returns true
	 */    
    public static final Predicate TRUE_PREDICATE = new TruePredicate();
	/**
	 * A predicate that always returns false
	 */    
    public static final Predicate FALSE_PREDICATE = new FalsePredicate();
    
	/**
	 * Restructive constructor
	 */
	private PredicateUtils() {
	    super();
	}

	/**
	 * Create a new predicate that returns true only if both of the passed
	 * in predicates are true.
	 * @param predicate1  the first predicate
	 * @param predicate2  the second predicate
	 */
	public static Predicate andPredicate(Predicate predicate1, Predicate predicate2) {
         return new AndPredicate(predicate1, predicate2);
    }

	/**
	 * Create a new predicate that returns true if either of the passed
	 * in predicates are true.
	 * @param predicate1  the first predicate
	 * @param predicate2  the second predicate
	 */
	public static Predicate orPredicate(Predicate predicate1, Predicate predicate2) {
         return new OrPredicate(predicate1, predicate2);
    }

	/**
	 * Create a new predicate that returns true if the passed in predicate
	 * returns false and vice versa.
	 * @param predicate  the predicate to not
	 */
	public static Predicate notPredicate(Predicate predicate) {
         return new NotPredicate(predicate);
    }

	/**
	 * Create a new predicate that checks if the object passed in is of
	 * a particular type.
	 * @param type  the type to check for
	 */
	public static Predicate instanceofPredicate(Class type) {
         return new InstanceofPredicate(type);
    }

	/**
	 * Perform the validation against the predicate.
	 * @param object  object to be validated
	 */	
	private static void validate(Predicate predicate, Object object) {
	    if (predicate.evaluate(object) == false) {
	        throw new IllegalArgumentException("Predicate validation: " +
	        	object + " cannot be added to the list");
	    }
	}

	/**
	 * True predicate implementation
	 */    
    private static class TruePredicate implements Predicate {
        private TruePredicate() {
            super();
        }
	    public boolean evaluate(Object input) {
	        return true;
	    }
    }
    
	/**
	 * False predicate implementation
	 */    
    private static class FalsePredicate implements Predicate {
        private FalsePredicate() {
            super();
        }
	    public boolean evaluate(Object input) {
	        return false;
	    }
    }
    
	/**
	 * And predicate implementation
	 */    
    private static class AndPredicate implements Predicate {
        private final Predicate iPredicate1;
        private final Predicate iPredicate2;
        
        /**
         * Constructor
         */
        private AndPredicate(Predicate predicate1, Predicate predicate2) {
            super();
    	    if ((predicate1 == null) || (predicate2 == null)) {
    	        throw new IllegalArgumentException("Predicate must not be null");
    	    }
            iPredicate1 = predicate1;
            iPredicate2 = predicate2;
        }
	    public boolean evaluate(Object input) {
	        return iPredicate1.evaluate(input) && iPredicate2.evaluate(input);
	    }
    }
    
	/**
	 * Or predicate implementation
	 */    
    private static class OrPredicate implements Predicate {
        private final Predicate iPredicate1;
        private final Predicate iPredicate2;
        
        /**
         * Constructor
         */
        private OrPredicate(Predicate predicate1, Predicate predicate2) {
            super();
    	    if ((predicate1 == null) || (predicate2 == null)) {
    	        throw new IllegalArgumentException("Predicate must not be null");
    	    }
            iPredicate1 = predicate1;
            iPredicate2 = predicate2;
        }
	    public boolean evaluate(Object input) {
	        return iPredicate1.evaluate(input) || iPredicate2.evaluate(input);
	    }
    }
    
	/**
	 * Not predicate implementation
	 */    
    private static class NotPredicate implements Predicate {
        private final Predicate iPredicate;
        
        /**
         * Constructor
         */
        private NotPredicate(Predicate predicate) {
            super();
    	    if (predicate == null) {
    	        throw new IllegalArgumentException("Predicate must not be null");
    	    }
            iPredicate = predicate;
        }
	    public boolean evaluate(Object input) {
	        return ! iPredicate.evaluate(input);
	    }
    }
    
    /**
     * Predicate that checks the type of an object
     */
    public static class InstanceofPredicate implements Predicate {
        private final Class iType;
    
    	/**
    	 * Constructor
    	 * @param type  the type to validate for
    	 */
    	public InstanceofPredicate(Class type) {
    	    super();
    	    if (type == null) {
    	        throw new IllegalArgumentException("Type to be checked for must not be null");
    	    }
    	    iType = type;
    	}
    
        /**
         * Validate the input object to see if it is an instanceof the 
         * type of the predicate.
         * @param object  the object to be checked
         * @return true if it is an instance
         */
        public boolean evaluate(Object object) {
            return iType.isInstance(object);
        }
    }
}

