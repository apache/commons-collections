/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/java/org/apache/commons/collections/ClosureUtils.java,v 1.3 2003/08/31 17:26:44 scolebourne Exp $
 * ====================================================================
 *
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

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

/**
 * <code>ClosureUtils</code> provides reference implementations and utilities
 * for the Closure functor interface. The supplied closures are:
 * <ul>
 * <li>Invoker - invokes a method on the input object
 * <li>For - repeatedly calls a closure for a fixed number of times
 * <li>While - repeatedly calls a closure while a predicate is true
 * <li>DoWhile - repeatedly calls a closure while a predicate is true
 * <li>Chained - chains two or more closures together
 * <li>Switch - calls one closure based on one or more predicates
 * <li>SwitchMap - calls one closure looked up from a Map
 * <li>Transformer - wraps a Transformer as a Closure
 * <li>NOP - does nothing
 * <li>Exception - always throws an exception
 * </ul>
 * All the supplied closures are Serializable.
 * 
 * @since Commons Collections 3.0
 * @version $Revision: 1.3 $ $Date: 2003/08/31 17:26:44 $
 *
 * @author Stephen Colebourne
 */
public class ClosureUtils {

    /**
     * A Closure that always throws an exception
     */
    private static final Closure EXCEPTION_CLOSURE = new ExceptionClosure();
    /**
     * A Closure that does nothing
     */
    private static final Closure NOP_CLOSURE = new NOPClosure();

    /**
     * This class is not normally instantiated.
     */
    public ClosureUtils() {
        super();
    }

    /**
     * Gets a Closure that always throws an exception.
     * This could be useful during testing as a placeholder.
     *
     * @return the closure
     */
    public static Closure exceptionClosure() {
        return EXCEPTION_CLOSURE;
    }

    /**
     * Gets a Closure that will do nothing.
     * This could be useful during testing as a placeholder.
     *
     * @return the closure
     */
    public static Closure nopClosure() {
        return NOP_CLOSURE;
    }

    /**
     * Creates a Closure that calls a Transformer each time it is called.
     * The transformer will be called using the closure's input object.
     * The transformer's result will be ignored.
     *
     * @param transformer  the transformer to run each time in the closure
     * @return the closure.
     */
    public static Closure asClosure(Transformer transformer) {
        if (transformer == null) {
            throw new IllegalArgumentException("The transformer must not be null");
        }
        return new TransformerClosure(transformer);
    }

    /**
     * Creates a Closure that will call the closure <code>count</code> times.
     *
     * @param count  the number of times to loop
     * @param closure  the closure to call repeatedly
     * @return the <code>for</code> closure
     * @throws IllegalArgumentException if either argument is null
     */
    public static Closure forClosure(int count, Closure closure) {
        if (count < 0) {
            throw new IllegalArgumentException("The loop count must not be less than zero, it was " + count);
        }
        if (closure == null) {
            throw new IllegalArgumentException("The closure must not be null");
        }
        return new ForClosure(count, closure);
    }

    /**
     * Creates a Closure that will call the closure repeatedly until the 
     * predicate returns false.
     *
     * @param predicate  the predicate to use as an end of loop test
     * @param closure  the closure to call repeatedly
     * @return the <code>while</code> closure
     * @throws IllegalArgumentException if either argument is null
     */
    public static Closure whileClosure(Predicate predicate, Closure closure) {
        if (predicate == null) {
            throw new IllegalArgumentException("The predicate must not be null");
        }
        if (closure == null) {
            throw new IllegalArgumentException("The closure must not be null");
        }
        return new WhileClosure(predicate, closure, false);
    }

    /**
     * Creates a Closure that will call the closure once and then repeatedly
     * until the predicate returns false.
     *
     * @param closure  the closure to call repeatedly
     * @param predicate  the predicate to use as an end of loop test
     * @return the <code>do-while</code> closure
     * @throws IllegalArgumentException if either argument is null
     */
    public static Closure doWhileClosure(Closure closure, Predicate predicate) {
        if (closure == null) {
            throw new IllegalArgumentException("The closure must not be null");
        }
        if (predicate == null) {
            throw new IllegalArgumentException("The predicate must not be null");
        }
        return new WhileClosure(predicate, closure, true);
    }

