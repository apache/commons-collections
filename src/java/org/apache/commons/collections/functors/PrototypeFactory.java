/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001-2004 The Apache Software Foundation.  All rights
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
 */
package org.apache.commons.collections.functors;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.commons.collections.Factory;
import org.apache.commons.collections.FunctorException;

/**
 * Factory implementation that creates a new instance each time based on a prototype.
 * 
 * @since Commons Collections 3.0
 * @version $Revision: 1.5 $ $Date: 2004/01/14 21:43:09 $
 *
 * @author Stephen Colebourne
 */
public class PrototypeFactory {

    /**
     * Factory method that performs validation.
     * <p>
     * Creates a Factory that will return a clone of the same prototype object
     * each time the factory is used. The prototype will be cloned using one of these
     * techniques (in order):
     * <ul>
     * <li>public clone method
     * <li>public copy constructor
     * <li>serialization clone
     * <ul>
     *
     * @param prototype  the object to clone each time in the factory
     * @return the <code>prototype</code> factory
     * @throws IllegalArgumentException if the prototype is null
     * @throws IllegalArgumentException if the prototype cannot be cloned
     */
    public static Factory getInstance(Object prototype) {
        if (prototype == null) {
            return ConstantFactory.NULL_INSTANCE;
        }
        try {
            Method method = prototype.getClass().getMethod("clone", null);
            return new PrototypeCloneFactory(prototype, method);

        } catch (NoSuchMethodException ex) {
            try {
                prototype.getClass().getConstructor(new Class[] { prototype.getClass()});
                return new InstantiateFactory(
                    prototype.getClass(),
                    new Class[] { prototype.getClass()},
                    new Object[] { prototype });

            } catch (NoSuchMethodException ex2) {
                if (prototype instanceof Serializable) {
                    return new PrototypeSerializationFactory((Serializable) prototype);
                }
            }
        }
        throw new IllegalArgumentException("The prototype must be cloneable via a public clone method");
    }

    /**
     * Constructor that performs no validation.
     * Use <code>getInstance</code> if you want that.
     */
    private PrototypeFactory() {
    }

    // PrototypeCloneFactory
    //-----------------------------------------------------------------------
    /**
     * PrototypeCloneFactory creates objects by copying a prototype using the clone method.
     */
    static class PrototypeCloneFactory implements Factory, Serializable {
        
        /** The serial version */
        static final long serialVersionUID = 5604271422565175555L;
        
        /** The object to clone each time */
        private final Object iPrototype;
        /** The method used to clone */
        private transient Method iCloneMethod;

        /**
         * Constructor to store prototype.
         */
        private PrototypeCloneFactory(Object prototype, Method method) {
            super();
            iPrototype = prototype;
            iCloneMethod = method;
        }

        /**
         * Find the Clone method for the class specified.
         */
        private void findCloneMethod() {
            try {
                iCloneMethod = iPrototype.getClass().getMethod("clone", null);

            } catch (NoSuchMethodException ex) {
                throw new IllegalArgumentException("PrototypeCloneFactory: The clone method must exist and be public ");
            }
        }

        /**
         * Return clone of prototype
         */
        public Object create() {
            // needed for post-serialization
            if (iCloneMethod == null) {
                findCloneMethod();
            }

            try {
                return iCloneMethod.invoke(iPrototype, null);

            } catch (IllegalAccessException ex) {
                throw new FunctorException("PrototypeCloneFactory: Clone method must be public", ex);
            } catch (InvocationTargetException ex) {
                throw new FunctorException("PrototypeCloneFactory: Clone method threw an exception", ex);
            }
        }
    }

    // PrototypeSerializationFactory
    //-----------------------------------------------------------------------
    /**
     * PrototypeSerializationFactory creates objects by cloning a prototype using serialization.
     */
    static class PrototypeSerializationFactory implements Factory, Serializable {
        
        /** The serial version */
        static final long serialVersionUID = -8704966966139178833L;
        
        /** The object to clone via serialization each time */
        private final Serializable iPrototype;

        /**
         * Constructor to store prototype
         */
        private PrototypeSerializationFactory(Serializable prototype) {
            super();
            iPrototype = prototype;
        }

        /**
         * Return clone of prototype by serialization
         */
        public Object create() {
            ByteArrayOutputStream baos = new ByteArrayOutputStream(512);
            ByteArrayInputStream bais = null;
            try {
                ObjectOutputStream out = new ObjectOutputStream(baos);
                out.writeObject(iPrototype);

                bais = new ByteArrayInputStream(baos.toByteArray());
                ObjectInputStream in = new ObjectInputStream(bais);
                return in.readObject();

            } catch (ClassNotFoundException ex) {
                throw new FunctorException(ex);
            } catch (IOException ex) {
                throw new FunctorException(ex);
            } finally {
                try {
                    if (bais != null) {
                        bais.close();
                    }
                } catch (IOException ignored) {
                }
                try {
                    if (baos != null) {
                        baos.close();
                    }
                } catch (IOException ignored) {
                }
            }
        }
    }

}
