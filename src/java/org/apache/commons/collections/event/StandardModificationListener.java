/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/java/org/apache/commons/collections/event/Attic/StandardModificationListener.java,v 1.1 2003/08/28 18:31:13 scolebourne Exp $
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003 The Apache Software Foundation.  All rights
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
package org.apache.commons.collections.event;

/**
 * A listener that receives events from the <code>StandardModificationHandler</code>.
 * <p>
 * This listener has two methods.
 * <ol>
 * <li><code>modificationOccurring</code> - called before the modification
 * occurs and can veto the change.
 * <li><code>modificationOccurred</code> - called after the change and is
 * for information.
 * </ol>
 *
 * @since Commons Collections 3.0
 * @version $Revision: 1.1 $ $Date: 2003/08/28 18:31:13 $
 * 
 * @author Stephen Colebourne
 */
public interface StandardModificationListener extends ModificationListener {

    /**
     * A collection modification is occurring.
     * <p>
     * To veto the change, throw <code>ModicationVetoedException</code>.
     * <p>
     * This method should be processed quickly, as with all event handling.
     * It should also avoid modifying the event source (the collection).
     * 
     * @param event  the event detail
     * @throws ModicationVetoedException to veto
     */
    public void modificationOccurring(StandardModificationEvent event);
    
    /**
     * A collection modification occurred.
     * <p>
     * This method should be processed quickly, as with all event handling.
     * It should also avoid modifying the event source (the collection).
     * Finally it should avoid throwing an exception.
     * 
     * @param event  the event detail
     */
    public void modificationOccurred(StandardModificationEvent event);
    
}
