import java.util.*;
import java.util.stream.Collectors;

public class NamesTest {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        scanner.nextLine();
        Names names = new Names();
        for (int i = 0; i < n; ++i) {
            String name = scanner.nextLine();
            names.addName(name);
        }
        n = scanner.nextInt();
        System.out.printf("===== PRINT NAMES APPEARING AT LEAST %d TIMES =====\n", n);
        names.printN(n);
        System.out.println("===== FIND NAME =====");
        int len = scanner.nextInt();
        int index = scanner.nextInt();
        System.out.println(names.findName(len, index));
        scanner.close();

    }
}

// vashiot kod ovde
class Names {
    List<String> names;
    public Names() {
        names = new ArrayList<>();
    }
    public void addName(String name) {
        names.add(name);
    }
    public void printN(int n) {
        names.stream().collect(
                Collectors.groupingBy(
                        String::valueOf,
                        TreeMap::new,
                        Collectors.toCollection(ArrayList::new)
                )
        ).entrySet().stream().filter(e -> (long) e.getValue().size() >=n)
                .forEach( e-> {
                    Set<Character> set = new HashSet<>(e.getKey().toLowerCase().chars().mapToObj(c -> (char)c).collect(Collectors.toList()));
                    System.out.printf("%s (%d) %d\n", e.getKey(), e.getValue().size(), set.size());
                }
        );
    }
    public String findName(int len, int x) {
        Set<String> namesSet = new TreeSet<>(names);
        namesSet = namesSet.stream().filter(n -> n.length()<len).collect(Collectors.toCollection(TreeSet::new));
        return namesSet.stream().skip(x%namesSet.size()).findFirst().get();
    }
}

//class Name {
//    private String name;
//    public Name(String name) {
//        this.name = name;
//    }
//    public String getName() { return this.name; }
//}