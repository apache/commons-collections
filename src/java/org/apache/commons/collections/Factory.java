/*
 * Copyright 1999-2004 The Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.collections;



/**
 * Factory
 * A simple interface that describes the most basic means of having the ability
 * to create an object.
 *
 * @author Arron Bates
 * @version $Revision: 1.3.2.1 $
 * @since 2.1
 */
public interface Factory {

  /** Simple method from which will come the new object from the factory.
   *
   * @return Object reference to the new object.
   */
  public Object create();
  
}
