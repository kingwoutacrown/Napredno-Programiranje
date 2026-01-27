import java.io.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.lang.Integer.parseInt;

public class F1Test {

    public static void main(String[] args) {
        F1Race f1Race = new F1Race();
        f1Race.readResults(System.in);
        f1Race.printSorted(System.out);
    }

}

class F1Race {
    // vashiot kod ovde
    Map<String,Driver> driverMap;
    F1Race() {
        driverMap = new HashMap<>();
    }
    public void readResults(InputStream inputStream) {
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        br.lines().forEach(l -> {
            String[] parts = l.split(" ");
            String name = parts[0];
            List<String> laps = new ArrayList<>();
            Arrays.stream(parts).skip(1).forEach(
                    laps::add
            );
            driverMap.put(name,new Driver(name,laps));
        });
    }

    public void printSorted(OutputStream outputStream) {
        PrintWriter pw = new PrintWriter(outputStream);
        List<Driver> newList = driverMap.values().stream().sorted(
                Comparator.comparing(
                        Driver::getBestTime
                )
        ).collect(Collectors.toCollection(ArrayList::new));
        for(int i=0;i<newList.size();i++) {
            System.out.printf("%d. %-10s %9s\n", i+1, newList.get(i).getName(),newList.get(i).getBestLap());
        }
    }

}

class Driver {
    private String name;
    private List<String> laps;
    public Driver(String name, List<String> laps) {
        this.name = name;
        this.laps = laps;
    }
    public String getName() { return this.name; }
    public List<String> getLaps() { return this.laps; }
    public int getParsed(String lap) {
        String[] parts = lap.split(":");
        int minutes = parseInt(parts[0]);
        int seconds = parseInt(parts[1]);
        int millis = parseInt(parts[2]);
        return (minutes * 60 * 1000) + (seconds * 1000) + millis;
    }
    public String getBestTime() {
        return laps.stream().skip(laps.size()-3).min(
                Comparator.comparing(this::getParsed)
        ).orElse("");
    }
    public String getBestLap() {
        return laps.stream().min(Comparator.comparing(this::getParsed)).orElse("");
    }
}