public class ReportService {

    public static Submission[] topKLargest(
            Submission[] all,
            int k
    ) {
        if (k <= 0 || all.length == 0) {
            return new Submission[0];
        }

        SizeMinHeap heap =
                new SizeMinHeap(Math.min(k, all.length));

        for (int i = 0; i < all.length; i++) {
            heap.offer(all[i]);
        }

        Submission[] result =
                new Submission[heap.size()];

        for (int i = result.length - 1; i >= 0; i--) {
            result[i] = heap.removeMin();
        }

        return result;
    }

    public Submission[] sortByTimeFast(
            Submission[] all
    ) {
        Submission[] copy = copyOf(all);
        Submission[] temporary =
                new Submission[copy.length];

        mergeSort(
                copy, temporary, 0, copy.length - 1
        );

        return copy;
    }

    public Submission[] sortByTimeInsertion(
            Submission[] all
    ) {
        Submission[] copy = copyOf(all);

        for (int i = 1; i < copy.length; i++) {
            Submission current = copy[i];
            int j = i - 1;

            while (
                    j >= 0
                            && comesAfter(
                                    copy[j], current
                            )
            ) {
                copy[j + 1] = copy[j];
                j--;
            }

            copy[j + 1] = current;
        }

        return copy;
    }

    public static int findFirstAfter(
            Submission[] ascending,
            long deadlineMs
    ) {
        int left = 0;
        int right = ascending.length - 1;
        int answer = -1;

        while (left <= right) {
            int middle =
                    left + (right - left) / 2;

            if (
                    ascending[middle].getTimestampMs()
                            > deadlineMs
            ) {
                answer = middle;
                right = middle - 1;
            } else {
                left = middle + 1;
            }
        }

        return answer;
    }

    public void printSheet(
            Submission[] ascending
    ) {
        System.out.printf(
                "%-10s %-24s %-8s %-13s %-5s%n",
                "STUDENT", "FILE", "VERSION",
                "TIME", "LATE"
        );
        System.out.println(
                "---------------------------------------------------------------"
        );

        for (int i = 0; i < ascending.length; i++) {
            Submission submission = ascending[i];

            System.out.printf(
                    "%-10s %-24s %-8d %-13s %-5s%n",
                    fit(submission.getStudentId(), 10),
                    fit(submission.getFileName(), 24),
                    submission.getVersion(),
                    submission.clock(),
                    submission.isLate() ? "YES" : "NO"
            );
        }
    }

    private void mergeSort(
            Submission[] values,
            Submission[] temporary,
            int left,
            int right
    ) {
        if (left >= right) {
            return;
        }

        int middle = left + (right - left) / 2;

        mergeSort(values, temporary, left, middle);
        mergeSort(
                values, temporary, middle + 1, right
        );
        merge(
                values, temporary, left, middle, right
        );
    }

    private void merge(
            Submission[] values,
            Submission[] temporary,
            int left,
            int middle,
            int right
    ) {
        int leftIndex = left;
        int rightIndex = middle + 1;
        int targetIndex = left;

        while (
                leftIndex <= middle
                        && rightIndex <= right
        ) {
            if (
                    !comesAfter(
                            values[leftIndex],
                            values[rightIndex]
                    )
            ) {
                temporary[targetIndex++] =
                        values[leftIndex++];
            } else {
                temporary[targetIndex++] =
                        values[rightIndex++];
            }
        }

        while (leftIndex <= middle) {
            temporary[targetIndex++] =
                    values[leftIndex++];
        }

        while (rightIndex <= right) {
            temporary[targetIndex++] =
                    values[rightIndex++];
        }

        for (int i = left; i <= right; i++) {
            values[i] = temporary[i];
        }
    }

    private boolean comesAfter(
            Submission first,
            Submission second
    ) {
        if (
                first.getTimestampMs()
                        != second.getTimestampMs()
        ) {
            return first.getTimestampMs()
                    > second.getTimestampMs();
        }

        return first.getStudentId().compareTo(
                second.getStudentId()
        ) > 0;
    }

    private Submission[] copyOf(
            Submission[] original
    ) {
        Submission[] copy =
                new Submission[original.length];

        for (int i = 0; i < original.length; i++) {
            copy[i] = original[i];
        }

        return copy;
    }

    private String fit(String value, int width) {
        return value.length() <= width
                ? value
                : value.substring(0, width);
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
                return;
            }

            if (!isLarger(submission, heap[0])) {
                return;
            }

            heap[0] = submission;
            siftDown(0);
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
            int current = index;

            while (current > 0) {
                int parent = (current - 1) / 2;

                if (
                        !isSmaller(
                                heap[current], heap[parent]
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
                int left = current * 2 + 1;
                int right = current * 2 + 2;
                int smallest = current;

                if (
                        left < count
                                && isSmaller(
                                        heap[left],
                                        heap[smallest]
                                )
                ) {
                    smallest = left;
                }

                if (
                        right < count
                                && isSmaller(
                                        heap[right],
                                        heap[smallest]
                                )
                ) {
                    smallest = right;
                }

                if (smallest == current) {
                    break;
                }

                swap(current, smallest);
                current = smallest;
            }
        }

        private boolean isSmaller(
                Submission first,
                Submission second
        ) {
            if (
                    first.getSizeKb()
                            != second.getSizeKb()
            ) {
                return first.getSizeKb()
                        < second.getSizeKb();
            }

            return first.getStudentId().compareTo(
                    second.getStudentId()
            ) > 0;
        }

        private boolean isLarger(
                Submission first,
                Submission second
        ) {
            if (
                    first.getSizeKb()
                            != second.getSizeKb()
            ) {
                return first.getSizeKb()
                        > second.getSizeKb();
            }

            return first.getStudentId().compareTo(
                    second.getStudentId()
            ) < 0;
        }

        private void swap(int first, int second) {
            Submission temporary = heap[first];
            heap[first] = heap[second];
            heap[second] = temporary;
        }
    }
}
