public class SubmissionRegistry {
    private static class Entry {
        String key;
        Submission value;
        Entry next;

        Entry(String key, Submission value) {
            this.key = key;
            this.value = value;
        }
    }

    private Entry[] table = new Entry[8];
    private int count;

    private int indexFor(String key) {
        return Math.floorMod(key.hashCode(), table.length);
    }

    public void put(Submission submission) {
        String key = submission.getStudentId();
        int index = indexFor(key);
        Entry current = table[index];

        while (current != null) {
            if (current.key.equals(key)) {
                current.value = submission;
                return;
            }
            current = current.next;
        }

        Entry newEntry = new Entry(key, submission);
        newEntry.next = table[index];
        table[index] = newEntry;
        count++;

        if ((double) count / table.length > 0.75) {
            resize();
        }
    }

    public Submission lookup(String studentId) {
        Entry current = table[indexFor(studentId)];

        while (current != null) {
            if (current.key.equals(studentId)) {
                return current.value;
            }
            current = current.next;
        }
        return null;
    }

    public int updateVersion(String studentId, String fileName,
                             int sizeKb, long timestampMs) {
        Submission submission = lookup(studentId);
        if (submission == null) {
            throw new IllegalArgumentException("Student not found: " + studentId);
        }

        submission.replaceFile(fileName, sizeKb, timestampMs);
        return submission.getVersion();
    }

    public int size() {
        return count;
    }

    private void resize() {
        Entry[] oldTable = table;
        table = new Entry[oldTable.length * 2];

        for (int i = 0; i < oldTable.length; i++) {
            Entry current = oldTable[i];
            while (current != null) {
                Entry next = current.next;
                int newIndex = indexFor(current.key);
                current.next = table[newIndex];
                table[newIndex] = current;
                current = next;
            }
        }
    }
}
