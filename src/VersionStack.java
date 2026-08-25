import java.util.NoSuchElementException;

public class VersionStack {

    private static class Node {
        VersionRecord value;
        Node next;

        Node(VersionRecord value) {
            this.value = value;
            this.next = null;
        }
    }

    private Node top;
    private int count;

    public VersionStack() {
        top = null;
        count = 0;
    }

    public void push(VersionRecord version) {
        Node newNode = new Node(version);

        newNode.next = top;
        top = newNode;
        count++;
    }

    public VersionRecord pop() {
        if (isEmpty()) {
            throw new NoSuchElementException(
                    "Version stack is empty."
            );
        }

        VersionRecord removed = top.value;

        top = top.next;
        count--;

        return removed;
    }

    public boolean isEmpty() {
        return top == null;
    }

    public int size() {
        return count;
    }

    public void printState() {
        System.out.print("Stack top -> ");

        Node current = top;

        if (current == null) {
            System.out.println("[empty]");
            return;
        }

        while (current != null) {
            System.out.print(current.value);

            if (current.next != null) {
                System.out.print(" | ");
            }

            current = current.next;
        }

        System.out.println();
    }
}
