import java.io.*;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public class TermFrequencyTest {
    public static void main(String[] args) throws FileNotFoundException {
        String[] stop = new String[] { "во", "и", "се", "за", "ќе", "да", "од",
                "ги", "е", "со", "не", "тоа", "кои", "до", "го", "или", "дека",
                "што", "на", "а", "но", "кој", "ја" };
        TermFrequency tf = new TermFrequency(System.in,
                stop);
        System.out.println(tf.countTotal());
        System.out.println(tf.countDistinct());
        System.out.println(tf.mostOften(10));
    }
}
// vasiot kod ovde

class TermFrequency {

    private List<String> words;
    private Set<String> stopWords;

    public TermFrequency(InputStream inputStream, String[] stopWords) {
        words = new ArrayList<>();
        this.stopWords = Arrays.stream(stopWords)
                .map(String::toLowerCase)
                .collect(Collectors.toSet());

        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));

        br.lines().forEach(line -> {
            line = line.toLowerCase()
                    .replaceAll("[.,]", " ")
                    .replaceAll("[-!?<>@#$%*&=+/]", "");

            for (String word : line.split("\\s+")) {
                if (!word.isEmpty() && !this.stopWords.contains(word)) {
                    words.add(word);
                }
            }
        });
    }

    public int countTotal() {
        return words.size();
    }

    public int countDistinct() {
        return new HashSet<>(words).size();
    }

    public List<String> mostOften(int k) {
//        Map<String, Integer> freq = new HashMap<>();
//
//        for (String w : words) {
//            freq.put(w, freq.getOrDefault(w, 0) + 1);
//        }

//        return freq.entrySet()
//                .stream()
//                .sorted(
//                        Map.Entry.<String,Integer>comparingByValue(Comparator.reverseOrder())
//                                .thenComparing(Map.Entry::getKey)
//                )
//                .limit(k)
//                .map(Map.Entry::getKey)
//                .collect(Collectors.toList());
        return words.stream().collect(Collectors.groupingBy(
                        word -> word,
                        Collectors.counting()
                )).entrySet().stream().sorted(
                        Map.Entry.<String,Long>comparingByValue(Comparator.reverseOrder())
                                .thenComparing(Map.Entry::getKey))
                .limit(k).map(Map.Entry::getKey).collect(Collectors.toList());
    }
}