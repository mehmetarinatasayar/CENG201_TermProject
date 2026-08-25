public class HeapDispatcher {

    private Submission[] heap;
    private int count;

    private static final int INITIAL_CAPACITY = 8;

    public HeapDispatcher() {
        heap = new Submission[INITIAL_CAPACITY];
        count = 0;
    }

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
        int requiredCapacity = Math.max(
                INITIAL_CAPACITY,
                burst.length
        );

        heap = new Submission[requiredCapacity];

        for (int i = 0; i < burst.length; i++) {
            heap[i] = burst[i];
        }

        count = burst.length;

        for (int i = count / 2 - 1; i >= 0; i--) {
            siftDown(i);
        }
    }

    public int size() {
        return count;
    }

    private void siftUp(int index) {
        int current = index;

        while (current > 0) {
            int parent = (current - 1) / 2;

            if (
                    !hasHigherPriority(
                            heap[current],
                            heap[parent]
                    )
            ) {
                break;
            }

            swap(current, parent);
            current = parent;
        }
    }

    private void siftDown(int index) {
        int current = index;

        while (true) {
            int leftChild = current * 2 + 1;
            int rightChild = current * 2 + 2;
            int highest = current;

            if (
                    leftChild < count
                            && hasHigherPriority(
                                    heap[leftChild],
                                    heap[highest]
                            )
            ) {
                highest = leftChild;
            }

            if (
                    rightChild < count
                            && hasHigherPriority(
                                    heap[rightChild],
                                    heap[highest]
                            )
            ) {
                highest = rightChild;
            }

            if (highest == current) {
                break;
            }

            swap(current, highest);
            current = highest;
        }
    }

    private boolean hasHigherPriority(
            Submission first,
            Submission second
    ) {
        if (
                first.hasAccommodation()
                        != second.hasAccommodation()
        ) {
            return first.hasAccommodation();
        }

        if (
                first.getTimestampMs()
                        != second.getTimestampMs()
        ) {
            return first.getTimestampMs()
                    < second.getTimestampMs();
        }

        return first.getStudentId().compareTo(
                second.getStudentId()
        ) < 0;
    }

    private void swap(int firstIndex, int secondIndex) {
        Submission temporary = heap[firstIndex];
        heap[firstIndex] = heap[secondIndex];
        heap[secondIndex] = temporary;
    }

    private void resize() {
        Submission[] larger =
                new Submission[heap.length * 2];

        for (int i = 0; i < count; i++) {
            larger[i] = heap[i];
        }

        heap = larger;
    }
}