    /**
     * Creates a Closure that will invoke a specific method on the closure's
     * input object by reflection.
     *
     * @param methodName  the name of the method
     * @return the <code>invoker</code> closure
     * @throws IllegalArgumentException if the method name is null
     */
    public static Closure invokerClosure(String methodName) {
        // reuse transformer as it has caching - this is lazy really, should have inner class here
        return asClosure(TransformerUtils.invokerTransformer(methodName, null, null));
    }

    /**
     * Creates a Closure that will invoke a specific method on the closure's
     * input object by reflection.
     *
     * @param methodName  the name of the method
     * @param paramTypes  the parameter types
     * @param args  the arguments
     * @return the <code>invoker</code> closure
     * @throws IllegalArgumentException if the method name is null
     * @throws IllegalArgumentException if the paramTypes and args don't match
     */
    public static Closure invokerClosure(String methodName, Class[] paramTypes, Object[] args) {
        // reuse transformer as it has caching - this is lazy really, should have inner class here
        return asClosure(TransformerUtils.invokerTransformer(methodName, paramTypes, args));
    }

    /**
     * Create a new Closure that calls two Closures, passing the result of
     * the first into the second.
     * 
     * @param closure1  the first closure
     * @param closure2  the second closure
     * @return the <code>chained</code> closure
     * @throws IllegalArgumentException if either closure is null
     */
    public static Closure chainedClosure(Closure closure1, Closure closure2) {
        Closure[] closures = new Closure[] { closure1, closure2 };
        validate(closures);
        return new ChainedClosure(closures);
    }

    /**
     * Create a new Closure that calls each closure in turn, passing the 
     * result into the next closure.
     * 
     * @param closures  an array of closures to chain
     * @return the <code>chained</code> closure
     * @throws IllegalArgumentException if the closures array is null
     * @throws IllegalArgumentException if the closures array has 0 elements
     * @throws IllegalArgumentException if any closure in the array is null
     */
    public static Closure chainedClosure(Closure[] closures) {
        closures = copy(closures);
        validate(closures);
        return new ChainedClosure(closures);
    }

    /**
     * Create a new Closure that calls each closure in turn, passing the 
     * result into the next closure. The ordering is that of the iterator()
     * method on the collection.
     * 
     * @param closures  a collection of closures to chain
     * @return the <code>chained</code> closure
     * @throws IllegalArgumentException if the closures collection is null
     * @throws IllegalArgumentException if the closures collection is empty
     * @throws IllegalArgumentException if any closure in the collection is null
     */
    public static Closure chainedClosure(Collection closures) {
        if (closures == null) {
            throw new IllegalArgumentException("The closure collection must not be null");
        }
        // convert to array like this to guarantee iterator() ordering
        Closure[] cmds = new Closure[closures.size()];
        int i = 0;
        for (Iterator it = closures.iterator(); it.hasNext();) {
            cmds[i++] = (Closure) it.next();
        }
        validate(cmds);
        return new ChainedClosure(cmds);
    }

    /**
     * Create a new Closure that calls one of two closures depending 
     * on the specified predicate.
     * 
     * @param predicate  the predicate to switch on
     * @param trueClosure  the closure called if the predicate is true
     * @param falseClosure  the closure called if the predicate is false
     * @return the <code>switch</code> closure
     * @throws IllegalArgumentException if the predicate is null
     * @throws IllegalArgumentException if either closure is null
     */
    public static Closure switchClosure(Predicate predicate, Closure trueClosure, Closure falseClosure) {
        return switchClosureInternal(new Predicate[] { predicate }, new Closure[] { trueClosure }, falseClosure);
    }

