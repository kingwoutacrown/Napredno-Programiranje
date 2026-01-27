import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

public class EventCalendarTest {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        scanner.nextLine();
        int year = scanner.nextInt();
        scanner.nextLine();

        EventCalendar eventCalendar = new EventCalendar(year);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

        for (int i = 0; i < n; ++i) {
            String line = scanner.nextLine();
            String[] parts = line.split(";");
            String name = parts[0];
            String location = parts[1];
            LocalDateTime date = LocalDateTime.parse(parts[2], formatter);
            try {
                eventCalendar.addEvent(name, location, date);
            } catch (WrongDateException e) {
                System.out.println(e.getMessage());
            }
        }

        LocalDateTime date = LocalDateTime.parse(scanner.nextLine(), formatter);
        eventCalendar.listEvents(date);
        eventCalendar.listByMonth();
    }
}

// vashiot kod ovde
class EventCalendar {
    private int year;
    List<Event> eventList;

    public EventCalendar(int year) {
        this.year = year;
        eventList = new ArrayList<>();
    }

    public void addEvent(String name, String location, LocalDateTime date) throws WrongDateException {
        if (date.getYear() != year)
            throw new WrongDateException(String.format("Wrong date: %s", DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss 'UTC' yyyy", Locale.ENGLISH)
                    .format(date)));
        else
            eventList.add(new Event(name,location,date));
    }

    public void listEvents(LocalDateTime date) {
        List<Event> eventList2 = eventList.stream()
                .filter(e -> e.getDate().toLocalDate().equals(date.toLocalDate()))
                .sorted(
                        Comparator.comparing(Event::getDate)
                                .thenComparing(Event::getName)
                ).collect(Collectors.toList());
        if(eventList2.isEmpty()) {
            System.out.println("No events on this day!");
        }
                eventList2.forEach(e ->
                        System.out.printf("%s at %s, %s%n",
                                DateTimeFormatter.ofPattern("d MMM, yyyy HH:mm", Locale.ENGLISH)
                                        .format(e.getDate()), e.getLocation(), e.getName())
                );
    }

    public void listByMonth() {
        Map<Integer,Long> newMap = eventList.stream()
                .collect(
                        Collectors.groupingBy(
                                Event::getMonth,
                                TreeMap::new,
                                Collectors.counting()
                        )
                );
        for(int m=1;m<=12;m++) {
            System.out.printf("%d : %d\n",m, newMap.getOrDefault(m,0L));
        }
    }
}

class Event {
    private String name;
    private String location;
    private LocalDateTime date;

    public Event(String name, String location, LocalDateTime date) {
        this.name = name;
        this.location = location;
        this.date = date;
    }

    public String getName() { return name; }
    public String getLocation() { return location; }
    public LocalDateTime getDate() { return date; }

    public int getMonth() {
        return date.getMonthValue();
    }
}

class WrongDateException extends Exception {
    public WrongDateException(String message) {
        super(message);
    }
}
