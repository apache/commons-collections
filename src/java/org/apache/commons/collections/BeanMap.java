/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/java/org/apache/commons/collections/BeanMap.java,v 1.5 2002/03/13 04:15:49 mas Exp $
 * $Revision: 1.5 $
 * $Date: 2002/03/13 04:15:49 $
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

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;


/** An implementation of Map for JavaBeans which uses introspection to
  * get and put properties in the bean.
  *
  * If an exception occurs during attempts to get or set a property then the
  * property is considered non existent in the Map
  *
  * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
  */

public class BeanMap extends AbstractMap {

    private Object bean;

    private HashMap readMethods = new HashMap();
    private HashMap writeMethods = new HashMap();
    private HashMap types = new HashMap();

    public static final Object[] NULL_ARGUMENTS = {};
    public static HashMap defaultTransformers = new HashMap();
    
    static {
        defaultTransformers.put( 
            Boolean.TYPE, 
            new Transformer() {
                public Object transform( Object input ) {
                    return Boolean.valueOf( input.toString() );
                }
            }
        );
        defaultTransformers.put( 
            Character.TYPE, 
            new Transformer() {
                public Object transform( Object input ) {
                    return new Character( input.toString().charAt( 0 ) );
                }
            }
        );
        defaultTransformers.put( 
            Byte.TYPE, 
            new Transformer() {
                public Object transform( Object input ) {
                    return Byte.valueOf( input.toString() );
                }
            }
        );
        defaultTransformers.put( 
            Short.TYPE, 
            new Transformer() {
                public Object transform( Object input ) {
                    return Short.valueOf( input.toString() );
                }
            }
        );
        defaultTransformers.put( 
            Integer.TYPE, 
            new Transformer() {
                public Object transform( Object input ) {
                    return Integer.valueOf( input.toString() );
                }
            }
        );
        defaultTransformers.put( 
            Long.TYPE, 
            new Transformer() {
                public Object transform( Object input ) {
                    return Long.valueOf( input.toString() );
                }
            }
        );
        defaultTransformers.put( 
            Float.TYPE, 
            new Transformer() {
                public Object transform( Object input ) {
                    return Float.valueOf( input.toString() );
                }
            }
        );
        defaultTransformers.put( 
            Double.TYPE, 
            new Transformer() {
                public Object transform( Object input ) {
                    return Double.valueOf( input.toString() );
                }
            }
        );
    }
    
    
    // Constructors
    //-------------------------------------------------------------------------
    public BeanMap() {
    }

    public BeanMap(Object bean) {
        this.bean = bean;
        initialise();
    }

    // Map interface
    //-------------------------------------------------------------------------

    public Object clone() {
        Class beanClass = null;
        try {
            beanClass = bean.getClass();
            Object newBean = beanClass.newInstance();
            Map newMap = new BeanMap( newBean );
            newMap.putAll( this );
            return newMap;
        } 
        catch (Exception e) {
            throw new UnsupportedOperationException( "Could not create new instance of class: " + beanClass );
        }
    }

    /**
     *  This method reinitializes the bean map to have default values for the
     *  bean's properties.  This is accomplished by constructing a new instance
     *  of the bean which th emap uses as its underlying data source.  This
     *  behavior for <code>clear()</code> differs from the Map contract in that
     *  the mappings are not actually removed from the map (the mappings for a
     *  BeanMap are fixed).
     **/
    public void clear() {
        Class beanClass = null;
        try {
            beanClass = bean.getClass();
            bean = beanClass.newInstance();
        }
        catch (Exception e) {
            throw new UnsupportedOperationException( "Could not create new instance of class: " + beanClass );
        }
    }

    public boolean containsKey(Object name) {
        Method method = getReadMethod( name );
        return method != null;
    }

    public boolean containsValue(Object value) {
        // use default implementation
        return super.containsValue( value );
    }

    public Object get(Object name) {
        if ( bean != null ) {
            Method method = getReadMethod( name );
            if ( method != null ) {
                try {
                    return method.invoke( bean, NULL_ARGUMENTS );
                }
                catch (  IllegalAccessException e ) {
                    logWarn( e );
                }
                catch ( IllegalArgumentException e ) {
                    logWarn(  e );
                }
                catch ( InvocationTargetException e ) {
                    logWarn(  e );
                }
                catch ( NullPointerException e ) {
                    logWarn(  e );
                }
            }
        }
        return null;
    }

    public Object put(Object name, Object value) throws IllegalArgumentException, ClassCastException {
        if ( bean != null ) {
            Object oldValue = get( name );
            Method method = getWriteMethod( name );
            if ( method == null ) {
                throw new IllegalArgumentException( "The bean of type: "+ bean.getClass().getName() + " has no property called: " + name );
            }
            try {
                Object[] arguments = createWriteMethodArguments( method, value );
                method.invoke( bean, arguments );

                Object newValue = get( name );
                firePropertyChange( name, oldValue, newValue );
            }
            catch ( InvocationTargetException e ) {
                logInfo( e );
                throw new IllegalArgumentException( e.getMessage() );
            }
            catch ( IllegalAccessException e ) {
                logInfo( e );
                throw new IllegalArgumentException( e.getMessage() );
            }
            return oldValue;
        }
        return null;
    }
                    
    public int size() {
        return readMethods.size();
    }

    
    public Set keySet() {
        return readMethods.keySet();
    }

