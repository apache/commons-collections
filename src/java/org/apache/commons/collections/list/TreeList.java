/*
 *  Copyright 2004 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.commons.collections.list;

import java.util.AbstractList;
import java.util.Collection;

/**
 * A <code>List</code> implementation that is optimised for fast insertions and
 * removals at any index in the list.
 * <p>
 * This list implementation utilises a tree structure internally to ensure that
 * all insertions and removals are O(log n). This provides much faster performance
 * than both an <code>ArrayList</code> and a <code>LinkedList</code> where elements
 * are inserted and removed repeatedly from anywhere in the list.
 * <p>
 * The trade-off versus <code>ArrayList</code> is memory usage. <code>TreeList</code>
 * stores each entry in an object which uses up more memory. Also, <code>ArrayList</code>
 * is faster if additions and removals only occur at the end of the list, not in the middle.
 * <p>
 * The trade-off versus <code>LinkedList</code> is based on how you use the list.
 * If additions and removals only occur at the start or end of the list, not in the
 * middle then <code>LinkedList</code> is faster.
 * <p>
 * The following performance statistics are indicative of this class:
 * <pre>
 *           add   insert      get
 * TreeList  300      501      110
 * ArrayList  70    20390       20
 * LinkedList 50   226636   279742
 * </pre>
 * 
 * @since Commons Collections 3.1
 * @version $Revision: 1.1 $ $Date: 2004/05/10 19:59:03 $
 *
 * @author Joerg Schmuecker
 * @author Stephen Colebourne
 */
public class TreeList extends AbstractList {
//    Add; insert; get
//    tree   = 980;170;50;
//    array  = 280;6920;0;
//    linked = 380;55480;55800;

    /** The root node in the AVL tree */
    private AVLNode root;

    /** The current size of the list */
    private int size;

    //-----------------------------------------------------------------------
    /**
     * Constructs a new empty list.
     */
    public TreeList() {
        super();
    }

