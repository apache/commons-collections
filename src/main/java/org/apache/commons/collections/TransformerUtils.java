/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.collections;

import java.util.Collection;
import java.util.Map;

import org.apache.commons.collections.functors.ChainedTransformer;
import org.apache.commons.collections.functors.CloneTransformer;
import org.apache.commons.collections.functors.ClosureTransformer;
import org.apache.commons.collections.functors.ConstantTransformer;
import org.apache.commons.collections.functors.EqualPredicate;
import org.apache.commons.collections.functors.ExceptionTransformer;
import org.apache.commons.collections.functors.FactoryTransformer;
import org.apache.commons.collections.functors.InstantiateTransformer;
import org.apache.commons.collections.functors.InvokerTransformer;
import org.apache.commons.collections.functors.MapTransformer;
import org.apache.commons.collections.functors.NOPTransformer;
import org.apache.commons.collections.functors.PredicateTransformer;
import org.apache.commons.collections.functors.StringValueTransformer;
import org.apache.commons.collections.functors.SwitchTransformer;

/**
 * <code>TransformerUtils</code> provides reference implementations and 
 * utilities for the Transformer functor interface. The supplied transformers are:
 * <ul>
 * <li>Invoker - returns the result of a method call on the input object
 * <li>Clone - returns a clone of the input object
 * <li>Constant - always returns the same object
 * <li>Closure - performs a Closure and returns the input object
 * <li>Predicate - returns the result of the predicate as a Boolean
 * <li>Factory - returns a new object from a factory
 * <li>Chained - chains two or more transformers together
 * <li>Switch - calls one transformer based on one or more predicates
 * <li>SwitchMap - calls one transformer looked up from a Map
 * <li>Instantiate - the Class input object is instantiated
 * <li>Map - returns an object from a supplied Map
 * <li>Null - always returns null
 * <li>NOP - returns the input object, which should be immutable
 * <li>Exception - always throws an exception
 * <li>StringValue - returns a <code>java.lang.String</code> representation of the input object
 * </ul>
 * All the supplied transformers are Serializable.
 *
 * @since Commons Collections 3.0
 * @version $Revision$
 *
 * @author Stephen Colebourne
 * @author James Carman
 */
public class TransformerUtils {

    /**
     * This class is not normally instantiated.
     */
    public TransformerUtils() {
        super();
    }

    /**
     * Gets a transformer that always throws an exception.
     * This could be useful during testing as a placeholder.
     * 
     * @see org.apache.commons.collections.functors.ExceptionTransformer
     * 
     * @return the transformer
     */
    public static <I, O> Transformer<I, O> exceptionTransformer() {
        return ExceptionTransformer.<I, O>exceptionTransformer();
    }

    /**
     * Gets a transformer that always returns null.
     * 
     * @see org.apache.commons.collections.functors.ConstantTransformer
     * 
     * @return the transformer
     */
    public static <I, O> Transformer<I, O> nullTransformer() {
        return ConstantTransformer.<I, O>nullTransformer();
    }

    /**
     * Gets a transformer that returns the input object.
     * The input object should be immutable to maintain the
     * contract of Transformer (although this is not checked).
     * 
     * @see org.apache.commons.collections.functors.NOPTransformer
     * 
     * @return the transformer
     */
    public static <T> Transformer<T, T> nopTransformer() {
        return NOPTransformer.<T>nopTransformer();
    }

    /**
     * Gets a transformer that returns a clone of the input
     * object. The input object will be cloned using one of these
     * techniques (in order):
     * <ul>
     * <li>public clone method
     * <li>public copy constructor
     * <li>serialization clone
     * <ul>
     * 
     * @see org.apache.commons.collections.functors.CloneTransformer
     * 
     * @return the transformer
     */
    public static <T> Transformer<T, T> cloneTransformer() {
        return CloneTransformer.<T>cloneTransformer();
    }

    /**
     * Creates a Transformer that will return the same object each time the 
     * transformer is used.
     *
     * @see org.apache.commons.collections.functors.ConstantTransformer
     * 
     * @param constantToReturn  the constant object to return each time in the transformer
     * @return the transformer.
     */
    public static <I, O> Transformer<I, O> constantTransformer(O constantToReturn) {
        return ConstantTransformer.constantTransformer(constantToReturn);
    }

