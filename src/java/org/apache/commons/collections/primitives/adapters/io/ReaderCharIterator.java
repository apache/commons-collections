/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/java/org/apache/commons/collections/primitives/adapters/io/Attic/ReaderCharIterator.java,v 1.2 2003/08/31 12:58:19 scolebourne Exp $
 * ====================================================================
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

package org.apache.commons.collections.primitives.adapters.io;

import java.io.IOException;
import java.io.Reader;
import java.util.NoSuchElementException;

import org.apache.commons.collections.primitives.CharIterator;

/**
 * Adapts a {@link Reader} to the {@link CharIterator} interface.
 * 
 * @version $Revision: 1.2 $ $Date: 2003/08/31 12:58:19 $
 * @author Rodney Waldhoff
 */
public class ReaderCharIterator implements CharIterator {

    public ReaderCharIterator(Reader in) {
        this.reader = in;
    }

    public static CharIterator adapt(Reader in) {
        return null == in ? null : new ReaderCharIterator(in);
    }
    
    public boolean hasNext() {
        ensureNextAvailable();
        return (-1 != next);
    }

    public char next() {
        if(!hasNext()) {
            throw new NoSuchElementException("No next element");
        } else {
            nextAvailable = false;
            return (char)next;
        }
    }
    
    /**
     * Not supported.
     * @throws UnsupportedOperationException
     */
    public void remove() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("remove() is not supported here");
    }

    private void ensureNextAvailable() {
        if(!nextAvailable) {
            readNext();
        }
    }

    private void readNext() {
        try {
            next = reader.read();
            nextAvailable = true;
        } catch(IOException e) {
            // TODO: Fix me using tunnelled exception, see 
            // http://radio.weblogs.com/0122027/2003/04/01.html#a7
            // for example            
            throw new RuntimeException(e.toString());
        }
    }
    
    private Reader reader = null;
    private boolean nextAvailable = false;
    private int next;

}
