public class SubmissionRegistry {

    /*
     * Hash table içindeki bağlı liste düğümü.
     * Aynı indekse düşen kayıtlar next ile bağlanır.
     */
    private static class Entry {
        String key;
        Submission value;
        Entry next;

        Entry(String key, Submission value) {
            this.key = key;
            this.value = value;
            this.next = null;
        }
    }

    private Entry[] table;
    private int count;

    private static final int INITIAL_CAPACITY = 8;
    private static final double LOAD_FACTOR_LIMIT = 0.75;

    public SubmissionRegistry() {
        table = new Entry[INITIAL_CAPACITY];
        count = 0;
    }

    /*
     * Öğrenci numarasını geçerli bir dizi indeksine çevirir.
     */
    private int indexFor(String key) {
        return Math.floorMod(key.hashCode(), table.length);
    }

    /*
     * Yeni bir submission ekler.
     * Aynı öğrenci zaten varsa aktif kaydı değiştirir.
     */
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

        // Yeni kayıt zincirin başına eklenir.
        newEntry.next = table[index];
        table[index] = newEntry;
        count++;

        double loadFactor = (double) count / table.length;

        if (loadFactor > LOAD_FACTOR_LIMIT) {
            resize();
        }
    }

    /*
     * Öğrencinin aktif submission kaydını bulur.
     * Öğrenci yoksa null döndürür.
     */
    public Submission lookup(String studentId) {
        int index = indexFor(studentId);
        Entry current = table[index];

        while (current != null) {
            if (current.key.equals(studentId)) {
                return current.value;
            }

            current = current.next;
        }

        return null;
    }

    /*
     * Re-upload işleminde dosya bilgilerini değiştirir
     * ve version değerini bir artırır.
     */
    public int updateVersion(
            String studentId,
            String fileName,
            int sizeKb,
            long timestampMs
    ) {
        Submission submission = lookup(studentId);

        if (submission == null) {
            throw new IllegalArgumentException(
                    "Student not found: " + studentId
            );
        }

        submission.replaceFile(fileName, sizeKb, timestampMs);

        return submission.getVersion();
    }

    /*
     * Hash table içindeki öğrenci sayısını döndürür.
     */
    public int size() {
        return count;
    }

    /*
     * Load factor sınırı aşılınca tablo kapasitesini iki katına çıkarır.
     * Bütün kayıtlar yeni kapasiteye göre tekrar yerleştirilir.
     */
    private void resize() {
        Entry[] oldTable = table;
        table = new Entry[oldTable.length * 2];

        for (int i = 0; i < oldTable.length; i++) {
            Entry current = oldTable[i];

            while (current != null) {
                Entry nextEntry = current.next;

                int newIndex = indexFor(current.key);

                current.next = table[newIndex];
                table[newIndex] = current;

                current = nextEntry;
            }
        }
    }
}
