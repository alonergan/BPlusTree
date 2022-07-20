class newChildEntry {
    KVPair keyValue;
    BPlusTreeNode child;

    public newChildEntry(KVPair keyValue, BPlusTreeNode child) {
        this.keyValue = keyValue;
        this.child = child;
    }
}

class KVPair {
    long key;
    long value;

    KVPair(long key, long value) {
        this.key = key;
        this.value = value;
    }
}

class BPlusTreeNode {

    /**
     * New storage for key-value pairs. Easier to sort/compare, since recordId corresponds to studentId
     * With B+ tree of degree t,
     * Each node can have numKeys: ceil(t/2)-1 < numKeys < t-1
     */
    KVPair[] keyValues;

    /**
     * Minimum degree (defines the range for number of keys)
     **/
    int t;
    /**
     * Pointers to the children, if this node is not a leaf.  If
     * this node is a leaf, then null.
     */
    BPlusTreeNode[] children;
    /**
     * number of key-value pairs in the B-tree
     */
    int size;
    /**
     * true when node is leaf. Otherwise false
     */
    boolean leaf;

    /**
     * point to other next node when it is a leaf node. Otherwise null
     */
    BPlusTreeNode next;

    // Constructor
    BPlusTreeNode(int t, boolean leaf) {
        this.t = t;
        this.leaf = leaf;
        this.size = 0;
        this.next = null;
        // Instantiate keyValues array
        this.keyValues = new KVPair[2*t];
        // Instantiate children
        this.children = new BPlusTreeNode[2*t + 1];
    }

    void clearKeys() {
        for (int i = 0; i < size; i++) {
            this.keyValues[i] = null;
        }
        this.size = 0;
    }
}
