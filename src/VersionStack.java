import java.util.NoSuchElementException;

public class VersionStack {
    private static class Node {
        VersionRecord value;
        Node next;

        Node(VersionRecord value) {
            this.value = value;
        }
    }

    private Node top;
    private int count;

    public void push(VersionRecord version) {
        Node newNode = new Node(version);
        newNode.next = top;
        top = newNode;
        count++;
    }

    public VersionRecord pop() {
        if (top == null) {
            throw new NoSuchElementException("Version stack is empty.");
        }

        VersionRecord result = top.value;
        top = top.next;
        count--;
        return result;
    }

    public boolean isEmpty() {
        return top == null;
    }

    public int size() {
        return count;
    }

    public void printState() {
        System.out.print("Stack top -> ");
        if (top == null) {
            System.out.println("[empty]");
            return;
        }

        Node current = top;
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
