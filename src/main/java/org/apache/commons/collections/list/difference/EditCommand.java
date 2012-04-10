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


/** Abstract base class for all commands used to transform an objects
 *  sequence into another one.

 * <p>When two objects sequences are compared through the {@link
 * SequencesComparator#getScript SequencesComparator.getScript}
 * method, the result is provided has a {@link EditScript script}
 * containing the commands that progressively transform the first
 * sequence into the second one.</p>

 * <p>There are only three types of commands, all of which are
 * subclasses of this abstract class. Each command is associated with
 * one object belonging to at least one of the sequences. These
 * commands are {@link InsertCommand InsertCommand} which correspond
 * to an object of the second sequence beeing inserted into the first
 * sequence, {@link DeleteCommand DeleteCommand} which correspond to
 * an object of the first sequence beeing removed and {@link
 * KeepCommand KeepCommand} which correspond to an object of the first
 * sequence which <code>equals</code> an object in the second
 * sequence. It is guaranteed that comparison is always performed this
 * way (i.e. the <code>equals</code> method of the object from the
 * first sequence is used and the object passed as an argument comes
 * from the second sequence) ; this can be important if subclassing is
 * used for some elements in the first sequence and the
 * <code>equals</code> method is specialized.</p>

 * @see SequencesComparator
 * @see EditScript

 * @since 4.0
 * @author Jordane Sarda
 * @author Luc Maisonobe
 * @version $Id$
 */
public abstract class EditCommand<T> {

    /** Simple constructor.
     * Creates a new instance of EditCommand
     * @param object reference to the object associated with this
     * command, this refers to an element of one of the sequences
     * beeing compared
     */
    protected EditCommand(T object) {
        this.object = object;
    }

    /** Accept a visitor.
     * This method is invoked for each commands belonging to an {@link
     * EditScript EditScript}, in order to implement the visitor
     * design pattern
     * @param visitor the visitor to be accepted
     */    
    public abstract void accept(CommandVisitor<T> visitor);

    /** Object on which the command should be applied. */
    protected T object;

}
