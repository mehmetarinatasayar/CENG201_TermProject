public class RollbackService {

    private static class HistoryEntry {
        String studentId;
        VersionStack stack;
        HistoryEntry next;

        HistoryEntry(String studentId) {
            this.studentId = studentId;
            this.stack = new VersionStack();
            this.next = null;
        }
    }

    private final SubmissionRegistry registry;
    private HistoryEntry[] histories;
    private int historyCount;

    private static final int INITIAL_CAPACITY = 8;
    private static final double LOAD_FACTOR_LIMIT = 0.75;

    public RollbackService(SubmissionRegistry registry) {
        this.registry = registry;
        this.histories =
                new HistoryEntry[INITIAL_CAPACITY];
        this.historyCount = 0;
    }

    public int reupload(
            String studentId,
            String fileName,
            int sizeKb,
            long timestampMs
    ) {
        Submission active =
                registry.lookup(studentId);

        if (active == null) {
            throw new IllegalArgumentException(
                    "Student not found: " + studentId
            );
        }

        VersionRecord oldVersion =
                new VersionRecord(
                        active.getFileName(),
                        active.getSizeKb(),
                        active.getTimestampMs(),
                        active.getVersion()
                );

        HistoryEntry history =
                getOrCreateHistory(studentId);

        history.stack.push(oldVersion);

        return registry.updateVersion(
                studentId,
                fileName,
                sizeKb,
                timestampMs
        );
    }

    public void rollback(String studentId) {
        Submission active =
                registry.lookup(studentId);

        if (active == null) {
            System.out.println(
                    "Student not found: " + studentId
            );
            return;
        }

        HistoryEntry history =
                findHistory(studentId);

        if (
                history == null
                        || history.stack.isEmpty()
        ) {
            System.out.println(
                    "No earlier version for " + studentId
            );
            return;
        }

        VersionRecord previous =
                history.stack.pop();

        active.restoreFile(
                previous.getFileName(),
                previous.getSizeKb(),
                previous.getTimestampMs(),
                previous.getVersion()
        );

        System.out.println(
                "Rollback completed for "
                        + studentId
                        + ": restored v"
                        + previous.getVersion()
        );
    }

    public void printHistory(String studentId) {
        HistoryEntry history =
                findHistory(studentId);

        System.out.print(studentId + " ");

        if (history == null) {
            System.out.println(
                    "Stack top -> [empty]"
            );
            return;
        }

        history.stack.printState();
    }

    private HistoryEntry findHistory(
            String studentId
    ) {
        int index = indexFor(
                studentId,
                histories.length
        );

        HistoryEntry current = histories[index];

        while (current != null) {
            if (current.studentId.equals(studentId)) {
                return current;
            }

            current = current.next;
        }

        return null;
    }

    private HistoryEntry getOrCreateHistory(
            String studentId
    ) {
        HistoryEntry existing =
                findHistory(studentId);

        if (existing != null) {
            return existing;
        }

        int index = indexFor(
                studentId,
                histories.length
        );

        HistoryEntry newEntry =
                new HistoryEntry(studentId);

        newEntry.next = histories[index];
        histories[index] = newEntry;
        historyCount++;

        double loadFactor =
                (double) historyCount
                        / histories.length;

        if (loadFactor > LOAD_FACTOR_LIMIT) {
            resize();
        }

        return newEntry;
    }

    private int indexFor(
            String studentId,
            int capacity
    ) {
        return Math.floorMod(
                studentId.hashCode(),
                capacity
        );
    }

    private void resize() {
        HistoryEntry[] oldHistories = histories;

        histories =
                new HistoryEntry[
                        oldHistories.length * 2
                ];

        for (
                int i = 0;
                i < oldHistories.length;
                i++
        ) {
            HistoryEntry current =
                    oldHistories[i];

            while (current != null) {
                HistoryEntry nextEntry =
                        current.next;

                int newIndex = indexFor(
                        current.studentId,
                        histories.length
                );

                current.next =
                        histories[newIndex];

                histories[newIndex] = current;
                current = nextEntry;
            }
        }
    }
}
