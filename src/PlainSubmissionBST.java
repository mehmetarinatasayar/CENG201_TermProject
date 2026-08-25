public class PlainSubmissionBST {

    private static class Node {
        Submission value;
        Node left;
        Node right;

        Node(Submission value) {
            this.value = value;
            this.left = null;
            this.right = null;
        }
    }

    private Node root;
    private int treeHeight;

    public PlainSubmissionBST() {
        root = null;
        treeHeight = 0;
    }

    public void insert(Submission submission) {
        Node newNode = new Node(submission);

        if (root == null) {
            root = newNode;
            treeHeight = 1;
            return;
        }

        Node current = root;
        int depth = 1;

        while (true) {
            depth++;

            if (
                    submission.getTimestampMs()
                            < current.value.getTimestampMs()
            ) {
                if (current.left == null) {
                    current.left = newNode;
                    break;
                }

                current = current.left;

            } else if (
                    submission.getTimestampMs()
                            > current.value.getTimestampMs()
            ) {
                if (current.right == null) {
                    current.right = newNode;
                    break;
                }

                current = current.right;

            } else {
                throw new IllegalArgumentException(
                        "Duplicate timestamp: "
                                + submission.getTimestampMs()
                );
            }
        }

        if (depth > treeHeight) {
            treeHeight = depth;
        }
    }

    public int height() {
        return treeHeight;
    }
}
