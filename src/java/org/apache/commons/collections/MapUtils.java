/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/java/org/apache/commons/collections/MapUtils.java,v 1.6 2002/08/13 00:26:51 pjack Exp $
 * $Revision: 1.6 $
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

import java.io.*;
import java.text.*;
import java.util.*;

/** A helper class for using {@link Map Map} instances.
  *
  * It contains various typesafe methods
  * as well as other useful features like deep copying
  *
  * @since 1.0
  * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
  * @author <a href="mailto:nissim@nksystems.com">Nissim Karpenstein</a>
  * @author <a href="mailto:knielsen@apache.org">Kasper Nielsen</a>
  * @author Paul Jack
  */
public class MapUtils {

    private static int debugIndent = 0;
    
    
    
    // Type safe getters
    //-------------------------------------------------------------------------
    public static Object getObject( Map map, Object key ) {
        if ( map != null ) {
            return map.get( key );
        }
        return null;
    }

    public static String getString( Map map, Object key ) {
        if ( map != null ) {
            Object answer = map.get( key );
            if ( answer != null ) {
                return answer.toString();
            }
        }
        return null;
    }

    public static Boolean getBoolean( Map map, Object key ) {
        if ( map != null ) {
            Object answer = map.get( key );
            if ( answer != null ) {
                if ( answer instanceof Boolean ) {
                    return (Boolean) answer;
                }
                else
                if ( answer instanceof String ) {
                    return new Boolean( (String) answer );
                }
                else
                if ( answer instanceof Number ) {
                    Number n = (Number) answer;
                    return ( n.intValue() != 0 ) ? Boolean.TRUE : Boolean.FALSE;
                }
            }
        }
        return null;
    }

    public static Number getNumber( Map map, Object key ) {
        if ( map != null ) {
            Object answer = map.get( key );
            if ( answer != null ) {
                if ( answer instanceof Number ) {
                    return (Number) answer;
                }
                else
                if ( answer instanceof String ) {
                    try {
                        String text = (String) answer;
                        return NumberFormat.getInstance().parse( text );
                    }
                    catch (ParseException e) {
                        logInfo( e );
                    }
                }
            }
        }
        return null;
    }

    public static Byte getByte( Map map, Object key ) {
        Number answer = getNumber( map, key );
        if ( answer == null ) {
            return null;
        }
        else
        if ( answer instanceof Byte ) {
            return (Byte) answer;
        }
        return new Byte( answer.byteValue() );
    }

    public static Short getShort( Map map, Object key ) {
        Number answer = getNumber( map, key );
        if ( answer == null ) {
            return null;
        }
        else
        if ( answer instanceof Short ) {
            return (Short) answer;
        }
        return new Short( answer.shortValue() );
    }

    public static Integer getInteger( Map map, Object key ) {
        Number answer = getNumber( map, key );
        if ( answer == null ) {
            return null;
        }
        else
        if ( answer instanceof Integer ) {
            return (Integer) answer;
        }
        return new Integer( answer.intValue() );
    }

    public static Long getLong( Map map, Object key ) {
        Number answer = getNumber( map, key );
        if ( answer == null ) {
            return null;
        }
        else
        if ( answer instanceof Long ) {
            return (Long) answer;
        }
        return new Long( answer.longValue() );
    }

    public static Float getFloat( Map map, Object key ) {
        Number answer = getNumber( map, key );
        if ( answer == null ) {
            return null;
        }
        else
        if ( answer instanceof Float ) {
            return (Float) answer;
        }
        return new Float( answer.floatValue() );
    }

    public static Double getDouble( Map map, Object key ) {
        Number answer = getNumber( map, key );
        if ( answer == null ) {
            return null;
        }
        else
        if ( answer instanceof Double ) {
            return (Double) answer;
        }
        return new Double( answer.doubleValue() );
    }

    public static Map getMap( Map map, Object key ) {
        if ( map != null ) {
            Object answer = map.get( key );
            if ( answer != null && answer instanceof Map ) {
                return (Map) answer;
            }
        }
        return null;
    }

    // Type safe getters with default values
    //-------------------------------------------------------------------------
    public static Object getObject( Map map, Object key, Object defaultValue ) {
        if ( map != null ) {
            Object answer = map.get( key );
            if ( answer != null ) {
                return answer;
            }
        }
        return defaultValue;
    }

