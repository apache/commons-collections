/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/java/org/apache/commons/collections/Attic/NodeCachingLinkedList.java,v 1.4 2003/01/10 20:21:23 rwaldhoff Exp $
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2002-2003 The Apache Software Foundation.  All rights
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
package org.apache.commons.collections;

import java.util.Collection;

/**
 * A linked list implementation that caches the nodes used internally to prevent
 * unnecessary object creates and deletion. This should result in a performance
 * improvement.
 * 
 * @since Commons Collections 2.2
 * @author Jeff Varszegi
 * @author <a href="mailto:rich@rd.gen.nz">Rich Dougherty</a>
 * @version $Revision: 1.4 $ $Date: 2003/01/10 20:21:23 $
 */
public class NodeCachingLinkedList extends CommonsLinkedList {

    private static final long serialVersionUID = 1;

    /**
     * The default value for {@link #maximumCacheSize}.
     */
    private static final int DEFAULT_MAXIMUM_CACHE_SIZE = 20;

    /**
     * The first cached node, or <code>null</code> if no nodes are cached.
     * Cached nodes are stored in a singly-linked list with {@link Node#next}
     * pointing to the next element.
     */
    private transient Node firstCachedNode;
    
    /**
     * The size of the cache.
     */
    private transient int cacheSize = 0;

    /**
     * The maximum size of the cache.
     */
    private int maximumCacheSize = DEFAULT_MAXIMUM_CACHE_SIZE;

    /**
     * Constructor.
     */
    public NodeCachingLinkedList() {
        super();
    }

    /**
     * Constructor that copies the specified collection
     * 
     * @param coll  the collection to copy
     */
    public NodeCachingLinkedList(Collection coll) {
        super(coll);
    }
    
    /**
     * Constructor that species the maximum cache size.
     * 
     * @param maximumCacheSize  the maximum cache size
     */
    public NodeCachingLinkedList(int maximumCacheSize) {
        super();
        this.maximumCacheSize = maximumCacheSize;
    }

    // Cache operations

    /**
     * Gets the maximum size of the cache.
     */
    public int getMaximumCacheSize() {
        return maximumCacheSize;
    }

    /**
     * Sets the maximum size of the cache.
     */
    public void setMaximumCacheSize(int maximumCacheSize) {
        this.maximumCacheSize = maximumCacheSize;
        shrinkCacheToMaximumSize();
    }

    /**
     * Reduce the size of the cache to the maximum, if necessary.
     */
    private void shrinkCacheToMaximumSize() {
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
     * @return A node, or <code>null</code> if there are no nodes in the cache.
     */
    private Node getNodeFromCache() {
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
    
    private boolean cacheFull() {
        return cacheSize >= maximumCacheSize;
    }
    
    /**
     * Adds a node to the cache, if the cache isn't full. The node's contents
     * are cleared to so they can be garbage collected.
     */
    private void addNodeToCache(Node node) {
        if (cacheFull()) {
            // Don't cache the node.
            return;
        }
        // Clear the node's contents and add it to the cache.
        Node nextCachedNode = firstCachedNode;
        node.previous = null;
        node.next = nextCachedNode;
        node.element = null;
        firstCachedNode = node;
        cacheSize++;
    }
    
    // Node operations

    /**
     * Create a node, getting it from the cache if possible.
     */
    protected Node createNode() {
        Node cachedNode = getNodeFromCache();
        if (cachedNode == null) {
            return super.createNode();
        } else {
            return cachedNode;
        }
    }
    
    /**
     * Create a node, getting it from the cache if possible.
     */
    protected Node createNode(Node next, Node previous, Object element) {
        Node cachedNode = getNodeFromCache();
        if (cachedNode == null) {
            return super.createNode(next, previous, element);
        } else {
            cachedNode.next = next;
            cachedNode.previous = previous;
            cachedNode.element = element;
            return cachedNode;
        }
    }

    /**
     * Calls the superclass' implementation then calls
     * <code>addNodeToCache</code> on the node which has 
     * been removed.
     * 
     * @see CommonsLinkedList#removeNode
     */
    protected void removeNode(Node node) {
        super.removeNode(node);
        addNodeToCache(node);
    }
    
    protected void removeAllNodes() {
        // Add the removed nodes to the cache, then remove the rest.
        // We can add them to the cache before removing them, since
        // {@link CommonsLinkedList.removeAllNodes()} removes the
        // nodes by removing references directly from {@link #marker}.
        int numberOfNodesToCache = Math.min(size, maximumCacheSize - cacheSize);
        Node node = marker.next;
        for (int currentIndex = 0; currentIndex < numberOfNodesToCache; currentIndex++) {
            Node oldNode = node;
            node = node.next;
            addNodeToCache(oldNode);
        }
        super.removeAllNodes();        
    }

}
