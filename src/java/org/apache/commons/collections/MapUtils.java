/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/java/org/apache/commons/collections/MapUtils.java,v 1.2 2002/02/10 08:07:42 jstrachan Exp $
 * $Revision: 1.2 $
 * $Date: 2002/02/10 08:07:42 $
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
  * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
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
        // XXXX: should probably use log4j here instead...
        System.out.println( "INFO: Exception: " + e );
    }
}
