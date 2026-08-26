public class WP2Demo {
    public static void main(String[] args) {
        smallDemo();
        timingTest();
    }

    private static void smallDemo() {
        CircularUploadQueue queue = new CircularUploadQueue(5);
        Submission[] data = createData(8);

        System.out.println("=== CIRCULAR QUEUE DEMO ===");
        System.out.println("Initial state:");
        queue.printState();

        System.out.println("\nFill the queue:");
        for (int i = 0; i < 5; i++) {
            printEnqueue(queue, data[i]);
        }

        System.out.println("\nDequeue two:");
        System.out.println("Removed: " + queue.dequeue().getStudentId());
        System.out.println("Removed: " + queue.dequeue().getStudentId());
        queue.printState();

        System.out.println("\nEnqueue three:");
        for (int i = 5; i < 8; i++) {
            printEnqueue(queue, data[i]);
        }

        System.out.println("\nS-0008 is rejected because the queue is full.");
        System.out.println("Full-buffer policy: reject the new upload.");
    }

    private static void printEnqueue(CircularUploadQueue queue, Submission s) {
        boolean accepted = queue.enqueue(s);
        System.out.println(s.getStudentId() + " accepted: " + accepted);
        queue.printState();
    }

    private static Submission[] createData(int count) {
        Submission[] data = new Submission[count];
        for (int i = 0; i < count; i++) {
            data[i] = new Submission(
                    String.format("S-%04d", i + 1),
                    "project" + (i + 1) + ".pdf",
                    1000 + i, 80_000_000L + i, 1, false);
        }
        return data;
    }

    private static void timingTest() {
        int count = 10_000;
        Submission[] data = createData(count);
        runQueues(data, 1_000); // Warm-up run is not printed.

        long[] times = runQueues(data, count);
        System.out.println("\n=== 10,000 UPLOAD TIMING ===");
        System.out.println("Naive queue:    " + times[0] + " ns");
        System.out.println("Circular queue: " + times[1] + " ns");
        System.out.printf("Circular speedup: %.2f times%n",
                (double) times[0] / times[1]);
    }

    private static long[] runQueues(Submission[] data, int count) {
        NaiveUploadQueue naive = new NaiveUploadQueue(count);
        long start = System.nanoTime();
        for (int i = 0; i < count; i++) {
            naive.enqueue(data[i]);
        }
        while (naive.dequeue() != null) {
            // Keep removing until empty.
        }
        long naiveTime = System.nanoTime() - start;

        CircularUploadQueue circular = new CircularUploadQueue(count);
        start = System.nanoTime();
        for (int i = 0; i < count; i++) {
            circular.enqueue(data[i]);
        }
        while (circular.dequeue() != null) {
            // Keep removing until empty.
        }
        long circularTime = System.nanoTime() - start;

        return new long[]{naiveTime, circularTime};
    }
}
