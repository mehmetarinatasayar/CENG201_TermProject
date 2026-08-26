public class ReportService {
    public static Submission[] topKLargest(Submission[] all, int k) {
        if (k <= 0 || all.length == 0) {
            return new Submission[0];
        }

        SizeMinHeap heap = new SizeMinHeap(Math.min(k, all.length));
        for (int i = 0; i < all.length; i++) {
            heap.offer(all[i]);
        }

        Submission[] result = new Submission[heap.size()];
        for (int i = result.length - 1; i >= 0; i--) {
            result[i] = heap.removeMin();
        }
        return result;
    }

    public Submission[] sortByTimeFast(Submission[] all) {
        Submission[] result = copyArray(all);
        Submission[] temp = new Submission[result.length];
        mergeSort(result, temp, 0, result.length - 1);
        return result;
    }

    public Submission[] sortByTimeInsertion(Submission[] all) {
        Submission[] result = copyArray(all);

        for (int i = 1; i < result.length; i++) {
            Submission current = result[i];
            int j = i - 1;

            while (j >= 0 && comesAfter(result[j], current)) {
                result[j + 1] = result[j];
                j--;
            }
            result[j + 1] = current;
        }
        return result;
    }

    public static int findFirstAfter(Submission[] sorted, long deadlineMs) {
        int left = 0;
        int right = sorted.length - 1;
        int answer = -1;

        while (left <= right) {
            int middle = (left + right) / 2;

            if (sorted[middle].getTimestampMs() > deadlineMs) {
                answer = middle;
                right = middle - 1;
            } else {
                left = middle + 1;
            }
        }
        return answer;
    }

    public void printSheet(Submission[] sorted) {
        System.out.printf("%-10s %-24s %-8s %-13s %-5s%n",
                "STUDENT", "FILE", "VERSION", "TIME", "LATE");
        System.out.println("---------------------------------------------------------------");

        for (int i = 0; i < sorted.length; i++) {
            Submission s = sorted[i];
            String late = s.isLate() ? "YES" : "NO";

            System.out.printf("%-10s %-24s %-8d %-13s %-5s%n",
                    fit(s.getStudentId(), 10), fit(s.getFileName(), 24),
                    s.getVersion(), s.clock(), late);
        }
    }

    private void mergeSort(Submission[] values, Submission[] temp,
                           int left, int right) {
        if (left >= right) {
            return;
        }

        int middle = (left + right) / 2;
        mergeSort(values, temp, left, middle);
        mergeSort(values, temp, middle + 1, right);
        merge(values, temp, left, middle, right);
    }

    private void merge(Submission[] values, Submission[] temp,
                       int left, int middle, int right) {
        int i = left;
        int j = middle + 1;
        int k = left;

        while (i <= middle && j <= right) {
            if (!comesAfter(values[i], values[j])) {
                temp[k] = values[i];
                i++;
            } else {
                temp[k] = values[j];
                j++;
            }
            k++;
        }

        while (i <= middle) {
            temp[k] = values[i];
            i++;
            k++;
        }
        while (j <= right) {
            temp[k] = values[j];
            j++;
            k++;
        }
        for (i = left; i <= right; i++) {
            values[i] = temp[i];
        }
    }

    private boolean comesAfter(Submission first, Submission second) {
        if (first.getTimestampMs() != second.getTimestampMs()) {
            return first.getTimestampMs() > second.getTimestampMs();
        }
        return first.getStudentId().compareTo(second.getStudentId()) > 0;
    }

    private Submission[] copyArray(Submission[] original) {
        Submission[] copy = new Submission[original.length];
        for (int i = 0; i < original.length; i++) {
            copy[i] = original[i];
        }
        return copy;
    }

    private String fit(String text, int width) {
        if (text.length() <= width) {
            return text;
        }
        return text.substring(0, width);
    }

    private static class SizeMinHeap {
        private final Submission[] heap;
        private int count;

        SizeMinHeap(int capacity) {
            heap = new Submission[capacity];
        }

        void offer(Submission submission) {
            if (count < heap.length) {
                heap[count] = submission;
                siftUp(count);
                count++;
            } else if (isLarger(submission, heap[0])) {
                heap[0] = submission;
                siftDown(0);
            }
        }

        Submission removeMin() {
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

        int size() {
            return count;
        }

        private void siftUp(int index) {
            while (index > 0) {
                int parent = (index - 1) / 2;
                if (!isSmaller(heap[index], heap[parent])) {
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
                int smallest = index;

                if (left < count && isSmaller(heap[left], heap[smallest])) {
                    smallest = left;
                }
                if (right < count && isSmaller(heap[right], heap[smallest])) {
                    smallest = right;
                }
                if (smallest == index) {
                    break;
                }

                swap(index, smallest);
                index = smallest;
            }
        }

        private boolean isSmaller(Submission first, Submission second) {
            if (first.getSizeKb() != second.getSizeKb()) {
                return first.getSizeKb() < second.getSizeKb();
            }
            return first.getStudentId().compareTo(second.getStudentId()) > 0;
        }

        private boolean isLarger(Submission first, Submission second) {
            if (first.getSizeKb() != second.getSizeKb()) {
                return first.getSizeKb() > second.getSizeKb();
            }
            return first.getStudentId().compareTo(second.getStudentId()) < 0;
        }

        private void swap(int first, int second) {
            Submission temp = heap[first];
            heap[first] = heap[second];
            heap[second] = temp;
        }
    }
}
