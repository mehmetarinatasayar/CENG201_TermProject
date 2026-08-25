import java.util.NoSuchElementException;

public class WP4Demo {

    public static void main(String[] args) {
        SubmissionRegistry registry =
                new SubmissionRegistry();

        RollbackService rollbackService =
                new RollbackService(registry);

        Submission firstUpload =
                new Submission(
                        "S-0042",
                        "hw3.pdf",
                        900,
                        79_800_000L,
                        1,
                        false
                );

        registry.put(firstUpload);

        System.out.println(
                "=== WP4 VERSION ROLLBACK DEMO ==="
        );

        printState(
                "First upload",
                registry,
                rollbackService
        );

        rollbackService.reupload(
                "S-0042",
                "hw3_final.pdf",
                950,
                85_200_000L
        );

        printState(
                "Second upload",
                registry,
                rollbackService
        );

        rollbackService.reupload(
                "S-0042",
                "chemistry_lab.pdf",
                1100,
                86_280_000L
        );

        printState(
                "Wrong third upload",
                registry,
                rollbackService
        );

        System.out.println();
        System.out.println("First rollback:");
        rollbackService.rollback("S-0042");

        printState(
                "After first rollback",
                registry,
                rollbackService
        );

        System.out.println();
        System.out.println("Second rollback:");
        rollbackService.rollback("S-0042");

        printState(
                "After second rollback",
                registry,
                rollbackService
        );

        System.out.println();
        System.out.println("Third rollback:");
        rollbackService.rollback("S-0042");

        printState(
                "After third rollback",
                registry,
                rollbackService
        );

        testEmptyStackException();
    }

    private static void printState(
            String label,
            SubmissionRegistry registry,
            RollbackService rollbackService
    ) {
        System.out.println();
        System.out.println("--- " + label + " ---");

        System.out.println(
                "Active: "
                        + registry.lookup("S-0042")
        );

        rollbackService.printHistory("S-0042");
    }

    private static void testEmptyStackException() {
        System.out.println();
        System.out.println(
                "=== EMPTY STACK POP TEST ==="
        );

        VersionStack emptyStack =
                new VersionStack();

        try {
            emptyStack.pop();

            System.out.println(
                    "ERROR: exception was not thrown."
            );
        } catch (NoSuchElementException exception) {
            System.out.println(
                    "Expected exception: "
                            + exception.getMessage()
            );
        }
    }
}
