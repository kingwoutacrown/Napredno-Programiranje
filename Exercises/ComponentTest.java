import java.util.*;

public class ComponentTest {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String name = scanner.nextLine();
        Window window = new Window(name);
        Component prev = null;
        while (true) {
            try {
                int what = scanner.nextInt();
                scanner.nextLine();
                if (what == 0) {
                    int position = scanner.nextInt();
                    window.addComponent(position, prev);
                } else if (what == 1) {
                    String color = scanner.nextLine();
                    int weight = scanner.nextInt();
                    Component component = new Component(color, weight);
                    prev = component;
                } else if (what == 2) {
                    String color = scanner.nextLine();
                    int weight = scanner.nextInt();
                    Component component = new Component(color, weight);
                    prev.addComponent(component);
                    prev = component;
                } else if (what == 3) {
                    String color = scanner.nextLine();
                    int weight = scanner.nextInt();
                    Component component = new Component(color, weight);
                    prev.addComponent(component);
                } else if(what == 4) {
                    break;
                }

            } catch (InvalidPositionException e) {
                System.out.println(e.getMessage());
            }
            scanner.nextLine();
        }

        System.out.println("=== ORIGINAL WINDOW ===");
        System.out.println(window);
        int weight = scanner.nextInt();
        scanner.nextLine();
        String color = scanner.nextLine();
        window.changeColor(weight, color);
        System.out.println(String.format("=== CHANGED COLOR (%d, %s) ===", weight, color));
        System.out.println(window);
        int pos1 = scanner.nextInt();
        int pos2 = scanner.nextInt();
        System.out.println(String.format("=== SWITCHED COMPONENTS %d <-> %d ===", pos1, pos2));
        window.switchComponents(pos1, pos2);
        System.out.println(window);
    }
}

// вашиот код овде
class Component {
    private String color;
    private int weight;
    private List<Component> componentList;
    public Component(String color, int weight) {
        this.color = color;
        this.weight = weight;
        componentList = new ArrayList<>();
    }

    public String getColor() {
        return color;
    }

    public int getWeight() {
        return weight;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public List<Component> getComponentList() {
        return componentList;
    }

    public void addComponent(Component component) {
        componentList.add(component);
        componentList.sort(Comparator.comparing(
                Component::getWeight
        ).thenComparing(Component::getColor));
    }

    public void changeColor(int weight, String color) {
        if(this.weight<weight) {
            this.color = color;
        }
        if(!componentList.isEmpty()) {
            componentList.forEach(c -> c.changeColor(weight,color));
        }
    }
    public String toString(int indent) {
        StringBuilder sb = new StringBuilder();
        sb.append("---".repeat(indent));
        sb.append(weight).append(":").append(color).append("\n");
        if(!componentList.isEmpty()) {
            componentList.forEach(c -> sb.append(c.toString(indent+1)));
        }
        return sb.toString();
    }
}

class Window {
    private String name;
    private Map<Integer,Component> componentMap;
    public Window(String name) {
        this.name = name;
        componentMap = new TreeMap<>();
    }

    public Map<Integer,Component> getComponentMap() {
        return componentMap;
    }

    public String getName() {
        return name;
    }

    public void addComponent(int position, Component component) throws InvalidPositionException {
        if(componentMap.containsKey(position)) {
            throw new InvalidPositionException(String.format("Invalid position %d, alredy taken!",position));
        }
        componentMap.put(position, component);
    }

    public void changeColor(int weight, String color) {
        componentMap.values().forEach(c -> c.changeColor(weight,color));
    }

    public void switchComponents(int pos1, int pos2) {
        Component component1 = componentMap.get(pos1);
        Component component2 = componentMap.get(pos2);
        componentMap.put(pos1, component2);
        componentMap.put(pos2, component1);
    }
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("WINDOW ").append(name).append("\n");
        if(!componentMap.isEmpty()) {
            componentMap.entrySet().forEach(c -> {
                sb.append(c.getKey()).append(":");
                sb.append(c.getValue().toString(0));
            });
        }
        return sb.toString();
    }
}

class InvalidPositionException extends Exception {
    public InvalidPositionException(String message) {
        super(message);
    }
}