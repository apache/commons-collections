/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/java/org/apache/commons/collections/MapUtils.java,v 1.36 2003/09/20 12:03:52 scolebourne Exp $
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

import java.io.PrintStream;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.collections.decorators.FixedSizeMap;
import org.apache.commons.collections.decorators.FixedSizeSortedMap;
import org.apache.commons.collections.decorators.LazyMap;
import org.apache.commons.collections.decorators.LazySortedMap;
import org.apache.commons.collections.decorators.PredicatedMap;
import org.apache.commons.collections.decorators.PredicatedSortedMap;
import org.apache.commons.collections.decorators.TransformedMap;
import org.apache.commons.collections.decorators.TransformedSortedMap;
import org.apache.commons.collections.decorators.TypedMap;
import org.apache.commons.collections.decorators.TypedSortedMap;

/** 
 * Provides useful utility methods for {@link Map Map} instances.
 * <p>
 * It contains various typesafe methods
 * as well as other useful features like deep copying.
 * <p>
 * It also provides the following decorators:
 *
 *  <ul>
 *  <li>{@link #fixedSizeMap(Map)}
 *  <li>{@link #fixedSizeSortedMap(SortedMap)}
 *  <li>{@link #lazyMap(Map,Factory)}
 *  <li>{@link #lazyMap(Map,Transformer)}
 *  <li>{@link #lazySortedMap(SortedMap,Factory)}
 *  <li>{@link #lazySortedMap(SortedMap,Transformer)}
 *  <li>{@link #predicatedMap(Map,Predicate,Predicate)}
 *  <li>{@link #predicatedSortedMap(SortedMap,Predicate,Predicate)}
 *  <li>{@link #transformedMap(Map, Transformer, Transformer)}
 *  <li>{@link #transformedSortedMap(SortedMap, Transformer, Transformer)}
 *  <li>{@link #typedMap(Map, Class, Class)}
 *  <li>{@link #typedSortedMap(SortedMap, Class, Class)}
 *  </ul>
 *
 * @since Commons Collections 1.0
 * @version $Revision: 1.36 $ $Date: 2003/09/20 12:03:52 $
 * 
 * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
 * @author <a href="mailto:nissim@nksystems.com">Nissim Karpenstein</a>
 * @author <a href="mailto:knielsen@apache.org">Kasper Nielsen</a>
 * @author Paul Jack
 * @author Stephen Colebourne
 * @author Matthew Hawthorne
 * @author Arun Mammen Thomas
 * @author Janek Bogucki
 * @author Max Rydahl Andersen
 */
public class MapUtils {
    
    /**
     * An empty unmodifiable map.
     * This was not provided in JDK1.2.
     */
    public static final Map EMPTY_MAP = Collections.unmodifiableMap(new HashMap(1));
    /**
     * An empty unmodifiable sorted map.
     * This is not provided in the JDK.
     */
    public static final SortedMap EMPTY_SORTED_MAP = Collections.unmodifiableSortedMap(new TreeMap());
    /**
     * String used to indent the verbose and debug Map prints.
     */
    private static final String INDENT_STRING = "    ";

    /**
     * <code>MapUtils</code> should not normally be instantiated.
     */
    public MapUtils() {
    }    
    
    // Type safe getters
    //-------------------------------------------------------------------------
    /**
     * Gets from a Map in a null-safe manner.
     *
     * @param map  the map to use
     * @param key  the key to look up
     * @return the value in the Map, <code>null</code> if null map input
     */
    public static Object getObject(final Map map, final Object key) {
        if (map != null) {
            return map.get(key);
        }
        return null;
    }

    /**
     * Gets a String from a Map in a null-safe manner.
     * <p>
     * The String is obtained via <code>toString</code>.
     *
     * @param map  the map to use
     * @param key  the key to look up
     * @return the value in the Map as a String, <code>null</code> if null map input
     */
    public static String getString(final Map map, final Object key) {
        if (map != null) {
            Object answer = map.get(key);
            if (answer != null) {
                return answer.toString();
            }
        }
        return null;
    }

    /**
     * Gets a Boolean from a Map in a null-safe manner.
     * <p>
     * If the value is a <code>Boolean</code> it is returned directly.
     * If the value is a <code>String</code> and it equals 'true' ignoring case
     * then <code>true</code> is returned, otherwise <code>false</code>.
     * If the value is a <code>Number</code> an integer zero value returns
     * <code>false</code> and non-zero returns <code>true</code>.
     * Otherwise, <code>null</code> is returned.
     *
     * @param map  the map to use
     * @param key  the key to look up
     * @return the value in the Map as a Boolean, <code>null</code> if null map input
     */
    public static Boolean getBoolean(final Map map, final Object key) {
        if (map != null) {
            Object answer = map.get(key);
            if (answer != null) {
                if (answer instanceof Boolean) {
                    return (Boolean) answer;
                    
                } else if (answer instanceof String) {
                    return new Boolean((String) answer);
                    
                } else if (answer instanceof Number) {
                    Number n = (Number) answer;
                    return (n.intValue() != 0) ? Boolean.TRUE : Boolean.FALSE;
                }
            }
        }
        return null;
    }

