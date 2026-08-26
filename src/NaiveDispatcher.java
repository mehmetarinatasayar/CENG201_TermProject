public class NaiveDispatcher {
    private Submission[] submissions = new Submission[8];
    private int count;

    public void submit(Submission submission) {
        if (count == submissions.length) {
            resize();
        }

        int i = count - 1;
        while (i >= 0 && hasHigherPriority(submissions[i], submission)) {
            submissions[i + 1] = submissions[i];
            i--;
        }

        submissions[i + 1] = submission;
        count++;
    }

    public Submission next() {
        if (count == 0) {
            return null;
        }

        count--;
        Submission result = submissions[count];
        submissions[count] = null;
        return result;
    }

    public int size() {
        return count;
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

    private void resize() {
        Submission[] larger = new Submission[submissions.length * 2];
        for (int i = 0; i < count; i++) {
            larger[i] = submissions[i];
        }
        submissions = larger;
    }
}
