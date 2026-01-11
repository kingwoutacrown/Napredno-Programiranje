import java.util.*;

/*
YOUR CODE HERE
DO NOT MODIFY THE interfaces and classes below!!!
*/
class DeliveryApp {
    private String name;
    private Map<String, DeliveryPerson> deliveryPersonMap;
    private Map<String, Restaurant> restaurantMap;
    private Map<String, User> userMap;
    public DeliveryApp(String name) {
        this.name=name;
        deliveryPersonMap = new HashMap<>();
        restaurantMap = new HashMap<>();
        userMap = new HashMap<>();
    }
    void registerDeliveryPerson(String id, String name, Location currentLocation) {
        deliveryPersonMap.put(id, new DeliveryPerson(id,name,currentLocation));
    }
    void addRestaurant(String id, String name, Location location) {
        restaurantMap.put(id, new Restaurant(id,name,location));
    }
    void addUser(String id, String name) {
        userMap.put(id, new User(id,name));
    }
    void addAddress(String id, String addressName, Location location) {
        userMap.get(id).addAddress(addressName,location);
    }
    void orderFood(String userId, String userAddressName, String restaurantId, float cost) {
        User user = userMap.get(userId);
        Restaurant restaurant = restaurantMap.get(restaurantId);


        user.addSpendingFee(cost);
        restaurant.addEarnings(cost);


        DeliveryPerson deliveryPerson = deliveryPersonMap.values()
                .stream()
                .min(Comparator
                        .comparing((DeliveryPerson d) -> d.getLocation().distance(restaurant.getLocation()))
                        .thenComparing(DeliveryPerson::getTotalDelivers).thenComparing(DeliveryPerson::getId))
                .orElse(null);

        if (deliveryPerson == null) return;

        int distance = restaurant.getLocation().distance(deliveryPerson.getLocation());
        int bonus = distance / 10;
        double fee = 90.0 + bonus * 10;

        deliveryPerson.addDeliveryFee(fee);
        deliveryPerson.setLocation(user.getAddress(userAddressName));
    }


    public void printUsers() {
        userMap.values().stream().sorted(Comparator.comparing(User::getTotalSpending, Comparator.reverseOrder()).thenComparing(User::getId,Comparator.reverseOrder())).forEach(System.out::println);
    }
    public void printRestaurants() {
        restaurantMap.values().stream().sorted(Comparator.comparing(Restaurant::getAverageEarnings, Comparator.reverseOrder()).thenComparing(Restaurant::getId, Comparator.reverseOrder())).forEach(System.out::println);
    }

    public void printDeliveryPeople() {
        deliveryPersonMap.values().stream().sorted(Comparator.comparing(DeliveryPerson::getTotalMoney, Comparator.reverseOrder()).thenComparing(DeliveryPerson::getId,Comparator.reverseOrder())).forEach(System.out::println);
    }
}

abstract class Person {
    private String id;
    private String name;
    public Person(String id, String name) {
        this.id=id;
        this.name=name;
    }
    public String getId() { return id;}
    public String getName() { return name;}
}

class DeliveryPerson extends Person {
    private List<Double> deliveryFees;
    private Location location;
    public DeliveryPerson(String id, String name, Location location) {
        super(id, name);
        this.location = location;
        deliveryFees = new ArrayList<>();
    }
    public Location getLocation() { return location;}
    public double getTotalMoney() {
        if(deliveryFees.isEmpty()) {
            return 0; }
        return deliveryFees.stream().mapToDouble(Double::doubleValue).sum();
        }
    public void addDeliveryFee(Double fee) {
        deliveryFees.add(fee);
    }
    public int getTotalDelivers() {
        return deliveryFees.size();
    }
    public double getAverageFee() {
        return deliveryFees.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
    }
    public void setLocation(Location location) {
        this.location=location;
    }
    @Override
    public String toString() {
        return String.format("ID: %s Name: %s Total deliveries: %d Total delivery fee: %.2f Average delivery fee: %.2f",getId(),getName(),getTotalDelivers(),getTotalMoney(),getAverageFee());
    }
}

