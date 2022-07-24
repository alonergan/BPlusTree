public class newChildEntry {
    KVPair keyValue;
    BPlusTreeNode child;

    public newChildEntry(KVPair keyValue, BPlusTreeNode child) {
        this.keyValue = keyValue;
        this.child = child;
    }
}