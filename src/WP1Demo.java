public class WP1Demo {

    public static void main(String[] args) {
        SubmissionRegistry registry = new SubmissionRegistry();

        Submission[] submissions = {
                new Submission(
                        "S-0001", "project1.pdf",
                        1000, 80_000_001L, 1, false
                ),
                new Submission(
                        "S-0002", "project2.pdf",
                        1100, 80_000_002L, 1, false
                ),
                new Submission(
                        "S-0003", "project3.pdf",
                        1200, 80_000_003L, 1, false
                ),
                new Submission(
                        "S-0004", "project4.pdf",
                        1300, 80_000_004L, 1, false
                ),
                new Submission(
                        "S-0005", "project5.pdf",
                        1400, 80_000_005L, 1, true
                ),
                new Submission(
                        "S-0006", "project6.pdf",
                        1500, 80_000_006L, 1, false
                ),
                new Submission(
                        "S-0007", "project7.pdf",
                        1600, 80_000_007L, 1, false
                ),
                new Submission(
                        "S-0009", "collision.pdf",
                        1700, 80_000_008L, 1, false
                )
        };

        System.out.println("=== PUT AND LOOKUP TEST ===");

        for (int i = 0; i < submissions.length; i++) {
            registry.put(submissions[i]);
        }

        for (int i = 0; i < submissions.length; i++) {
            String studentId = submissions[i].getStudentId();
            System.out.println(registry.lookup(studentId));
        }

        System.out.println();
        System.out.println("Registry size: " + registry.size());

        System.out.println();
        System.out.println("=== VERSION UPDATE TEST ===");

        int version2 = registry.updateVersion(
                "S-0001",
                "project_final.pdf",
                1250,
                85_000_000L
        );

        int version3 = registry.updateVersion(
                "S-0001",
                "project_really_final.pdf",
                1300,
                86_000_000L
        );

        System.out.println("Version after first update: " + version2);
        System.out.println("Version after second update: " + version3);
        System.out.println("Active record: "
                + registry.lookup("S-0001"));

        System.out.println();
        System.out.println("=== UNKNOWN STUDENT TEST ===");
        System.out.println(
                "Unknown lookup: " + registry.lookup("S-9999")
        );

        System.out.println();
        System.out.println("=== 100,000 LOOKUP TIMING ===");

        ScenarioGenerator generator =
                new ScenarioGenerator(20260725L);

        long start = System.nanoTime();

        for (int i = 0; i < 100_000; i++) {
            int studentNumber = i % 8;
            String studentId =
                    submissions[studentNumber].getStudentId();

            registry.lookup(studentId);
        }

        long end = System.nanoTime();

        System.out.println(
                "100,000 lookups: " + (end - start) + " ns"
        );
    }
}
