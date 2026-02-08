import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Partial exam II 2016/2017
 */
public class FileSystemTest {
    public static void main(String[] args) {
        FileSystem fileSystem = new FileSystem();
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        scanner.nextLine();
        for (int i = 0; i < n; i++) {
            String line = scanner.nextLine();
            String[] parts = line.split(":");
            fileSystem.addFile(parts[0].charAt(0), parts[1],
                    Integer.parseInt(parts[2]),
                    LocalDateTime.of(2016, 12, 29, 0, 0, 0).minusDays(Integer.parseInt(parts[3]))
            );
        }
        int action = scanner.nextInt();
        if (action == 0) {
            scanner.nextLine();
            int size = scanner.nextInt();
            System.out.println("== Find all hidden files with size less then " + size);
            List<File> files = fileSystem.findAllHiddenFilesWithSizeLessThen(size);
            files.forEach(System.out::println);
        } else if (action == 1) {
            scanner.nextLine();
            String[] parts = scanner.nextLine().split(":");
            System.out.println("== Total size of files from folders: " + Arrays.toString(parts));
            int totalSize = fileSystem.totalSizeOfFilesFromFolders(Arrays.stream(parts)
                    .map(s -> s.charAt(0))
                    .collect(Collectors.toList()));
            System.out.println(totalSize);
        } else if (action == 2) {
            System.out.println("== Files by year");
            Map<Integer, Set<File>> byYear = fileSystem.byYear();
            byYear.keySet().stream().sorted()
                    .forEach(key -> {
                        System.out.printf("Year: %d\n", key);
                        Set<File> files = byYear.get(key);
                        files.stream()
                                .sorted()
                                .forEach(System.out::println);
                    });
        } else if (action == 3) {
            System.out.println("== Size by month and day");
            Map<String, Long> byMonthAndDay = fileSystem.sizeByMonthAndDay();
            byMonthAndDay.keySet().stream().sorted()
                    .forEach(key -> System.out.printf("%s -> %d\n", key, byMonthAndDay.get(key)));
        }
        scanner.close();
    }
}

// Your code here

class File implements Comparable<File> {
    private Character folder;
    private String name;
    private int size;
    LocalDateTime createdAt;
    public File(Character folder, String name, int size, LocalDateTime createdAt) {
        this.folder = folder;
        this.name = name;
        this.size = size;
        this.createdAt = createdAt;
    }
    public Character getFolder() { return this.folder; }
    public String getName() { return this.name; }
    public int getSize() { return this.size; }
    public LocalDateTime getCreatedAt() { return this.createdAt; }
    public int getCreationYear() {
        return createdAt.getYear();
    }
    public String getMonthAndDay() {
        return String.valueOf(createdAt.getMonth())+"-" + String.valueOf(createdAt.getDayOfMonth());
    }
    @Override
    public String toString() {
        return String.format("%-10s %5dB %s",name,size,createdAt);
    }


    @Override
    public int compareTo(File o) {
        if(!this.getCreatedAt().isEqual(o.getCreatedAt())) {
            return this.getCreatedAt().compareTo(o.getCreatedAt());
        }
        else {
            if(this.getName().compareTo(o.getName()) != 0) {
                return this.getName().compareTo(o.getName());
            }
            else {
                return Integer.compare(this.getSize(),o.getSize());
            }
        }
    }
}

class FileSystem {
    Map<String, File> fileMap;
    public FileSystem() {
        fileMap = new LinkedHashMap<>();
    }

    public void addFile(char folder, String name, int size, LocalDateTime createdAt) {
        fileMap.putIfAbsent(name, new File(folder,name,size,createdAt));
    }

    public List<File> findAllHiddenFilesWithSizeLessThen(int size) {
        return fileMap.values().stream().filter(
                f -> f.getName().startsWith(".") && f.getSize()<size
        ).sorted(
                Comparator.comparing(
                        File::getFolder
                ).thenComparing(File::getCreatedAt)
                )
        .collect(Collectors.toList());
    }

    public int totalSizeOfFilesFromFolders(List<Character> folders) {
        return fileMap.values().stream().filter(
                f -> folders.contains(f.getFolder())
        ).mapToInt(File::getSize).sum();
    }

    public Map<Integer, Set<File>> byYear() {
        return fileMap.values().stream().
                collect(Collectors.groupingBy(
                        File::getCreationYear,
                        Collectors.toCollection(TreeSet::new)
                ));
    }

    public Map<String, Long> sizeByMonthAndDay() {
        return fileMap.values().stream().
                collect(Collectors.groupingBy(
                        File::getMonthAndDay,
                        TreeMap::new,
                        Collectors.summingLong(File::getSize)
                ));
    }

}