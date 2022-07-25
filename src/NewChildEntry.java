/**
 * A new child entry to place inside a BPlusTreeNode's children[] and keyValues[] arrays
 */
public class NewChildEntry {
    KVPair keyValue;
    BPlusTreeNode child;

    public NewChildEntry(KVPair keyValue, BPlusTreeNode child) {
        this.keyValue = keyValue;
        this.child = child;
    }
}