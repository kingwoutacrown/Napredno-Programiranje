import java.lang.reflect.Array;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Word vectors test
 */
public class WordVectorsTest {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        scanner.nextLine();
        String[] words = new String[n];
        List<List<Integer>> vectors = new ArrayList<>(n);
        for (int i = 0; i < n; ++i) {
            String line = scanner.nextLine();
            String[] parts = line.split("\\s+");
            words[i] = parts[0];
            List<Integer> vector = Arrays.stream(parts[1].split(":"))
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());
            vectors.add(vector);
        }
        n = scanner.nextInt();
        scanner.nextLine();
        List<String> wordsList = new ArrayList<>(n);
        for (int i = 0; i < n; ++i) {
            wordsList.add(scanner.nextLine());
        }
        WordVectors wordVectors = new WordVectors(words, vectors);
        wordVectors.readWords(wordsList);
        n = scanner.nextInt();
        List<Integer> result = wordVectors.slidingWindow(n);
        System.out.println(result.stream()
                .map(Object::toString)
                .collect(Collectors.joining(",")));
        scanner.close();
    }
}

class WordVectors {
    Map<String, Vector> vectorMap;
    List<Vector> vectorsRead;
    Vector neutral;
    public WordVectors(String[] words, List<List<Integer>> vectors) {
        vectorMap = new HashMap<>();
        vectorsRead = new ArrayList<>();
        neutral = new Vector(Arrays.asList(5,5,5,5,5));
        for(int i=0;i<words.length;i++) {
            Vector vector = new Vector(vectors.get(i));
            vectorMap.put(words[i], vector);
        }
    }
    public void readWords(List<String> words) {
//        for(String w: words) {
//            if(vectorMap.containsKey(w)) {
//                vectorsRead.add(vectorMap.get(w));
//            }
//            else {
//                vectorsRead.add(new Vector(neutral));
//            }
//        }
        words.forEach(w -> {
            vectorsRead.add(vectorMap.getOrDefault(w,neutral));
        });
    }

    public List<Integer> slidingWindow(int n) {
        List<Integer> maxIntegers = new ArrayList<>();
        for(int i=0;i<vectorsRead.size()-n+1;i++) {
            Vector newVector = vectorsRead.stream().skip(i).limit(n).reduce(
                    Vector::add
            ).orElse(null);
            if(newVector == null) continue;
            maxIntegers.add(newVector.getMax());
        }
        return maxIntegers;
    }
}

class Vector {
    private List<Integer> vector;
    public Vector(List<Integer> vector) {
        this.vector = vector;
    }
    public List<Integer> getVector() { return this.vector; }
    public Integer getMax() {
        return vector.stream().max(Comparator.comparing(Integer::intValue)).orElse(0);
    }
    public Vector add(Vector other) {
        List<Integer> newVector = new ArrayList<>();
        for(int i=0;i<vector.size();i++) {
            newVector.add(vector.get(i)+other.getVector().get(i));
        }
        return new Vector(newVector);
    }
}