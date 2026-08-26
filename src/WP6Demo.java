public class WP6Demo {
    public static void main(String[] args) {
        reportDemo();
        System.out.println();
        sortTiming(1_000);
        System.out.println();
        sortTiming(100_000);
    }

    private static void reportDemo() {
        Submission[] data = {
                create("S-0001", "small.pdf", 500, 86_000_000L),
                create("S-0002", "largest.pdf", 9_000, 85_000_000L),
                create("S-0003", "medium.pdf", 2_000, 86_200_000L),
                create("S-0004", "second-largest.pdf", 8_000, Submission.DEADLINE_MS),
                create("S-0005", "late-one.pdf", 1_500, Submission.DEADLINE_MS + 1),
                create("S-0006", "third-largest.pdf", 7_000, 84_000_000L),
                create("S-0007", "late-two.pdf", 3_000, 86_400_000L),
                create("S-0008", "normal.pdf", 1_200, 83_000_000L),
                create("S-0009", "another.pdf", 2_500, 85_500_000L)
        };

        ReportService service = new ReportService();
        Submission[] topThree = ReportService.topKLargest(data, 3);
        Submission[] fast = service.sortByTimeFast(data);
        Submission[] insertion = service.sortByTimeInsertion(data);

        System.out.println("=== WP6 DEADLINE REPORT DEMO ===");
        System.out.println("\nTop-3 largest files:");
        for (int i = 0; i < topThree.length; i++) {
            Submission s = topThree[i];
            System.out.println((i + 1) + ". " + s.getStudentId() + " "
                    + s.getFileName() + " " + s.getSizeKb() + " KB");
        }

        System.out.println("\nBoth sorts match: " + sameOrder(fast, insertion));
        System.out.println("\nComplete sheet:");
        service.printSheet(fast);

        int firstLate = ReportService.findFirstAfter(fast, Submission.DEADLINE_MS);
        System.out.println("\nFirst late index: " + firstLate);
        System.out.println("Late submissions:");

        if (firstLate == -1) {
            System.out.println("Nobody is late.");
        } else {
            for (int i = firstLate; i < fast.length; i++) {
                System.out.println(fast[i]);
            }
        }

        binarySearchTests(fast);
    }

    private static void binarySearchTests(Submission[] sorted) {
        long beforeAll = sorted[0].getTimestampMs() - 1;
        long between = (sorted[3].getTimestampMs() + sorted[4].getTimestampMs()) / 2;
        long afterAll = sorted[sorted.length - 1].getTimestampMs() + 1;

        System.out.println("\n=== BINARY SEARCH EDGE CASES ===");
        System.out.println("Deadline before all -> index: "
                + ReportService.findFirstAfter(sorted, beforeAll));
        System.out.println("Deadline between two -> index: "
                + ReportService.findFirstAfter(sorted, between));
        System.out.println("Deadline after all -> index: "
                + ReportService.findFirstAfter(sorted, afterAll));
    }

    private static void sortTiming(int count) {
        Submission[] data = reverseData(count);
        ReportService service = new ReportService();

        // Small warm-up is not included in the result.
        Submission[] warmUp = reverseData(500);
        service.sortByTimeFast(warmUp);
        service.sortByTimeInsertion(warmUp);

        long start = System.nanoTime();
        Submission[] merge = service.sortByTimeFast(data);
        long mergeTime = System.nanoTime() - start;

        start = System.nanoTime();
        Submission[] insertion = service.sortByTimeInsertion(data);
        long insertionTime = System.nanoTime() - start;

        System.out.println("=== " + count + " RECORD SORT TIMING ===");
        System.out.println("Merge sort:     " + mergeTime + " ns");
        System.out.println("Insertion sort: " + insertionTime + " ns");
        System.out.println("Results match: " + sameOrder(merge, insertion));
        System.out.printf("Merge speedup: %.2f times%n",
                (double) insertionTime / mergeTime);
    }

    private static Submission[] reverseData(int count) {
        Submission[] result = new Submission[count];
        for (int i = 0; i < count; i++) {
            result[i] = new Submission(
                    String.format("B-%06d", i + 1), "benchmark.pdf",
                    1000 + i % 4_000, 80_000_000L + count - i, 1, false);
        }
        return result;
    }

    private static boolean sameOrder(Submission[] first, Submission[] second) {
        if (first.length != second.length) {
            return false;
        }

        for (int i = 0; i < first.length; i++) {
            if (first[i].getTimestampMs() != second[i].getTimestampMs()
                    || !first[i].getStudentId().equals(second[i].getStudentId())) {
                return false;
            }
        }
        return true;
    }

    private static Submission create(String id, String file, int size, long time) {
        return new Submission(id, file, size, time, 1, false);
    }
}
