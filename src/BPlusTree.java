import javax.xml.crypto.dsig.keyinfo.KeyValue;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * B+Tree Structure
 * Key - StudentId
 * Leaf Node should contain [ key,recordId ]
 */
class BPlusTree {

    /**
     * Pointer to the root node.
     */
    private BPlusTreeNode root;
    /**
     * Number of key-value pairs allowed in the tree/the minimum degree of B+Tree
     **/
    final private int t;
    final private int max;

    BPlusTree(int t) {
        this.root = null;
        this.t = t;
        this.max = 2 * t; // should this be 2 * t?
    }

    long search(long studentId) {
        /*
         * TODO:
         * Implement this function to search in the B+Tree.
         * Return recordID for the given StudentID.
         * Otherwise, print out a message that the given studentId has not been found in the table and return -1.
         */
        if (root == null) {
            System.out.println("Tree empty: Root is null");
        } else {
            BPlusTreeNode curr = root;

            while (!curr.leaf) {
                /*
                   Search for the value within the current node and update curr to correct child
                   if value is not found
                             |  key[0]  |  key[1]  |  key[2]  | ... |  key[n-1]  |
                             /          \          /                          \
                   | children[0] | children[1] | children[2] | ... | children[n] |
                 */
                for (int i = 0; i < curr.size; i++) {
                    if (studentId < curr.keyValues[i].key) {
                        curr = curr.children[i];
                        break;
                    }
                    if (i == curr.size - 1) {
                        curr = curr.children[i + 1];
                        break;
                    }
                }
            }
            // Find node with studentID
            for (int i = 0; i < curr.size; i++) {
                // If found return recordID
                if (curr.keyValues[i].key == studentId) {
                    return curr.keyValues[i].value;
                }
            }
        }
        System.out.println("studentID: " + studentId + " was not found in the tree");
        return -1;
    }

    newChildEntry insertHelper(BPlusTreeNode node, KVPair entry, newChildEntry newChildEntry) {
        // 1) If node is a non-leaf node (N)
        if (!node.leaf) {
            // Choose subtree and recursively call method
            int i = 0;
            while (i < node.size && entry.key >= node.keyValues[i].key) {
                i++;
            }
            newChildEntry = insertHelper(node.children[i], entry, newChildEntry);

            // Didn't split child return
            if (newChildEntry == null) {
                return newChildEntry;
            }
            // We split child, must insert newChildEntry into parent
            else {
                // If node has space insert key
                if (node.size < max) {
                    node.keyValues[node.size] = newChildEntry.keyValue;
                    node.children[node.numChildren] = newChildEntry.child;
                    node.numChildren++;
                    node.size++;
                    newChildEntry = null;
                    return newChildEntry;
                }
                // If no space in interior node, must split
                else {
                    BPlusTreeNode tmp = new BPlusTreeNode(t, true);     // Copy of array values
                    BPlusTreeNode node2 = new BPlusTreeNode(t, false);   // Right node of split node
                    System.arraycopy(node.keyValues, 0, tmp.keyValues, 0, node.keyValues.length); // Copy vals into tmp
                    System.arraycopy(node.children, 0, tmp.children, 0, node.children.length);
                    node.clearKeys(); // Clear current node
                    node.clearChildren();

                    // Fill left node
                    System.arraycopy(tmp.keyValues, 0, node.keyValues, 0, t); // First t entries and t+1 children stay
                    System.arraycopy(tmp.children, 0, node.children, 0, t + 1);
                    node.size = t;
                    node.numChildren = t+1;

                    // Fill right node
                    System.arraycopy(tmp.keyValues, t, node2.keyValues, 0, tmp.keyValues.length - t); // Rest go into split node
                    System.arraycopy(tmp.children, t + 1, node2.children, 0, tmp.children.length - (t + 1));
                    node2.size = tmp.keyValues.length - t;
                    node2.numChildren = tmp.children.length - (t+1);

                    // Add newChildEntry to parent
                    node2.keyValues[node2.size] = newChildEntry.keyValue; // Add new key
                    node2.children[node2.numChildren] = newChildEntry.child; // Add child reference
                    node2.size++;
                    node2.numChildren++;

                    newChildEntry = new newChildEntry(node2.keyValues[0], node2);
                    // If root node was just split, revise tree
                    if (node == root) {
                        BPlusTreeNode newRoot = new BPlusTreeNode(t, false); // Create new root and set values
                        newRoot.keyValues[0] = newChildEntry.keyValue;
                        newRoot.children[0] = node;
                        newRoot.children[1] = newChildEntry.child;
                        newRoot.numChildren = 2;
                        newRoot.size = 1;
                        this.root = newRoot;
                    }
                    return newChildEntry;
                }
            }
        }
        // 2) If node is a leaf node
        else {
            // If node has space insert entry, set newChildEntry to null, return
            if (node.size < max) {
                node.keyValues[node.size] = entry;
                node.size++;
                newChildEntry = null;
                return newChildEntry;
            }
            // Leaf is full, we must split
            else {
                KVPair[] tmp = new KVPair[max + 1];     // Copy of array values
                BPlusTreeNode leaf2 = new BPlusTreeNode(t, true);   // Right node of split leaf
                System.arraycopy(node.keyValues, 0, tmp, 0, node.keyValues.length); // Copy vals into tmp
                tmp[max] = entry; // Add new entry before split
                node.clearKeys(); // Clear current node
                System.arraycopy(tmp, 0, node.keyValues, 0, t); // First t entries stay
                System.arraycopy(tmp, t, leaf2.keyValues, 0, tmp.length - t); // Rest go into split node
                newChildEntry = new newChildEntry(leaf2.keyValues[0], leaf2);
                node.size = t;
                leaf2.size = tmp.length - t;
                node.next = leaf2;

                // If root node was just split, revise tree
                if (node == root) {
                    BPlusTreeNode newRoot = new BPlusTreeNode(t, false); // Create new root and set values
                    newRoot.keyValues[0] = newChildEntry.keyValue;
                    newRoot.children[0] = node;
                    newRoot.children[1] = newChildEntry.child;
                    newRoot.numChildren = 2;
                    newRoot.size = 1;
                    this.root = newRoot;
                }
                return newChildEntry;
            }
        }
    }