    /**
     * Gets a Number from a Map in a null-safe manner.
     * <p>
     * If the value is a <code>Number</code> it is returned directly.
     * If the value is a <code>String</code> it is converted using
     * {@link NumberFormat#parse(String)} on the system default formatter
     * returning <code>null</code> if the conversion fails.
     * Otherwise, <code>null</code> is returned.
     *
     * @param map  the map to use
     * @param key  the key to look up
     * @return the value in the Map as a Number, <code>null</code> if null map input
     */
    public static Number getNumber(final Map map, final Object key) {
        if (map != null) {
            Object answer = map.get(key);
            if (answer != null) {
                if (answer instanceof Number) {
                    return (Number) answer;
                    
                } else if (answer instanceof String) {
                    try {
                        String text = (String) answer;
                        return NumberFormat.getInstance().parse(text);
                        
                    } catch (ParseException e) {
                        logInfo(e);
                    }
                }
            }
        }
        return null;
    }

    /**
     * Gets a Byte from a Map in a null-safe manner.
     * <p>
     * The Byte is obtained from the results of {@link #getNumber(Map,Object)}.
     *
     * @param map  the map to use
     * @param key  the key to look up
     * @return the value in the Map as a Byte, <code>null</code> if null map input
     */
    public static Byte getByte(final Map map, final Object key) {
        Number answer = getNumber(map, key);
        if (answer == null) {
            return null;
        } else if (answer instanceof Byte) {
            return (Byte) answer;
        }
        return new Byte(answer.byteValue());
    }

    /**
     * Gets a Short from a Map in a null-safe manner.
     * <p>
     * The Short is obtained from the results of {@link #getNumber(Map,Object)}.
     *
     * @param map  the map to use
     * @param key  the key to look up
     * @return the value in the Map as a Short, <code>null</code> if null map input
     */
    public static Short getShort(final Map map, final Object key) {
        Number answer = getNumber(map, key);
        if (answer == null) {
            return null;
        } else if (answer instanceof Short) {
            return (Short) answer;
        }
        return new Short(answer.shortValue());
    }

    /**
     * Gets a Integer from a Map in a null-safe manner.
     * <p>
     * The Integer is obtained from the results of {@link #getNumber(Map,Object)}.
     *
     * @param map  the map to use
     * @param key  the key to look up
     * @return the value in the Map as a Integer, <code>null</code> if null map input
     */
    public static Integer getInteger(final Map map, final Object key) {
        Number answer = getNumber(map, key);
        if (answer == null) {
            return null;
        } else if (answer instanceof Integer) {
            return (Integer) answer;
        }
        return new Integer(answer.intValue());
    }

    /**
     * Gets a Long from a Map in a null-safe manner.
     * <p>
     * The Long is obtained from the results of {@link #getNumber(Map,Object)}.
     *
     * @param map  the map to use
     * @param key  the key to look up
     * @return the value in the Map as a Long, <code>null</code> if null map input
     */
    public static Long getLong(final Map map, final Object key) {
        Number answer = getNumber(map, key);
        if (answer == null) {
            return null;
        } else if (answer instanceof Long) {
            return (Long) answer;
        }
        return new Long(answer.longValue());
    }

    /**
     * Gets a Float from a Map in a null-safe manner.
     * <p>
     * The Float is obtained from the results of {@link #getNumber(Map,Object)}.
     *
     * @param map  the map to use
     * @param key  the key to look up
     * @return the value in the Map as a Float, <code>null</code> if null map input
     */
    public static Float getFloat(final Map map, final Object key) {
        Number answer = getNumber(map, key);
        if (answer == null) {
            return null;
        } else if (answer instanceof Float) {
            return (Float) answer;
        }
        return new Float(answer.floatValue());
    }

    /**
     * Gets a Double from a Map in a null-safe manner.
     * <p>
     * The Double is obtained from the results of {@link #getNumber(Map,Object)}.
     *
     * @param map  the map to use
     * @param key  the key to look up
     * @return the value in the Map as a Double, <code>null</code> if null map input
     */
    public static Double getDouble(final Map map, final Object key) {
        Number answer = getNumber(map, key);
        if (answer == null) {
            return null;
        } else if (answer instanceof Double) {
            return (Double) answer;
        }
        return new Double(answer.doubleValue());
    }

