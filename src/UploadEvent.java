public class UploadEvent extends Submission {

    private final boolean reupload;
    private final boolean rollbackRequested;
    private final int burstNumber;

    public UploadEvent(
            String studentId,
            String fileName,
            int sizeKb,
            long timestampMs,
            boolean accommodationFlag,
            boolean reupload,
            boolean rollbackRequested,
            int burstNumber
    ) {
        super(
                studentId,
                fileName,
                sizeKb,
                timestampMs,
                1,
                accommodationFlag
        );

        this.reupload = reupload;
        this.rollbackRequested =
                rollbackRequested;
        this.burstNumber = burstNumber;
    }

    public boolean isReupload() {
        return reupload;
    }

    public boolean isRollbackRequested() {
        return rollbackRequested;
    }

    public int getBurstNumber() {
        return burstNumber;
    }
}
