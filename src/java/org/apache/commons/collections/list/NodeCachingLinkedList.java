/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/java/org/apache/commons/collections/list/NodeCachingLinkedList.java,v 1.3 2003/12/29 01:04:44 scolebourne Exp $
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001-2003 The Apache Software Foundation.  All rights
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
 *    any, must include the following acknowledgement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgement may appear in the software itself,
 *    if and wherever such third-party acknowledgements normally appear.
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
package org.apache.commons.collections.list;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;

/**
 * A <code>List</code> implementation that stores a cache of internal Node objects
 * in an effort to reduce wasteful object creation.
 * <p>
 * A linked list creates one Node for each item of data added. This can result in
 * a lot of object creation and garbage collection. This implementation seeks to
 * avoid that by maintaining a store of cached nodes.
 * <p>
 * This implementation is suitable for long-lived lists where both add and remove
 * are used. Short-lived lists, or lists which only grow will have worse performance
 * using this class.
 * <p>
 * <b>Note that this implementation is not synchronized.</b>
 * 
 * @since Commons Collections 3.0
 * @version $Revision: 1.3 $ $Date: 2003/12/29 01:04:44 $
 * 
 * @author Jeff Varszegi
 * @author Rich Dougherty
 * @author Phil Steitz
 * @author Stephen Colebourne
 */
public class NodeCachingLinkedList extends AbstractLinkedList implements Serializable {

    /** Serialization version */
    static final long serialVersionUID = 6897789178562232073L;

    /**
     * The default value for {@link #maximumCacheSize}.
     */
    protected static final int DEFAULT_MAXIMUM_CACHE_SIZE = 20;

    /**
     * The first cached node, or <code>null</code> if no nodes are cached.
     * Cached nodes are stored in a singly-linked list with
     * <code>next</code> pointing to the next element.
     */
    protected transient Node firstCachedNode;
    
    /**
     * The size of the cache.
     */
    protected transient int cacheSize;

    /**
     * The maximum size of the cache.
     */
    protected int maximumCacheSize;

    //-----------------------------------------------------------------------
    /**
     * Constructor that creates.
     */
    public NodeCachingLinkedList() {
        this(DEFAULT_MAXIMUM_CACHE_SIZE);
    }

    /**
     * Constructor that copies the specified collection
     * 
     * @param coll  the collection to copy
     */
    public NodeCachingLinkedList(Collection coll) {
        super(coll);
        this.maximumCacheSize = DEFAULT_MAXIMUM_CACHE_SIZE;
    }
    
    /**
     * Constructor that species the maximum cache size.
     *
     * @param maximumCacheSize  the maximum cache size
     */
    public NodeCachingLinkedList(int maximumCacheSize) {
        super();
        this.maximumCacheSize = maximumCacheSize;
        init();  // must call init() as use super();
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the maximum size of the cache.
     * 
     * @return the maximum cache size
     */
    protected int getMaximumCacheSize() {
        return maximumCacheSize;
    }

    /**
     * Sets the maximum size of the cache.
     * 
     * @param maximumCacheSize  the new maximum cache size
     */
    protected void setMaximumCacheSize(int maximumCacheSize) {
        this.maximumCacheSize = maximumCacheSize;
        shrinkCacheToMaximumSize();
    }

    /**
     * Reduce the size of the cache to the maximum, if necessary.
     */
    protected void shrinkCacheToMaximumSize() {
        // Rich Dougherty: This could be more efficient.
        while (cacheSize > maximumCacheSize) {
            getNodeFromCache();
        }
    }
    
    /**
     * Gets a node from the cache. If a node is returned, then the value of
     * {@link #cacheSize} is decreased accordingly. The node that is returned
     * will have <code>null</code> values for next, previous and element.
     *
     * @return a node, or <code>null</code> if there are no nodes in the cache.
     */
    protected Node getNodeFromCache() {
        if (cacheSize == 0) {
            return null;
        }
        Node cachedNode = firstCachedNode;
        firstCachedNode = cachedNode.next;
        cachedNode.next = null; // This should be changed anyway, but defensively
                                // set it to null.                    
        cacheSize--;
        return cachedNode;
    }
    
    /**
     * Checks whether the cache is full.
     * 
     * @return true if the cache is full
     */
    protected boolean isCacheFull() {
        return cacheSize >= maximumCacheSize;
    }
    
    /**
     * Adds a node to the cache, if the cache isn't full.
     * The node's contents are cleared to so they can be garbage collected.
     * 
     * @param node  the node to add to the cache
     */
    protected void addNodeToCache(Node node) {
        if (isCacheFull()) {
            // don't cache the node.
            return;
        }
        // clear the node's contents and add it to the cache.
        Node nextCachedNode = firstCachedNode;
        node.previous = null;
        node.next = nextCachedNode;
        node.value = null;
        firstCachedNode = node;
        cacheSize++;
    }

    //-----------------------------------------------------------------------    
    /**
     * Creates a new node, either by reusing one from the cache or creating
     * a new one.
     * 
     * @param value  value of the new node
     * @return the newly created node
     */
    protected Node createNode(Object value) {
        Node cachedNode = getNodeFromCache();
        if (cachedNode == null) {
            return super.createNode(value);
        } else {
            cachedNode.value = value;
            return cachedNode;
        }
    }

    /**
     * Removes the node from the list, storing it in the cache for reuse
     * if the cache is not yet full.
     * 
     * @param node  the node to remove
     */
    protected void removeNode(Node node) {
        super.removeNode(node);
        addNodeToCache(node);
    }
    
    /**
     * Removes all the nodes from the list, storing as many as required in the
     * cache for reuse.
     * 
     * @param node  the node to remove
     */
    protected void removeAllNodes() {
        // Add the removed nodes to the cache, then remove the rest.
        // We can add them to the cache before removing them, since
        // {@link AbstractLinkedList.removeAllNodes()} removes the
        // nodes by removing references directly from {@link #header}.
        int numberOfNodesToCache = Math.min(size, maximumCacheSize - cacheSize);
        Node node = header.next;
        for (int currentIndex = 0; currentIndex < numberOfNodesToCache; currentIndex++) {
            Node oldNode = node;
            node = node.next;
            addNodeToCache(oldNode);
        }
        super.removeAllNodes();        
    }

    //-----------------------------------------------------------------------
    /**
     * Serializes the data held in this object to the stream specified.
     */
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        doWriteObject(out);
    }

    /**
     * Deserializes the data held in this object to the stream specified.
     */
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        doReadObject(in);
    }

}
