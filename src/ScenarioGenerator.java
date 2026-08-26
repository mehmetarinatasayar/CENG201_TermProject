import java.util.Random;

public class ScenarioGenerator {
    public static final int STUDENT_COUNT = 800;
    public static final int EVENT_COUNT = 2_500;
    public static final int BURST_1_SIZE = 800;
    public static final int BURST_2_SIZE = 800;
    public static final int BURST_3_SIZE = 900;
    public static final int REUPLOADING_STUDENT_COUNT = 80;
    public static final long WINDOW_OPENS_MS = 79_200_000L;

    private final Random random;
    private final boolean[] accommodation = new boolean[STUDENT_COUNT];
    private long clockMs = WINDOW_OPENS_MS;

    public ScenarioGenerator(long seed) {
        random = new Random(seed);

        for (int i = 0; i < STUDENT_COUNT; i++) {
            accommodation[i] = random.nextInt(100) < 3;
        }
    }

    public String studentId(int index) {
        return String.format("S-%04d", index + 1);
    }

    public boolean hasAccommodation(int index) {
        return accommodation[index];
    }

    public Submission nextUpload(int index) {
        clockMs += 1 + random.nextInt(2_000);
        int sizeKb = 200 + random.nextInt(4_800);
        String fileName = studentId(index) + "_project.pdf";

        return new Submission(studentId(index), fileName, sizeKb,
                clockMs, 1, accommodation[index]);
    }

    public UploadEvent[] generateUploadEvents() {
        UploadEvent[] events = new UploadEvent[EVENT_COUNT];
        int eventIndex = 0;
        clockMs = WINDOW_OPENS_MS;

        // Burst 1: every student uploads once.
        for (int student = 0; student < STUDENT_COUNT; student++) {
            clockMs += 3_000L;
            events[eventIndex] = createEvent(student, eventIndex,
                    false, false, 1);
            eventIndex++;
        }

        // Burst 2: re-uploads from 80 students.
        for (int i = 0; i < BURST_2_SIZE; i++) {
            clockMs += 1_200L;
            int student = reuploadStudentIndex(i);
            boolean rollback = i > 0 && i % 40 == 0;
            events[eventIndex] = createEvent(student, eventIndex,
                    true, rollback, 2);
            eventIndex++;
        }

        // Burst 3 starts two minutes before the deadline.
        clockMs = Submission.DEADLINE_MS - 120_000L;
        for (int i = 0; i < BURST_3_SIZE; i++) {
            clockMs += 300L;
            int student = reuploadStudentIndex(BURST_2_SIZE + i);
            boolean rollback = i > 0 && i % 45 == 0;
            events[eventIndex] = createEvent(student, eventIndex,
                    true, rollback, 3);
            eventIndex++;
        }

        return events;
    }

    private UploadEvent createEvent(int student, int eventIndex,
                                    boolean reupload, boolean rollback,
                                    int burstNumber) {
        int sizeKb = 200 + random.nextInt(4_800);
        String fileName = studentId(student) + "_upload_"
                + (eventIndex + 1) + ".pdf";

        return new UploadEvent(studentId(student), fileName, sizeKb, clockMs,
                accommodation[student], reupload, rollback, burstNumber);
    }

    private int reuploadStudentIndex(int eventNumber) {
        return (eventNumber * 37) % REUPLOADING_STUDENT_COUNT;
    }

    public static void main(String[] args) {
        ScenarioGenerator generator = new ScenarioGenerator(20260725L);
        UploadEvent[] events = generator.generateUploadEvents();

        System.out.println("Generated events: " + events.length);
        System.out.println("First event: " + events[0]);
        System.out.println("Last event:  " + events[events.length - 1]);
    }
}
