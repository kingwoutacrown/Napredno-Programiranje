import java.io.*;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.Integer.parseInt;

public class TasksManagerTest {

    public static void main(String[] args) throws DeadlineNotValidException {

        TaskManager manager = new TaskManager();

        System.out.println("Tasks reading");
        manager.readTasks(System.in);
        System.out.println("By categories with priority");
        manager.printTasks(System.out, true, true);
        System.out.println("-------------------------");
        System.out.println("By categories without priority");
        manager.printTasks(System.out, false, true);
        System.out.println("-------------------------");
        System.out.println("All tasks without priority");
        manager.printTasks(System.out, false, false);
        System.out.println("-------------------------");
        System.out.println("All tasks with priority");
        manager.printTasks(System.out, true, false);
        System.out.println("-------------------------");

    }
}

class TaskManager {
    List<Task> taskList;
    public TaskManager() {
        taskList = new ArrayList<>();
    }
    public void readTasks(InputStream inputStream) throws DeadlineNotValidException {
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        try {
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split(",");
                Task task = new BaseTask(parts[0], parts[1], parts[2]);

                if (parts.length == 3) {
                    taskList.add(task);
                } else if (parts.length == 4) {
                    if (parts[3].contains(":")) {
                        LocalDateTime deadline = LocalDateTime.parse(parts[3]);
                        if (deadline.isAfter(LocalDateTime.parse("2020-06-02T00:00:00"))) {
                            throw new DeadlineNotValidException(parts[3]);
                        } else {
                            taskList.add(new DateTask(task, deadline));
                        }
                    } else {
                        taskList.add(new PriorityTask(task, parseInt(parts[3])));
                    }
                } else if (parts.length == 5) {
                    LocalDateTime deadline = LocalDateTime.parse(parts[3]);
                    if (deadline.isAfter(LocalDateTime.parse("2020-06-02T00:00:00"))) {
                        throw new DeadlineNotValidException(parts[3]);
                    }
                    int priority = parseInt(parts[4]);
                    taskList.add(new PriorityTask(new DateTask(task, deadline), priority));
                } else {
                    // optional: handle invalid input length
                    System.err.println("Invalid task format: " + line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void printTasks(OutputStream os, boolean includePriority, boolean includeCategory) {
        PrintWriter pw = new PrintWriter(os);

        if (includeCategory) {
            taskList.stream()
                    .collect(Collectors.groupingBy(
                            Task::getCategory,
                            HashMap::new,
                            Collectors.toCollection(ArrayList::new)
                    ))
                    .forEach((cat, tasks) -> {
                        pw.println(cat.toUpperCase());
                        tasks.forEach(pw::println);
                    });
        }

        Comparator<Task> dateComparator = Comparator.comparingLong(
                t -> {
                    LocalDateTime dt = t.getDateTime();
                    return dt == null ? Long.MAX_VALUE : Math.abs(ChronoUnit.DAYS.between(LocalDateTime.now(), dt));
                }
        );

        if (includePriority) {
            taskList.stream()
                    .sorted(Comparator.comparingInt(Task::getPriority)
                            .thenComparing(dateComparator))
                    .forEach(pw::println);
        } else if (!includePriority && !includeCategory) {
            taskList.stream()
                    .sorted(dateComparator)
                    .forEach(pw::println);
        }

        pw.flush();
    }

}

interface Task {
    public String getName();
    public String getCategory();
    public String getDescription();
    public int getPriority();
    public LocalDateTime getDateTime();
}

class BaseTask implements Task{
    private String category;
    private String name;
    private String description;
    public BaseTask(String category, String name, String description) {
        this.category = category;
        this.name = name;
        this.description = description;
    }
    public String getCategory() { return this.category; }
    public String getName() { return this.name; }
    public String getDescription() { return this.description; }

    @Override
    public int getPriority() {
        return 1_000_000;
    }

    @Override
    public LocalDateTime getDateTime() {
        return null;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Task{name='").append(name).append("', description='").append(description).append("'}");
        return sb.toString();
    }
}

class DateTask implements Task{
    private Task task;
    private LocalDateTime dateTime;
    public DateTask(Task task, LocalDateTime dateTime) {
        this.task = task;
        this.dateTime = dateTime;
    }
    public Task getTask() { return this.task; }
    @Override
    public LocalDateTime getDateTime() { return this.dateTime; }
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(task.toString());
        sb.deleteCharAt(sb.length()-1);
        sb.append(", deadline=").append(dateTime).append("}");
        return sb .toString();
    }

    @Override
    public String getName() {
        return task.getName();
    }

    @Override
    public String getCategory() {
        return task.getCategory();
    }

    @Override
    public String getDescription() {
        return task.getDescription();
    }

    @Override
    public int getPriority() {
        return task.getPriority();
    }
}

class PriorityTask implements Task{
    private Task task;
    private int priority;
    public PriorityTask(Task task, int priority) {
        this.task = task;
        this.priority = priority;
    }
    public Task getTask() { return this.task; }

    @Override
    public int getPriority() { return this.priority; }

    @Override
    public LocalDateTime getDateTime() {
        return task.getDateTime();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(task.toString());
        sb.deleteCharAt(sb.length()-1);
        sb.append(", priority=").append(priority).append("}");
        return sb.toString();
    }

    @Override
    public String getName() {
        return task.getName();
    }

    @Override
    public String getCategory() {
        return task.getCategory();
    }

    @Override
    public String getDescription() {
        return task.getDescription();
    }
}

class DeadlineNotValidException extends Exception {
    public DeadlineNotValidException(String message) {
        super(message);
    }
}