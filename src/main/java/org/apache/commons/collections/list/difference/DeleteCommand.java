/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.collections.list.difference;


/** Command representing the deletion of one object of the first sequence.

 * When one object of the first sequence has no corresponding object
 * in the second sequence at the right place, the {@link EditScript
 * edit script} transforming the first sequence into the second
 * sequence uses an instance of this class to represent the deletion
 * of this object. The objects embedded in these type of commands
 * always come from the first sequence.

 * @see SequencesComparator
 * @see EditScript

 * @since 4.0
 * @author Jordane Sarda
 * @author Luc Maisonobe
 * @version $Id$
 */
public class DeleteCommand<T> extends EditCommand<T> {
    
    /** Simple constructor.
     * Creates a new instance of DeleteCommand
     * @param object the object of the first sequence that should be deleted
     */
    public DeleteCommand(T object) {
      super(object);
    }
    
    /** Accept a visitor.
     * When a <code>DeleteCommand</code> accepts a visitor, it calls
     * its {@link CommandVisitor#visitDeleteCommand
     * visitDeleteCommand} method.
     * @param visitor the visitor to be accepted
     */    
    public void accept(CommandVisitor<T> visitor) {
      visitor.visitDeleteCommand(object);
    }    
}