    /**
     * Gets a Map from a Map in a null-safe manner.
     * <p>
     * If the value returned from the specified map is not a Map then
     * <code>null</code> is returned.
     *
     * @param map  the map to use
     * @param key  the key to look up
     * @return the value in the Map as a Map, <code>null</code> if null map input
     */
    public static Map getMap(final Map map, final Object key) {
        if (map != null) {
            Object answer = map.get(key);
            if (answer != null && answer instanceof Map) {
                return (Map) answer;
            }
        }
        return null;
    }

    // Type safe getters with default values
    //-------------------------------------------------------------------------
    /**
     *  Looks up the given key in the given map, converting null into the
     *  given default value.
     *
     *  @param map  the map whose value to look up
     *  @param key  the key of the value to look up in that map
     *  @param defaultValue  what to return if the value is null
     *  @return  the value in the map, or defaultValue if the original value
     *    is null or the map is null
     */
    public static Object getObject( Map map, Object key, Object defaultValue ) {
        if ( map != null ) {
            Object answer = map.get( key );
            if ( answer != null ) {
                return answer;
            }
        }
        return defaultValue;
    }

    /**
     *  Looks up the given key in the given map, converting the result into
     *  a string, using the default value if the the conversion fails.
     *
     *  @param map  the map whose value to look up
     *  @param key  the key of the value to look up in that map
     *  @param defaultValue  what to return if the value is null or if the
     *     conversion fails
     *  @return  the value in the map as a string, or defaultValue if the 
     *    original value is null, the map is null or the string conversion
     *    fails
     */
    public static String getString( Map map, Object key, String defaultValue ) {
        String answer = getString( map, key );
        if ( answer == null ) {
            answer = defaultValue;
        }
        return answer;
    }

    /**
     *  Looks up the given key in the given map, converting the result into
     *  a boolean, using the default value if the the conversion fails.
     *
     *  @param map  the map whose value to look up
     *  @param key  the key of the value to look up in that map
     *  @param defaultValue  what to return if the value is null or if the
     *     conversion fails
     *  @return  the value in the map as a boolean, or defaultValue if the 
     *    original value is null, the map is null or the boolean conversion
     *    fails
     */
    public static Boolean getBoolean( Map map, Object key, Boolean defaultValue ) {
        Boolean answer = getBoolean( map, key );
        if ( answer == null ) {
            answer = defaultValue;
        }
        return answer;
    }

    /**
     *  Looks up the given key in the given map, converting the result into
     *  a number, using the default value if the the conversion fails.
     *
     *  @param map  the map whose value to look up
     *  @param key  the key of the value to look up in that map
     *  @param defaultValue  what to return if the value is null or if the
     *     conversion fails
     *  @return  the value in the map as a number, or defaultValue if the 
     *    original value is null, the map is null or the number conversion
     *    fails
     */
    public static Number getNumber( Map map, Object key, Number defaultValue ) {
        Number answer = getNumber( map, key );
        if ( answer == null ) {
            answer = defaultValue;
        }
        return answer;
    }

    /**
     *  Looks up the given key in the given map, converting the result into
     *  a byte, using the default value if the the conversion fails.
     *
     *  @param map  the map whose value to look up
     *  @param key  the key of the value to look up in that map
     *  @param defaultValue  what to return if the value is null or if the
     *     conversion fails
     *  @return  the value in the map as a number, or defaultValue if the 
     *    original value is null, the map is null or the number conversion
     *    fails
     */
    public static Byte getByte( Map map, Object key, Byte defaultValue ) {
        Byte answer = getByte( map, key );
        if ( answer == null ) {
            answer = defaultValue;
        }
        return answer;
    }

    /**
     *  Looks up the given key in the given map, converting the result into
     *  a short, using the default value if the the conversion fails.
     *
     *  @param map  the map whose value to look up
     *  @param key  the key of the value to look up in that map
     *  @param defaultValue  what to return if the value is null or if the
     *     conversion fails
     *  @return  the value in the map as a number, or defaultValue if the 
     *    original value is null, the map is null or the number conversion
     *    fails
     */
    public static Short getShort( Map map, Object key, Short defaultValue ) {
        Short answer = getShort( map, key );
        if ( answer == null ) {
            answer = defaultValue;
        }
        return answer;
    }

    /**
     *  Looks up the given key in the given map, converting the result into
     *  an integer, using the default value if the the conversion fails.
     *
     *  @param map  the map whose value to look up
     *  @param key  the key of the value to look up in that map
     *  @param defaultValue  what to return if the value is null or if the
     *     conversion fails
     *  @return  the value in the map as a number, or defaultValue if the 
     *    original value is null, the map is null or the number conversion
     *    fails
     */
    public static Integer getInteger( Map map, Object key, Integer defaultValue ) {
        Integer answer = getInteger( map, key );
        if ( answer == null ) {
            answer = defaultValue;
        }
        return answer;
    }

