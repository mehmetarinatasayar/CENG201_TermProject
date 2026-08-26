public class ExamGateEngine {
    private static final int INTAKE_CAPACITY = 800;

    private final CircularUploadQueue intake = new CircularUploadQueue(INTAKE_CAPACITY);
    private final HeapDispatcher dispatcher = new HeapDispatcher();
    private final SubmissionRegistry registry = new SubmissionRegistry();
    private final RollbackService rollbackService = new RollbackService(registry);
    private final SubmissionTimeline timeline = new SubmissionTimeline();
    private final ReportService reportService = new ReportService();
    private final UploadEvent[] processedEvents =
            new UploadEvent[ScenarioGenerator.EVENT_COUNT];

    private int processedCount;
    private int acceptedCount;
    private int policyActivationCount;
    private int reuploadCount;
    private int rollbackCount;
    private int lateCount;
    private boolean deadlineCheckpointPrinted;

    public void run(long seed) {
        ScenarioGenerator generator = new ScenarioGenerator(seed);
        UploadEvent[] events = generator.generateUploadEvents();

        System.out.println("=== EXAMGATE DEADLINE NIGHT ===");
        System.out.println("Seed: " + seed);
        System.out.println("Students: " + ScenarioGenerator.STUDENT_COUNT);
        System.out.println("Generated uploads: " + events.length);
        System.out.println("Re-uploading students: "
                + ScenarioGenerator.REUPLOADING_STUDENT_COUNT);

        processBurst(events, 0, ScenarioGenerator.BURST_1_SIZE, 1);
        drainPipeline();
        printCheckpoint("AFTER BURST 1");

        int burst2Start = ScenarioGenerator.BURST_1_SIZE;
        int burst2End = burst2Start + ScenarioGenerator.BURST_2_SIZE;

        processBurst(events, burst2Start, burst2End, 2);
        drainPipeline();
        processBurst(events, burst2End, events.length, 3);
        drainPipeline();

        if (!deadlineCheckpointPrinted) {
            printCheckpoint("AT 23:59");
        }

        printCheckpoint("FINAL");
        printFinalReport();
    }

    private void processBurst(UploadEvent[] events, int start,
                              int end, int burstNumber) {
        System.out.println();
        System.out.println("Receiving burst " + burstNumber + "...");

        for (int i = start; i < end; i++) {
            UploadEvent event = events[i];

            if (intake.enqueue(event)) {
                acceptedCount++;
                if (event.isReupload()) {
                    reuploadCount++;
                }
                if (event.isLate()) {
                    lateCount++;
                }
            } else {
                policyActivationCount++;
            }

            if (!deadlineCheckpointPrinted
                    && event.getTimestampMs() == Submission.DEADLINE_MS) {
                printCheckpoint("AT 23:59");
                deadlineCheckpointPrinted = true;
            }
        }
    }

    private void drainPipeline() {
        Submission current;

        while ((current = intake.dequeue()) != null) {
            dispatcher.submit(current);
        }
        while ((current = dispatcher.next()) != null) {
            processEvent((UploadEvent) current);
        }
    }

    private void processEvent(UploadEvent event) {
        if (event.isReupload()) {
            int version = rollbackService.reupload(
                    event.getStudentId(), event.getFileName(),
                    event.getSizeKb(), event.getTimestampMs());

            event.restoreFile(event.getFileName(), event.getSizeKb(),
                    event.getTimestampMs(), version);

            if (event.isRollbackRequested()) {
                rollbackService.rollback(event.getStudentId());
                rollbackCount++;
            }
        } else {
            Submission active = new Submission(
                    event.getStudentId(), event.getFileName(),
                    event.getSizeKb(), event.getTimestampMs(),
                    event.getVersion(), event.hasAccommodation());
            registry.put(active);
        }

        timeline.insert(event);
        processedEvents[processedCount] = event;
        processedCount++;
    }

    private void printCheckpoint(String name) {
        System.out.println();
        System.out.println("=== CHECKPOINT: " + name + " ===");
        System.out.println("Buffer occupancy: " + intake.size() + "/" + INTAKE_CAPACITY);
        System.out.println("Uploads accepted: " + acceptedCount);
        System.out.println("Policy activations: " + policyActivationCount);
        System.out.println("Re-uploads seen: " + reuploadCount);
        System.out.println("Versions rolled back: " + rollbackCount);
        System.out.println("Late count: " + lateCount);
    }

    private void printFinalReport() {
        UploadEvent[] accepted = new UploadEvent[processedCount];
        for (int i = 0; i < processedCount; i++) {
            accepted[i] = processedEvents[i];
        }

        Submission[] sorted = reportService.sortByTimeFast(accepted);
        Submission[] topTen = ReportService.topKLargest(accepted, 10);
        int firstLate = ReportService.findFirstAfter(sorted, Submission.DEADLINE_MS);

        System.out.println();
        System.out.println("=== FINAL REPORT SUMMARY ===");
        System.out.println("Processed uploads: " + processedCount);
        System.out.println("Registry students: " + registry.size());
        System.out.println("Timeline height: " + timeline.height());
        System.out.println("First late index: " + firstLate);
        System.out.println("Top-10 largest accepted files:");

        for (int i = 0; i < topTen.length; i++) {
            Submission s = topTen[i];
            System.out.println((i + 1) + ". " + s.getStudentId() + " "
                    + s.getSizeKb() + " KB " + s.getFileName());
        }
    }

    public static void main(String[] args) {
        ExamGateEngine engine = new ExamGateEngine();
        engine.run(20260725L);
    }
}