    public static String getString( Map map, Object key, String defaultValue ) {
        String answer = getString( map, key );
        if ( answer == null ) {
            answer = defaultValue;
        }
        return answer;
    }

    public static Boolean getBoolean( Map map, Object key, Boolean defaultValue ) {
        Boolean answer = getBoolean( map, key );
        if ( answer == null ) {
            answer = defaultValue;
        }
        return answer;
    }

    public static Number getNumber( Map map, Object key, Number defaultValue ) {
        Number answer = getNumber( map, key );
        if ( answer == null ) {
            answer = defaultValue;
        }
        return answer;
    }

    public static Byte getByte( Map map, Object key, Byte defaultValue ) {
        Byte answer = getByte( map, key );
        if ( answer == null ) {
            answer = defaultValue;
        }
        return answer;
    }

    public static Short getShort( Map map, Object key, Short defaultValue ) {
        Short answer = getShort( map, key );
        if ( answer == null ) {
            answer = defaultValue;
        }
        return answer;
    }

    public static Integer getInteger( Map map, Object key, Integer defaultValue ) {
        Integer answer = getInteger( map, key );
        if ( answer == null ) {
            answer = defaultValue;
        }
        return answer;
    }

    public static Long getLong( Map map, Object key, Long defaultValue ) {
        Long answer = getLong( map, key );
        if ( answer == null ) {
            answer = defaultValue;
        }
        return answer;
    }

    public static Float getFloat( Map map, Object key, Float defaultValue ) {
        Float answer = getFloat( map, key );
        if ( answer == null ) {
            answer = defaultValue;
        }
        return answer;
    }

    public static Double getDouble( Map map, Object key, Double defaultValue ) {
        Double answer = getDouble( map, key );
        if ( answer == null ) {
            answer = defaultValue;
        }
        return answer;
    }

    public static Map getMap( Map map, Object key, Map defaultValue ) {
        Map answer = getMap( map, key );
        if ( answer == null ) {
            answer = defaultValue;
        }
        return answer;
    }

    // Conversion methods
    //-------------------------------------------------------------------------
    public static Properties toProperties(Map input) {
        Properties answer = new Properties();
        if ( input != null ) {
            for ( Iterator iter = input.entrySet().iterator(); iter.hasNext(); ) {
                Map.Entry entry = (Map.Entry) iter.next();
                Object key = entry.getKey();
                Object value = entry.getValue();
                answer.put(key, value);
            }
        }
        return answer;
    }

    // Printing methods
    //-------------------------------------------------------------------------
    public static synchronized void verbosePrint( PrintStream out, Object key, Map map ) {
        debugPrintIndent( out );
        out.println( key + " = " );

        debugPrintIndent( out );
        out.println( "{" );
        ++debugIndent;

        for ( Iterator iter = map.entrySet().iterator(); iter.hasNext(); ) {
            Map.Entry entry = (Map.Entry) iter.next();
            String childKey = (String) entry.getKey();
            Object childValue = entry.getValue();
            if ( childValue instanceof Map ) {
                verbosePrint( out, childKey, (Map) childValue );
            }
            else {
                debugPrintIndent( out );
                out.println( childKey + " = " + childValue);
            }
        }
        --debugIndent;
        debugPrintIndent( out );
        out.println( "}" );
    }

    public static synchronized void debugPrint( PrintStream out, Object key, Map map ) {
        debugPrintIndent( out );
        out.println( key + " = " );

        debugPrintIndent( out );
        out.println( "{" );
        ++debugIndent;

        for ( Iterator iter = map.entrySet().iterator(); iter.hasNext(); ) {
            Map.Entry entry = (Map.Entry) iter.next();
            String childKey = (String) entry.getKey();
            Object childValue = entry.getValue();
            if ( childValue instanceof Map ) {
                verbosePrint( out, childKey, (Map) childValue );
            }
            else {
                debugPrintIndent( out );

                String typeName = ( childValue != null )
                    ? childValue.getClass().getName()
                    : null;

                out.println( childKey + " = " + childValue + " class: " + typeName );
            }
        }
        --debugIndent;
        debugPrintIndent( out );
        out.println( "}" );
    }

