public class WP3Demo {

    public static void main(String[] args) {
        runPriorityDemo();

        System.out.println();
        runTimingTest(1_000);

        System.out.println();
        runTimingTest(10_000);
    }

    private static void runPriorityDemo() {
        System.out.println("=== WP3 PRIORITY DEMO ===");

        Submission[] submissions = {
                create("S-0001", 83_000_000L, false),
                create("S-0002", 81_000_000L, true),
                create("S-0003", 80_000_000L, false),
                create("S-0004", 84_000_000L, false),
                create("S-0005", 82_000_000L, true),
                create("S-0006", 79_500_000L, false),
                create("S-0007", 85_000_000L, false),
                create("S-0008", 81_500_000L, false)
        };

        NaiveDispatcher naive = new NaiveDispatcher();
        HeapDispatcher heap = new HeapDispatcher();

        for (int i = 0; i < submissions.length; i++) {
            naive.submit(submissions[i]);
            heap.submit(submissions[i]);
        }

        Submission[] naiveOrder =
                new Submission[submissions.length];
        Submission[] heapOrder =
                new Submission[submissions.length];

        System.out.println();
        System.out.println("Naive order:");

        for (int i = 0; i < submissions.length; i++) {
            naiveOrder[i] = naive.next();
            System.out.println(naiveOrder[i]);
        }

        System.out.println();
        System.out.println("Heap order:");

        for (int i = 0; i < submissions.length; i++) {
            heapOrder[i] = heap.next();
            System.out.println(heapOrder[i]);
        }

        boolean sameOrder = true;

        for (int i = 0; i < submissions.length; i++) {
            if (naiveOrder[i] != heapOrder[i]) {
                sameOrder = false;
                break;
            }
        }

        System.out.println();
        System.out.println(
                "Both dispatchers match: " + sameOrder
        );

        runBuildHeapDemo(submissions, naiveOrder);
    }

    private static void runBuildHeapDemo(
            Submission[] submissions,
            Submission[] expectedOrder
    ) {
        HeapDispatcher burstHeap = new HeapDispatcher();
        burstHeap.loadBurst(submissions);

        boolean sameOrder = true;

        for (int i = 0; i < expectedOrder.length; i++) {
            Submission actual = burstHeap.next();

            if (actual != expectedOrder[i]) {
                sameOrder = false;
            }
        }

        System.out.println(
                "Bottom-up loadBurst matches: " + sameOrder
        );
    }

    private static Submission create(
            String studentId,
            long timestamp,
            boolean accommodation
    ) {
        return new Submission(
                studentId,
                studentId + "_project.pdf",
                1000,
                timestamp,
                1,
                accommodation
        );
    }

    private static void runTimingTest(int recordCount) {
        System.out.println(
                "=== " + recordCount + " RECORD TIMING ==="
        );

        Submission[] burst = generateBurst(recordCount);
        warmUp(burst);

        long naiveTime = timeNaive(burst);
        long heapTime = timeHeap(burst);
        long buildHeapTime = timeBuildHeap(burst);

        System.out.println(
                "Naive dispatcher: " + naiveTime + " ns"
        );
        System.out.println(
                "Heap dispatcher:  " + heapTime + " ns"
        );
        System.out.println(
                "Bottom-up heap:    " + buildHeapTime + " ns"
        );

        if (heapTime > 0) {
            double speedup =
                    (double) naiveTime / heapTime;

            System.out.printf(
                    "Heap speedup: %.2f times%n",
                    speedup
            );
        }
    }

    private static Submission[] generateBurst(
            int recordCount
    ) {
        Submission[] burst =
                new Submission[recordCount];

        ScenarioGenerator generator =
                new ScenarioGenerator(20260725L);

        for (int i = 0; i < recordCount; i++) {
            int studentIndex =
                    i % ScenarioGenerator.STUDENT_COUNT;
            burst[i] =
                    generator.nextUpload(studentIndex);
        }

        return burst;
    }

    private static long timeNaive(
            Submission[] burst
    ) {
        NaiveDispatcher dispatcher =
                new NaiveDispatcher();

        long start = System.nanoTime();

        for (int i = 0; i < burst.length; i++) {
            dispatcher.submit(burst[i]);
        }

        while (dispatcher.size() > 0) {
            dispatcher.next();
        }

        return System.nanoTime() - start;
    }

    private static long timeHeap(
            Submission[] burst
    ) {
        HeapDispatcher dispatcher =
                new HeapDispatcher();

        long start = System.nanoTime();

        for (int i = 0; i < burst.length; i++) {
            dispatcher.submit(burst[i]);
        }

        while (dispatcher.size() > 0) {
            dispatcher.next();
        }

        return System.nanoTime() - start;
    }

    private static long timeBuildHeap(
            Submission[] burst
    ) {
        HeapDispatcher dispatcher =
                new HeapDispatcher();

        long start = System.nanoTime();

        dispatcher.loadBurst(burst);

        while (dispatcher.size() > 0) {
            dispatcher.next();
        }

        return System.nanoTime() - start;
    }

    private static void warmUp(
            Submission[] burst
    ) {
        int warmUpSize =
                Math.min(500, burst.length);

        Submission[] warmUpBurst =
                new Submission[warmUpSize];

        for (int i = 0; i < warmUpSize; i++) {
            warmUpBurst[i] = burst[i];
        }

        timeNaive(warmUpBurst);
        timeHeap(warmUpBurst);
        timeBuildHeap(warmUpBurst);
    }
}
