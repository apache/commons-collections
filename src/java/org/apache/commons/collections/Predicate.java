/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.commons.collections;

/** Performs some predicate which returns true or false based on the input object.
  * Predicate instances can be used to implement queries or to do filtering.
  *
  * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
  */
public interface Predicate {

    /** @return true if the input object matches this predicate, else returns false
      */
    public boolean evaluate(Object input);
}
