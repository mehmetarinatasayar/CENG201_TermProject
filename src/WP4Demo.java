import java.util.NoSuchElementException;

public class WP4Demo {
    public static void main(String[] args) {
        SubmissionRegistry registry = new SubmissionRegistry();
        RollbackService service = new RollbackService(registry);

        registry.put(new Submission(
                "S-0042", "hw3.pdf", 900, 79_800_000L, 1, false));

        System.out.println("=== WP4 VERSION ROLLBACK DEMO ===");
        printState("First upload", registry, service);

        service.reupload("S-0042", "hw3_final.pdf", 950, 85_200_000L);
        printState("Second upload", registry, service);

        service.reupload("S-0042", "chemistry_lab.pdf", 1100, 86_280_000L);
        printState("Wrong third upload", registry, service);

        System.out.println("\nFirst rollback:");
        service.rollback("S-0042");
        printState("After first rollback", registry, service);

        System.out.println("\nSecond rollback:");
        service.rollback("S-0042");
        printState("After second rollback", registry, service);

        System.out.println("\nThird rollback:");
        service.rollback("S-0042");
        printState("After third rollback", registry, service);

        System.out.println("\n=== EMPTY STACK POP TEST ===");
        try {
            new VersionStack().pop();
            System.out.println("ERROR: exception was not thrown.");
        } catch (NoSuchElementException e) {
            System.out.println("Expected exception: " + e.getMessage());
        }
    }

    private static void printState(String title, SubmissionRegistry registry,
                                   RollbackService service) {
        System.out.println("\n--- " + title + " ---");
        System.out.println("Active: " + registry.lookup("S-0042"));
        service.printHistory("S-0042");
    }
}