    public Set entrySet() {
        return readMethods.keySet();
    }

    public Collection values() {
        ArrayList answer = new ArrayList( readMethods.size() );
        for ( Iterator iter = valueIterator(); iter.hasNext(); ) {
            answer.add( iter.next() );
        }
        return answer;
    }


    // Helper methods
    //-------------------------------------------------------------------------
    
    public Class getType(String name) {
        return (Class) types.get( name );
    }

    public Iterator keyIterator() {
        return readMethods.keySet().iterator();
    }

    public Iterator valueIterator() {
        final Iterator iter = keyIterator();
        return new Iterator() {            
            public boolean hasNext() {
                return iter.hasNext();
            }
            public Object next() {
                Object key = iter.next();
                return get( (String) key );
            }
            public void remove() {
                throw new UnsupportedOperationException( "remove() not supported for BeanMap" );
            }
        };
    }

    public Iterator entryIterator() {
        final Iterator iter = keyIterator();
        return new Iterator() {            
            public boolean hasNext() {
                return iter.hasNext();
            }            
            public Object next() {
                Object key = iter.next();
                Object value = get( (String) key );
                return new MyMapEntry( BeanMap.this, key, value );
            }            
            public void remove() {
                throw new UnsupportedOperationException( "remove() not supported for BeanMap" );
            }
        };
    }


    // Properties
    //-------------------------------------------------------------------------
    public Object getBean() {
        return bean;
    }

    public void setBean( Object newBean ) {
        bean = newBean;
        reinitialise();
    }


    // Implementation methods
    //-------------------------------------------------------------------------

    protected Method getReadMethod( Object name ) {
        return (Method) readMethods.get( name );
    }

    protected Method getWriteMethod( Object name ) {
        return (Method) writeMethods.get( name );
    }

    protected void reinitialise() {
        readMethods.clear();
        writeMethods.clear();
        types.clear();
        initialise();
    }

    private void initialise() {
        Class  beanClass = getBean().getClass();
        try {
            //BeanInfo beanInfo = Introspector.getBeanInfo( bean, null );
            BeanInfo beanInfo = Introspector.getBeanInfo( beanClass );
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
            if ( propertyDescriptors != null ) {
                for ( int i = 0; i < propertyDescriptors.length; i++ ) {
                    PropertyDescriptor propertyDescriptor = propertyDescriptors[i];
                    if ( propertyDescriptor != null ) {
                        String name = propertyDescriptor.getName();
                        Method readMethod = propertyDescriptor.getReadMethod();
                        Method writeMethod = propertyDescriptor.getWriteMethod();
                        Class aType = propertyDescriptor.getPropertyType();

                        if ( readMethod != null ) {
                            readMethods.put( name, readMethod );
                        }
                        if ( writeMethods != null ) {
                            writeMethods.put( name, writeMethod );
                        }
                        types.put( name, aType );
                    }
                }
            }
        }
        catch ( IntrospectionException e ) {
            logWarn(  e );
        }
    }

    protected void firePropertyChange( Object key, Object oldValue, Object newValue ) {
    }

    // Implementation classes
    //-------------------------------------------------------------------------
    protected static class MyMapEntry extends DefaultMapEntry {        
        private BeanMap owner;
        
        protected MyMapEntry( BeanMap owner, Object key, Object value ) {
            super( key, value );
            this.owner = owner;
        }

        public Object setValue(Object value) {
            Object key = getKey();
            Object oldValue = owner.get( key );

            owner.put( key, value );
            Object newValue = owner.get( key );
            super.setValue( newValue );
            return oldValue;
        }
    }
    
    protected Object[] createWriteMethodArguments( Method method, Object value ) throws IllegalAccessException, ClassCastException {            
        try {
            if ( value != null ) {
                Class[] types = method.getParameterTypes();
                if ( types != null && types.length > 0 ) {
                    Class paramType = types[0];
                    if ( ! paramType.isAssignableFrom( value.getClass() ) ) {
                        value = convertType( paramType, value );
                    }
                }
            }
            Object[] answer = { value };
            return answer;
        }
        catch ( InvocationTargetException e ) {
            logInfo( e );
            throw new IllegalArgumentException( e.getMessage() );
        }
        catch ( InstantiationException e ) {
            logInfo( e );
            throw new IllegalArgumentException( e.getMessage() );
        }
    }
    
    protected Object convertType( Class newType, Object value ) 
        throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        
        // try call constructor
        Class[] types = { value.getClass() };
        try {
            Constructor constructor = newType.getConstructor( types );        
            Object[] arguments = { value };
            return constructor.newInstance( arguments );
        }
        catch ( NoSuchMethodException e ) {
            // try using the transformers
            Transformer transformer = getTypeTransformer( newType );
            if ( transformer != null ) {
                return transformer.transform( value );
            }
            return value;
        }
    }
    
    protected Transformer getTypeTransformer( Class aType ) {
        return (Transformer) defaultTransformers.get( aType );
    }
    
    protected void logInfo(Exception e) {
        // XXXX: should probably use log4j here instead...
        System.out.println( "INFO: Exception: " + e );
    }
    
    protected void logWarn(Exception e) {
        // XXXX: should probably use log4j here instead...
        System.out.println( "WARN: Exception: " + e );
        e.printStackTrace();
    }
}


