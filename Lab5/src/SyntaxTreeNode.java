public class SyntaxTreeNode {
    int index;
    String info;
    int parentIndex;
    int rightSibling;

    public SyntaxTreeNode(int index, String info, int parentIndex, int rightSibling) {
        this.index = index;
        this.info = info;
        this.parentIndex = parentIndex;
        this.rightSibling = rightSibling;
    }

    @Override
    public String toString() {
        return "SyntaxTreeNode{" +
                "index=" + index +
                ", info='" + info + '\'' +
                ", parentIndex=" + parentIndex +
                ", rightSibling=" + rightSibling +
                '}';
    }
}
