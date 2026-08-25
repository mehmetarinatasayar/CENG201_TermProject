public class SubmissionTimeline {

    private static class Node {
        Submission value;
        Node left;
        Node right;
        int height;

        Node(Submission value) {
            this.value = value;
            this.height = 1;
        }
    }

    private Node root;
    private int lastVisitedCount;
    private int llRotationCount;
    private int rrRotationCount;
    private int lrRotationCount;
    private int rlRotationCount;

    public void insert(Submission submission) {
        root = insertRecursive(root, submission);
    }

    private Node insertRecursive(
            Node node,
            Submission submission
    ) {
        if (node == null) {
            return new Node(submission);
        }

        long key = submission.getTimestampMs();
        long nodeKey = node.value.getTimestampMs();

        if (key < nodeKey) {
            node.left = insertRecursive(
                    node.left, submission
            );
        } else if (key > nodeKey) {
            node.right = insertRecursive(
                    node.right, submission
            );
        } else {
            throw new IllegalArgumentException(
                    "Duplicate timestamp: " + key
            );
        }

        updateHeight(node);
        int balance = balanceOf(node);

        if (
                balance > 1
                        && key
                        < node.left.value.getTimestampMs()
        ) {
            llRotationCount++;
            return rotateRight(node);
        }

        if (
                balance < -1
                        && key
                        > node.right.value.getTimestampMs()
        ) {
            rrRotationCount++;
            return rotateLeft(node);
        }

        if (
                balance > 1
                        && key
                        > node.left.value.getTimestampMs()
        ) {
            lrRotationCount++;
            node.left = rotateLeft(node.left);
            return rotateRight(node);
        }

        if (
                balance < -1
                        && key
                        < node.right.value.getTimestampMs()
        ) {
            rlRotationCount++;
            node.right = rotateRight(node.right);
            return rotateLeft(node);
        }

        return node;
    }

    public int height() {
        return heightOf(root);
    }

    public Submission[] submittedBetween(
            long t1,
            long t2
    ) {
        if (t1 > t2) {
            lastVisitedCount = 0;
            return new Submission[0];
        }

        int resultCount =
                countBetween(root, t1, t2);

        Submission[] result =
                new Submission[resultCount];

        int[] resultIndex = {0};
        lastVisitedCount = 0;

        fillBetween(
                root, t1, t2, result, resultIndex
        );

        return result;
    }

    public int getLastVisitedCount() {
        return lastVisitedCount;
    }

    public int getLlRotationCount() {
        return llRotationCount;
    }

    public int getRrRotationCount() {
        return rrRotationCount;
    }

    public int getLrRotationCount() {
        return lrRotationCount;
    }

    public int getRlRotationCount() {
        return rlRotationCount;
    }

    private int countBetween(
            Node node,
            long t1,
            long t2
    ) {
        if (node == null) {
            return 0;
        }

        long timestamp =
                node.value.getTimestampMs();

        int count = 0;

        if (t1 < timestamp) {
            count += countBetween(
                    node.left, t1, t2
            );
        }

        if (
                timestamp >= t1
                        && timestamp <= t2
        ) {
            count++;
        }

        if (timestamp < t2) {
            count += countBetween(
                    node.right, t1, t2
            );
        }

        return count;
    }

    private void fillBetween(
            Node node,
            long t1,
            long t2,
            Submission[] result,
            int[] resultIndex
    ) {
        if (node == null) {
            return;
        }

        lastVisitedCount++;

        long timestamp =
                node.value.getTimestampMs();

        if (t1 < timestamp) {
            fillBetween(
                    node.left,
                    t1,
                    t2,
                    result,
                    resultIndex
            );
        }

        if (
                timestamp >= t1
                        && timestamp <= t2
        ) {
            result[resultIndex[0]] = node.value;
            resultIndex[0]++;
        }

        if (timestamp < t2) {
            fillBetween(
                    node.right,
                    t1,
                    t2,
                    result,
                    resultIndex
            );
        }
    }

    private Node rotateRight(Node oldRoot) {
        Node newRoot = oldRoot.left;
        Node movedSubtree = newRoot.right;

        newRoot.right = oldRoot;
        oldRoot.left = movedSubtree;

        updateHeight(oldRoot);
        updateHeight(newRoot);

        return newRoot;
    }

    private Node rotateLeft(Node oldRoot) {
        Node newRoot = oldRoot.right;
        Node movedSubtree = newRoot.left;

        newRoot.left = oldRoot;
        oldRoot.right = movedSubtree;

        updateHeight(oldRoot);
        updateHeight(newRoot);

        return newRoot;
    }

    private void updateHeight(Node node) {
        node.height = 1 + Math.max(
                heightOf(node.left),
                heightOf(node.right)
        );
    }

    private int heightOf(Node node) {
        return node == null ? 0 : node.height;
    }

    private int balanceOf(Node node) {
        return node == null
                ? 0
                : heightOf(node.left)
                - heightOf(node.right);
    }
}
