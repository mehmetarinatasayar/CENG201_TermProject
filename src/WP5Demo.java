public class WP5Demo {
    private static final long BASE_TIME = 80_000_000L;

    public static void main(String[] args) {
        rotationDemo();
        System.out.println();
        rangeDemo();
        System.out.println();
        heightDemo();
    }

    private static void rotationDemo() {
        SubmissionTimeline ll = makeTree(30, 20, 10, "LL");
        SubmissionTimeline rr = makeTree(10, 20, 30, "RR");
        SubmissionTimeline lr = makeTree(30, 10, 20, "LR");
        SubmissionTimeline rl = makeTree(10, 30, 20, "RL");

        System.out.println("=== ALL FOUR AVL ROTATIONS ===");
        System.out.println("LL rotations: " + ll.getLlRotationCount());
        System.out.println("RR rotations: " + rr.getRrRotationCount());
        System.out.println("LR rotations: " + lr.getLrRotationCount());
        System.out.println("RL rotations: " + rl.getRlRotationCount());
    }

    private static SubmissionTimeline makeTree(int a, int b, int c, String name) {
        SubmissionTimeline tree = new SubmissionTimeline();
        tree.insert(create(name + "-1", a));
        tree.insert(create(name + "-2", b));
        tree.insert(create(name + "-3", c));
        return tree;
    }

    private static void rangeDemo() {
        SubmissionTimeline timeline = new SubmissionTimeline();
        int[] times = {50, 30, 70, 20, 40, 60, 80, 10, 90, 55};

        for (int i = 0; i < times.length; i++) {
            timeline.insert(create(String.format("S-%04d", i + 1), times[i]));
        }

        System.out.println("=== RECURSIVE RANGE QUERY DEMO ===");
        System.out.println("Timeline height: " + timeline.height());

        runQuery(timeline, "Middle window [35, 65]", 35, 65);
        runQuery(timeline, "Empty window [100, 110]", 100, 110);
        runQuery(timeline, "Full window [0, 100]", 0, 100);
    }

    private static void runQuery(SubmissionTimeline timeline, String title,
                                 int start, int end) {
        Submission[] result = timeline.submittedBetween(
                timestamp(start), timestamp(end));

        System.out.println("\n" + title);
        System.out.println("Matches: " + result.length);
        System.out.println("Visited nodes: " + timeline.getLastVisitedCount());

        for (int i = 0; i < result.length; i++) {
            System.out.println(result[i].getStudentId() + " " + result[i].clock());
        }
    }

    private static void heightDemo() {
        int count = 10_000;
        Submission[] data = new Submission[count];

        for (int i = 0; i < count; i++) {
            data[i] = new Submission(String.format("H-%05d", i + 1),
                    "height-test.pdf", 1000, BASE_TIME + i, 1, false);
        }

        PlainSubmissionBST plain = new PlainSubmissionBST();
        SubmissionTimeline avl = new SubmissionTimeline();

        long start = System.nanoTime();
        for (int i = 0; i < count; i++) {
            plain.insert(data[i]);
        }
        long plainTime = System.nanoTime() - start;

        start = System.nanoTime();
        for (int i = 0; i < count; i++) {
            avl.insert(data[i]);
        }
        long avlTime = System.nanoTime() - start;

        System.out.println("=== 10,000 INCREASING INSERTS ===");
        System.out.println("Plain BST height: " + plain.height());
        System.out.println("AVL height:       " + avl.height());
        System.out.println("Plain insert time: " + plainTime + " ns");
        System.out.println("AVL insert time:   " + avlTime + " ns");
        System.out.println("AVL RR rotations during increasing inserts: "
                + avl.getRrRotationCount());
    }

    private static Submission create(String id, int time) {
        return new Submission(id, id + ".pdf", 1000,
                timestamp(time), 1, false);
    }

    private static long timestamp(int value) {
        return BASE_TIME + value * 1_000L;
    }
}