    /**
     * Creates a Transformer that calls a Closure each time the transformer is used.
     * The transformer returns the input object.
     *
     * @see org.apache.commons.collections.functors.ClosureTransformer
     * 
     * @param closure  the closure to run each time in the transformer, not null
     * @return the transformer
     * @throws IllegalArgumentException if the closure is null
     */
    public static <T> Transformer<T, T> asTransformer(Closure<? super T> closure) {
        return ClosureTransformer.closureTransformer(closure);
    }

    /**
     * Creates a Transformer that calls a Predicate each time the transformer is used.
     * The transformer will return either Boolean.TRUE or Boolean.FALSE.
     *
     * @see org.apache.commons.collections.functors.PredicateTransformer
     * 
     * @param predicate  the predicate to run each time in the transformer, not null
     * @return the transformer
     * @throws IllegalArgumentException if the predicate is null
     */
    public static <T> Transformer<T, Boolean> asTransformer(Predicate<? super T> predicate) {
        return PredicateTransformer.predicateTransformer(predicate);
    }

    /**
     * Creates a Transformer that calls a Factory each time the transformer is used.
     * The transformer will return the value returned by the factory.
     *
     * @see org.apache.commons.collections.functors.FactoryTransformer
     * 
     * @param factory  the factory to run each time in the transformer, not null
     * @return the transformer
     * @throws IllegalArgumentException if the factory is null
     */
    public static <I, O> Transformer<I, O> asTransformer(Factory<? extends O> factory) {
        return FactoryTransformer.factoryTransformer(factory);
    }

    /**
     * Create a new Transformer that calls two transformers, passing the result of
     * the first into the second.
     * 
     * @see org.apache.commons.collections.functors.ChainedTransformer
     * 
     * @param transformer1  the first transformer
     * @param transformer2  the second transformer
     * @return the transformer
     * @throws IllegalArgumentException if either transformer is null
     */
    @SuppressWarnings("unchecked")
    public static <T> Transformer<T, T> chainedTransformer(
            Transformer<? super T, ? extends T> transformer1,
            Transformer<? super T, ? extends T> transformer2) {
        return ChainedTransformer.<T> chainedTransformer(transformer1, transformer2);
    }

    /**
     * Create a new Transformer that calls each transformer in turn, passing the 
     * result into the next transformer.
     * 
     * @see org.apache.commons.collections.functors.ChainedTransformer
     * 
     * @param transformers  an array of transformers to chain
     * @return the transformer
     * @throws IllegalArgumentException if the transformers array is null
     * @throws IllegalArgumentException if any transformer in the array is null
     */
    public static <T> Transformer<T, T> chainedTransformer(Transformer<? super T, ? extends T>[] transformers) {
        return ChainedTransformer.chainedTransformer(transformers);
    }

    /**
     * Create a new Transformer that calls each transformer in turn, passing the 
     * result into the next transformer. The ordering is that of the iterator()
     * method on the collection.
     * 
     * @see org.apache.commons.collections.functors.ChainedTransformer
     * 
     * @param transformers  a collection of transformers to chain
     * @return the transformer
     * @throws IllegalArgumentException if the transformers collection is null
     * @throws IllegalArgumentException if any transformer in the collection is null
     */
    public static <T> Transformer<T, T> chainedTransformer(
            Collection<? extends Transformer<T, T>> transformers) {
        return ChainedTransformer.chainedTransformer(transformers);
    }

    /**
     * Create a new Transformer that calls one of two transformers depending 
     * on the specified predicate.
     * 
     * @see org.apache.commons.collections.functors.SwitchTransformer
     * 
     * @param predicate  the predicate to switch on
     * @param trueTransformer  the transformer called if the predicate is true
     * @param falseTransformer  the transformer called if the predicate is false
     * @return the transformer
     * @throws IllegalArgumentException if the predicate is null
     * @throws IllegalArgumentException if either transformer is null
     */
    @SuppressWarnings("unchecked")
    public static <I, O> Transformer<I, O> switchTransformer(Predicate<? super I> predicate,
            Transformer<? super I, ? extends O> trueTransformer,
            Transformer<? super I, ? extends O> falseTransformer) {
        return SwitchTransformer.switchTransformer(new Predicate[] { predicate },
                new Transformer[] { trueTransformer }, falseTransformer);
    }

