public class NaiveUploadQueue {
    private final Submission[] queue;
    private int count;

    public NaiveUploadQueue(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be positive.");
        }
        queue = new Submission[capacity];
    }

    public boolean enqueue(Submission submission) {
        if (count == queue.length) {
            return false;
        }
        queue[count] = submission;
        count++;
        return true;
    }

    public Submission dequeue() {
        if (count == 0) {
            return null;
        }

        Submission first = queue[0];

        // This loop makes the naive dequeue O(n).
        for (int i = 1; i < count; i++) {
            queue[i - 1] = queue[i];
        }

        count--;
        queue[count] = null;
        return first;
    }

    public int size() {
        return count;
    }

    public void printState() {
        System.out.print("[");
        for (int i = 0; i < count; i++) {
            System.out.print(queue[i].getStudentId());
            if (i < count - 1) {
                System.out.print(", ");
            }
        }
        System.out.println("]");
    }
}
