public class ScalingStudy {

    private static final int[] SIZES = {1_000, 10_000, 100_000};

    public static void main(String[] args) {
        warmUp();

        System.out.println("=== SCALING STUDY ===");
        System.out.println("Times are in milliseconds.");
        System.out.println();
        System.out.printf("%-9s %12s %12s %12s %12s %14s%n",
                "Records", "Naive queue", "Circular", "Naive dispatch",
                "Binary heap", "End-to-end");
        System.out.println("--------------------------------------------------------------------------");

        for (int i = 0; i < SIZES.length; i++) {
            int size = SIZES[i];
            Submission[] records = createRecords(size);

            double naiveQueue = measureNaiveQueue(records);
            double circularQueue = measureCircularQueue(records);
            double naiveDispatcher = measureNaiveDispatcher(records);
            double heapDispatcher = measureHeapDispatcher(records);
            double endToEnd = measureEndToEnd(records);

            System.out.printf("%,9d %12.3f %12.3f %12.3f %12.3f %14.3f%n",
                    size, naiveQueue, circularQueue, naiveDispatcher,
                    heapDispatcher, endToEnd);
        }
    }

    private static void warmUp() {
        Submission[] records = createRecords(1_000);
        measureNaiveQueue(records);
        measureCircularQueue(records);
        measureNaiveDispatcher(records);
        measureHeapDispatcher(records);
        measureEndToEnd(records);
    }

    private static Submission[] createRecords(int count) {
        Submission[] records = new Submission[count];

        for (int i = 0; i < count; i++) {
            records[i] = new Submission(
                    String.format("S-%06d", i + 1),
                    "upload_" + (i + 1) + ".pdf",
                    500 + (i * 37) % 4_500,
                    80_000_000L + i,
                    1,
                    i % 33 == 0
            );
        }

        return records;
    }

    private static double measureNaiveQueue(Submission[] records) {
        NaiveUploadQueue queue = new NaiveUploadQueue(records.length);
        long start = System.nanoTime();

        for (int i = 0; i < records.length; i++) {
            queue.enqueue(records[i]);
        }
        while (queue.dequeue() != null) {
            // Dequeue every record so shifting cost is measured.
        }

        return millisecondsSince(start);
    }

    private static double measureCircularQueue(Submission[] records) {
        CircularUploadQueue queue = new CircularUploadQueue(records.length);
        long start = System.nanoTime();

        for (int i = 0; i < records.length; i++) {
            queue.enqueue(records[i]);
        }
        while (queue.dequeue() != null) {
            // Dequeue every record.
        }

        return millisecondsSince(start);
    }

    private static double measureNaiveDispatcher(Submission[] records) {
        NaiveDispatcher dispatcher = new NaiveDispatcher();
        long start = System.nanoTime();

        for (int i = 0; i < records.length; i++) {
            dispatcher.submit(records[i]);
        }
        while (dispatcher.next() != null) {
            // Dispatch every record.
        }

        return millisecondsSince(start);
    }

    private static double measureHeapDispatcher(Submission[] records) {
        HeapDispatcher dispatcher = new HeapDispatcher();
        long start = System.nanoTime();

        dispatcher.loadBurst(records);
        while (dispatcher.next() != null) {
            // Dispatch every record.
        }

        return millisecondsSince(start);
    }

    private static double measureEndToEnd(Submission[] records) {
        CircularUploadQueue intake = new CircularUploadQueue(records.length);
        HeapDispatcher dispatcher = new HeapDispatcher();
        SubmissionRegistry registry = new SubmissionRegistry();
        SubmissionTimeline timeline = new SubmissionTimeline();

        long start = System.nanoTime();

        for (int i = 0; i < records.length; i++) {
            intake.enqueue(records[i]);
        }

        Submission current = intake.dequeue();
        while (current != null) {
            dispatcher.submit(current);
            current = intake.dequeue();
        }

        current = dispatcher.next();
        while (current != null) {
            registry.put(current);
            timeline.insert(current);
            current = dispatcher.next();
        }

        if (registry.size() != records.length) {
            throw new IllegalStateException("A record was lost in the pipeline.");
        }

        return millisecondsSince(start);
    }

    private static double millisecondsSince(long start) {
        return (System.nanoTime() - start) / 1_000_000.0;
    }
}