    /**
     * Create a new Transformer that calls one of the transformers depending 
     * on the predicates. The transformer at array location 0 is called if the
     * predicate at array location 0 returned true. Each predicate is evaluated
     * until one returns true. If no predicates evaluate to true, null is returned.
     * 
     * @see org.apache.commons.collections.functors.SwitchTransformer
     * 
     * @param predicates  an array of predicates to check
     * @param transformers  an array of transformers to call
     * @return the transformer
     * @throws IllegalArgumentException if the either array is null
     * @throws IllegalArgumentException if the either array has 0 elements
     * @throws IllegalArgumentException if any element in the arrays is null
     * @throws IllegalArgumentException if the arrays are different sizes
     */
    public static <I, O> Transformer<I, O> switchTransformer(Predicate<? super I>[] predicates,
            Transformer<? super I, ? extends O>[] transformers) {
        return SwitchTransformer.<I, O>switchTransformer(predicates, transformers, null);
    }

    /**
     * Create a new Transformer that calls one of the transformers depending 
     * on the predicates. The transformer at array location 0 is called if the
     * predicate at array location 0 returned true. Each predicate is evaluated
     * until one returns true. If no predicates evaluate to true, the default
     * transformer is called. If the default transformer is null, null is returned.
     * 
     * @see org.apache.commons.collections.functors.SwitchTransformer
     * 
     * @param predicates  an array of predicates to check
     * @param transformers  an array of transformers to call
     * @param defaultTransformer  the default to call if no predicate matches, null means return null
     * @return the transformer
     * @throws IllegalArgumentException if the either array is null
     * @throws IllegalArgumentException if the either array has 0 elements
     * @throws IllegalArgumentException if any element in the arrays is null
     * @throws IllegalArgumentException if the arrays are different sizes
     */
    public static <I, O> Transformer<I, O> switchTransformer(Predicate<? super I>[] predicates,
            Transformer<? super I, ? extends O>[] transformers,
            Transformer<? super I, ? extends O> defaultTransformer) {
        return SwitchTransformer.<I, O>switchTransformer(predicates, transformers, defaultTransformer);
    }

    /**
     * Create a new Transformer that calls one of the transformers depending 
     * on the predicates. 
     * <p>
     * The Map consists of Predicate keys and Transformer values. A transformer 
     * is called if its matching predicate returns true. Each predicate is evaluated
     * until one returns true. If no predicates evaluate to true, the default
     * transformer is called. The default transformer is set in the map with a 
     * null key. If no default transformer is set, null will be returned in a default
     * case. The ordering is that of the iterator() method on the entryset collection 
     * of the map.
     * 
     * @see org.apache.commons.collections.functors.SwitchTransformer
     * 
     * @param predicatesAndTransformers  a map of predicates to transformers
     * @return the transformer
     * @throws IllegalArgumentException if the map is null
     * @throws IllegalArgumentException if the map is empty
     * @throws IllegalArgumentException if any transformer in the map is null
     * @throws ClassCastException  if the map elements are of the wrong type
     */
    public static <I, O> Transformer<I, O> switchTransformer(
            Map<Predicate<I>, Transformer<I, O>> predicatesAndTransformers) {
        return SwitchTransformer.<I, O>switchTransformer(predicatesAndTransformers);
    }