    BPlusTree insert(Student student) {
        KVPair entry = new KVPair(student.studentId, student.recordId);
        // If root is null, create first node in tree
        if (root == null) {
            BPlusTreeNode node = new BPlusTreeNode(t, true);
            node.keyValues[0] = entry;
            node.size++;
            root = node;
            return this;
        }
        // Else call recursive function
        insertHelper(root, entry, null);
        return this;
    }

    KVPair deleteHelper(BPlusTreeNode parent, BPlusTreeNode current, long studentId, KVPair oldchildentry) {
        
        // if node pointer is a non leaf 
        if (!current.leaf) {
            // choose subtree
            int i;
            // logic: i will be the location of the child to use
            for (i = 0; i < current.size; i++) {
                if (studentId < current.keyValues[i].key) {
                    break;
                }
            }
            oldchildentry = deleteHelper(current, current.children[i], studentId, oldchildentry); // recursive delete

            if (oldchildentry == null) { // means we did not merge on the last recursive call
                return oldchildentry;
            }
            // we merged, (discarded child node) need to update rest of tree
            else {
                // remove oldchild entry from N (find it, then remove it, then update keys and children)
                boolean found = false;

                // NOTE: I am assuming that the keyval array is properly ordered/filled in
                // with null vals only at the end of the array and not in the middle

                // NOTE: I am also assuming that the children array was already updated
                // in the previous recursion (when node M was discarded)

                for (i = 0; i< current.size; i++) {
                    if (current.keyValues[i].key == oldchildentry.key) {
                        found = true;
                    }
                    // edge case: deleting last entry/child in array
                    if (i == current.size-1) {
                        current.keyValues[i] = null;
                        break;
                    }
                    //update values in array
                    if ((found == true) && (i != current.size-1)) {
                        current.keyValues[i] = current.keyValues[i+1];
                    }
                }
                current.size--;
                // now check min occupancy
                // if current (N in algo) has entries to spare
                if (current.size > this.t) {
                    oldchildentry = null;
                    return oldchildentry;
                }
                // else: get a sibling of current (hueristic use current.next)
                else {
                    // redistribution have to make changes to the parent.children
                    // redistribute evenly amongst even number 2*t of max pairs
                    if(current.next.size > this.t) {
                        
                    }
                }
            }
        }
        return null;
    }


    boolean delete(long studentId) {
        /*
         * TODO:
         * Implement this function to delete in the B+Tree.
         * Also, delete in student.csv after deleting in B+Tree, if it exists.
         * Return true if the student is deleted successfully otherwise, return false.
         */

        // cant delete here
        if (this.root == null) {
            return false;
        }
        else {
            deleteHelper(null, this.root, studentId, null);
        }
        return true;
    }

    List<Long> print() {

        List<Long> listOfRecordID = new ArrayList<>();

        // Get leftmost leaf node
        BPlusTreeNode current = root;
        while (!current.leaf) {
            // Get leftmost child
            current = current.children[0];
        }

        // Traverse leaf nodes
        do {
            // Get all recordID for current leaf node and move to sibling
            for (int i = 0; i < current.size; i++) {
                listOfRecordID.add(current.keyValues[i].value);
            }
            current = current.next;
        } while (current != null);

        return listOfRecordID;
    }

    private void sortKeys(BPlusTreeNode node) {
        KVPair[] key_values = node.keyValues;
        Arrays.sort(key_values, (a, b) -> {
            if (a.key == 0) return 1;
            if (b.key == 0) return -1;
            return Long.signum(a.key - b.key);
        });
    }
}