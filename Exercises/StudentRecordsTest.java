import java.io.*;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.lang.Integer.parseInt;

/**
 * January 2016 Exam problem 1
 */
public class StudentRecordsTest {
    public static void main(String[] args) throws IOException {
        System.out.println("=== READING RECORDS ===");
        StudentRecords studentRecords = new StudentRecords();
        int total = studentRecords.readRecords(System.in);
        System.out.printf("Total records: %d\n", total);
        System.out.println("=== WRITING TABLE ===");
        studentRecords.writeTable(System.out);
        System.out.println("=== WRITING DISTRIBUTION ===");
        studentRecords.writeDistribution(System.out);
    }
}

// your code here
class StudentRecords {
    List<Record> records;
    public StudentRecords() {
        records = new ArrayList<>();
    }
    public int readRecords(InputStream is) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        br.lines().forEach(l -> {
            String[] parts = l.split(" ");
            String code = parts[0];
            String nasoka = parts[1];
            List<Integer> grades = new ArrayList<>();
            Arrays.stream(parts).skip(2).forEach(p -> {
                grades.add(parseInt(p));
            });
            records.add(new Record(code,nasoka,grades));
        });
        br.close();
        return records.size();
    }
    public void writeTable(OutputStream outputStream) {
        PrintWriter pw = new PrintWriter(outputStream);
        records.stream().collect(Collectors.groupingBy(
                Record::getNasoka, TreeMap::new, Collectors.toCollection(ArrayList::new)
        )).forEach((r,b) -> {
            pw.println(r);
            b.stream().sorted(Comparator.comparing(Record::getAverageGrade,Comparator.reverseOrder())
                    .thenComparing(Record::getCode)).forEach(pw::println);
        });
        pw.flush();
    }
//    public void writeDistribution(OutputStream outputStream) {
//        PrintWriter pw = new PrintWriter(outputStream);
//        records.stream().collect(Collectors.groupingBy(
//                Record::getNasoka,
//                Collectors.mapping(
//                        Record::getGrades,
//                        Collectors.reducing(
//                                new ArrayList<Integer>(),
//                                (a,b) -> {
//                                    a.addAll(b);
//                                    return a;
//                                }
//                        )
//                )
//        )).entrySet().stream().sorted(
//                Comparator.comparing(
//                        k -> k.getValue().stream()
//                                .filter(i -> i == 10).count(), Comparator.reverseOrder()
//                )
//        ).forEach(r -> {
//            pw.println(r.getKey());
//            pw.println(String.format("%2d | %s(%d)",6, "*".repeat((int) r.getValue().stream().filter(i -> i==6).count()),r.getValue().stream().filter(i -> i==6).count()));
//            pw.println(String.format("%2d | %s(%d)",7, "*".repeat((int) r.getValue().stream().filter(i -> i==7).count()),r.getValue().stream().filter(i -> i==7).count()));
//            pw.println(String.format("%2d | %s(%d)",8, "*".repeat((int) r.getValue().stream().filter(i -> i==8).count()),r.getValue().stream().filter(i -> i==8).count()));
//            pw.println(String.format("%2d | %s(%d)",9, "*".repeat((int) r.getValue().stream().filter(i -> i==9).count()),r.getValue().stream().filter(i -> i==9).count()));
//            pw.println(String.format("%2d | %s(%d)",10, "*".repeat((int) r.getValue().stream().filter(i -> i==10).count()),r.getValue().stream().filter(i -> i==10).count()));
//        });
//    }
public void writeDistribution(OutputStream outputStream) {
    PrintWriter pw = new PrintWriter(outputStream);

    records.stream()
            .collect(Collectors.groupingBy(
                    Record::getNasoka,
//                    Collectors.mapping(
//                            Record::getGrades,
//                            Collectors.collectingAndThen(
//                                    Collectors.toList(),
//                                    listOfLists -> listOfLists.stream()
//                                            .flatMap(List::stream)
//                                            .collect(Collectors.toList())
//                            )
//                    )
                    Collectors.reducing(
                            new ArrayList<Integer>(),
                            Record::getGrades,
                            (a,b) -> {
                                List<Integer> newList = new ArrayList<>(a);
                                newList.addAll(b);
                                return newList;
                            }
                    )
            )).entrySet().stream()
            .sorted(Comparator.comparing(
                    e -> e.getValue().stream().filter(i -> i == 10).count(),
                    Comparator.reverseOrder()
            ))
//            .forEach(e -> {
//                String nasoka = e.getKey();
//                List<Integer> grades = e.getValue();
//
//                pw.println(nasoka);
//                for (int g = 6; g <= 10; g++) {
//                    final int grade = g;
//                    long count = grades.stream().filter(i -> i == grade).count();
//                    pw.println(String.format("%2d | %s(%d)", g, "*".repeat((int) Math.ceil(count/10.0)), count));
//                }
//            });
            .forEach(r -> {
                pw.println(r.getKey());
                pw.println(String.format("%2d | %s(%d)",6, "*".repeat((int) Math.ceil(r.getValue().stream().filter(i -> i==6).count()/10.0)),r.getValue().stream().filter(i -> i==6).count()));
                pw.println(String.format("%2d | %s(%d)",7, "*".repeat((int) Math.ceil(r.getValue().stream().filter(i -> i==7).count()/10.0)),r.getValue().stream().filter(i -> i==7).count()));
                pw.println(String.format("%2d | %s(%d)",8, "*".repeat((int) Math.ceil(r.getValue().stream().filter(i -> i==8).count()/10.0)),r.getValue().stream().filter(i -> i==8).count()));
                pw.println(String.format("%2d | %s(%d)",9, "*".repeat((int) Math.ceil(r.getValue().stream().filter(i -> i==9).count()/10.0)),r.getValue().stream().filter(i -> i==9).count()));
                pw.println(String.format("%2d | %s(%d)",10, "*".repeat((int) Math.ceil(r.getValue().stream().filter(i -> i==10).count()/10.0)),r.getValue().stream().filter(i -> i==10).count()));
            });

    pw.flush();
}

}

class Record {
    private String code;
    private String nasoka;
    private List<Integer> grades;
    public Record(String code, String nasoka, List<Integer> grades) {
        this.code = code;
        this.nasoka = nasoka;
        this.grades = grades;
    }
    public String getCode() { return this.code; }
    public String getNasoka() { return this.nasoka; }
    public List<Integer> getGrades() { return this.grades; }
    public Double getAverageGrade() {
        return grades.stream().mapToInt(Integer::intValue).average().orElse(0.0);
    }
    public long countA() {
        return grades.stream().filter(i -> i==10).count();
    }
    @Override
    public String toString() {
        return String.format("%s %.2f", getCode(),getAverageGrade());
    }
}