    /**
     * Create a new Closure that calls one of the closures depending 
     * on the predicates.
     * <p>
     * The closure at array location 0 is called if the predicate at array 
     * location 0 returned true. Each predicate is evaluated
     * until one returns true.
     * 
     * @param predicates  an array of predicates to check
     * @param closures  an array of closures to call
     * @return the <code>switch</code> closure
     * @throws IllegalArgumentException if the either array is null
     * @throws IllegalArgumentException if the either array has 0 elements
     * @throws IllegalArgumentException if any element in the arrays is null
     * @throws IllegalArgumentException if the arrays are different sizes
     */
    public static Closure switchClosure(Predicate[] predicates, Closure[] closures) {
        return switchClosureInternal(copy(predicates), copy(closures), null);
    }

    /**
     * Create a new Closure that calls one of the closures depending 
     * on the predicates.
     * <p>
     * The closure at array location 0 is called if the predicate at array
     * location 0 returned true. Each predicate is evaluated
     * until one returns true. If no predicates evaluate to true, the default
     * closure is called.
     * 
     * @param predicates  an array of predicates to check
     * @param closures  an array of closures to call
     * @param defaultClosure  the default to call if no predicate matches
     * @return the <code>switch</code> closure
     * @throws IllegalArgumentException if the either array is null
     * @throws IllegalArgumentException if the either array has 0 elements
     * @throws IllegalArgumentException if any element in the arrays is null
     * @throws IllegalArgumentException if the arrays are different sizes
     */
    public static Closure switchClosure(Predicate[] predicates, Closure[] closures, Closure defaultClosure) {
        return switchClosureInternal(copy(predicates), copy(closures), defaultClosure);
    }
    
    /**
     * Create a new Closure that calls one of the closures depending 
     * on the predicates. 
     * <p>
     * The Map consists of Predicate keys and Closure values. A closure 
     * is called if its matching predicate returns true. Each predicate is evaluated
     * until one returns true. If no predicates evaluate to true, the default
     * closure is called. The default closure is set in the map with a 
     * null key. The ordering is that of the iterator() method on the entryset 
     * collection of the map.
     * 
     * @param predicatesAndClosures  a map of predicates to closures
     * @return the <code>switch</code> closure
     * @throws IllegalArgumentException if the map is null
     * @throws IllegalArgumentException if the map is empty
     * @throws IllegalArgumentException if any closure in the map is null
     * @throws ClassCastException  if the map elements are of the wrong type
     */
    public static Closure switchClosure(Map predicatesAndClosures) {
        Closure[] trs = null;
        Predicate[] preds = null;
        if (predicatesAndClosures == null) {
            throw new IllegalArgumentException("The predicate and closure map must not be null");
        }
        // convert to array like this to guarantee iterator() ordering
        Closure def = (Closure) predicatesAndClosures.remove(null);
        int size = predicatesAndClosures.size();
        trs = new Closure[size];
        preds = new Predicate[size];
        int i = 0;
        for (Iterator it = predicatesAndClosures.entrySet().iterator(); it.hasNext();) {
            Map.Entry entry = (Map.Entry) it.next();
            preds[i] = (Predicate) entry.getKey();
            trs[i] = (Closure) entry.getValue();
            i++;
        }
        return switchClosureInternal(preds, trs, def);
    }

    /**
     * Validate input and create closure.
     * 
     * @param predicates  an array of predicates to check
     * @param closures  an array of closures to call
     * @param defaultClosure  the default to call if no predicate matches
     * @return the <code>switch</code> closure
     * @throws IllegalArgumentException if the either array is null
     * @throws IllegalArgumentException if the either array has 0 elements
     * @throws IllegalArgumentException if any element in the arrays is null
     * @throws IllegalArgumentException if the arrays are different sizes
     */
    private static Closure switchClosureInternal(Predicate[] predicates, Closure[] closures, Closure defaultClosure) {
        validate(predicates);
        validate(closures);
        if (predicates.length != closures.length) {
            throw new IllegalArgumentException("The predicate and closure arrays must be the same size");
        }
        if (defaultClosure == null) {
            defaultClosure = nopClosure();
        }
        return new SwitchClosure(predicates, closures, defaultClosure);
    }

