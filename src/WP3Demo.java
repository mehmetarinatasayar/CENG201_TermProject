public class WP3Demo {
    public static void main(String[] args) {
        priorityDemo();
        System.out.println();
        timingTest(1_000);
        System.out.println();
        timingTest(10_000);
    }

    private static void priorityDemo() {
        Submission[] data = {
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
        for (int i = 0; i < data.length; i++) {
            naive.submit(data[i]);
            heap.submit(data[i]);
        }

        Submission[] expected = new Submission[data.length];
        boolean same = true;

        System.out.println("=== WP3 PRIORITY DEMO ===");
        System.out.println("\nNaive order:");
        for (int i = 0; i < data.length; i++) {
            expected[i] = naive.next();
            System.out.println(expected[i]);
        }

        System.out.println("\nHeap order:");
        for (int i = 0; i < data.length; i++) {
            Submission actual = heap.next();
            System.out.println(actual);
            if (actual != expected[i]) {
                same = false;
            }
        }

        HeapDispatcher burstHeap = new HeapDispatcher();
        burstHeap.loadBurst(data);
        boolean burstSame = true;
        for (int i = 0; i < data.length; i++) {
            if (burstHeap.next() != expected[i]) {
                burstSame = false;
            }
        }

        System.out.println("\nBoth dispatchers match: " + same);
        System.out.println("Bottom-up loadBurst matches: " + burstSame);
    }

    private static Submission create(String id, long time, boolean flagged) {
        return new Submission(id, id + "_project.pdf", 1000,
                time, 1, flagged);
    }

    private static void timingTest(int count) {
        Submission[] data = createBurst(count);
        Submission[] warmUp = new Submission[Math.min(500, count)];
        for (int i = 0; i < warmUp.length; i++) {
            warmUp[i] = data[i];
        }

        timeNaive(warmUp);
        timeHeap(warmUp);
        timeBuildHeap(warmUp);

        long naiveTime = timeNaive(data);
        long heapTime = timeHeap(data);
        long buildTime = timeBuildHeap(data);

        System.out.println("=== " + count + " RECORD TIMING ===");
        System.out.println("Naive dispatcher: " + naiveTime + " ns");
        System.out.println("Heap dispatcher:  " + heapTime + " ns");
        System.out.println("Bottom-up heap:    " + buildTime + " ns");
        System.out.printf("Heap speedup: %.2f times%n",
                (double) naiveTime / heapTime);
    }

    private static Submission[] createBurst(int count) {
        Submission[] data = new Submission[count];
        ScenarioGenerator generator = new ScenarioGenerator(20260725L);

        for (int i = 0; i < count; i++) {
            data[i] = generator.nextUpload(i % ScenarioGenerator.STUDENT_COUNT);
        }
        return data;
    }

    private static long timeNaive(Submission[] data) {
        NaiveDispatcher dispatcher = new NaiveDispatcher();
        long start = System.nanoTime();

        for (int i = 0; i < data.length; i++) {
            dispatcher.submit(data[i]);
        }
        while (dispatcher.next() != null) {
            // Dispatch all records.
        }
        return System.nanoTime() - start;
    }

    private static long timeHeap(Submission[] data) {
        HeapDispatcher dispatcher = new HeapDispatcher();
        long start = System.nanoTime();

        for (int i = 0; i < data.length; i++) {
            dispatcher.submit(data[i]);
        }
        while (dispatcher.next() != null) {
            // Dispatch all records.
        }
        return System.nanoTime() - start;
    }

    private static long timeBuildHeap(Submission[] data) {
        HeapDispatcher dispatcher = new HeapDispatcher();
        long start = System.nanoTime();

        dispatcher.loadBurst(data);
        while (dispatcher.next() != null) {
            // Dispatch all records.
        }
        return System.nanoTime() - start;
    }
}