    /**
     *  Looks up the given key in the given map, converting the result into
     *  a long, using the default value if the the conversion fails.
     *
     *  @param map  the map whose value to look up
     *  @param key  the key of the value to look up in that map
     *  @param defaultValue  what to return if the value is null or if the
     *     conversion fails
     *  @return  the value in the map as a number, or defaultValue if the 
     *    original value is null, the map is null or the number conversion
     *    fails
     */
    public static Long getLong( Map map, Object key, Long defaultValue ) {
        Long answer = getLong( map, key );
        if ( answer == null ) {
            answer = defaultValue;
        }
        return answer;
    }

    /**
     *  Looks up the given key in the given map, converting the result into
     *  a float, using the default value if the the conversion fails.
     *
     *  @param map  the map whose value to look up
     *  @param key  the key of the value to look up in that map
     *  @param defaultValue  what to return if the value is null or if the
     *     conversion fails
     *  @return  the value in the map as a number, or defaultValue if the 
     *    original value is null, the map is null or the number conversion
     *    fails
     */
    public static Float getFloat( Map map, Object key, Float defaultValue ) {
        Float answer = getFloat( map, key );
        if ( answer == null ) {
            answer = defaultValue;
        }
        return answer;
    }

    /**
     *  Looks up the given key in the given map, converting the result into
     *  a double, using the default value if the the conversion fails.
     *
     *  @param map  the map whose value to look up
     *  @param key  the key of the value to look up in that map
     *  @param defaultValue  what to return if the value is null or if the
     *     conversion fails
     *  @return  the value in the map as a number, or defaultValue if the 
     *    original value is null, the map is null or the number conversion
     *    fails
     */
    public static Double getDouble( Map map, Object key, Double defaultValue ) {
        Double answer = getDouble( map, key );
        if ( answer == null ) {
            answer = defaultValue;
        }
        return answer;
    }

    /**
     *  Looks up the given key in the given map, converting the result into
     *  a map, using the default value if the the conversion fails.
     *
     *  @param map  the map whose value to look up
     *  @param key  the key of the value to look up in that map
     *  @param defaultValue  what to return if the value is null or if the
     *     conversion fails
     *  @return  the value in the map as a number, or defaultValue if the 
     *    original value is null, the map is null or the map conversion
     *    fails
     */
    public static Map getMap( Map map, Object key, Map defaultValue ) {
        Map answer = getMap( map, key );
        if ( answer == null ) {
            answer = defaultValue;
        }
        return answer;
    }

    // Conversion methods
    //-------------------------------------------------------------------------
    /**
     * Gets a new Properties object initialised with the values from a Map.
     * A null input will return an empty properties object.
     * 
     * @param map  the map to convert to a Properties object, may not be null
     * @return the properties object
     */
    public static Properties toProperties(final Map map) {
        Properties answer = new Properties();
        if (map != null) {
            for (Iterator iter = map.entrySet().iterator(); iter.hasNext();) {
                Map.Entry entry = (Map.Entry) iter.next();
                Object key = entry.getKey();
                Object value = entry.getValue();
                answer.put(key, value);
            }
        }
        return answer;
    }

    /**
     * Creates a new HashMap using data copied from a ResourceBundle.
     * 
     * @param resourceBundle  the resource bundle to convert, may not be null
     * @return the hashmap containing the data
     * @throws NullPointerException if the bundle is null
     */
    public static Map toMap(final ResourceBundle resourceBundle) {
        Enumeration enum = resourceBundle.getKeys();
        Map map = new HashMap();

        while (enum.hasMoreElements()) {
            String key = (String) enum.nextElement();
            Object value = resourceBundle.getObject(key);
            map.put(key, value);
        }
        
        return map;
    }
 
    // Printing methods
    //-------------------------------------------------------------------------
    /**
     * Prints the given map with nice line breaks.
     * <p>
     * This method prints a nicely formatted String describing the Map.
     * Each map entry will be printed with key and value.
     * When the value is a Map, recursive behaviour occurs.
     * <p>
     * This method is NOT thread-safe in any special way. You must manually
     * synchronize on either this class or the stream as required.
     *
     * @param out  the stream to print to, must not be null
     * @param label  The label to be used, may be <code>null</code>.
     *  If <code>null</code>, the label is not output.
     *  It typically represents the name of the property in a bean or similar.
     * @param map  The map to print, may be <code>null</code>.
     *  If <code>null</code>, the text 'null' is output.
     * @throws NullPointerException if the stream is <code>null</code>
     */
    public static void verbosePrint(
        final PrintStream out,
        final Object label,
        final Map map) {

        verbosePrintInternal(out, label, map, new ArrayStack(), false);
    }

