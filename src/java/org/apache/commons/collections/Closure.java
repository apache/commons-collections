/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.commons.collections;

/** An interface to represent some Closure, a block of code which is executed 
  * from inside some block, function or iteration which operates on an input 
  * object.
  *
  * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
  */
public interface Closure {

    /** Performs some operation on the input object
      */
    public void execute(Object input);
}