    /**
     * Create a new Closure that uses the input object as a key to find the
     * closure to call. 
     * <p>
     * The Map consists of object keys and Closure values. A closure 
     * is called if the input object equals the key. If there is no match, the
     * default closure is called. The default closure is set in the map
     * using a null key.
     * 
     * @param objectsAndClosures  a map of objects to closures
     * @return the closure
     * @throws IllegalArgumentException if the map is null
     * @throws IllegalArgumentException if the map is empty
     * @throws IllegalArgumentException if any closure in the map is null
     */
    public static Closure switchMapClosure(Map objectsAndClosures) {
        Closure[] trs = null;
        Predicate[] preds = null;
        if (objectsAndClosures == null) {
            throw new IllegalArgumentException("The object and closure map must not be null");
        }
        Closure def = (Closure) objectsAndClosures.remove(null);
        int size = objectsAndClosures.size();
        trs = new Closure[size];
        preds = new Predicate[size];
        int i = 0;
        for (Iterator it = objectsAndClosures.entrySet().iterator(); it.hasNext();) {
            Map.Entry entry = (Map.Entry) it.next();
            preds[i] = PredicateUtils.equalPredicate(entry.getKey());
            trs[i] = (Closure) entry.getValue();
            i++;
        }
        return switchClosure(preds, trs, def);
    }

    /**
     * Clone the predicates to ensure that the internal reference can't be messed with.
     * 
     * @param predicates  the predicates to copy
     * @return the cloned predicates
     */
    private static Predicate[] copy(Predicate[] predicates) {
        if (predicates == null) {
            return null;
        }
        return (Predicate[]) predicates.clone();
    }
    
    /**
     * Validate the predicates to ensure that all is well.
     * 
     * @param predicates  the predicates to validate
     * @return the validated predicates
     */
    private static void validate(Predicate[] predicates) {
        if (predicates == null) {
            throw new IllegalArgumentException("The predicate array must not be null");
        }
        if (predicates.length < 1) {
            throw new IllegalArgumentException(
                "At least 1 predicate must be specified in the predicate array, size was " + predicates.length);
        }
        for (int i = 0; i < predicates.length; i++) {
            if (predicates[i] == null) {
                throw new IllegalArgumentException("The predicate array must not contain a null predicate, index " + i + " was null");
            }
        }
    }

    /**
     * Clone the closures to ensure that the internal reference can't be messed with.
     * 
     * @param closures  the closures to copy
     * @return the cloned closures
     */
    private static Closure[] copy(Closure[] closures) {
        if (closures == null) {
            return null;
        }
        return (Closure[]) closures.clone();
    }
    
    /**
     * Validate the closures to ensure that all is well.
     * 
     * @param closures  the closures to validate
     * @return the validated closures
     */
    private static void validate(Closure[] closures) {
        if (closures == null) {
            throw new IllegalArgumentException("The closure array must not be null");
        }
        if (closures.length < 1) {
            throw new IllegalArgumentException(
                "At least 1 closure must be specified in the closure array, size was " + closures.length);
        }
        for (int i = 0; i < closures.length; i++) {
            if (closures[i] == null) {
                throw new IllegalArgumentException("The closure array must not contain a null closure, index " + i + " was null");
            }
        }
    }

    // ExceptionClosure
    //----------------------------------------------------------------------------------

    /**
     * ExceptionClosure always throws an exception
     */
    private static class ExceptionClosure implements Closure, Serializable {

        /**
         * Constructor
         */
        private ExceptionClosure() {
            super();
        }

        /**
         * Always throw an exception
         */
        public void execute(Object input) {
            throw new FunctorException("ExceptionClosure invoked");
        }
    }

    // NOPClosure
    //----------------------------------------------------------------------------------

