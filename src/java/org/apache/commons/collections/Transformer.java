/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.commons.collections;

/** An object capable of transforming an input object into some output object.
  *
  * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
  */
public interface Transformer {

    /** Transforms the input object (leaving it unchanged) into some output object.
      * @return the transformation of the input object to the output object
      */
    public Object transform(Object input);
}