    /**
     * Prints the given map with nice line breaks.
     * <p>
     * This method prints a nicely formatted String describing the Map.
     * Each map entry will be printed with key, value and value classname.
     * When the value is a Map, recursive behaviour occurs.
     * <p>
     * This method is NOT thread-safe in any special way. You must manually
     * synchronize on either this class or the stream as required.
     *
     * @param out  the stream to print to, must not be null
     * @param label  The label to be used, may be <code>null</code>.
     *  If <code>null</code>, the label is not output.
     *  It typically represents the name of the property in a bean or similar.
     * @param map  The map to print, may be <code>null</code>.
     *  If <code>null</code>, the text 'null' is output.
     * @throws NullPointerException if the stream is <code>null</code>
     */
    public static void debugPrint(
        final PrintStream out,
        final Object label,
        final Map map) {

        verbosePrintInternal(out, label, map, new ArrayStack(), true);
    }

    // Implementation methods
    //-------------------------------------------------------------------------
    /**
     * Logs the given exception to <code>System.out</code>.
     * <p>
     * This method exists as Jakarta Collections does not depend on logging.
     *
     * @param ex  the exception to log
     */
    protected static void logInfo(final Exception ex) {
        System.out.println("INFO: Exception: " + ex);
    }

    /**
     * Implementation providing functionality for {@link #debugPrint} and for 
     * {@link #verbosePrint}.  This prints the given map with nice line breaks.
     * If the debug flag is true, it additionally prints the type of the object 
     * value.  If the contents of a map include the map itself, then the text 
     * <em>(this Map)</em> is printed out.  If the contents include a 
     * parent container of the map, the the text <em>(ancestor[i] Map)</em> is 
     * printed, where i actually indicates the number of levels which must be 
     * traversed in the sequential list of ancesters (e.g. father, grandfather, 
     * great-grandfather, etc).  
     *
     * @param out  the stream to print to
     * @param label  the label to be used, may be <code>null</code>.
     *  If <code>null</code>, the label is not output.
     *  It typically represents the name of the property in a bean or similar.
     * @param map  the map to print, may be <code>null</code>.
     *  If <code>null</code>, the text 'null' is output
     * @param lineage  a stack consisting of any maps in which the previous 
     *  argument is contained. This is checked to avoid infinite recursion when
     *  printing the output
     * @param debug  flag indicating whether type names should be output.
     * @throws NullPointerException if the stream is <code>null</code>
     */
    private static void verbosePrintInternal(
        final PrintStream out,
        final Object label,
        final Map map,
        final ArrayStack lineage,
        final boolean debug) {
        
        printIndent(out, lineage.size());

        if (map == null) {
            if (label != null) {
                out.print(label);
                out.print(" = ");
            }
            out.println("null");
            return;
        }
        if (label != null) {
            out.print(label);
            out.println(" = ");
        }

        printIndent(out, lineage.size());
        out.println("{");

        lineage.push(map);

        for (Iterator it = map.entrySet().iterator(); it.hasNext();) {
            Map.Entry entry = (Map.Entry) it.next();
            Object childKey = entry.getKey();
            Object childValue = entry.getValue();
            if (childValue instanceof Map && !lineage.contains(childValue)) {
                verbosePrintInternal(
                    out,
                    (childKey == null ? "null" : childKey),
                    (Map) childValue,
                    lineage,
                    debug);
            } else {
                printIndent(out, lineage.size());
                out.print(childKey);
                out.print(" = ");
                
                final int lineageIndex = lineage.indexOf(childValue);
                if (lineageIndex == -1) {
                    out.print(childValue);
                } else if (lineage.size() - 1 == lineageIndex) {
                    out.print("(this Map)");    
                } else {
                    out.print(
                        "(ancestor["
                            + (lineage.size() - 1 - lineageIndex - 1)
                            + "] Map)");
                }
                
                if (debug && childValue != null) {
                    out.print(' ');
                    out.println(childValue.getClass().getName());
                } else {
                    out.println();
                }
            }
        }
        
        lineage.pop();

        printIndent(out, lineage.size());
        out.println(debug ? "} " + map.getClass().getName() : "}");
    }

    /**
     * Writes indentation to the given stream.
     *
     * @param out  the stream to indent
     */
    private static void printIndent(final PrintStream out, final int indent) {
        for (int i = 0; i < indent; i++) {
            out.print(INDENT_STRING);
        }
    }
    
