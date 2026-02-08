import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

import static java.lang.Integer.parseInt;

/**
 * Discounts
 */
public class DiscountsTest {
    public static void main(String[] args) {
        Discounts discounts = new Discounts();
        int stores = discounts.readStores(System.in);
        System.out.println("Stores read: " + stores);
        System.out.println("=== By average discount ===");
        discounts.byAverageDiscount().forEach(System.out::println);
        System.out.println("=== By total discount ===");
        discounts.byTotalDiscount().forEach(System.out::println);
    }
}

// Vashiot kod ovde

class Discounts {
    public Map<String,Store> storeMap;
    public Discounts() {
        storeMap = new HashMap<>();
    }

    public int readStores(InputStream inputStream) {
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        br.lines().forEach(l -> {
           String[] parts = l.split("\\s+");
           String name = parts[0];
           List<String> ceniList = new ArrayList<>();
           Arrays.stream(parts).skip(1).forEach(ceniList::add);
           storeMap.put(name,new Store(name,ceniList));
        });
        return storeMap.size();
    }

    public List<Store> byAverageDiscount() {
        return storeMap.values().stream().sorted(
                Comparator.comparing(
                        Store::getAverageDiscount, Comparator.reverseOrder()
                ).thenComparing(Store::getName)
        ).limit(3).collect(Collectors.toList());
    }

    public List<Store> byTotalDiscount() {
        return storeMap.values().stream().sorted(
                Comparator.comparing(
                        Store::getTotalDiscount
                ).thenComparing(Store::getName)
        ).limit(3).collect(Collectors.toList());
    }
}

class Store {
    private String name;
    private List<String> ceniList;
    public Store(String name, List<String> ceniList) {
        this.name = name;
        this.ceniList = ceniList;
    }
    public String getName() { return this.name; }
    public List<String> getCeniList() { return this.ceniList; }
    public Double getAverageDiscount() {
        return ceniList.stream().mapToDouble(this::getAverage
        ).average().orElse(0.0);
    }
    public int getAverage(String c) {
        String[] parts = c.split(":");
        return (getAbsolute(c))*100/parseInt(parts[1]);
    }

    public int getAbsolute(String c) {
        String[] parts = c.split(":");
        return parseInt(parts[1])-parseInt(parts[0]);
    }

    public int getTotalDiscount() {
        return ceniList.stream().mapToInt(
                this::getAbsolute
        ).sum();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(name).append("\nAverage discount: ");
        sb.append(String.format("%.1f%%\nTotal discount: %d\n",getAverageDiscount(),getTotalDiscount()));
        ceniList.stream().sorted(
                Comparator.comparing(
                        this::getAverage, Comparator.reverseOrder()
                ).thenComparing(this::getAbsolute, Comparator.reverseOrder())
        ).forEach(c -> {
            String[] parts = c.split(":");
            sb.append(String.format("%2d%% %d/%d\n",getAverage(c),parseInt(parts[0]),parseInt(parts[1])));
        });
        sb.deleteCharAt(sb.length()-1);
        return sb.toString();
    }
}
