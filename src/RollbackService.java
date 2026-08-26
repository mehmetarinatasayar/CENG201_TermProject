public class RollbackService {
    private static class History {
        String studentId;
        VersionStack stack = new VersionStack();
        History next;

        History(String studentId) {
            this.studentId = studentId;
        }
    }

    private final SubmissionRegistry registry;
    private History[] table = new History[8];
    private int count;

    public RollbackService(SubmissionRegistry registry) {
        this.registry = registry;
    }

    public int reupload(String studentId, String fileName,
                        int sizeKb, long timestampMs) {
        Submission active = registry.lookup(studentId);
        if (active == null) {
            throw new IllegalArgumentException("Student not found: " + studentId);
        }

        VersionRecord oldVersion = new VersionRecord(
                active.getFileName(), active.getSizeKb(),
                active.getTimestampMs(), active.getVersion());

        getOrCreateHistory(studentId).stack.push(oldVersion);
        return registry.updateVersion(studentId, fileName, sizeKb, timestampMs);
    }

    public void rollback(String studentId) {
        Submission active = registry.lookup(studentId);
        if (active == null) {
            System.out.println("Student not found: " + studentId);
            return;
        }

        History history = findHistory(studentId);
        if (history == null || history.stack.isEmpty()) {
            System.out.println("No earlier version for " + studentId);
            return;
        }

        VersionRecord old = history.stack.pop();
        active.restoreFile(old.getFileName(), old.getSizeKb(),
                old.getTimestampMs(), old.getVersion());

        System.out.println("Rollback completed for " + studentId
                + ": restored v" + old.getVersion());
    }

    public void printHistory(String studentId) {
        System.out.print(studentId + " ");
        History history = findHistory(studentId);

        if (history == null) {
            System.out.println("Stack top -> [empty]");
        } else {
            history.stack.printState();
        }
    }

    private int indexFor(String studentId) {
        return Math.floorMod(studentId.hashCode(), table.length);
    }

    private History findHistory(String studentId) {
        History current = table[indexFor(studentId)];
        while (current != null) {
            if (current.studentId.equals(studentId)) {
                return current;
            }
            current = current.next;
        }
        return null;
    }

    private History getOrCreateHistory(String studentId) {
        History history = findHistory(studentId);
        if (history != null) {
            return history;
        }

        int index = indexFor(studentId);
        history = new History(studentId);
        history.next = table[index];
        table[index] = history;
        count++;

        if ((double) count / table.length > 0.75) {
            resize();
        }
        return history;
    }

    private void resize() {
        History[] oldTable = table;
        table = new History[oldTable.length * 2];

        for (int i = 0; i < oldTable.length; i++) {
            History current = oldTable[i];
            while (current != null) {
                History next = current.next;
                int newIndex = indexFor(current.studentId);
                current.next = table[newIndex];
                table[newIndex] = current;
                current = next;
            }
        }
    }
}
