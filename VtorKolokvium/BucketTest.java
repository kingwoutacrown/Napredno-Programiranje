import java.util.*;
import java.util.stream.Collectors;


public class BucketTest {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        // bucket name is fixed
        Bucket bucket = new Bucket("bucket");

        while (sc.hasNextLine()) {
            String line = sc.nextLine().trim();
            if (line.isEmpty()) continue;

            String[] parts = line.split("\\s+", 2);
            String command = parts[0];

            if (command.equalsIgnoreCase("ADD")) {
                bucket.addObject(parts[1]);
            } else if (command.equalsIgnoreCase("REMOVE")) {
                bucket.removeObject(parts[1]);
            } else if (command.equalsIgnoreCase("PRINT")) {
                System.out.print(bucket);
            }
        }
    }
}

interface Component {
    public void addObject(String key);
    public void removeObject(String key);
    String toString(int indent);
    public boolean isEmpty();
}

class Bucket {
    private String name;
    Map<String, Component> componentMap;
    public Bucket(String name) {
        this.name = name;
        componentMap = new LinkedHashMap<>();
    }

    public void addObject(String key) {
        if(key.contains("/")) {
            String[] parts = key.split("/");
            String newKey = Arrays.stream(parts).skip(1).collect(Collectors.joining("/"));
            componentMap.putIfAbsent(parts[0], new Directory(parts[0]));
            componentMap.get(parts[0]).addObject(newKey);
        }
        else {
            componentMap.putIfAbsent(key, new Leaf(key));
        }
    }

    public void removeObject(String key) {
        String[] parts = key.split("/");
        if (componentMap.get(parts[0]) == null) return;

        if (componentMap.get(parts[0]) instanceof Leaf) {
            componentMap.remove(parts[0]);
        } else {
            String newKey = Arrays.stream(parts).skip(1).collect(Collectors.joining("/"));
            componentMap.get(parts[0]).removeObject(newKey);
            if (componentMap.get(parts[0]).isEmpty()) {
                componentMap.remove(parts[0]);
            }
        }
    }


    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("bucket/\n");
        componentMap.values().forEach(
                c -> {
                    sb.append(c.toString(1));
                }
        );
        return sb.toString();
    }
}

class Directory implements Component {
    String name;
    Map<String,Component> componentMap;
    public Directory(String name) {
        this.name = name;
        componentMap = new LinkedHashMap<>();
    }

    public void addObject(String key) {
        if(key.contains("/")) {
            String[] parts = key.split("/");
            String newKey = Arrays.stream(parts).skip(1).collect(Collectors.joining("/"));
            componentMap.putIfAbsent(parts[0], new Directory(parts[0]));
            componentMap.get(parts[0]).addObject(newKey);
        }
        else {
            componentMap.putIfAbsent(key, new Leaf(key));
        }
    }

    public void removeObject(String key) {
        String[] parts = key.split("/");
        if(componentMap.get(parts[0]) == null) return;

        if (componentMap.get(parts[0]) instanceof Leaf) {
            componentMap.remove(parts[0]);
        } else {
            String newKey = Arrays.stream(parts).skip(1).collect(Collectors.joining("/"));
            componentMap.get(parts[0]).removeObject(newKey);
            if (componentMap.get(parts[0]).isEmpty()) {
                componentMap.remove(parts[0]);
            }
        }
    }


    @Override
    public String toString(int indent) {
        StringBuilder sb = new StringBuilder();
        sb.append("    ".repeat(indent)).append(name).append("/").append("\n");
        componentMap.values().forEach(c ->
        {
           sb.append(c.toString(indent+1));
        });
        return sb.toString();
    }

    @Override
    public boolean isEmpty() {
        if(componentMap.isEmpty()) {
            return true;
        }
        else
            return false;
    }
}

class Leaf implements Component {
    private String name;
    public Leaf(String name) {
        this.name = name;
    }
    public String getName() { return this.name; }

    @Override
    public void addObject(String key) {
        return;
    }

    @Override
    public void removeObject(String key) {
        return;
    }

    @Override
    public String toString(int indent) {
        return "    ".repeat(indent) + name + "\n";
    }

    @Override
    public boolean isEmpty() {
        return false;
    }
}