    // Misc
    //-----------------------------------------------------------------------
    /**
     * Inverts the supplied map returning a new HashMap such that the keys of
     * the input are swapped with the values.
     * <p>
     * This operation assumes that the inverse mapping is well defined.
     * If the input map had multiple entries with the same value mapped to
     * different keys, the returned map will map one of those keys to the 
     * value, but the exact key which will be mapped is undefined.
     * 
     * @see DoubleOrderedMap
     * @param map  the map to invert, may not be null
     * @return a new HashMap containing the inverted data
     * @throws NullPointerException if the map is null
     */
    public static Map invertMap(Map map) {
        Map out = new HashMap(map.size());
        for (Iterator it = map.entrySet().iterator(); it.hasNext();) {
            Map.Entry entry = (Map.Entry) it.next();
            out.put(entry.getValue(), entry.getKey());
        }
        return out;
    }
     
    /**
     * Nice method for adding data to a map in such a way
     * as to not get NPE's. The point being that if the
     * value is null, map.put() will throw an exception.
     * That blows in the case of this class cause you may want to
     * essentially treat put("Not Null", null ) == put("Not Null", "")
     * We will still throw a NPE if the key is null cause that should
     * never happen.
     * 
     * @param map  the map to add to, may not be null
     * @param key  the key
     * @param value  the value
     * @throws NullPointerException if the map is null
     */
    public static void safeAddToMap(Map map, Object key, Object value) throws NullPointerException {
        if (value == null) {
            map.put ( key, "" );
        } else {
           map.put ( key, value );
        }
    }

    // Map decorators
    //-----------------------------------------------------------------------
    /**
     * Returns a synchronized map backed by the given map.
     * <p>
     * You must manually synchronize on the returned buffer's iterator to 
     * avoid non-deterministic behavior:
     *  
     * <pre>
     * Map m = MapUtils.synchronizedMap(myMap);
     * Set s = m.keySet();  // outside synchronized block
     * synchronized (m) {  // synchronized on MAP!
     *     Iterator i = s.iterator();
     *     while (i.hasNext()) {
     *         process (i.next());
     *     }
     * }
     * </pre>
     * 
     * This method uses the implementation in {@link java.util.Collections Collections}.
     * 
     * @param map  the map to synchronize, must not be null
     * @return a synchronized map backed by the given map
     * @throws IllegalArgumentException  if the map is null
     */
    public static Map synchronizedMap(Map map) {
        return Collections.synchronizedMap(map);
    }

    /**
     * Returns an unmodifiable map backed by the given map.
     * <p>
     * This method uses the implementation in {@link java.util.Collections Collections}.
     *
     * @param map  the map to make unmodifiable, must not be null
     * @return an unmodifiable map backed by the given map
     * @throws IllegalArgumentException  if the map is null
     */
    public static Map unmodifiableMap(Map map) {
        return Collections.unmodifiableMap(map);
    }

    /**
     * Returns a predicated map backed by the given map.  Only keys and
     * values that pass the given predicates can be added to the map.
     * It is important not to use the original map after invoking this 
     * method, as it is a backdoor for adding unvalidated objects.
     *
     * @param map  the map to predicate, must not be null
     * @param keyPred  the predicate for keys, null means no check
     * @param valuePred  the predicate for values, null means no check
     * @return a predicated map backed by the given map
     * @throws IllegalArgumentException  if the Map is null
     */
    public static Map predicatedMap(Map map, Predicate keyPred, Predicate valuePred) {
        return PredicatedMap.decorate(map, keyPred, valuePred);
    }

    /**
     * Returns a typed map backed by the given map.
     * <p>
     * Only keys and values of the specified types can be added to the map.
     * 
     * @param map  the map to limit to a specific type, must not be null
     * @param keyType  the type of keys which may be added to the map, must not be null
     * @param valueType  the type of values which may be added to the map, must not be null
     * @return a typed map backed by the specified map
     * @throws IllegalArgumentException  if the Map or Class is null
     */
    public static Map typedMap(Map map, Class keyType, Class valueType) {
        return TypedMap.decorate(map, keyType, valueType);
    }
    
    /**
     * Returns a transformed map backed by the given map.
     * <p>
     * Each object is passed through the transformers as it is added to the
     * Map. It is important not to use the original map after invoking this 
     * method, as it is a backdoor for adding untransformed objects.
     *
     * @param map  the map to transform, must not be null
     * @param keyTransformer  the transformer for the map keys, null means no transformation
     * @param valueTransformer  the transformer for the map values, null means no transformation
     * @return a transformed map backed by the given map
     * @throws IllegalArgumentException  if the Map is null
     */
    public static Map transformedMap(Map map, Transformer keyTransformer, Transformer valueTransformer) {
        return TransformedMap.decorate(map, keyTransformer, valueTransformer);
    }
    
    /**
     * Returns a fixed-sized map backed by the given map.
     * Elements may not be added or removed from the returned map, but 
     * existing elements can be changed (for instance, via the 
     * {@link Map#put(Object,Object)} method).
     *
     * @param map  the map whose size to fix, must not be null
     * @return a fixed-size map backed by that map
     * @throws IllegalArgumentException  if the Map is null
     */
    public static Map fixedSizeMap(Map map) {
        return FixedSizeMap.decorate(map);
    }