    // Implementation methods
    //-------------------------------------------------------------------------
    protected static void debugPrintIndent( PrintStream out ) {
        for ( int i = 0; i < debugIndent; i++ ) {
            out.print( "    " );
        }
    }
    
    protected static void logInfo(Exception e) {
        // mapX: should probably use log4j here instead...
        System.out.println( "INFO: Exception: " + e );
    }


    /**
     * Nice method for adding data to a map in such a way
     * as to not get NPE's. The point being that if the
     * value is null, map.put() will throw an exception.
     * That blows in the case of this class cause you may want to
     * essentially treat put("Not Null", null ) == put("Not Null", "")
     * We will still throw a NPE if the key is null cause that should
     * never happen.
     */
    public static final void safeAddToMap(Map map, Object key, Object value)
        throws NullPointerException
    {
        if (value == null)
        {
            map.put ( key, "" );
        }
        else
        {
           map.put ( key, value );
        }
    }


    static class PredicatedMap extends ProxyMap {

        final protected Predicate keyPredicate;
        final protected Predicate valuePredicate;


        public PredicatedMap(Map map, Predicate keyPred, Predicate valuePred) {
            super(map);
            this.keyPredicate = keyPred;
            this.valuePredicate = valuePred;
            Iterator iter = map.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry entry = (Map.Entry)iter.next();
                Object key = entry.getKey();
                Object value = entry.getValue();
                validate(key, value);
            }
        }

        public Object put(Object key, Object value) {
            validate(key, value);
            return map.put(key, value);
        }

