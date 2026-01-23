//package mk.ukim.finki.vtor_kolokvium;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

class OperationNotAllowedException extends Exception {
    public OperationNotAllowedException(String message) {
        super(message);
    }
}
class Term {
    private int term;
    private List<Course> courses;
    private Student student;
    public Term(int term, Student student) {
        this.term = term;
        this.student = student;
        courses = new ArrayList<>();
    }
    public void addCourse(Course course) throws OperationNotAllowedException {
        if(courses.size() >=3) {
            throw new OperationNotAllowedException(String.format("Student %s already has 3 grades in term %d",student.getId(),term));
        }
        else {
            courses.add(course);
        }
    }
    public Double getAveragePerTerm() {
        return courses.stream().mapToInt(Course::getGrade).average().orElse(0.0);
    }
    @Override
    public String toString() {
        return String.format("Term %d \nCourses: %d\nAverage grade for term: %.2f\n",term,courses.size(),getAveragePerTerm());
    }
}
class Course {
    private String courseName;
    private int grade;
    public Course(String courseName, int grade) {
        this.courseName= courseName;
        this.grade = grade;
    }
    public int getGrade() { return grade; }
    public String getCourseName() { return courseName; }
}
class Student {
    private String id;
    private int yearsOfStudies;
    private Map<Integer,Term> terms;
    private List<Course> courses;
    public Student(String id, int yearsOfStudies) {
        this.id=id;
        this.yearsOfStudies=yearsOfStudies;
        terms = new HashMap<>();
        courses = new ArrayList<>();
    }
    public void addTerm(int term) throws OperationNotAllowedException {
        if(yearsOfStudies == 3 && term > 6) {
            throw new OperationNotAllowedException(String.format("Term %d is not possible for student with ID %s",term,id));
        }
        else if (yearsOfStudies==4 && term > 8) {
            throw new OperationNotAllowedException(String.format("Term %d is not possible for student with ID %s",term,id));
        }
        else {
            terms.put(term,new Term(term,this));
        }
    }
    public void addGrade(int term, String courseName, int grade) throws OperationNotAllowedException {
        if(!terms.containsKey(term)) {
            addTerm(term);
        }
        Term t = terms.get(term);
        Course course = new Course(courseName,grade);
        t.addCourse(course);
        courses.add(course);
    }
    public boolean isGraduated() {
        if(yearsOfStudies==3 && courses.size() >= 18) {
            return true;
        }
        else return yearsOfStudies==4 && courses.size() >= 24;
    }
    public String getId() { return id;}
    public long passedCourses() {
        return courses.stream().filter(c -> c.getGrade() > 5).count();
    }
    public Double getAverageGrade() {
        return courses.stream().mapToInt(Course::getGrade).average().orElse(5.0);
    }
    public int getCoursesLength() { return courses.size();}
    public int getYearsOfStudies() { return yearsOfStudies; }
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Student: ").append(getId()).append("\n");
        for(int i=1;i<=yearsOfStudies*2;i++) {
            if(terms.containsKey(i))
                sb.append(terms.get(i));
            else {
                sb.append("Term ").append(i);
                sb.append("\nCourses: 0\nAverage grade for term: 5.00\n");
            }
        }
        sb.append("Average grade: ");
        sb.append(String.format("%.2f",getAverageGrade())).append("\n");
        sb.append("Courses attended: ");
        courses.stream().sorted(Comparator.comparing(Course::getCourseName)).forEach(c -> sb.append(c.getCourseName()).append(","));
        sb.deleteCharAt(sb.length()-1);
        return sb.toString();
    }
}
class Faculty {
    Map<String,Student> studentMap;
    List<String> logs;
    Map<String,List<Integer>> courseGrades;

    public Faculty() {
        studentMap = new HashMap<>();
        logs = new ArrayList<>();
        courseGrades = new HashMap<>();
    }

    void addStudent(String id, int yearsOfStudies) {
        Student student = new Student(id,yearsOfStudies);
        studentMap.putIfAbsent(id,student);
    }

    void addGradeToStudent(String studentId, int term, String courseName, int grade) throws OperationNotAllowedException {
        Student student = studentMap.get(studentId);
        if(student == null)
            return;
        student.addGrade(term,courseName,grade);
        courseGrades.computeIfAbsent(courseName, key -> new ArrayList<Integer>()).add(grade);
        if(student.isGraduated()) {
            logs.add(String.format("Student with ID %s graduated with average grade %.2f in %d years.",studentId,student.getAverageGrade(),student.getYearsOfStudies()));
            studentMap.remove(studentId);
        }
    }

