enum Priority {

    LOW, MEDIUM, HIGH, CRITICAL
}

public class Access {
    private final int id;
    private String title;
    private Priority priority;
    public static int taskCount = 0;

    public Access(String title, Priority priority) {
        taskCount++;
        this.id = taskCount;
        this.title = title;
        this.priority = priority;
    }

    public static void main(String[] args) {
        Access access1 = new Access("Task 1", Priority.HIGH);
        Access access2 = new Access("Task 2", Priority.CRITICAL);
        System.out.println("Task Count: " + Access.taskCount);
        System.out.println(access1.id + " " + access1.title + " " + access1.priority);
        System.out.println(access2.id + " " + access2.title + " " + access2.priority);

    }

}