import java.util.*;

public class AuditionTest {
    public static void main(String[] args) {
        Audition audition = new Audition();
        List<String> cities = new ArrayList<String>();
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String[] parts = line.split(";");
            if (parts.length > 1) {
                audition.addParticpant(parts[0], parts[1], parts[2],
                        Integer.parseInt(parts[3]));
            } else {
                cities.add(line);
            }
        }
        for (String city : cities) {
            System.out.printf("+++++ %s +++++\n", city);
            audition.listByCity(city);
        }
        scanner.close();
    }
}

class Audition {
    Map<String,City> cityMap;
    public Audition() {
        cityMap = new HashMap<>();
    }
    public void addParticpant(String city, String code, String name, int age) {
        cityMap.putIfAbsent(city, new City(city));
        cityMap.get(city).addParticipant(new Participant(code,name,age));
    }
    public void listByCity(String city) {
        cityMap.get(city).getParticipantMap().values().stream().sorted(
                Comparator.comparing(
                        Participant::getName
                ).thenComparing(Participant::getAge)
        ).forEach(System.out::println);
    }
}

class City {
    private String city;
    private Map<String, Participant> participantMap;
    public City(String city) {
        this.city = city;
        participantMap = new HashMap<>();
    }
    public void addParticipant(Participant participant) {
        if(!participantMap.containsKey(participant.getCode()))
            participantMap.put(participant.getCode(),participant);
    }
    Map<String, Participant> getParticipantMap() { return this.participantMap; }
}

class Participant {
    private String code;
    private String name;
    private int age;
    public Participant(String code, String name, int age) {
        this.code = code;
        this.name = name;
        this.age = age;
    }
    public String getCode() { return this.code; }
    public String getName() { return this.name; }
    public int getAge() { return this.age; }
    @Override
    public String toString() {
        return String.format("%s %s %d",code,name,age);
    }
}