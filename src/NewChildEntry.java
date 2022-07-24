public class NewChildEntry {
    KVPair keyValue;
    BPlusTreeNode child;

    public NewChildEntry(KVPair keyValue, BPlusTreeNode child) {
        this.keyValue = keyValue;
        this.child = child;
    }
}