import java.util.Arrays;

/** Commented out; moved classes into individual files due to issues with compiling */
//class newChildEntry {
//    KVPair keyValue;
//    BPlusTreeNode child;
//
//    public newChildEntry(KVPair keyValue, BPlusTreeNode child) {
//        this.keyValue = keyValue;
//        this.child = child;
//    }
//}
//
//class KVPair {
//    long key;
//    long value;
//
//    KVPair(long key, long value) {
//        this.key = key;
//        this.value = value;
//    }
//}

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
     * Tracks current number of children
     */
    int numChildren;

    /**
     * true when node is leaf. Otherwise false
     */
    boolean leaf;

    /**
     * point to other next node when it is a leaf node. Otherwise null
     */
    BPlusTreeNode next;

    // Constructor
    public BPlusTreeNode(int t, boolean leaf) {
        this.t = t;
        this.leaf = leaf;
        this.size = 0;
        this.next = null;
        // Instantiate keyValues array
        this.keyValues = new KVPair[2*t];
        // Instantiate children
        this.children = new BPlusTreeNode[2*t + 1];
        this.numChildren = 0;
    }

    /** Clears the keys */
    public void clearKeys() {
        for (int i = 0; i < size; i++) {
            this.keyValues[i] = null;
        }
        this.size = 0;
    }

    /** Clears the children */
    public void clearChildren() {
        for (int i = 0; i < numChildren; i++) {
            this.children[i] = null;
        }
        this.numChildren = 0;
    }

    /**
     * Sort the Key/Value pairs based on the key
     * Comparator/compareTo doesn't like when entries are null
     * Copy all the non-null values to a temporary array and sort
     */
    private void sortKeys() {
        KVPair[] kvPairs = this.keyValues;
        KVPair[] temp = new KVPair[this.size];
        System.arraycopy(this.keyValues, 0, temp, 0, this.size);
        Arrays.sort(temp, (a, b) -> {
            if (a.key < b.key) return -1;
            else return 1;
        });
        if (this.size >= 0) System.arraycopy(temp, 0, this.keyValues, 0, this.size);
    }

    /**
     *  Sorts the keys and nodes
     *  Sorting is crucial for retrieving a key. Call in insert (and delete?)
     */
    public void sortNode() {
        // error check space
        if (this.keyValues == null) return;

        sortKeys();
        if (!this.leaf) return;
        BPlusTreeNode[] newChildrenOrder = new BPlusTreeNode[2 * t + 1];
        BPlusTreeNode temp = null;
        for (int i = 0; i < this.size; i++) {
            for (int j = 0; j < this.numChildren; j++) {
                temp = this.children[j];
                if (temp.keyValues[temp.size -1].key < this.keyValues[i].key) {
                    newChildrenOrder[i] = this.children[i];
                    break;
                }
                if (i == this.size - 1 && temp.keyValues[0].key >= this.keyValues[i].key) {
                    newChildrenOrder[i] = this.children[i];
                }
            }
        }
        this.children = newChildrenOrder;
    }
}