class User extends Person {
    private Map<String,Location> addresses;
    private List<Double> spendings;
    public User(String id, String name) {
        super(id, name);
        addresses = new HashMap<>();
        spendings = new ArrayList<>();
    }
    public void addAddress(String addressName, Location location) {
        addresses.put(addressName,location);
    }
    public Location getAddress(String addressName) {
        return addresses.get(addressName);
    }
    public void addSpendingFee(double fee) {
        spendings.add(fee);
    }
    public int getTotalOrders() { return spendings.size();}
    public Double getAverageSpending() {
        return spendings.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
    }
    public Double getTotalSpending() {
        if(spendings.isEmpty()) {
            return 0.0;
        }
        return spendings.stream().mapToDouble(Double::doubleValue).sum();
    }
    @Override
    public String toString() {
        return String.format("ID: %s Name: %s Total orders: %d Total amount spent: %.2f Average amount spent: %.2f",getId(),getName(),getTotalOrders(), getTotalSpending(), getAverageSpending());
    }
}

class Restaurant {
    private String id;
    private String name;
    private Location location;
    private List<Float> earnings;
    public Restaurant(String id,String name, Location location) {
        this.id=id;
        this.name=name;
        this.location=location;
        earnings = new ArrayList<>();
    }

    public String getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public Location getLocation() {
        return location;
    }
    public int totalOrders() {
        return earnings.size();
    }
    public void addEarnings(float earnings) {
        this.earnings.add(earnings);
    }
    public Double getAverageEarnings() {
        return earnings.stream().mapToDouble(Float::doubleValue).average().orElse(0.0);
    }
    public Double getTotalEarnings() {
        return earnings.stream().mapToDouble(Float::doubleValue).sum();
    }
    @Override
    public String toString() {
        return String.format("ID: %s Name: %s Total orders: %d Total amount earned: %.2f Average amount earned: %.2f",id,name,totalOrders(),getTotalEarnings(),getAverageEarnings());
    }
}

interface Location {
    int getX();

    int getY();

    default int distance(Location other) {
        int xDiff = Math.abs(getX() - other.getX());
        int yDiff = Math.abs(getY() - other.getY());
        return xDiff + yDiff;
    }
}

class LocationCreator {
    public static Location create(int x, int y) {

        return new Location() {
            @Override
            public int getX() {
                return x;
            }

            @Override
            public int getY() {
                return y;
            }
        };
    }
}

public class DeliveryAppTester {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String appName = sc.nextLine();
        DeliveryApp app = new DeliveryApp(appName);
        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            String[] parts = line.split(" ");

            if (parts[0].equals("addUser")) {
                String id = parts[1];
                String name = parts[2];
                app.addUser(id, name);
            } else if (parts[0].equals("registerDeliveryPerson")) {
                String id = parts[1];
                String name = parts[2];
                int x = Integer.parseInt(parts[3]);
                int y = Integer.parseInt(parts[4]);
                app.registerDeliveryPerson(id, name, LocationCreator.create(x, y));
            } else if (parts[0].equals("addRestaurant")) {
                String id = parts[1];
                String name = parts[2];
                int x = Integer.parseInt(parts[3]);
                int y = Integer.parseInt(parts[4]);
                app.addRestaurant(id, name, LocationCreator.create(x, y));
            } else if (parts[0].equals("addAddress")) {
                String id = parts[1];
                String name = parts[2];
                int x = Integer.parseInt(parts[3]);
                int y = Integer.parseInt(parts[4]);
                app.addAddress(id, name, LocationCreator.create(x, y));
            } else if (parts[0].equals("orderFood")) {
                String userId = parts[1];
                String userAddressName = parts[2];
                String restaurantId = parts[3];
                float cost = Float.parseFloat(parts[4]);
                app.orderFood(userId, userAddressName, restaurantId, cost);
            } else if (parts[0].equals("printUsers")) {
                app.printUsers();
            } else if (parts[0].equals("printRestaurants")) {
                app.printRestaurants();
            } else {
                app.printDeliveryPeople();
            }

        }
    }
}
