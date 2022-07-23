import javax.xml.crypto.dsig.keyinfo.KeyValue;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Currency;
import java.util.List;
import java.util.Scanner;

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
//        System.out.println("studentID: " + studentId + " was not found in the tree");
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
                return null;
            }
            // We split child, must insert newChildEntry into parent
            else {
                // If node has space insert key
                if (node.size < max) {
                    node.keyValues[node.size] = newChildEntry.keyValue;
                    node.children[node.numChildren] = newChildEntry.child;
                    node.numChildren++;
                    node.size++;
                    return null;
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
            // If studentId already exists
            // I know we don't have to really consider this case, but it gives peace of mind
            for (int i = 0; i < node.size; i++) {
                if (node.keyValues[i].key == entry.key)
                    return null;
            }
            // If node has space insert entry, set newChildEntry to null, return
            if (node.size < this.max) {
                node.keyValues[node.size] = entry;
                node.size++;
                return null;
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
        // Update CSV
//        Scanner scanner = new Scanner();
        return this;
    }

    BPlusTreeNode deleteHelper(BPlusTreeNode parent, BPlusTreeNode current, long studentId, BPlusTreeNode oldchildentry) {
        
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
                return null;
            }
            // we merged, (discarded child node) need to update rest of tree
            else {
                // remove oldchild entry from N (find it, then remove it, then update children)
                boolean found = false;

                // current.children array is updated here
                for (i = 0; i< current.numChildren; i++) {
                    if (current.children[i] == oldchildentry) {
                        found = true;
                    }
                    // edge case: deleting last entry/child in array
                    if (i == current.numChildren-1) {
                        current.children[i] = null;
                        break;
                    }
                    //update values in array
                    if ((found == true) && (i != current.numChildren - 1)) {
                        current.children[i] = current.children[i+1];
                    }
                }
                current.numChildren--;
                // now check occupancy
                // if current (N in algo) is above 50% capacity or if current == root (it can be below 50%)
                if ((current.size >= this.t) || (current == this.root)) {
                    return null;
                }
                // else: get a sibling of current (Algo states we can use parent pointer to find sibling)
                else {
                    // finding index of current
                    int j =0;
                    while (current != parent.children[j]) {
                        j++;
                    }
                    // if current is the end node (must choose left sibling) j-1
                    if (j == parent.numChildren -1) {
                        // if sibling has entries to spare: redistribute evenly
                        if (parent.children[j-1].size > this.t) {
                            int totalkeys = parent.children[j-1].size + parent.children[j].size;
                            int entriestotake = parent.children[j-1].size - (int)Math.floor(totalkeys/2);
                            int sibsize = parent.children[j-1].size;
                            // redistribute thru parent
                            for (i =0; i<entriestotake; i++) {
                                // make space in current
                                for (int c =0; c< current.size; c++) {
                                    current.keyValues[c+1] = current.keyValues[c];
                                }
                                for (int b =0; b< current.numChildren; b++) {
                                    current.children[b+1] = current.children[b];
                                }
                                // add parent to current.keyvals[0], update current.size
                                current.keyValues[0] = parent.keyValues[j-1];
                                current.size++;
                                // add sibling child to current.children[0]
                                current.children[0] = parent.children[j-1].children[sibsize - i];
                                parent.children[j-1].numChildren--;
                                current.numChildren++;
                                // put sibling keyval into parent, update sibling.size
                                parent.keyValues[j-1]= parent.children[j-1].keyValues[sibsize - i-1];
                                parent.children[j-1].size--;
                            }
                            oldchildentry = null;
                            return oldchildentry;
                        }
                        else { // merge
                            oldchildentry = parent.children[j];
                            int entriestomove = this.t -1;
                            // pull parent keyval into sibling (node on left)
                            parent.children[j-1].keyValues[this.t] = parent.keyValues[j-1];
                            parent.size--;
                            parent.children[j-1].size++;
                            // bring in leftmost pointer in M
                            parent.children[j-1].children[this.t+1] = current.children[0];
                            parent.children[j-1].numChildren++;
                            current.numChildren--;
                            // bring in rest of M
                            for(int a=0; a<entriestomove; a++) {
                                // first bring in key
                                parent.children[j-1].keyValues[this.t + a+ 1] = current.keyValues[a];
                                parent.children[j-1].size++;
                                current.size--;
                                // then children pters
                                parent.children[j-1].children[this.t+2+a] = current.children[a+1];
                                parent.children[j-1].numChildren++;
                                current.numChildren--;
                            }
                            // discard M
                            parent.children[j] = null;
                            parent.numChildren--;
                            // update root if needed
                            if (parent == this.root && parent.numChildren==0) {
                                this.root = parent.children[j-1];
                            }
                            return oldchildentry;
                        }

                    }
                    // Otherwise choose the sibling to the right j+1 (current is the leftmost node)
                    else {
                        // if sibling has entries to spare: redistribute evenly
                        if (parent.children[j+1].size > this.t) {
                            int totalkeys = parent.children[j+1].size + parent.children[j].size;
                            int entriestotake = parent.children[j+1].size - (int)Math.floor(totalkeys/2);
                            int sibsize = parent.children[j+1].size;
                            // redistribute thru parent
                            for (i= 0; i< entriestotake; i++) {
                                // bring down parent
                                current.keyValues[current.size] = parent.keyValues[j];
                                current.size++;
                                // bring in sibling child
                                current.children[current.numChildren] = parent.children[j+1].children[0];
                                current.numChildren++;
                                parent.children[j+1].numChildren--;
                                // put sibling keyval into parent, update size
                                parent.keyValues[j] = parent.children[j+1].keyValues[0];
                                parent.children[j+1].size--;
                                // update sibling
                                for (int c = 0; c< sibsize-i; c++) {
                                    parent.children[j+1].keyValues[c] = parent.children[j+1].keyValues[c+1];
                                }
                                for (int b = 0; b< parent.children[j+1].numChildren +1; b++) {
                                    parent.children[j+1].children[b] = parent.children[j+1].children[b+1];
                                }
                            }
                            oldchildentry = null;
                            return oldchildentry;
                        }
                        else { // merge
                            oldchildentry = parent.children[j+1];
                            int entriestomove = parent.children[j+1].size;
                            // pull parent keyval into current (node on left)
                            current.keyValues[this.t-1] = parent.keyValues[j];
                            parent.size--;
                            current.size++;
                            // bring in leftmost pointer in M
                            current.children[this.t] = parent.children[j+1].children[0];
                            current.numChildren++;
                            parent.children[j+1].numChildren--;
                            // bring in rest of M
                            for(int a=0; a<entriestomove; a++) {
                                // first bring in key
                                current.keyValues[this.t + a] = parent.children[j+1].keyValues[a];
                                current.size++;
                                parent.children[j+1].size--;
                                // then children pters
                                current.children[this.t+1+a] = parent.children[j+1].children[a+1];
                                current.numChildren++;
                                parent.children[j+1].numChildren--;
                            }
                            // discard M
                            parent.children[j+1] = null;
                            parent.numChildren--;
                            // update root if needed
                            if (parent == this.root && parent.numChildren==0) {
                                this.root = current;
                            }
                            return oldchildentry;
                        }
                    }
                }
            }
        }
        // Current node is a leaf; delete KV pair and return.
        else {
            // if Current has entries to spare
            if (current.size > this.t) {
                int i;
                // remove entry
                boolean found = false;
                for (i = 0; i< current.size; i++) {
                    if (current.keyValues[i].key == studentId) {
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
                oldchildentry = null;
                current.size--;
                return oldchildentry;
            }
            else {
                // get a sibling (first find index in parent)
                int j =0;
                while (current != parent.children[j]) {
                    j++;
                }
                // if current is the end node (must choose left sibling) j-1
                if (j == parent.numChildren -1) {
                    // redistribute
                    if (parent.children[j-1].size > this.t) {

                    }
                    else { // merge

                    }
                }
                // choose right sibling j+1
                else {
                    // redistribute
                    if (parent.children[j+1].size > this.t) {

                    }
                    // merge
                    else {
                        
                    }
                }
            }
        }
        // shouldn't ever reach this?
        return null;
    }


    int redistFind(int j, BPlusTreeNode parent) {
        /*
         * Finds if redistribution option exists, returns redistributors
         * index if found. Otherwise -1.
         */
        if (j != parent.numChildren) {
            if(parent.children[j+1].size > this.t) {
                return j+1;
            }
        }
        else if(j != 0) {
            if(parent.children[j-1].size > this.t) {
                return j-1;
            }
        }
        return -1;
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