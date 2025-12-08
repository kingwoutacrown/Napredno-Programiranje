import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

public class Anagrams {

    public static void main(String[] args) {
        findAll(System.in);
    }

    public static void findAll(InputStream inputStream) {
        // Vasiod kod ovde
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        Map<String, TreeSet<String>> map = new HashMap<>();
        br.lines().forEach(line -> {
            char[] chars = line.toCharArray();
            Arrays.sort(chars);
            String sorted = new String(chars);

            map.computeIfAbsent(sorted, k -> new TreeSet<>()).add(line);
        });
        map.values().stream().sorted(Comparator.comparing(TreeSet::first)).forEach(set-> {
            System.out.println(String.join(" ", set));
        });
    }
}
