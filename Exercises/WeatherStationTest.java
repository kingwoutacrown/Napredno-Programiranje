import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.time.ZoneId;
import java.util.stream.Stream;

class WeatherStation {
    private int days;
    List<Measurement> measurementList;

    public WeatherStation(int days) {
        this.days = days;
        measurementList = new ArrayList<>();
    }

    public void addMeasurment(float temperature, float wind, float humidity,
                              float visibility, LocalDateTime date) {

        LocalDateTime maxDate = Stream.concat(
                measurementList.stream().map(Measurement::getDate),
                Stream.of(date)
        ).max(LocalDateTime::compareTo).orElse(date);

        LocalDateTime windowStart = maxDate.minusDays(days);

        measurementList = measurementList.stream()
                .filter(m -> m.getDate().isAfter(windowStart))
                .collect(Collectors.toCollection(ArrayList::new));


        if (!measurementList.isEmpty()) {
            LocalDateTime last = measurementList.stream()
                    .map(Measurement::getDate)
                    .max(LocalDateTime::compareTo)
                    .orElse(null);

            if (Math.abs(Duration.between(last, date).toSeconds()) <= 150) {
                return;
            }
        }


        measurementList.add(new Measurement(temperature, wind, humidity, visibility, date));
    }

    public int total() {
        return measurementList.size();
    }

    public void status(LocalDateTime from, LocalDateTime to) {
        List<Measurement> filteredMeasurements = measurementList.stream()
                .filter(m -> !m.getDate().isBefore(from) && !m.getDate().isAfter(to))
                .sorted(Comparator.comparing(Measurement::getDate))
                .collect(Collectors.toList());

        if (filteredMeasurements.isEmpty()) {
            throw new RuntimeException();
        }

        filteredMeasurements.forEach(System.out::println);

        System.out.printf("Average temperature: %.2f",
                filteredMeasurements.stream()
                        .mapToDouble(Measurement::getTemperature)
                        .average()
                        .orElse(0.0));
    }
}

class Measurement {
    private float temperature;
    private float wind;
    private float humidity;
    private float visibility;
    private LocalDateTime date;

    public Measurement(float temperature, float wind,
                       float humidity, float visibility,
                       LocalDateTime date) {
        this.temperature = temperature;
        this.wind = wind;
        this.humidity = humidity;
        this.visibility = visibility;
        this.date = date;
    }

    public float getTemperature() {
        return temperature;
    }

    public float getWind() {
        return wind;
    }

    public float getHumidity() {
        return humidity;
    }

    public float getVisibility() {
        return visibility;
    }

    public LocalDateTime getDate() {
        return date;
    }

    @Override
    public String toString() {
        return String.format(
                "%.1f %.1f km/h %.1f%% %.1f km %s",
                temperature,
                wind,
                humidity,
                visibility,
                date.atZone(ZoneId.of("GMT")).format(
                        DateTimeFormatter.ofPattern(
                                "EEE MMM dd HH:mm:ss z yyyy",
                                Locale.ENGLISH
                        )
                )
        );
    }
}

public class WeatherStationTest {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");

        int n = scanner.nextInt();
        scanner.nextLine();

        WeatherStation ws = new WeatherStation(n);

        while (true) {
            String line = scanner.nextLine();
            if (line.equals("=====")) {
                break;
            }

            String[] parts = line.split(" ");
            float temp = Float.parseFloat(parts[0]);
            float wind = Float.parseFloat(parts[1]);
            float hum = Float.parseFloat(parts[2]);
            float vis = Float.parseFloat(parts[3]);

            line = scanner.nextLine();
            LocalDateTime date = LocalDateTime.parse(line, formatter);

            ws.addMeasurment(temp, wind, hum, vis, date);
        }

        LocalDateTime from = LocalDateTime.parse(scanner.nextLine(), formatter);
        LocalDateTime to = LocalDateTime.parse(scanner.nextLine(), formatter);

        scanner.close();

        System.out.println(ws.total());

        try {
            ws.status(from, to);
        } catch (RuntimeException e) {
            System.out.println(e);
        }
    }
}
