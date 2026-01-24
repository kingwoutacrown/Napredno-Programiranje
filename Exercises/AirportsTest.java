import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class AirportsTest {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Airports airports = new Airports();
        int n = scanner.nextInt();
        scanner.nextLine();
        String[] codes = new String[n];
        for (int i = 0; i < n; ++i) {
            String al = scanner.nextLine();
            String[] parts = al.split(";");
            airports.addAirport(parts[0], parts[1], parts[2], Integer.parseInt(parts[3]));
            codes[i] = parts[2];
        }
        int nn = scanner.nextInt();
        scanner.nextLine();
        for (int i = 0; i < nn; ++i) {
            String fl = scanner.nextLine();
            String[] parts = fl.split(";");
            airports.addFlights(parts[0], parts[1], Integer.parseInt(parts[2]), Integer.parseInt(parts[3]));
        }
        int f = scanner.nextInt();
        int t = scanner.nextInt();
        String from = codes[f];
        String to = codes[t];
        System.out.printf("===== FLIGHTS FROM %S =====\n", from);
        airports.showFlightsFromAirport(from);
        System.out.printf("===== DIRECT FLIGHTS FROM %S TO %S =====\n", from, to);
        airports.showDirectFlightsFromTo(from, to);
        t += 5;
        t = t % n;
        to = codes[t];
        System.out.printf("===== DIRECT FLIGHTS TO %S =====\n", to);
        airports.showDirectFlightsTo(to);
    }
}

// vashiot kod ovde

class Airports {
    Map<String, Airport> airportMap;
    Map<String,TreeSet<Flight>> flightMap;
    public Airports() {
        airportMap = new HashMap<>();
        flightMap = new HashMap<>();
    }
    public void addAirport(String name, String country, String code, int passengers) {
        Airport airport = new Airport(name,country,code,passengers);
        airportMap.put(code, airport);
    }

    public void addFlights(String from, String to, int time, int duration) {
        Flight flight = new Flight(from,to,time,duration);
        flightMap.putIfAbsent(from, new TreeSet<>(Comparator.comparing(
                Flight::getTo
        ).thenComparing(Flight::getTime)));
        flightMap.get(from).add(flight);
    }

    public void showFlightsFromAirport(String code) {
        Airport airport = airportMap.get(code);
        System.out.printf("%s (%s)\n", airport.getName(),airport.getCode());
        System.out.println(airport.getCountry());
        System.out.println(airport.getPassengers());
        List<Flight> flights = new ArrayList<>(flightMap.get(code));
        IntStream.range(0,flights.size()).forEach(
                i -> {
                    System.out.println((i+1) + ". " + flights.get(i));
                }
        );
    }

    public void showDirectFlightsFromTo(String from, String to) {
        List<Flight> newList = flightMap.get(from).stream().filter(f -> f.getTo().equals(to))
                .collect(Collectors.toCollection(ArrayList::new));
        if(newList.isEmpty()) {
            System.out.printf("No flights from %s to %s\n",from,to);
        }
        else {
            newList.forEach(System.out::println);
        }
    }

    public void showDirectFlightsTo(String to) {
        flightMap.values().stream().flatMap(f -> f.stream().filter(d -> d.getTo().equals(to)))
                .collect(Collectors.toCollection( () ->
                        new TreeSet<>(
                                Comparator.comparing(
                                Flight::getTime).thenComparing(Flight::getDuration)
                                )
                )).forEach(System.out::println);
    }
}

class Airport {
    private String name;
    private String country;
    private String code;
    private int passengers;
    public Airport(String name, String country, String code, int passengers) {
        this.name = name;
        this.country = country;
        this.code = code;
        this.passengers = passengers;
    }

    public String getName() {
        return name;
    }

    public String getCountry() {
        return country;
    }

    public String getCode() {
        return code;
    }

    public int getPassengers() {
        return passengers;
    }
}

class Flight {
    private String from;
    private String to;
    private int time;
    private int duration;
    public Flight(String from, String to, int time, int duration) {
        this.from = from;
        this.to = to;
        this.time = time;
        this.duration = duration;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public int getTime() {
        return time;
    }

    public int getDuration() {
        return duration;
    }
    @Override
    public String toString() {
        int endTime = time + duration;
        int endHour = (endTime / 60) % 24;
        int endMinute = endTime % 60;
        int days = endTime / 1440;

        String dayStr = days > 0 ? " +" + days + "d" : "";

        return String.format("%s-%s %02d:%02d-%02d:%02d%s %dh%02dm",
                from, to,
                time / 60, time % 60,
                endHour, endMinute,
                dayStr,
                duration / 60, duration % 60
        );
    }


}