    /**
     * Returns a "lazy" map whose values will be created on demand.
     * <p>
     * When the key passed to the returned map's {@link Map#get(Object)}
     * method is not present in the map, then the factory will be used
     * to create a new object and that object will become the value
     * associated with that key.
     * <p>
     * For instance:
     * <pre>
     * Factory factory = new Factory() {
     *     public Object create() {
     *         return new Date();
     *     }
     * }
     * Map lazyMap = MapUtils.lazyMap(new HashMap(), factory);
     * Object obj = lazyMap.get("test");
     * </pre>
     *
     * After the above code is executed, <code>obj</code> will contain
     * a new <code>Date</code> instance.  Furthermore, that <code>Date</code>
     * instance is the value for the <code>"test"</code> key in the map.
     *
     * @param map  the map to make lazy, must not be null
     * @param factory  the factory for creating new objects, must not be null
     * @return a lazy map backed by the given map
     * @throws IllegalArgumentException  if the Map or Factory is null
     */
    public static Map lazyMap(Map map, Factory factory) {
        return LazyMap.decorate(map, factory);
    }

    /**
     * Returns a "lazy" map whose values will be created on demand.
     * <p>
     * When the key passed to the returned map's {@link Map#get(Object)}
     * method is not present in the map, then the factory will be used
     * to create a new object and that object will become the value
     * associated with that key. The factory is a {@link Transformer}
     * that will be passed the key which it must transform into the value.
     * <p>
     * For instance:
     * <pre>
     * Transformer factory = new Transformer() {
     *     public Object transform(Object mapKey) {
     *         return new File(mapKey);
     *     }
     * }
     * Map lazyMap = MapUtils.lazyMap(new HashMap(), factory);
     * Object obj = lazyMap.get("C:/dev");
     * </pre>
     *
     * After the above code is executed, <code>obj</code> will contain
     * a new <code>File</code> instance for the C drive dev directory.
     * Furthermore, that <code>File</code> instance is the value for the
     * <code>"C:/dev"</code> key in the map.
     * <p>
     * If a lazy map is wrapped by a synchronized map, the result is a simple
     * synchronized cache. When an object is not is the cache, the cache itself
     * calls back to the factory Transformer to populate itself, all within the
     * same synchronized block.
     *
     * @param map  the map to make lazy, must not be null
     * @param transformerFactory  the factory for creating new objects, must not be null
     * @return a lazy map backed by the given map
     * @throws IllegalArgumentException  if the Map or Transformer is null
     */
    public static Map lazyMap(Map map, Transformer transformerFactory) {
        return LazyMap.decorate(map, transformerFactory);
    }

    // SortedMap decorators
    //-----------------------------------------------------------------------
    /**
     * Returns a synchronized sorted map backed by the given sorted map.
     * <p>
     * You must manually synchronize on the returned buffer's iterator to 
     * avoid non-deterministic behavior:
     *  
     * <pre>
     * Map m = MapUtils.synchronizedSortedMap(myMap);
     * Set s = m.keySet();  // outside synchronized block
     * synchronized (m) {  // synchronized on MAP!
     *     Iterator i = s.iterator();
     *     while (i.hasNext()) {
     *         process (i.next());
     *     }
     * }
     * </pre>
     * 
     * This method uses the implementation in {@link java.util.Collections Collections}.
     * 
     * @param map  the map to synchronize, must not be null
     * @return a synchronized map backed by the given map
     * @throws IllegalArgumentException  if the map is null
     */
    public static Map synchronizedSortedMap(SortedMap map) {
        return Collections.synchronizedSortedMap(map);
    }

    /**
     * Returns an unmodifiable sorted map backed by the given sorted map.
     * <p>
     * This method uses the implementation in {@link java.util.Collections Collections}.
     *
     * @param map  the sorted map to make unmodifiable, must not be null
     * @return an unmodifiable map backed by the given map
     * @throws IllegalArgumentException  if the map is null
     */
    public static Map unmodifiableSortedMap(SortedMap map) {
        return Collections.unmodifiableSortedMap(map);
    }

    /**
     * Returns a predicated sorted map backed by the given map.  Only keys and
     * values that pass the given predicates can be added to the map.
     * It is important not to use the original map after invoking this 
     * method, as it is a backdoor for adding unvalidated objects.
     *
     * @param map  the map to predicate, must not be null
     * @param keyPred  the predicate for keys, null means no check
     * @param valuePred  the predicate for values, null means no check
     * @return a predicated map backed by the given map
     * @throws IllegalArgumentException  if the SortedMap is null
     */
    public static SortedMap predicatedSortedMap(SortedMap map, Predicate keyPred, Predicate valuePred) {
        return PredicatedSortedMap.decorate(map, keyPred, valuePred);
    }