    String getFacultyLogs() {
        return String.join("\n", logs);
    }

    String getDetailedReportForStudent(String id) {
        return studentMap.get(id).toString();
    }

    void printFirstNStudents(int n) {
        studentMap.values().stream().sorted(Comparator.comparing(Student::getCoursesLength,Comparator.reverseOrder()).thenComparing(Student::getAverageGrade,Comparator.reverseOrder())
                        .thenComparing(Student::getId,Comparator.reverseOrder()))
                .limit(n).forEach( s ->
                {
                    System.out.printf("Student: %s Courses passed: %d Average grade: %.2f\n",s.getId(),s.getCoursesLength(),s.getAverageGrade());
                });
    }

    void printCourses() {
        courseGrades.entrySet()
                .stream()
                .sorted(Comparator.comparing((Map.Entry<String,List<Integer>> e) -> e.getValue().size())
                        .thenComparing(e -> e.getValue().stream().mapToInt(Integer::intValue).average().orElse(0.0))
                        .thenComparing(k -> k.getKey()))
                        .forEach( k -> {
                            System.out.printf("%s %d %.2f\n", k.getKey(),k.getValue().size(), k.getValue().stream().mapToInt(Integer::intValue).average().orElse(0.0));
                        });
    }

}

public class FacultyTest {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int testCase = sc.nextInt();

