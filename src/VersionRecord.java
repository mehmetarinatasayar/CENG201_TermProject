public class VersionRecord {

    private final String fileName;
    private final int sizeKb;
    private final long timestampMs;
    private final int version;

    public VersionRecord(
            String fileName,
            int sizeKb,
            long timestampMs,
            int version
    ) {
        this.fileName = fileName;
        this.sizeKb = sizeKb;
        this.timestampMs = timestampMs;
        this.version = version;
    }

    public String getFileName() {
        return fileName;
    }

    public int getSizeKb() {
        return sizeKb;
    }

    public long getTimestampMs() {
        return timestampMs;
    }

    public int getVersion() {
        return version;
    }

    @Override
    public String toString() {
        return String.format(
                "v%d %s %d KB %s",
                version,
                fileName,
                sizeKb,
                clock()
        );
    }

    private String clock() {
        return String.format(
                "%02d:%02d:%02d.%03d",
                timestampMs / 3_600_000,
                (timestampMs / 60_000) % 60,
                (timestampMs / 1_000) % 60,
                timestampMs % 1_000
        );
    }
}
