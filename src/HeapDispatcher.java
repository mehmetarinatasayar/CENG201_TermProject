public class HeapDispatcher {
    private Submission[] heap = new Submission[8];
    private int count;

    public void submit(Submission submission) {
        if (count == heap.length) {
            resize();
        }
        heap[count] = submission;
        siftUp(count);
        count++;
    }

    public Submission next() {
        if (count == 0) {
            return null;
        }

        Submission result = heap[0];
        count--;
        heap[0] = heap[count];
        heap[count] = null;

        if (count > 0) {
            siftDown(0);
        }
        return result;
    }

    public void loadBurst(Submission[] burst) {
        int capacity = Math.max(8, burst.length);
        heap = new Submission[capacity];

        for (int i = 0; i < burst.length; i++) {
            heap[i] = burst[i];
        }
        count = burst.length;

        // Start from the last parent and move toward the root.
        for (int i = count / 2 - 1; i >= 0; i--) {
            siftDown(i);
        }
    }

    public int size() {
        return count;
    }

    private void siftUp(int index) {
        while (index > 0) {
            int parent = (index - 1) / 2;
            if (!hasHigherPriority(heap[index], heap[parent])) {
                break;
            }
            swap(index, parent);
            index = parent;
        }
    }

    private void siftDown(int index) {
        while (true) {
            int left = index * 2 + 1;
            int right = index * 2 + 2;
            int best = index;

            if (left < count && hasHigherPriority(heap[left], heap[best])) {
                best = left;
            }
            if (right < count && hasHigherPriority(heap[right], heap[best])) {
                best = right;
            }
            if (best == index) {
                break;
            }

            swap(index, best);
            index = best;
        }
    }

    private boolean hasHigherPriority(Submission first, Submission second) {
        if (first.hasAccommodation() != second.hasAccommodation()) {
            return first.hasAccommodation();
        }
        if (first.getTimestampMs() != second.getTimestampMs()) {
            return first.getTimestampMs() < second.getTimestampMs();
        }
        return first.getStudentId().compareTo(second.getStudentId()) < 0;
    }

    private void swap(int first, int second) {
        Submission temp = heap[first];
        heap[first] = heap[second];
        heap[second] = temp;
    }

    private void resize() {
        Submission[] larger = new Submission[heap.length * 2];
        for (int i = 0; i < count; i++) {
            larger[i] = heap[i];
        }
        heap = larger;
    }
}
