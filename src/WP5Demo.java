public class WP5Demo {

    private static final long BASE_TIME =
            80_000_000L;

    public static void main(String[] args) {
        runRotationDemo();
        System.out.println();
        runRangeQueryDemo();
        System.out.println();
        runHeightExperiment();
    }

    private static void runRotationDemo() {
        System.out.println(
                "=== ALL FOUR AVL ROTATIONS ==="
        );

        SubmissionTimeline llTree =
                new SubmissionTimeline();
        llTree.insert(create("LL-1", 30));
        llTree.insert(create("LL-2", 20));
        llTree.insert(create("LL-3", 10));
        System.out.println(
                "LL rotations: "
                        + llTree.getLlRotationCount()
        );

        SubmissionTimeline rrTree =
                new SubmissionTimeline();
        rrTree.insert(create("RR-1", 10));
        rrTree.insert(create("RR-2", 20));
        rrTree.insert(create("RR-3", 30));
        System.out.println(
                "RR rotations: "
                        + rrTree.getRrRotationCount()
        );

        SubmissionTimeline lrTree =
                new SubmissionTimeline();
        lrTree.insert(create("LR-1", 30));
        lrTree.insert(create("LR-2", 10));
        lrTree.insert(create("LR-3", 20));
        System.out.println(
                "LR rotations: "
                        + lrTree.getLrRotationCount()
        );

        SubmissionTimeline rlTree =
                new SubmissionTimeline();
        rlTree.insert(create("RL-1", 10));
        rlTree.insert(create("RL-2", 30));
        rlTree.insert(create("RL-3", 20));
        System.out.println(
                "RL rotations: "
                        + rlTree.getRlRotationCount()
        );
    }

    private static void runRangeQueryDemo() {
        System.out.println(
                "=== RECURSIVE RANGE QUERY DEMO ==="
        );

        SubmissionTimeline timeline =
                new SubmissionTimeline();

        int[] times = {
                50, 30, 70, 20, 40,
                60, 80, 10, 90, 55
        };

        for (int i = 0; i < times.length; i++) {
            timeline.insert(
                    create(
                            String.format(
                                    "S-%04d", i + 1
                            ),
                            times[i]
                    )
            );
        }

        System.out.println(
                "Timeline height: "
                        + timeline.height()
        );

        Submission[] middle =
                timeline.submittedBetween(
                        timestampFor(35),
                        timestampFor(65)
                );

        printQuery(
                "Middle window [35, 65]",
                middle,
                timeline.getLastVisitedCount()
        );

        Submission[] empty =
                timeline.submittedBetween(
                        timestampFor(100),
                        timestampFor(110)
                );

        printQuery(
                "Empty window [100, 110]",
                empty,
                timeline.getLastVisitedCount()
        );

        Submission[] full =
                timeline.submittedBetween(
                        timestampFor(0),
                        timestampFor(100)
                );

        printQuery(
                "Full window [0, 100]",
                full,
                timeline.getLastVisitedCount()
        );
    }

    private static void printQuery(
            String label,
            Submission[] result,
            int visitedCount
    ) {
        System.out.println();
        System.out.println(label);
        System.out.println(
                "Matches: " + result.length
        );
        System.out.println(
                "Visited nodes: " + visitedCount
        );

        for (int i = 0; i < result.length; i++) {
            System.out.println(
                    result[i].getStudentId()
                            + " "
                            + result[i].clock()
            );
        }
    }

    private static void runHeightExperiment() {
        System.out.println(
                "=== 10,000 INCREASING INSERTS ==="
        );

        final int recordCount = 10_000;

        Submission[] increasing =
                new Submission[recordCount];

        for (int i = 0; i < recordCount; i++) {
            increasing[i] =
                    new Submission(
                            String.format(
                                    "H-%05d", i + 1
                            ),
                            "height-test.pdf",
                            1000,
                            BASE_TIME + i,
                            1,
                            false
                    );
        }

        PlainSubmissionBST plainTree =
                new PlainSubmissionBST();
        SubmissionTimeline avlTree =
                new SubmissionTimeline();

        long plainStart = System.nanoTime();

        for (int i = 0; i < recordCount; i++) {
            plainTree.insert(increasing[i]);
        }

        long plainEnd = System.nanoTime();
        long avlStart = System.nanoTime();

        for (int i = 0; i < recordCount; i++) {
            avlTree.insert(increasing[i]);
        }

        long avlEnd = System.nanoTime();

        System.out.println(
                "Plain BST height: "
                        + plainTree.height()
        );
        System.out.println(
                "AVL height:       "
                        + avlTree.height()
        );
        System.out.println(
                "Plain insert time: "
                        + (plainEnd - plainStart)
                        + " ns"
        );
        System.out.println(
                "AVL insert time:   "
                        + (avlEnd - avlStart)
                        + " ns"
        );
        System.out.println(
                "AVL RR rotations during increasing inserts: "
                        + avlTree.getRrRotationCount()
        );
    }

    private static Submission create(
            String studentId,
            int timeValue
    ) {
        return new Submission(
                studentId,
                studentId + ".pdf",
                1000,
                timestampFor(timeValue),
                1,
                false
        );
    }

    private static long timestampFor(int value) {
        return BASE_TIME + value * 1_000L;
    }
}