        if (testCase == 1) {
            System.out.println("TESTING addStudent AND printFirstNStudents");
            Faculty faculty = new Faculty();
            for (int i = 0; i < 10; i++) {
                faculty.addStudent("student" + i, (i % 2 == 0) ? 3 : 4);
            }
            faculty.printFirstNStudents(10);

        } else if (testCase == 2) {
            System.out.println("TESTING addGrade and exception");
            Faculty faculty = new Faculty();
            faculty.addStudent("123", 3);
            faculty.addStudent("1234", 4);
            try {
                faculty.addGradeToStudent("123", 7, "NP", 10);
            } catch (OperationNotAllowedException e) {
                System.out.println(e.getMessage());
            }
            try {
                faculty.addGradeToStudent("1234", 9, "NP", 8);
            } catch (OperationNotAllowedException e) {
                System.out.println(e.getMessage());
            }
        } else if (testCase == 3) {
            System.out.println("TESTING addGrade and exception");
            Faculty faculty = new Faculty();
            faculty.addStudent("123", 3);
            faculty.addStudent("1234", 4);
            for (int i = 0; i < 4; i++) {
                try {
                    faculty.addGradeToStudent("123", 1, "course" + i, 10);
                } catch (OperationNotAllowedException e) {
                    System.out.println(e.getMessage());
                }
            }
            for (int i = 0; i < 4; i++) {
                try {
                    faculty.addGradeToStudent("1234", 1, "course" + i, 10);
                } catch (OperationNotAllowedException e) {
                    System.out.println(e.getMessage());
                }
            }
        } else if (testCase == 4) {
            System.out.println("Testing addGrade for graduation");
            Faculty faculty = new Faculty();
            faculty.addStudent("123", 3);
            faculty.addStudent("1234", 4);
            int counter = 1;
            for (int i = 1; i <= 6; i++) {
                for (int j = 1; j <= 3; j++) {
                    try {
                        faculty.addGradeToStudent("123", i, "course" + counter, (i % 2 == 0) ? 7 : 8);
                    } catch (OperationNotAllowedException e) {
                        System.out.println(e.getMessage());
                    }
                    ++counter;
                }
            }
            counter = 1;
            for (int i = 1; i <= 8; i++) {
                for (int j = 1; j <= 3; j++) {
                    try {
                        faculty.addGradeToStudent("1234", i, "course" + counter, (j % 2 == 0) ? 7 : 10);
                    } catch (OperationNotAllowedException e) {
                        System.out.println(e.getMessage());
                    }
                    ++counter;
                }
            }
            System.out.println("LOGS");
            System.out.println(faculty.getFacultyLogs());
            System.out.println("PRINT STUDENTS (there shouldn't be anything after this line!");
            faculty.printFirstNStudents(2);
        } else if (testCase == 5 || testCase == 6 || testCase == 7) {
            System.out.println("Testing addGrade and printFirstNStudents (not graduated student)");
            Faculty faculty = new Faculty();
            for (int i = 1; i <= 10; i++) {
                faculty.addStudent("student" + i, ((i % 2) == 1 ? 3 : 4));
                int courseCounter = 1;
                for (int j = 1; j < ((i % 2 == 1) ? 6 : 8); j++) {
                    for (int k = 1; k <= ((j % 2 == 1) ? 3 : 2); k++) {
                        try {
                            faculty.addGradeToStudent("student" + i, j, ("course" + courseCounter), i % 5 + 6);
                        } catch (OperationNotAllowedException e) {
                            System.out.println(e.getMessage());
                        }
                        ++courseCounter;
                    }
                }
            }
            if (testCase == 5)
                faculty.printFirstNStudents(10);
            else if (testCase == 6)
                faculty.printFirstNStudents(3);
            else
                faculty.printFirstNStudents(20);
        } else if (testCase == 8 || testCase == 9) {
            System.out.println("TESTING DETAILED REPORT");
            Faculty faculty = new Faculty();
            faculty.addStudent("student1", ((testCase == 8) ? 3 : 4));
            int grade = 6;
            int counterCounter = 1;
            for (int i = 1; i < ((testCase == 8) ? 6 : 8); i++) {
                for (int j = 1; j < 3; j++) {
                    try {
                        faculty.addGradeToStudent("student1", i, "course" + counterCounter, grade);
                    } catch (OperationNotAllowedException e) {
                        e.printStackTrace();
                    }
                    grade++;
                    if (grade == 10)
                        grade = 5;
                    ++counterCounter;
                }
            }
            System.out.println(faculty.getDetailedReportForStudent("student1"));
        } else if (testCase==10) {
            System.out.println("TESTING PRINT COURSES");
            Faculty faculty = new Faculty();
            for (int i = 1; i <= 10; i++) {
                faculty.addStudent("student" + i, ((i % 2) == 1 ? 3 : 4));
                int courseCounter = 1;
                for (int j = 1; j < ((i % 2 == 1) ? 6 : 8); j++) {
                    for (int k = 1; k <= ((j % 2 == 1) ? 3 : 2); k++) {
                        int grade = sc.nextInt();
                        try {
                            faculty.addGradeToStudent("student" + i, j, ("course" + courseCounter), grade);
                        } catch (OperationNotAllowedException e) {
                            System.out.println(e.getMessage());
                        }
                        ++courseCounter;
                    }
                }
            }
            faculty.printCourses();
        } else if (testCase==11) {
            System.out.println("INTEGRATION TEST");
            Faculty faculty = new Faculty();
            for (int i = 1; i <= 10; i++) {
                faculty.addStudent("student" + i, ((i % 2) == 1 ? 3 : 4));
                int courseCounter = 1;
                for (int j = 1; j <= ((i % 2 == 1) ? 6 : 8); j++) {
                    for (int k = 1; k <= ((j % 2 == 1) ? 2 : 3); k++) {
                        int grade = sc.nextInt();
                        try {
                            faculty.addGradeToStudent("student" + i, j, ("course" + courseCounter), grade);
                        } catch (OperationNotAllowedException e) {
                            System.out.println(e.getMessage());
                        }
                        ++courseCounter;
                    }
                }

            }

            for (int i=11;i<15;i++) {
                faculty.addStudent("student" + i, ((i % 2) == 1 ? 3 : 4));
                int courseCounter = 1;
                for (int j = 1; j <= ((i % 2 == 1) ? 6 : 8); j++) {
                    for (int k = 1; k <= 3; k++) {
                        int grade = sc.nextInt();
                        try {
                            faculty.addGradeToStudent("student" + i, j, ("course" + courseCounter), grade);
                        } catch (OperationNotAllowedException e) {
                            System.out.println(e.getMessage());
                        }
                        ++courseCounter;
                    }
                }
            }
            System.out.println("LOGS");
            System.out.println(faculty.getFacultyLogs());
            System.out.println("DETAILED REPORT FOR STUDENT");
            System.out.println(faculty.getDetailedReportForStudent("student2"));
            try {
                System.out.println(faculty.getDetailedReportForStudent("student11"));
                System.out.println("The graduated students should be deleted!!!");
            } catch (NullPointerException e) {
                System.out.println("The graduated students are really deleted");
            }
            System.out.println("FIRST N STUDENTS");
            faculty.printFirstNStudents(10);
            System.out.println("COURSES");
            faculty.printCourses();
        }
    }
}
