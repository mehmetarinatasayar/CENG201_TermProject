public class SubmissionTimeline {
    private static class Node {
        Submission value;
        Node left;
        Node right;
        int height = 1;

        Node(Submission value) {
            this.value = value;
        }
    }

    private Node root;
    private int lastVisitedCount;
    private int resultIndex;
    private int llRotationCount;
    private int rrRotationCount;
    private int lrRotationCount;
    private int rlRotationCount;

    public void insert(Submission submission) {
        root = insert(root, submission);
    }

    private Node insert(Node node, Submission submission) {
        if (node == null) {
            return new Node(submission);
        }

        long key = submission.getTimestampMs();
        long nodeKey = node.value.getTimestampMs();

        if (key < nodeKey) {
            node.left = insert(node.left, submission);
        } else if (key > nodeKey) {
            node.right = insert(node.right, submission);
        } else {
            throw new IllegalArgumentException("Duplicate timestamp: " + key);
        }

        updateHeight(node);
        int balance = getBalance(node);

        if (balance > 1 && key < node.left.value.getTimestampMs()) {
            llRotationCount++;
            return rotateRight(node);
        }
        if (balance < -1 && key > node.right.value.getTimestampMs()) {
            rrRotationCount++;
            return rotateLeft(node);
        }
        if (balance > 1 && key > node.left.value.getTimestampMs()) {
            lrRotationCount++;
            node.left = rotateLeft(node.left);
            return rotateRight(node);
        }
        if (balance < -1 && key < node.right.value.getTimestampMs()) {
            rlRotationCount++;
            node.right = rotateRight(node.right);
            return rotateLeft(node);
        }

        return node;
    }

    public int height() {
        return getHeight(root);
    }

    public Submission[] submittedBetween(long start, long end) {
        if (start > end) {
            lastVisitedCount = 0;
            return new Submission[0];
        }

        int count = countBetween(root, start, end);
        Submission[] result = new Submission[count];
        lastVisitedCount = 0;
        resultIndex = 0;
        fillBetween(root, start, end, result);
        return result;
    }

    private int countBetween(Node node, long start, long end) {
        if (node == null) {
            return 0;
        }

        long time = node.value.getTimestampMs();
        int count = 0;

        if (start < time) {
            count += countBetween(node.left, start, end);
        }
        if (time >= start && time <= end) {
            count++;
        }
        if (time < end) {
            count += countBetween(node.right, start, end);
        }
        return count;
    }

    private void fillBetween(Node node, long start, long end, Submission[] result) {
        if (node == null) {
            return;
        }

        lastVisitedCount++;
        long time = node.value.getTimestampMs();

        if (start < time) {
            fillBetween(node.left, start, end, result);
        }
        if (time >= start && time <= end) {
            result[resultIndex] = node.value;
            resultIndex++;
        }
        if (time < end) {
            fillBetween(node.right, start, end, result);
        }
    }

    private Node rotateRight(Node oldRoot) {
        Node newRoot = oldRoot.left;
        Node moved = newRoot.right;

        newRoot.right = oldRoot;
        oldRoot.left = moved;

        updateHeight(oldRoot);
        updateHeight(newRoot);
        return newRoot;
    }

    private Node rotateLeft(Node oldRoot) {
        Node newRoot = oldRoot.right;
        Node moved = newRoot.left;

        newRoot.left = oldRoot;
        oldRoot.right = moved;

        updateHeight(oldRoot);
        updateHeight(newRoot);
        return newRoot;
    }

    private void updateHeight(Node node) {
        node.height = 1 + Math.max(getHeight(node.left), getHeight(node.right));
    }

    private int getHeight(Node node) {
        if (node == null) {
            return 0;
        }
        return node.height;
    }

    private int getBalance(Node node) {
        return getHeight(node.left) - getHeight(node.right);
    }

    public int getLastVisitedCount() { return lastVisitedCount; }
    public int getLlRotationCount() { return llRotationCount; }
    public int getRrRotationCount() { return rrRotationCount; }
    public int getLrRotationCount() { return lrRotationCount; }
    public int getRlRotationCount() { return rlRotationCount; }
}
