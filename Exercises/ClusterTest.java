import java.util.*;
import java.util.stream.Collectors;

/**
 * January 2016 Exam problem 2
 */
public class ClusterTest {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Cluster<Point2D> cluster = new Cluster<>();
        int n = scanner.nextInt();
        scanner.nextLine();
        for (int i = 0; i < n; ++i) {
            String line = scanner.nextLine();
            String[] parts = line.split(" ");
            long id = Long.parseLong(parts[0]);
            float x = Float.parseFloat(parts[1]);
            float y = Float.parseFloat(parts[2]);
            cluster.addItem(new Point2D(id, x, y));
        }
        int id = scanner.nextInt();
        int top = scanner.nextInt();
        cluster.near(id, top);
        scanner.close();
    }
}

// your code here
class Cluster<T extends Point2D> {
    private List<T> elements;
    public Cluster() {
        elements = new ArrayList<>();
    }
    public List<T> getElements() { return this.elements; }
    public void addItem(T element) {
        elements.add(element);
    }
    public void near(long id, int top) {
        T element = elements.stream().filter(e -> e.getId() == id).findFirst().orElse(null);
        if(element == null)
            return;
        List<T> filtered = elements.stream().filter(e -> e.getId()!=element.getId())
                .sorted(
                Comparator.comparing(
                        e -> e.distance(element)
                )
        ).limit(top).collect(Collectors.toCollection(ArrayList::new));
        for(int i=0;i<filtered.size();i++) {
            System.out.printf("%d. %d -> %.3f\n",i+1, filtered.get(i).getId(), filtered.get(i).distance(element));
        }
    }
}

class Point2D {
    private long id;
    private float x;
    private float y;
    public Point2D(long id, float x, float y) {
        this.id = id;
        this.x = x;
        this.y = y;
    }
    public long getId() { return this.id; }
    public float getX() { return this.x; }
    public float getY() { return this.y; }
    public double distance(Point2D other) {
        return Math.sqrt(Math.pow((x - other.getX()),2) + Math.pow((y - other.getY()),2));
    }
}