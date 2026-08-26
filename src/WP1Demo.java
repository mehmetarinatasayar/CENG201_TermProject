public class WP1Demo {
    public static void main(String[] args) {
        SubmissionRegistry registry = new SubmissionRegistry();
        Submission[] data = new Submission[8];

        for (int i = 0; i < data.length; i++) {
            int number = i + 1;
            if (i == 7) {
                number = 9; // S-0009 collides with another ID in the small table.
            }

            data[i] = new Submission(
                    String.format("S-%04d", number),
                    i == 7 ? "collision.pdf" : "project" + number + ".pdf",
                    1000 + i * 100,
                    80_000_001L + i,
                    1,
                    i == 4);
        }

        System.out.println("=== PUT AND LOOKUP TEST ===");
        for (int i = 0; i < data.length; i++) {
            registry.put(data[i]);
        }
        for (int i = 0; i < data.length; i++) {
            System.out.println(registry.lookup(data[i].getStudentId()));
        }

        System.out.println("\nRegistry size: " + registry.size());
        System.out.println("\n=== VERSION UPDATE TEST ===");

        int version2 = registry.updateVersion(
                "S-0001", "project_final.pdf", 1250, 85_000_000L);
        int version3 = registry.updateVersion(
                "S-0001", "project_really_final.pdf", 1300, 86_000_000L);

        System.out.println("Version after first update: " + version2);
        System.out.println("Version after second update: " + version3);
        System.out.println("Active record: " + registry.lookup("S-0001"));

        System.out.println("\n=== UNKNOWN STUDENT TEST ===");
        System.out.println("Unknown lookup: " + registry.lookup("S-9999"));

        System.out.println("\n=== 100,000 LOOKUP TIMING ===");
        long start = System.nanoTime();
        for (int i = 0; i < 100_000; i++) {
            registry.lookup(data[i % data.length].getStudentId());
        }
        long time = System.nanoTime() - start;
        System.out.println("100,000 lookups: " + time + " ns");
    }
}