    /**
     * Returns a typed sorted map backed by the given map.
     * <p>
     * Only keys and values of the specified types can be added to the map.
     * 
     * @param map  the map to limit to a specific type, must not be null
     * @param keyType  the type of keys which may be added to the map, must not be null
     * @param valueType  the type of values which may be added to the map, must not be null
     * @return a typed map backed by the specified map
     */
    public static SortedMap typedSortedMap(SortedMap map, Class keyType, Class valueType) {
        return TypedSortedMap.decorate(map, keyType, valueType);
    }
    
    /**
     * Returns a transformed sorted map backed by the given map.
     * <p>
     * Each object is passed through the transformers as it is added to the
     * Map. It is important not to use the original map after invoking this 
     * method, as it is a backdoor for adding untransformed objects.
     *
     * @param map  the map to transform, must not be null
     * @param keyTransformer  the transformer for the map keys, null means no transformation
     * @param valueTransformer  the transformer for the map values, null means no transformation
     * @return a transformed map backed by the given map
     * @throws IllegalArgumentException  if the SortedMap is null
     */
    public static SortedMap transformedSortedMap(SortedMap map, Transformer keyTransformer, Transformer valueTransformer) {
        return TransformedSortedMap.decorate(map, keyTransformer, valueTransformer);
    }
    
    /**
     * Returns a fixed-sized sorted map backed by the given sorted map.
     * Elements may not be added or removed from the returned map, but 
     * existing elements can be changed (for instance, via the 
     * {@link Map#put(Object,Object)} method).
     *
     * @param map  the map whose size to fix, must not be null
     * @return a fixed-size map backed by that map
     * @throws IllegalArgumentException  if the SortedMap is null
     */
    public static SortedMap fixedSizeSortedMap(SortedMap map) {
        return FixedSizeSortedMap.decorate(map);
    }

    /**
     * Returns a "lazy" sorted map whose values will be created on demand.
     * <p>
     * When the key passed to the returned map's {@link Map#get(Object)}
     * method is not present in the map, then the factory will be used
     * to create a new object and that object will become the value
     * associated with that key.
     * <p>
     * For instance:
     *
     * <pre>
     * Factory factory = new Factory() {
     *     public Object create() {
     *         return new Date();
     *     }
     * }
     * SortedMap lazy = MapUtils.lazySortedMap(new TreeMap(), factory);
     * Object obj = lazy.get("test");
     * </pre>
     *
     * After the above code is executed, <code>obj</code> will contain
     * a new <code>Date</code> instance.  Furthermore, that <code>Date</code>
     * instance is the value for the <code>"test"</code> key.
     *
     * @param map  the map to make lazy, must not be null
     * @param factory  the factory for creating new objects, must not be null
     * @return a lazy map backed by the given map
     * @throws IllegalArgumentException  if the SortedMap or Factory is null
     */
    public static SortedMap lazySortedMap(SortedMap map, Factory factory) {
        return LazySortedMap.decorate(map, factory);
    }
    
    /**
     * Returns a "lazy" sorted map whose values will be created on demand.
     * <p>
     * When the key passed to the returned map's {@link Map#get(Object)}
     * method is not present in the map, then the factory will be used
     * to create a new object and that object will become the value
     * associated with that key. The factory is a {@link Transformer}
     * that will be passed the key which it must transform into the value.
     * <p>
     * For instance:
     * <pre>
     * Transformer factory = new Transformer() {
     *     public Object transform(Object mapKey) {
     *         return new File(mapKey);
     *     }
     * }
     * SortedMap lazy = MapUtils.lazySortedMap(new TreeMap(), factory);
     * Object obj = lazy.get("C:/dev");
     * </pre>
     *
     * After the above code is executed, <code>obj</code> will contain
     * a new <code>File</code> instance for the C drive dev directory.
     * Furthermore, that <code>File</code> instance is the value for the
     * <code>"C:/dev"</code> key in the map.
     * <p>
     * If a lazy map is wrapped by a synchronized map, the result is a simple
     * synchronized cache. When an object is not is the cache, the cache itself
     * calls back to the factory Transformer to populate itself, all within the
     * same synchronized block.
     *
     * @param map  the map to make lazy, must not be null
     * @param transformerFactory  the factory for creating new objects, must not be null
     * @return a lazy map backed by the given map
     * @throws IllegalArgumentException  if the Map or Transformer is null
     */
    public static SortedMap lazySortedMap(SortedMap map, Transformer transformerFactory) {
        return LazySortedMap.decorate(map, transformerFactory);
    }

}
