
public class BPlusTreeNode {

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

    public void clearKeys() {
        for (int i = 0; i < size; i++) {
            this.keyValues[i] = null;
        }
        this.size = 0;
    }

    public void clearChildren() {
        for (int i = 0; i < numChildren; i++) {
            this.children[i] = null;
        }
        this.numChildren = 0;
    }
}