import java.util.Random;

public class ScenarioGenerator {

    public static final int STUDENT_COUNT = 800;
    public static final int EVENT_COUNT = 2_500;
    public static final int BURST_1_SIZE = 800;
    public static final int BURST_2_SIZE = 800;
    public static final int BURST_3_SIZE = 900;
    public static final int REUPLOADING_STUDENT_COUNT = 80;
    public static final long WINDOW_OPENS_MS =
            79_200_000L;

    private final Random rng;
    private final boolean[] accommodation;
    private long clockMs = WINDOW_OPENS_MS;

    public ScenarioGenerator(long seed) {
        rng = new Random(seed);
        accommodation =
                new boolean[STUDENT_COUNT];

        for (int i = 0; i < STUDENT_COUNT; i++) {
            accommodation[i] =
                    rng.nextInt(100) < 3;
        }
    }

    public String studentId(int index) {
        return String.format(
                "S-%04d", index + 1
        );
    }

    public boolean hasAccommodation(int index) {
        return accommodation[index];
    }

    public Submission nextUpload(int index) {
        clockMs += 1 + rng.nextInt(2_000);
        int sizeKb = 200 + rng.nextInt(4_800);
        String fileName =
                studentId(index) + "_project.pdf";

        return new Submission(
                studentId(index), fileName, sizeKb,
                clockMs, 1, accommodation[index]
        );
    }

    public UploadEvent[] generateUploadEvents() {
        UploadEvent[] events =
                new UploadEvent[EVENT_COUNT];

        int eventIndex = 0;
        clockMs = WINDOW_OPENS_MS;

        for (
                int studentIndex = 0;
                studentIndex < STUDENT_COUNT;
                studentIndex++
        ) {
            clockMs += 3_000L;

            events[eventIndex] = createEvent(
                    studentIndex, eventIndex,
                    false, false, 1
            );
            eventIndex++;
        }

        for (int i = 0; i < BURST_2_SIZE; i++) {
            clockMs += 1_200L;

            int studentIndex =
                    reuploadStudentIndex(i);

            boolean rollbackRequested =
                    i > 0 && i % 40 == 0;

            events[eventIndex] = createEvent(
                    studentIndex, eventIndex,
                    true, rollbackRequested, 2
            );
            eventIndex++;
        }

        clockMs =
                Submission.DEADLINE_MS - 120_000L;

        for (int i = 0; i < BURST_3_SIZE; i++) {
            clockMs += 300L;

            int studentIndex =
                    reuploadStudentIndex(
                            BURST_2_SIZE + i
                    );

            boolean rollbackRequested =
                    i > 0 && i % 45 == 0;

            events[eventIndex] = createEvent(
                    studentIndex, eventIndex,
                    true, rollbackRequested, 3
            );
            eventIndex++;
        }

        return events;
    }

    private UploadEvent createEvent(
            int studentIndex,
            int eventIndex,
            boolean reupload,
            boolean rollbackRequested,
            int burstNumber
    ) {
        int sizeKb = 200 + rng.nextInt(4_800);

        String fileName =
                studentId(studentIndex)
                        + "_upload_"
                        + (eventIndex + 1)
                        + ".pdf";

        return new UploadEvent(
                studentId(studentIndex),
                fileName,
                sizeKb,
                clockMs,
                accommodation[studentIndex],
                reupload,
                rollbackRequested,
                burstNumber
        );
    }

    private int reuploadStudentIndex(
            int eventNumber
    ) {
        return (
                eventNumber * 37
        ) % REUPLOADING_STUDENT_COUNT;
    }

    public static void main(String[] args) {
        ScenarioGenerator generator =
                new ScenarioGenerator(20260725L);

        UploadEvent[] events =
                generator.generateUploadEvents();

        System.out.println(
                "Generated events: " + events.length
        );
        System.out.println(
                "First event: " + events[0]
        );
        System.out.println(
                "Last event:  "
                        + events[events.length - 1]
        );
    }
}
