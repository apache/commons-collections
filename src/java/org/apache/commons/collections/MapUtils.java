/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/java/org/apache/commons/collections/MapUtils.java,v 1.29 2003/08/31 13:05:44 scolebourne Exp $
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
 *    any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
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
 * A helper class for using {@link Map Map} instances.
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
 * @version $Revision: 1.29 $ $Date: 2003/08/31 13:05:44 $
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
 * @author Arun Mammen Thomas
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

    private static int indentDepth = 0;  // must be synchronized

    private static final String INDENT_STRING = "    ";

    /**
     * <code>MapUtils</code> should not normally be instantiated.
     */
    public MapUtils() {
    }    
    
    // Type safe getters
    //-------------------------------------------------------------------------

    /**
     *  Synonym for {@link Map#get(Object)}.
     *
     *  @param map  the map whose value to look up
     *  @param key  the key whose value to look up in that map
     *  @return null if the map is null; or the result of 
     *     <Code>map.get(key)</Code>
     */
    public static Object getObject( Map map, Object key ) {
        if ( map != null ) {
            return map.get( key );
        }
        return null;
    }

    /**
     *  Looks up the given key in the given map, converting the result into
     *  a string.
     *
     *  @param map  the map whose value to look up
     *  @param key  the key whose value to look up in that map
     *  @return  null if the map is null; null if the value mapped by that
     *    key is null; or the <Code>toString()</Code> 
     *     result of the value for that key
     */
    public static String getString( Map map, Object key ) {
        if ( map != null ) {
            Object answer = map.get( key );
            if ( answer != null ) {
                return answer.toString();
            }
        }
        return null;
    }

    /**
     *  Looks up the given key in the given map, converting the result into
     *  a {@link Boolean}.  If the map is null, this method returns null.
     *  If the value mapped by the given key is a 
     *  {@link Boolean}, then it is returned as-is.  Otherwise, if the value
     *  is a string, then if that string ignoring case equals "true", then
     *  a true {@link Boolean} is returned.  Any other string value will
     *  result in a false {@link Boolean} being returned.  OR, if the value
     *  is a {@link Number}, and that {@link Number} is 0, then a false
     *  {@link Boolean} is returned.  Any other {@link Number} value results
     *  in a true {@link Boolean} being returned.<P>
     *
     *  Any value that is not a {@link Boolean}, {@link String} or 
     *  {@link Number} results in null being returned.<P>
     *
     *  @param map  the map whose value to look up
     *  @param key  the key whose value to look up in that map
     *  @return  a {@link Boolean} or null
     */
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

    /**
     *  Looks up the given key in the given map, converting the result into
     *  a {@link Number}.  If the map is null, this method returns null.
     *  Otherwise, if the key maps to a {@link Number}, then that number
     *  is returned as-is.  Otherwise, if the key maps to a {@link String},
     *  that string is parsed into a number using the system default
     *  {@link NumberFormat}.<P>
     *
     *  If the value is not a {@link Number} or a {@link String}, or if
     *  the value is a {@link String} that cannot be parsed into a 
     *  {@link Number}, then null is returned.<P>
     *
     *  @param map  the map whose value to look up
     *  @param key  the key whose value to look up in that map
     *  @return  a {@link Number} or null
     */
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

    /**
     *  Looks up the given key in the given map, converting the result into
     *  a {@link Byte}.  First, {@link #getNumber(Map,Object)} is invoked.
     *  If the result is null, then null is returned.  Otherwise, the 
     *  byte value of the resulting {@link Number} is returned.
     *
     *  @param map  the map whose value to look up
     *  @param key  the key whose value to look up in that map
     *  @return  a {@link Byte} or null
     */
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

    /**
     *  Looks up the given key in the given map, converting the result into
     *  a {@link Short}.  First, {@link #getNumber(Map,Object)} is invoked.
     *  If the result is null, then null is returned.  Otherwise, the 
     *  short value of the resulting {@link Number} is returned.
     *
     *  @param map  the map whose value to look up
     *  @param key  the key whose value to look up in that map
     *  @return  a {@link Short} or null
     */
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

    /**
     *  Looks up the given key in the given map, converting the result into
     *  an {@link Integer}.  First, {@link #getNumber(Map,Object)} is invoked.
     *  If the result is null, then null is returned.  Otherwise, the 
     *  integer value of the resulting {@link Number} is returned.
     *
     *  @param map  the map whose value to look up
     *  @param key  the key whose value to look up in that map
     *  @return  an {@link Integer} or null
     */
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

    /**
     *  Looks up the given key in the given map, converting the result into
     *  a {@link Long}.  First, {@link #getNumber(Map,Object)} is invoked.
     *  If the result is null, then null is returned.  Otherwise, the 
     *  long value of the resulting {@link Number} is returned.
     *
     *  @param map  the map whose value to look up
     *  @param key  the key whose value to look up in that map
     *  @return  a {@link Long} or null
     */
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

    /**
     *  Looks up the given key in the given map, converting the result into
     *  a {@link Float}.  First, {@link #getNumber(Map,Object)} is invoked.
     *  If the result is null, then null is returned.  Otherwise, the 
     *  float value of the resulting {@link Number} is returned.
     *
     *  @param map  the map whose value to look up
     *  @param key  the key whose value to look up in that map
     *  @return  a {@link Float} or null
     */
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

    /**
     *  Looks up the given key in the given map, converting the result into
     *  a {@link Double}.  First, {@link #getNumber(Map,Object)} is invoked.
     *  If the result is null, then null is returned.  Otherwise, the 
     *  double value of the resulting {@link Number} is returned.
     *
     *  @param map  the map whose value to look up
     *  @param key  the key whose value to look up in that map
     *  @return  a {@link Double} or null
     */
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

    /**
     *  Looks up the given key in the given map, returning another map.
     *  If the given map is null or if the given key doesn't map to another
     *  map, then this method returns null.  Otherwise the mapped map is
     *  returned.
     *
     *  @param map  the map whose value to look up
     *  @param key  the key whose value to look up in that map
     *  @return  a {@link Map} or null
     */
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
     * @throws NullPointerException if the map is null
     */
    public static Properties toProperties(Map map) {
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
    public static Map toMap(ResourceBundle resourceBundle) {
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
     *
     * @param out  the stream to print to, must not be null
     * @param label  the label to be applied to the output generated.  This 
     *              may well be the key associated with this map within a 
     *              surrounding map in which this is nested.  If the label is 
     *              <code>null</code> then no label is output.
     * @param map  the map to print, may be <code>null</code>
     * @throws NullPointerException if the stream is <code>null</code>
     */
    public static synchronized void verbosePrint(
        final PrintStream out,
        final Object label,
        final Map map) {

        indentDepth = 0;
        verbosePrintInternal(out, label, map, false);
    }

    /**
     * Prints the given map with nice line breaks.
     * <p>
     * This method prints a nicely formatted String describing the Map.
     * Each map entry will be printed with key, value and value classname.
     * When the value is a Map, recursive behaviour occurs.
     *
     * @param out  the stream to print to, must not be null
     * @param label  the label to be applied to the output generated.  This 
     *              may well be the key associated with this map within a 
     *              surrounding map in which this is nested.  If the label is 
     *              <code>null</code> then no label is output.
     * @param map  the map to print, may be <code>null</code>
     * @throws NullPointerException if the stream is <code>null</code>
     */
    public static synchronized void debugPrint(
        final PrintStream out,
        final Object label,
        final Map map) {

        indentDepth = 0;
        verbosePrintInternal(out, label, map, true);
    }

    // Implementation methods
    //-------------------------------------------------------------------------

    /**
     * Writes indentation to the given stream.
     *
     * @param out  the stream to indent
     */
    protected static void printIndent(PrintStream out) {
        for (int i = 0; i < indentDepth; i++) {
            out.print(INDENT_STRING);
        }
    }
    
    /**
     * Logs the given exception to <code>System.out</code>.
     *
     * @param ex  the exception to log
     */
    protected static void logInfo(Exception ex) {
        System.out.println("INFO: Exception: " + ex);
    }

    /**
     * Implementation providing functionality for {@link #debugPrint} and for 
     * {@link #verbosePrint}.  This prints the given map with nice line breaks.
     * If the debug flag is true, it additionally prints the type of the object 
     * value.
     *
     * @param out  the stream to print to
     * @param label  the label to be applied to the output generated.  This 
     *              may well be the key associated with this map within a 
     *              surrounding map in which this is nested.  If the label is 
     *              <code>null</code>, then it is not output.
     * @param map  the map to print, may be <code>null</code>
     * @param debug flag indicating whether type names should be output.
     * @throws NullPointerException if the stream is <code>null</code>
     */
    private static void verbosePrintInternal(  // externally synchronized
        final PrintStream out,
        final Object label,
        final Map map,
        final boolean debug) {

        printIndent(out);

        if (label != null) {
            out.print(label);
            out.print(" = ");
            out.println(map == null ? "null" : "");
        }
        if (map == null) {
            return;
        }

        printIndent(out);
        out.println("{");
        indentDepth++;

        for (Iterator it = map.entrySet().iterator(); it.hasNext();) {
            Map.Entry entry = (Map.Entry) it.next();
            Object childKey = entry.getKey();
            Object childValue = entry.getValue();
            if (childValue instanceof Map && childValue != map) {
                verbosePrintInternal(out, (childKey == null ? "null" : childKey), (Map) childValue, debug);
            } else {
                printIndent(out);
                out.print(childKey);
                out.print(" = ");
                out.print(childValue == map ? "(this Map)" : childValue);
                if (debug && childValue != null) {
                    out.print(' ');
                    out.println(childValue.getClass().getName());
                } else {
                    out.println();
                }
            }
        }
        
        indentDepth--;
        printIndent(out);
        out.println(debug ? "} " + map.getClass().getName() : "}");
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
     * @param map  the map to predicate, must not be null
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
     * @param map  the map to predicate, must not be null
     * @param keyTransformer  the transformer for the map keys, null means no transformation
     * @param valueTransformer  the transformer for the map values, null means no transformation
     * @return a transformed map backed by the given map
     * @throws IllegalArgumentException  if the Map is null
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
    public static SortedMap lazySortedMap(SortedMap map, Transformer transformerFactory) {
        return LazySortedMap.decorate(map, transformerFactory);
    }

}
