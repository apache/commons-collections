package org.apache.commons.collections;

import java.util.Collection;

/**
 * A linked list implementation that caches the nodes used internally to prevent
 * unnecessary object creates and deletion. This should result in a performance
 * improvement.
 * 
 * @author Jeff Varszegi
 * @author <a href="mailto:rich@rd.gen.nz">Rich Dougherty</a>
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

    public NodeCachingLinkedList() {
        super();
    }

    public NodeCachingLinkedList(Collection c) {
        super(c);
    }
    
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
     * {@link #addNodeToCache(Node)} on the node which has been removed.
     * 
     * @see org.apache.commons.collections.CommonsLinkedList#removeNode(Node)
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
