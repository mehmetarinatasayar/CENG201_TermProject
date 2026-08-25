public class WP2Demo {

    public static void main(String[] args) {
        runSmallDemo();
        runTimingTest();
    }

    private static void runSmallDemo() {
        System.out.println("=== CIRCULAR QUEUE DEMO ===");

        CircularUploadQueue queue =
                new CircularUploadQueue(5);

        Submission[] submissions = new Submission[8];

        for (int i = 0; i < submissions.length; i++) {
            submissions[i] = new Submission(
                    String.format("S-%04d", i + 1),
                    "project" + (i + 1) + ".pdf",
                    1000 + i,
                    80_000_000L + i,
                    1,
                    false
            );
        }

        System.out.println("Initial state:");
        queue.printState();

        System.out.println("\nFill the queue:");

        for (int i = 0; i < 5; i++) {
            boolean accepted = queue.enqueue(submissions[i]);

            System.out.println(
                    submissions[i].getStudentId()
                            + " accepted: " + accepted
            );

            queue.printState();
        }

        System.out.println("\nDequeue two:");

        Submission first = queue.dequeue();
        Submission second = queue.dequeue();

        System.out.println("Removed: " + first.getStudentId());
        System.out.println("Removed: " + second.getStudentId());
        queue.printState();

        System.out.println("\nEnqueue three:");

        for (int i = 5; i < 8; i++) {
            boolean accepted = queue.enqueue(submissions[i]);

            System.out.println(
                    submissions[i].getStudentId()
                            + " accepted: " + accepted
            );

            queue.printState();
        }

        System.out.println(
                "\nS-0008 is rejected because the queue is full."
        );
        System.out.println(
                "Full-buffer policy: reject the new upload."
        );
    }

    private static void runTimingTest() {
        System.out.println();
        System.out.println("=== 10,000 UPLOAD TIMING ===");

        final int uploadCount = 10_000;
        Submission[] burst = new Submission[uploadCount];

        ScenarioGenerator generator =
                new ScenarioGenerator(20260725L);

        for (int i = 0; i < uploadCount; i++) {
            int studentIndex =
                    i % ScenarioGenerator.STUDENT_COUNT;
            burst[i] = generator.nextUpload(studentIndex);
        }

        warmUp(burst);

        NaiveUploadQueue naive =
                new NaiveUploadQueue(uploadCount);

        long naiveStart = System.nanoTime();

        for (int i = 0; i < uploadCount; i++) {
            naive.enqueue(burst[i]);
        }

        while (naive.size() > 0) {
            naive.dequeue();
        }

        long naiveEnd = System.nanoTime();

        CircularUploadQueue circular =
                new CircularUploadQueue(uploadCount);

        long circularStart = System.nanoTime();

        for (int i = 0; i < uploadCount; i++) {
            circular.enqueue(burst[i]);
        }

        while (circular.size() > 0) {
            circular.dequeue();
        }

        long circularEnd = System.nanoTime();

        long naiveTime = naiveEnd - naiveStart;
        long circularTime = circularEnd - circularStart;

        System.out.println(
                "Naive queue:    " + naiveTime + " ns"
        );
        System.out.println(
                "Circular queue: " + circularTime + " ns"
        );

        if (circularTime > 0) {
            double speedup =
                    (double) naiveTime / circularTime;

            System.out.printf(
                    "Circular speedup: %.2f times%n",
                    speedup
            );
        }
    }

    private static void warmUp(Submission[] burst) {
        int warmUpSize = 1_000;

        NaiveUploadQueue naive =
                new NaiveUploadQueue(warmUpSize);
        CircularUploadQueue circular =
                new CircularUploadQueue(warmUpSize);

        for (int i = 0; i < warmUpSize; i++) {
            naive.enqueue(burst[i]);
            circular.enqueue(burst[i]);
        }

        while (naive.size() > 0) {
            naive.dequeue();
        }

        while (circular.size() > 0) {
            circular.dequeue();
        }
    }
}