    /**
     * Constructs a new empty list that copies the specified list.
     * 
     * @param coll  the collection to copy
     * @throws NullPointerException if the collection is null
     */
    public TreeList(Collection coll) {
        super();
        addAll(coll);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the element at the specified index.
     * 
     * @param index  the index to retrieve
     * @return the element at the specified index
     */
    public Object get(int index) {
        checkInterval(index, 0, size() - 1);
        return root.get(index).getValue();
    }

    /**
     * Gets the current size of the list.
     * 
     * @return the current size
     */
    public int size() {
        return size;
    }

    /**
     * Gets an iterator over the list.
     * 
     * @return an iterator over the list
     */
//    public Iterator iterator() {
//        // override to go 65% faster
//        if (size() == 0) {
//            return IteratorUtils.EMPTY_ITERATOR;
//        }
//        return new TreeIterator(this);
//    }

    /**
     * Searches for the index of an object in the list.
     * 
     * @return the index of the object, -1 if not found
     */
    public int indexOf(Object object) {
        // override to go 75% faster
        if (root == null) {
            return -1;
        }
        return root.indexOf(object, root.relativePosition);
    }

    /**
     * Searches for the presence of an object in the list.
     * 
     * @return true if the object is found
     */
    public boolean contains(Object object) {
        return (indexOf(object) >= 0);
    }

    /**
     * Converts the list into an array.
     * 
     * @return the list as an array
     */
    public Object[] toArray() {
        // override to go 40% faster
        Object[] array = new Object[size()];
        if (root != null) {
            root.toArray(array, root.relativePosition);
        }
        return array;
    }

    //-----------------------------------------------------------------------
    /**
     * Adds a new element to the list.
     * 
     * @param index  the index to add before
     * @param obj  the element to add
     */
    public void add(int index, Object obj) {
        checkInterval(index, 0, size());
        if (root == null) {
            root = new AVLNode(index, obj);
        } else {
            root = root.insert(index, obj);
        }
        size++;
    }

    /**
     * Sets the element at the specified index.
     * 
     * @param index  the index to set
     * @param obj  the object to store at the specified index
     * @return the previous object at that index
     * @throws IndexOutOfBoundsException if the index is invalid
     */
    public Object set(int index, Object obj) {
        checkInterval(index, 0, size() - 1);
        AVLNode node = root.get(index);
        Object result = node.value;
        node.setValue(obj);
        return result;
    }

    /**
     * Removes the element at the specified index.
     * 
     * @param index  the index to remove
     * @return the previous object at that index
     */
    public Object remove(int index) {
        checkInterval(index, 0, size() - 1);
        Object result = get(index);
        root = root.remove(index);
        size--;
        return result;
    }

    /**
     * Clears the list, removing all entries.
     */
    public void clear() {
        root = null;
        size = 0;
    }

    //-----------------------------------------------------------------------
    /**
     * Checks whether the index is valid.
     * 
     * @param index  the index to check
     * @param startIndex  the first allowed index
     * @param endIndex  the last allowed index
     * @throws IndexOutOfBoundsException if the index is invalid
     */
    private void checkInterval(int index, int startIndex, int endIndex) {
        if (index < startIndex || index > endIndex) {
            throw new IndexOutOfBoundsException("Invalid index:" + index + ", size=" + size());
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Implements an AVLNode which keeps the offset updated.
     * <p>
     * This node contains the real work.
     * TreeList is just there to implement {@link java.util.List}.
     */
    static class AVLNode {
        /** The left child node */
        private AVLNode left;
        /** The right child node */
        private AVLNode right;
        /** How many levels of left/right are below this one */
        private int height;
        /** The relative position, root holds absolute position */
        private int relativePosition;
        /** The stored element */
        private Object value;

        /**
         * Constructs a new node with a relative position.
         * 
         * @param relativePosition  the relative position of the node
         * @param obj  the element
         */
        public AVLNode(int relativePosition, Object obj) {
            super();
            this.relativePosition = relativePosition;
            this.value = obj;
        }

        /**
         * Gets the value.
         * 
         * @return the value of this node
         */
        Object getValue() {
            return value;
        }

        /**
         * Sets the value.
         * 
         * @param obj  the value to store
         */
        void setValue(Object obj) {
            this.value = obj;
        }

        /**
         * Locate the element with the given index relative to the
         * offset of the parent of this node.
         */
        AVLNode get(int index) {
            int indexRelativeToMe = index - relativePosition;

            if (indexRelativeToMe == 0) {
                return this;
            }

            AVLNode nextNode = ((indexRelativeToMe < 0) ? left : right);
            if (nextNode == null) {
                int i = 1;
            }
            return nextNode.get(indexRelativeToMe);
        }

        /**
         * Locate the index that contains the specified object.
         */
        int indexOf(Object object, int index) {
            if (left != null) {
                int result = left.indexOf(object, index + left.relativePosition);
                if (result != -1) {
                    return result;
                }
            }
            if (value == null ? value == object : value.equals(object)) {
                return index;
            }
            if (right != null) {
                return right.indexOf(object, index + right.relativePosition);
            }
            return -1;
        }

        /**
         * Stores the node and its children into the array specified.
         */
        void toArray(Object[] array, int index) {
            array[index] = value;
            if (left != null) {
                left.toArray(array, index + left.relativePosition);
            }
            if (right != null) {
                right.toArray(array, index + right.relativePosition);
            }
        }

        //-----------------------------------------------------------------------
        /**
         * Balances according to the AVL algorithm.
         */
        private AVLNode balance() {
            switch (heightRightMinusLeft()) {
                case 1 :
                case 0 :
                case -1 :
                    return this;
                case -2 :
                    if (left.heightRightMinusLeft() > 0) {
                        setLeft(left.rotateLeft());
                    }
                    return rotateRight();
                case 2 :
                    if (right.heightRightMinusLeft() < 0) {
                        setRight(right.rotateRight());
                    }
                    return rotateLeft();
                default :
                    throw new RuntimeException("tree inconsistent!");
            }
        }

        /**
         * Returns the height of the node or -1 if the node is null.
         * 
         * Convenience method.
         */
        private int getHeight(AVLNode n) {
            return (n == null ? -1 : n.height);
        }

        /**
         * Returns the height difference
         */
        private int heightRightMinusLeft() {
            return getHeight(right) - getHeight(left);
        }

        /**
         * Inserts a node at the position index.
         * 
         * @param index  is the index of the position relative to the position of 
         *  the parent node.
         * @param obj  is the object to be stored in the position.
         */
        AVLNode insert(int index, Object obj) {
            int indexRelativeToMe = index - relativePosition;

            if (indexRelativeToMe <= 0) {
                return insertOnLeft(indexRelativeToMe, obj);
            } else {
                return insertOnRight(indexRelativeToMe, obj);
            }
        }

        private AVLNode insertOnLeft(int indexRelativeToMe, Object obj) {
            AVLNode ret = this;

            if (left == null) {
                left = new AVLNode(-1, obj);
            } else {
                left = left.insert(indexRelativeToMe, obj);

            }
            if (relativePosition >= 0) {
                relativePosition++;
            }
            ret = balance();
            recalcHeight();
            return ret;
        }

        private AVLNode insertOnRight(int indexRelativeToMe, Object obj) {
            AVLNode ret = this;

            if (right == null) {
                right = new AVLNode(+1, obj);
            } else {
                right = right.insert(indexRelativeToMe, obj);

            }
            if (relativePosition < 0) {
                relativePosition--;
            }
            ret = balance();
            recalcHeight();
            return ret;
        }

        private void recalcHeight() {
            height = Math.max(left == null ? -1 : left.height, right == null ? -1 : right.height) + 1;
        }

        private AVLNode rotateLeft() {
            AVLNode newTop = right;
            AVLNode movedNode = right.left;

            int newTopPosition = relativePosition + getOffset(right);
            int myNewPosition = -right.relativePosition;
            int movedPosition = getOffset(right) + getOffset(movedNode);

            setRight(right.left);
            newTop.setLeft(this);

            setOffset(newTop, newTopPosition);
            setOffset(this, myNewPosition);
            setOffset(movedNode, movedPosition);
            return newTop;
        }

        private int getOffset(AVLNode node) {
            if (node == null) {
                return 0;
            }
            return node.relativePosition;
        }

        private AVLNode rotateRight() {
            AVLNode newTop = left;
            AVLNode movedNode = left.right;

            int newTopPosition = relativePosition + getOffset(left);
            int myNewPosition = -left.relativePosition;
            int movedPosition = getOffset(left) + getOffset(movedNode);

            setLeft(left.right);
            newTop.setRight(this);

            setOffset(newTop, newTopPosition);
            setOffset(this, myNewPosition);
            setOffset(movedNode, movedPosition);
            return newTop;

        }

        private void setLeft(AVLNode node) {
            left = node;
            recalcHeight();
        }

        private int setOffset(AVLNode node, int newOffest) {
            if (node == null) {
                return 0;
            }
            int oldOffset = getOffset(node);
            node.relativePosition = newOffest;
            return oldOffset;
        }

        private void setRight(AVLNode node) {
            right = node;
            recalcHeight();
        }

        /**
         * Removes the node at a given position.
         * 
         * @param index  is the index of the element to be removed relative to
         *  the position of the parent node of the current node.
         * @return the new root of the tree
         */
        AVLNode remove(int index) {
            int indexRelativeToMe = index - relativePosition;

            if (indexRelativeToMe == 0) {
                return removeSelf();
            }
            if (indexRelativeToMe > 0) {
                right = right.remove(indexRelativeToMe);
                if (relativePosition < 0) {
                    relativePosition++;
                }
            } else {
                left = left.remove(indexRelativeToMe);
                if (relativePosition > 0) {
                    relativePosition--;
                }
            }
            recalcHeight();
            return balance();
        }

        private AVLNode removeSelf() {
            if (right == null && left == null)
                return null;
            if (right == null) {
                if (relativePosition > 0) {
                    left.relativePosition += relativePosition + (relativePosition > 0 ? 0 : 1);
                }
                return left;
            }
            if (left == null) {
                right.relativePosition += relativePosition - (relativePosition < 0 ? 0 : 1);
                return right;
            }

            if (heightRightMinusLeft() > 0) {
                value = right.min().value;
                right = right.removeMin();
                if (relativePosition < 0) {
                    relativePosition++;
                }
            } else {
                value = left.max().value;
                left = left.removeMax();
                if (relativePosition > 0) {
                    relativePosition--;
                }
            }
            recalcHeight();
            return this;
        }

        private AVLNode removeMin() {
            if (left == null) {
                return removeSelf();
            }
            left = left.removeMin();
            adjustOffsetForRemovalLeft();
            recalcHeight();
            return balance();
        }

        private void adjustOffsetForRemovalLeft() {
            if (relativePosition > 0) {
                relativePosition--;
            }
        }

        private void adjustOffsetForRemovalRight() {
            if (relativePosition < 0) {
                relativePosition++;
            }
        }

        private AVLNode min() {
            return (left == null) ? this : left.min();
        }

        private AVLNode removeMax() {
            if (right == null) {
                return removeSelf();
            }
            right = right.removeMax();
            adjustOffsetForRemovalRight();
            recalcHeight();
            return balance();
        }

        private AVLNode max() {
            return (right == null) ? this : right.max();
        }

        /**
         * Used for debugging.
         */
        public String toString() {
            return "AVLNode(" + relativePosition + "," + (left != null) + "," + value + "," + (right != null) + ")";
        }

    }

    //-----------------------------------------------------------------------
//    /**
//     * Iterator over the TreeList.
//     * <p>
//     * This iterator is good at iteration, but bad at removal.
//     * Implementing ListIterator would be even more complex, so has been avoided.
//     */
//    static class TreeIterator implements Iterator {
//        /** The parent list */
//        private final TreeList parent;
//        /** A stack built up during iteration to avoid each node referencing its parent */
//        private ArrayStack stack = new ArrayStack();
//        /** Whether remove is currently allowed */
//        private boolean canRemoveOrSet;
//        /** The last node returned */
//        private AVLNode lastNode;
//        /** The next index */
//        private int nextIndex;
//        
//        /**
//         * Constructor.
//         * 
//         * @param parent  the parent list
//         */
//        TreeIterator(TreeList parent) {
//            this.parent = parent;
//        }
//
//        private AVLNode findNext() {
//            AVLNode node = lastNode;
//            if (node == null) {
//                node = parent.root;
//                while (node.left != null) {
//                    stack.add(node);
//                    node = node.left;
//                }
//                return node;
//            }
//            if (node.right != null) {
//                node = node.right;
//                while (node.left != null) {
//                    stack.add(node);
//                    node = node.left;
//                }
//                return node;
//            }
//            if (stack.isEmpty()) {
//                throw new NoSuchElementException();
//            }
//            return (AVLNode) stack.pop();
//        }
//
//        public boolean hasNext() {
//            return (nextIndex < parent.size());
//        }
//
//        public Object next() {
//            if (hasNext() == false) {
//                throw new NoSuchElementException();
//            }
//            lastNode = findNext();
//            nextIndex++;
//            canRemoveOrSet = true;
//            return lastNode.getValue();
//        }
//
//        public int nextIndex() {
//            return nextIndex;
//        }
//
////        public boolean hasPrevious() {
////            return (nextIndex > 0);
////        }
////
////        public Object previous() {
////            if (hasPrevious() == false) {
////                throw new NoSuchElementException();
////            }
////            return parent.get(nextIndex--);
////        }
////
////        public int previousIndex() {
////            return nextIndex() - 1;
////        }
//
//        public void remove() {
//            if (canRemoveOrSet == false) {
//                throw new IllegalStateException();
//            }
//            if (nextIndex == 1) {
//                parent.remove(--nextIndex);
//                this.lastNode = null;
//                this.stack.clear();
//            } else if (hasNext()) {
//                AVLNode nextNode = findNext();
//                parent.remove(--nextIndex);
//                TreeIterator it = new TreeIterator(parent);
//                AVLNode node = null;
//                while (it.hasNext()) {
//                    it.next();
//                    if (it.lastNode == nextNode) {
//                        this.stack = it.stack;
//                        break;
//                    }
//                    node = it.lastNode;
//                }
//                this.lastNode = node;
//            } else {
//                parent.remove(--nextIndex);
//                this.lastNode = parent.root.get(parent.size() - 1);
//                this.stack.clear();
//            }
//            canRemoveOrSet = false;
//        }
//
////        public void set(Object obj) {
////            if (canRemoveOrSet == false) {
////                throw new IllegalStateException();
////            }
////            lastNode.setValue(obj);
////        }
////
////        public void add(Object obj) {
////        }
//    }

}
