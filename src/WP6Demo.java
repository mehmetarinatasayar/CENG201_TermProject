public class WP6Demo {

    public static void main(String[] args) {
        runReportDemo();
        System.out.println();
        runSortTiming(1_000);
        System.out.println();
        runSortTiming(100_000);
    }

    private static void runReportDemo() {
        System.out.println(
                "=== WP6 DEADLINE REPORT DEMO ==="
        );

        Submission[] submissions = {
                create("S-0001", "small.pdf", 500,
                        86_000_000L),
                create("S-0002", "largest.pdf", 9_000,
                        85_000_000L),
                create("S-0003", "medium.pdf", 2_000,
                        86_200_000L),
                create("S-0004", "second-largest.pdf", 8_000,
                        Submission.DEADLINE_MS),
                create("S-0005", "late-one.pdf", 1_500,
                        Submission.DEADLINE_MS + 1),
                create("S-0006", "third-largest.pdf", 7_000,
                        84_000_000L),
                create("S-0007", "late-two.pdf", 3_000,
                        86_400_000L),
                create("S-0008", "normal.pdf", 1_200,
                        83_000_000L),
                create("S-0009", "another.pdf", 2_500,
                        85_500_000L)
        };

        ReportService service = new ReportService();

        Submission[] topThree =
                ReportService.topKLargest(
                        submissions, 3
                );

        System.out.println();
        System.out.println("Top-3 largest files:");

        for (int i = 0; i < topThree.length; i++) {
            System.out.println(
                    (i + 1) + ". "
                            + topThree[i].getStudentId()
                            + " "
                            + topThree[i].getFileName()
                            + " "
                            + topThree[i].getSizeKb()
                            + " KB"
            );
        }

        Submission[] fast =
                service.sortByTimeFast(submissions);
        Submission[] insertion =
                service.sortByTimeInsertion(submissions);

        System.out.println();
        System.out.println(
                "Both sorts match: "
                        + sameOrder(fast, insertion)
        );
        System.out.println();
        System.out.println("Complete sheet:");
        service.printSheet(fast);

        int lateStart =
                ReportService.findFirstAfter(
                        fast, Submission.DEADLINE_MS
                );

        System.out.println();
        System.out.println(
                "First late index: " + lateStart
        );
        System.out.println("Late submissions:");

        if (lateStart == -1) {
            System.out.println("Nobody is late.");
        } else {
            for (int i = lateStart; i < fast.length; i++) {
                System.out.println(fast[i]);
            }
        }

        runBinarySearchTests(fast);
    }

    private static void runBinarySearchTests(
            Submission[] ascending
    ) {
        System.out.println();
        System.out.println(
                "=== BINARY SEARCH EDGE CASES ==="
        );

        long beforeAll =
                ascending[0].getTimestampMs() - 1;
        long betweenTwo =
                (
                        ascending[3].getTimestampMs()
                                + ascending[4].getTimestampMs()
                ) / 2;
        long afterAll =
                ascending[
                        ascending.length - 1
                ].getTimestampMs() + 1;

        System.out.println(
                "Deadline before all -> index: "
                        + ReportService.findFirstAfter(
                                ascending, beforeAll
                        )
        );
        System.out.println(
                "Deadline between two -> index: "
                        + ReportService.findFirstAfter(
                                ascending, betweenTwo
                        )
        );
        System.out.println(
                "Deadline after all -> index: "
                        + ReportService.findFirstAfter(
                                ascending, afterAll
                        )
        );
    }

    private static void runSortTiming(int count) {
        System.out.println(
                "=== " + count
                        + " RECORD SORT TIMING ==="
        );

        Submission[] reverse =
                generateReverseData(count);

        warmUp();

        ReportService service = new ReportService();

        long mergeStart = System.nanoTime();
        Submission[] merge =
                service.sortByTimeFast(reverse);
        long mergeTime =
                System.nanoTime() - mergeStart;

        long insertionStart = System.nanoTime();
        Submission[] insertion =
                service.sortByTimeInsertion(reverse);
        long insertionTime =
                System.nanoTime() - insertionStart;

        System.out.println(
                "Merge sort:     " + mergeTime + " ns"
        );
        System.out.println(
                "Insertion sort: " + insertionTime + " ns"
        );
        System.out.println(
                "Results match: "
                        + sameOrder(merge, insertion)
        );

        if (mergeTime > 0) {
            System.out.printf(
                    "Merge speedup: %.2f times%n",
                    (double) insertionTime / mergeTime
            );
        }
    }

    private static Submission[] generateReverseData(
            int count
    ) {
        Submission[] result =
                new Submission[count];

        for (int i = 0; i < count; i++) {
            result[i] = new Submission(
                    String.format("B-%06d", i + 1),
                    "benchmark.pdf",
                    1000 + i % 4_000,
                    80_000_000L + count - i,
                    1,
                    false
            );
        }

        return result;
    }

    private static void warmUp() {
        Submission[] data =
                generateReverseData(500);
        ReportService service = new ReportService();
        service.sortByTimeFast(data);
        service.sortByTimeInsertion(data);
    }

    private static boolean sameOrder(
            Submission[] first,
            Submission[] second
    ) {
        if (first.length != second.length) {
            return false;
        }

        for (int i = 0; i < first.length; i++) {
            if (
                    first[i].getTimestampMs()
                            != second[i].getTimestampMs()
                    || !first[i].getStudentId().equals(
                            second[i].getStudentId()
                    )
            ) {
                return false;
            }
        }

        return true;
    }

    private static Submission create(
            String id,
            String file,
            int size,
            long timestamp
    ) {
        return new Submission(
                id, file, size, timestamp,
                1, false
        );
    }
}
