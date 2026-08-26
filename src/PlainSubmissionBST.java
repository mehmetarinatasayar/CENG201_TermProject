public class PlainSubmissionBST {
    private static class Node {
        Submission value;
        Node left;
        Node right;

        Node(Submission value) {
            this.value = value;
        }
    }

    private Node root;
    private int treeHeight;

    public void insert(Submission submission) {
        Node newNode = new Node(submission);

        if (root == null) {
            root = newNode;
            treeHeight = 1;
            return;
        }

        Node current = root;
        int depth = 1;
        long newTime = submission.getTimestampMs();

        while (true) {
            depth++;
            long currentTime = current.value.getTimestampMs();

            if (newTime < currentTime) {
                if (current.left == null) {
                    current.left = newNode;
                    break;
                }
                current = current.left;
            } else if (newTime > currentTime) {
                if (current.right == null) {
                    current.right = newNode;
                    break;
                }
                current = current.right;
            } else {
                throw new IllegalArgumentException("Duplicate timestamp: " + newTime);
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