    /**
     * NOPClosure does nothing
     */
    private static class NOPClosure implements Closure, Serializable {

        /**
         * Constructor
         */
        private NOPClosure() {
            super();
        }

        /**
         * Do nothing
         */
        public void execute(Object input) {
            // do nothing
        }
    }

    // TransformerClosure
    //----------------------------------------------------------------------------------

    /**
     * TransformerClosure calls a Transformer using the input object and ignore the result.
     */
    private static class TransformerClosure implements Closure, Serializable {
        /** The transformer to wrap */
        private final Transformer iTransformer;

        /**
         * Constructor to store transformer
         */
        private TransformerClosure(Transformer transformer) {
            super();
            iTransformer = transformer;
        }

        /**
         * Call the transformer
         */
        public void execute(Object input) {
            iTransformer.transform(input);
        }
    }

    // ChainedClosure
    //----------------------------------------------------------------------------------

    /**
     * ChainedClosure calls a list of closures.
     */
    private static class ChainedClosure implements Closure, Serializable {
        /** The closures to call in turn */
        private final Closure[] iClosures;

        /**
         * Constructor to store params
         */
        private ChainedClosure(Closure[] closures) {
            super();
            iClosures = closures;
        }

        /**
         * Execute a list of closures
         */
        public void execute(Object input) {
            for (int i = 0; i < iClosures.length; i++) {
                iClosures[i].execute(input);
            }
        }
    }

    // SwitchClosure
    //----------------------------------------------------------------------------------

    /**
     * SwitchClosure calls the closure whose predicate returns true.
     */
    private static class SwitchClosure implements Closure, Serializable {
        /** The tests to consider */
        private final Predicate[] iPredicates;
        /** The matching closures to call */
        private final Closure[] iClosures;
        /** The default closure to call if no tests match */
        private final Closure iDefault;

        /**
         * Constructor to store params
         */
        private SwitchClosure(Predicate[] predicates, Closure[] closures, Closure defaultClosure) {
            super();
            iPredicates = predicates;
            iClosures = closures;
            iDefault = defaultClosure;
        }

        /**
         * Execute the closure whose predicate returns true
         */
        public void execute(Object input) {
            for (int i = 0; i < iPredicates.length; i++) {
                if (iPredicates[i].evaluate(input) == true) {
                    iClosures[i].execute(input);
                    return;
                }
            }
            iDefault.execute(input);
        }
    }

    // ForClosure
    //----------------------------------------------------------------------------------

    /**
     * ForClosure calls the closure a fixed number of times.
     */
    private static class ForClosure implements Closure, Serializable {
        /** The number of times to loop */
        private final int iCount;
        /** The closure to call */
        private final Closure iClosure;

        /**
         * Constructor to store params
         */
        private ForClosure(int count, Closure closure) {
            super();
            iCount = count;
            iClosure = closure;
        }

        /**
         * Execute the closure count times
         */
        public void execute(Object input) {
            for (int i = 0; i < iCount; i++) {
                iClosure.execute(input);
            }
        }
    }

    // WhileClosure
    //----------------------------------------------------------------------------------

    /**
     * WhileClosure calls the closure until the predicate is false.
     */
    private static class WhileClosure implements Closure, Serializable {
        /** The test condition */
        private final Predicate iPredicate;
        /** The closure to call */
        private final Closure iClosure;
        /** The flag, true is a do loop, false is a while */
        private final boolean iDoLoop;

        /**
         * Constructor to store params
         */
        private WhileClosure(Predicate predicate, Closure closure, boolean doLoop) {
            super();
            iPredicate = predicate;
            iClosure = closure;
            iDoLoop = doLoop;
        }

        /**
         * Execute the closure until the predicate is false
         */
        public void execute(Object input) {
            if (iDoLoop) {
                iClosure.execute(input);
            }
            while (iPredicate.evaluate(input)) {
                iClosure.execute(input);
            }
        }
    }

}
