/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
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
