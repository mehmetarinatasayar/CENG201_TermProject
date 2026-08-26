public class CircularUploadQueue {
    private final Submission[] queue;
    private int head;
    private int tail;
    private int count;

    public CircularUploadQueue(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be positive.");
        }
        queue = new Submission[capacity];
    }

    public boolean enqueue(Submission submission) {
        if (count == queue.length) {
            return false; // Reject the new upload when the buffer is full.
        }

        queue[tail] = submission;
        tail = (tail + 1) % queue.length;
        count++;
        return true;
    }

    public Submission dequeue() {
        if (count == 0) {
            return null;
        }

        Submission first = queue[head];
        queue[head] = null;
        head = (head + 1) % queue.length;
        count--;
        return first;
    }

    public int size() {
        return count;
    }

    public void printState() {
        System.out.print("[");
        for (int i = 0; i < count; i++) {
            int index = (head + i) % queue.length;
            System.out.print(queue[index].getStudentId());
            if (i < count - 1) {
                System.out.print(", ");
            }
        }
        System.out.println("]");
    }
}