        public void putAll(Map m) {
            Iterator iter = m.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry entry = (Map.Entry)iter.next();
                Object key = entry.getKey();
                Object value = entry.getValue();
                validate(key, value);
            }
            map.putAll(m);
        }

        public Set entrySet() {
            return new PredicatedMapEntrySet(map.entrySet(), valuePredicate);
        }


        private void validate(Object key, Object value) {
            if (!keyPredicate.evaluate(key)) {
                throw new IllegalArgumentException("Invalid key.");
            }
            if (!valuePredicate.evaluate(value)) {
                throw new IllegalArgumentException("Invalid value.");
            }
        }
    }


    static class PredicatedMapEntrySet 
    extends CollectionUtils.CollectionWrapper
    implements Set {

        final private Predicate predicate;

        public PredicatedMapEntrySet(Set set, Predicate p) {
            super(set);
            this.predicate = p;
        }

        public Iterator iterator() {
            final Iterator iterator = collection.iterator();
            return new Iterator() {
                public boolean hasNext() {
                    return iterator.hasNext();
                }

                public Object next() {
                    Map.Entry entry = (Map.Entry)iterator.next();
                    return new PredicatedMapEntry(entry, predicate);
                }

                public void remove() {
                    iterator.remove();
                }
            };
        }
    }


    static class PredicatedMapEntry implements Map.Entry {

        final private Map.Entry entry;
        final private Predicate predicate;


        public PredicatedMapEntry(Map.Entry entry, Predicate p) {
            this.entry = entry;
            this.predicate = p;
        }

        public boolean equals(Object o) {
            return entry.equals(o);
        }

        public int hashCode() {
            return entry.hashCode();
        }

        public String toString() {
            return entry.toString();
        }

        public Object getKey() {
            return entry.getKey();
        }

        public Object getValue() {
            return entry.getValue();
        }

        public Object setValue(Object o) {
            if (!predicate.evaluate(o)) {
                throw new IllegalArgumentException("Invalid value.");
            }
            return entry.setValue(o);
        }
    }


    static class BoundedMap extends ProxyMap {

        final protected int maxSize;

        public BoundedMap(Map map, int maxSize) {
            super(map);
            this.maxSize = maxSize;
        }

        public Object put(Object key, Object value) {
            if (!containsKey(key)) validate(1);
            return map.put(key, value);
        }

        public void putAll(Map m) {
            int delta = 0;
            for (Iterator iter = m.keySet().iterator(); iter.hasNext(); ) {
                 if (!map.containsKey(iter.next())) delta++;
            }
            validate(delta);
            map.putAll(m);
        }


        protected void validate(int delta) {
            if (map.size() + delta > maxSize) {
                throw new IllegalStateException("Maximum size reached.");
            }
        }
    }


    static class FixedSizeMap extends ProxyMap {

        public FixedSizeMap(Map map) {
            super(map);
        }


        public Object put(Object key, Object value) {
            if (!map.containsKey(key)) {
                throw new IllegalArgumentException("Can't add new keys.");
            }
            return map.put(key, value);
        }


        public void putAll(Map m) {
            for (Iterator iter = m.keySet().iterator(); iter.hasNext(); ) {
                if (!map.containsKey(iter.next())) {
                    throw new IllegalArgumentException("Can't add new keys.");
                }
            }
            map.putAll(m);
        }

    }


    static class LazyMap extends ProxyMap {

        final protected SimpleObjectFactory factory;


        public LazyMap(Map map, SimpleObjectFactory factory) {
            super(map);
            this.factory = factory;
        }


        public Object get(Object key) {
            if (!map.containsKey(key)) {
                Object value = factory.createObject();
                map.put(key, value);
                return value;
            }
            return map.get(key);
        }

    }



    static class PredicatedSortedMap extends PredicatedMap 
    implements SortedMap {

        public PredicatedSortedMap(SortedMap map, Predicate k, Predicate v) {
            super(map, k, v);
        }

        public Object firstKey() {
            return getSortedMap().firstKey();
        }


        public Object lastKey() {
            return getSortedMap().lastKey();
        }


        public Comparator comparator() {
            return getSortedMap().comparator();
        }


        public SortedMap subMap(Object o1, Object o2) {
            SortedMap sub = getSortedMap().subMap(o1, o2);
            return new PredicatedSortedMap(sub, keyPredicate, valuePredicate);
        }

        public SortedMap headMap(Object o1) {
            SortedMap sub = getSortedMap().headMap(o1);
            return new PredicatedSortedMap(sub, keyPredicate, valuePredicate);
        }

        public SortedMap tailMap(Object o1) {
            SortedMap sub = getSortedMap().tailMap(o1);
            return new PredicatedSortedMap(sub, keyPredicate, valuePredicate);
        }

        private SortedMap getSortedMap() {
            return (SortedMap)map;
        }

    }


    static class FixedSizeSortedMap extends FixedSizeMap implements SortedMap {

        public FixedSizeSortedMap(SortedMap m) {
            super(m);
        }

        public Object firstKey() {
            return getSortedMap().firstKey();
        }


        public Object lastKey() {
            return getSortedMap().lastKey();
        }


        public Comparator comparator() {
            return getSortedMap().comparator();
        }


        public SortedMap subMap(Object o1, Object o2) {
            return new FixedSizeSortedMap(getSortedMap().subMap(o1, o2));
        }

        public SortedMap headMap(Object o1) {
            return new FixedSizeSortedMap(getSortedMap().headMap(o1));
        }

        public SortedMap tailMap(Object o1) {
            return new FixedSizeSortedMap(getSortedMap().tailMap(o1));
        }

        private SortedMap getSortedMap() {
            return (SortedMap)map;
        }

    }


    static class LazySortedMap extends LazyMap implements SortedMap {

        public LazySortedMap(SortedMap m, SimpleObjectFactory factory) {
            super(m, factory);
        }

        public Object firstKey() {
            return getSortedMap().firstKey();
        }


        public Object lastKey() {
            return getSortedMap().lastKey();
        }


        public Comparator comparator() {
            return getSortedMap().comparator();
        }


        public SortedMap subMap(Object o1, Object o2) {
            return new LazySortedMap(getSortedMap().subMap(o1, o2), factory);
        }

        public SortedMap headMap(Object o1) {
            return new LazySortedMap(getSortedMap().headMap(o1), factory);
        }

        public SortedMap tailMap(Object o1) {
            return new LazySortedMap(getSortedMap().tailMap(o1), factory);
        }

        private SortedMap getSortedMap() {
            return (SortedMap)map;
        }

    }


    /**
     *  Returns a predicated map backed by the given map.  Only keys and
     *  values that pass the given predicates can be added to the map.
     *  It is important not to use the original map after invoking this 
     *  method, as it is a backdoor for adding unvalidated objects.
     *
     *  @param map  the map to predicate
     *  @param keyPred  the predicate for keys
     *  @param valuePred  the predicate for values
     *  @return  a predicated map backed by the given map
     */
    public static Map predicatedMap(Map map, Predicate keyPred, Predicate valuePred) {
        return new PredicatedMap(map, keyPred, valuePred);
    }


    /**
     *  Returns a bounded map backed by the given map.
     *  New pairs may only be added to the returned map if its 
     *  size is less than the specified maximum; otherwise, an
     *  {@link IllegalStateException} will be thrown.
     *
     *  @param b  the map whose size to bind
     *  @param maxSize  the maximum size of the returned map
     *  @return  a bounded map 
     */
    public static Map boundedMap(Map map, int maxSize) {
        return new BoundedMap(map, maxSize);
    }


    /**
     *  Returns a fixed-sized map backed by the given map.
     *  Elements may not be added or removed from the returned map, but 
     *  existing elements can be changed (for instance, via the 
     *  {@link Map#put(Object,Object)} method).
     *
     *  @param map  the map whose size to fix
     *  @return  a fixed-size map backed by that map
     */
    public static Map fixedSizeMap(Map map) {
        return new FixedSizeMap(map);
    }


    /**
     *  Returns a "lazy" map whose values will be created on demand.<P>
     *  <P>
     *  When the key passed to the returned map's {@link Map#get(Object)}
     *  method is not present in the map, then the factory will be used
     *  to create a new object and that object will become the value
     *  associated with that key.
     *  <P>
     *  For instance:
     *
     *  <Pre>
     *  SimpleObjectFactory factory = new SimpleObjectFactory() {
     *      public Object createObject() {
     *          return new Date();
     *      }
     *  }
     *  Map lazy = MapUtils.lazyMap(new HashMap(), factory);
     *  Object obj = lazy.get("test");
     *  </Pre>
     *
     *  After the above code is executed, <Code>obj</Code> will contain
     *  a new <Code>Date</Code> instance.  Furthermore, that <Code>Date</Code>
     *  instance is the value for the <Code>test</Code> key.<P>
     *
     *  @param map  the map to make lazy
     *  @param factory  the factory for creating new objects
     *  @return a lazy map backed by the given map
     */
    public static Map lazyMap(Map map, SimpleObjectFactory factory) {
        return new LazyMap(map, factory);
    }


    /**
     *  Returns a predicated sorted map backed by the given map.  Only keys and
     *  values that pass the given predicates can be added to the map.
     *  It is important not to use the original map after invoking this 
     *  method, as it is a backdoor for adding unvalidated objects.
     *
     *  @param map  the map to predicate
     *  @param keyPred  the predicate for keys
     *  @param valuePred  the predicate for values
     *  @return  a predicated map backed by the given map
     */
    public static SortedMap predicatedSortedMap(SortedMap map, Predicate keyPred, Predicate valuePred) {
        return new PredicatedSortedMap(map, keyPred, valuePred);
    }


    /**
     *  Returns a fixed-sized sorted map backed by the given sorted map.
     *  Elements may not be added or removed from the returned map, but 
     *  existing elements can be changed (for instance, via the 
     *  {@link Map#put(Object,Object)} method).
     *
     *  @param map  the map whose size to fix
     *  @return  a fixed-size map backed by that map
     */
    public static SortedMap fixedSizeSortedMap(SortedMap map) {
        return new FixedSizeSortedMap(map);
    }


    /**
     *  Returns a "lazy" sorted map whose values will be created on demand.
     *  <P>
     *  When the key passed to the returned map's {@link Map#get(Object)}
     *  method is not present in the map, then the factory will be used
     *  to create a new object and that object will become the value
     *  associated with that key.
     *  <P>
     *  For instance:
     *
     *  <Pre>
     *  SimpleObjectFactory factory = new SimpleObjectFactory() {
     *      public Object createObject() {
     *          return new Date();
     *      }
     *  }
     *  SortedMap lazy = MapUtils.lazySortedMap(new TreeMap(), factory);
     *  Object obj = lazy.get("test");
     *  </Pre>
     *
     *  After the above code is executed, <Code>obj</Code> will contain
     *  a new <Code>Date</Code> instance.  Furthermore, that <Code>Date</Code>
     *  instance is the value for the <Code>test</Code> key.<P>
     *
     *  @param map  the map to make lazy
     *  @param factory  the factory for creating new objects
     *  @return a lazy map backed by the given map
     */
    public static SortedMap lazySortedMap(SortedMap map, SimpleObjectFactory factory) {
        return new LazySortedMap(map, factory);
    }
}
