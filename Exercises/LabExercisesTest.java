import java.util.stream.Collectors;
import java.util.*;

import static java.lang.Integer.parseInt;

public class LabExercisesTest {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        LabExercises labExercises = new LabExercises();
        while (sc.hasNextLine()) {
            String input = sc.nextLine();
            String[] parts = input.split("\\s+");
            String index = parts[0];
            List<Integer> points = Arrays.stream(parts).skip(1)
                    .mapToInt(Integer::parseInt)
                    .boxed()
                    .collect(Collectors.toList());

            labExercises.addStudent(new Student(index, points));
        }

        System.out.println("===printByAveragePoints (ascending)===");
        labExercises.printByAveragePoints(true, 100);
        System.out.println("===printByAveragePoints (descending)===");
        labExercises.printByAveragePoints(false, 100);
        System.out.println("===failed students===");
        labExercises.failedStudents().forEach(System.out::println);
        System.out.println("===statistics by year");
        labExercises.getStatisticsByYear().entrySet().stream()
                .map(entry -> String.format("%d : %.2f", entry.getKey(), entry.getValue()))
                .forEach(System.out::println);

    }
}

class Student {
    private String index;
    private List<Integer> points;
    public Student(String index, List<Integer> points) {
        this.index = index;
        this.points = points;
    }
    public String isPassed() {
        return points.size() >= 8 ? "YES" : "NO";
    }
    public int getGodina() {
        return parseInt(index.substring(2,3));
    }
    public String getIndex() { return this.index; }
    public List<Integer> getPoints() { return this.points; }
    public Double getAverage() {
        return points.stream().mapToInt(Integer::intValue).sum()/10.0;
        //return points.stream().mapToInt(Integer::intValue).average().orElse(0.0);
    }
    @Override
    public String toString() {
        return String.format("%s %s %.2f", getIndex(),isPassed(),getAverage());
    }
}

class LabExercises {
    Map<String, Student> studentMap;
    public LabExercises() {
        studentMap = new HashMap<>();
    }
    public void addStudent(Student student) {
        //if(!studentMap.containsKey(student.getIndex())) {
            studentMap.put(student.getIndex(),student);
        //}
    }
    public void printByAveragePoints(boolean ascending, int n) {
        if(ascending) {
            studentMap.values().stream().sorted(
                    Comparator.comparing(
                            Student::getAverage
                    ).thenComparing(
                            Student::getIndex
                    )
            ).limit(n).forEach(
                    System.out::println
            );
        }
        else {
            studentMap.values().stream().sorted(
                    Comparator.comparing(
                        Student::getAverage, Comparator.reverseOrder()
                            ).thenComparing(
                                    Student::getIndex, Comparator.reverseOrder()
                    )
            ).limit(n).forEach(System.out::println);
        }
    }
    public List<Student> failedStudents() {
        return studentMap.values().stream().filter(s -> s.isPassed().equals("NO"))
                .sorted(
                        Comparator.comparing(
                                Student::getIndex
                        ).thenComparing(
                            Student::getAverage
                                )
                ).collect(Collectors.toList());
    }
    public Map<Integer,Double> getStatisticsByYear() {
        return studentMap.values().stream().filter(s -> s.isPassed().equals("YES"))
                .collect(
                        Collectors.groupingBy(
                                Student::getGodina,
                                TreeMap::new,
                                Collectors.averagingDouble(Student::getAverage)
                        )
                );
    }
}