    /**
     * Create a new Transformer that uses the input object as a key to find the
     * transformer to call. 
     * <p>
     * The Map consists of object keys and Transformer values. A transformer 
     * is called if the input object equals the key. If there is no match, the
     * default transformer is called. The default transformer is set in the map
     * using a null key. If no default is set, null will be returned in a default case.
     * 
     * @see org.apache.commons.collections.functors.SwitchTransformer
     * 
     * @param objectsAndTransformers  a map of objects to transformers
     * @return the transformer
     * @throws IllegalArgumentException if the map is null
     * @throws IllegalArgumentException if the map is empty
     * @throws IllegalArgumentException if any transformer in the map is null
     */
    @SuppressWarnings("unchecked")
    public static <I, O> Transformer<I, O> switchMapTransformer(Map<I, Transformer<I, O>> objectsAndTransformers) {
        Transformer<? super I, ? extends O>[] trs = null;
        Predicate<I>[] preds = null;
        if (objectsAndTransformers == null) {
            throw new IllegalArgumentException("The object and transformer map must not be null");
        }
        Transformer<? super I, ? extends O> def = objectsAndTransformers.remove(null);
        int size = objectsAndTransformers.size();
        trs = new Transformer[size];
        preds = new Predicate[size];
        int i = 0;
        for (Map.Entry<I, Transformer<I, O>> entry : objectsAndTransformers.entrySet()) {
            preds[i] = EqualPredicate.<I>equalPredicate(entry.getKey());
            trs[i++] = entry.getValue();
        }
        return TransformerUtils.<I, O>switchTransformer(preds, trs, def);
    }

    /**
     * Gets a Transformer that expects an input Class object that it will instantiate.
     * 
     * @see org.apache.commons.collections.functors.InstantiateTransformer
     * 
     * @return the transformer
     */
    public static <T> Transformer<Class<? extends T>, T> instantiateTransformer() {
        return InstantiateTransformer.<T>instantiateTransformer();
    }

    /** 
     * Creates a Transformer that expects an input Class object that it will 
     * instantiate. The constructor used is determined by the arguments specified
     * to this method.
     *
     * @see org.apache.commons.collections.functors.InstantiateTransformer
     * 
     * @param paramTypes  parameter types for the constructor, can be null
     * @param args  the arguments to pass to the constructor, can be null
     * @return the transformer
     * @throws IllegalArgumentException if the paramTypes and args don't match
     */
    public static <T> Transformer<Class<? extends T>, T> instantiateTransformer(
            Class<?>[] paramTypes, Object[] args) {
        return InstantiateTransformer.<T>instantiateTransformer(paramTypes, args);
    }

    /** 
     * Creates a Transformer that uses the passed in Map to transform the input 
     * object (as a simple lookup).
     *
     * @see org.apache.commons.collections.functors.MapTransformer
     * 
     * @param map  the map to use to transform the objects
     * @return the transformer
     * @throws IllegalArgumentException if the map is null
     */
    public static <I, O> Transformer<I, O> mapTransformer(Map<? super I, ? extends O> map) {
        return MapTransformer.mapTransformer(map);
    }

    /**
     * Gets a Transformer that invokes a method on the input object.
     * The method must have no parameters. If the input object is null, 
     * null is returned.
     * <p>
     * For example, <code>TransformerUtils.invokerTransformer("getName");</code>
     * will call the <code>getName/code> method on the input object to 
     * determine the transformer result.
     * 
     * @see org.apache.commons.collections.functors.InvokerTransformer
     * 
     * @param methodName  the method name to call on the input object, may not be null
     * @return the transformer
     * @throws IllegalArgumentException if the methodName is null.
     */
    public static <I, O> Transformer<I, O> invokerTransformer(String methodName){
        return InvokerTransformer.<I, O>invokerTransformer(methodName, null, null);
    }

    /**
     * Gets a Transformer that invokes a method on the input object.
     * The method parameters are specified. If the input object is null, 
     * null is returned.
     * 
     * @see org.apache.commons.collections.functors.InvokerTransformer
     * 
     * @param methodName  the name of the method
     * @param paramTypes  the parameter types
     * @param args  the arguments
     * @return the transformer
     * @throws IllegalArgumentException if the method name is null
     * @throws IllegalArgumentException if the paramTypes and args don't match
     */
    public static <I, O> Transformer<I, O> invokerTransformer(String methodName, Class<?>[] paramTypes, Object[] args){
        return InvokerTransformer.<I, O>invokerTransformer(methodName, paramTypes, args);
    }

    /**
     * Gets a transformer that returns a <code>java.lang.String</code>
     * representation of the input object. This is achieved via the
     * <code>toString</code> method, <code>null</code> returns 'null'.
     * 
     * @see org.apache.commons.collections.functors.StringValueTransformer
     * 
     * @return the transformer
     */
    public static <T> Transformer<T, String> stringValueTransformer() {
        return StringValueTransformer.<T>stringValueTransformer();
    }
    
}
