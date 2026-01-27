import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

enum COMPARATOR_TYPE {
    NEWEST_FIRST,
    OLDEST_FIRST,
    LOWEST_PRICE_FIRST,
    HIGHEST_PRICE_FIRST,
    MOST_SOLD_FIRST,
    LEAST_SOLD_FIRST
}

class ProductNotFoundException extends Exception {
    ProductNotFoundException(String message) {
        super(message);
    }
}


class Product {
    private String category;
    private String id;
    private String name;
    private LocalDateTime createdAt;
    private double price;
    private int totalSold;
    public Product(String category, String id, String name, LocalDateTime createdAt, double price) {
        this.category = category;
        this.id = id;
        this.name = name;
        this.createdAt = createdAt;
        this.price = price;
        totalSold = 0;
    }
    public String getId() { return this.id; }
    public String getName() { return this.name; }
    public String getCategory() { return this.category; }
    public LocalDateTime getCreatedAt() { return this.createdAt; }
    public double getPrice() {
        return this.price;
    }
    public void addSold(int sold) {
        totalSold+= sold;
    }
    public int getTotalSold() { return this.totalSold; }
    @Override
    public String toString() {
        //Product{id='050be27b', name='product0', createdAt=2019-01-14T23:17:46.715710, price=2913.14, quantitySold=14}
        String cleanPrice = BigDecimal.valueOf(price)
                .stripTrailingZeros()
                .toPlainString();
        return String.format("Product{id='%s', name='%s', createdAt=%s, price=%s, quantitySold=%d}",
                getId(),getName(), getCreatedAt(), cleanPrice, getTotalSold());
    }
}


class OnlineShop {
    Map<String, Product> productMap;

    OnlineShop() {
        productMap = new HashMap<>();
    }

    void addProduct(String category, String id, String name, LocalDateTime createdAt, double price){
        productMap.put(id, new Product(category,id,name,createdAt,price));
    }

    double buyProduct(String id, int quantity) throws ProductNotFoundException{
        if(!productMap.containsKey(id)) {
            throw new ProductNotFoundException(String.format("Product with id %s does not exist in the online shop!", id));
        }
        else {
            productMap.get(id).addSold(quantity);
            return productMap.get(id).getPrice()*quantity;
        }
    }

    List<List<Product>> listProducts(String category, COMPARATOR_TYPE comparatorType, int pageSize) {
        List<List<Product>> result = new ArrayList<>();
        List<Product> filteredProducts = productMap.values().stream().filter(
                p -> category == null || p.getCategory().equals(category)
        ).sorted(getComparator(comparatorType)).collect(Collectors.toList());
        for(int i=0;i<filteredProducts.size();i+=pageSize) {
            result.add(filteredProducts.subList(i,Math.min(i+pageSize,filteredProducts.size())));
        }
        return result;
    }
    private Comparator<Product> getComparator(COMPARATOR_TYPE type) {
        switch(type) {
            case NEWEST_FIRST:
                return Comparator.comparing(Product::getCreatedAt, Comparator.reverseOrder());
            case OLDEST_FIRST:
                return Comparator.comparing(Product::getCreatedAt);
            case MOST_SOLD_FIRST:
                return Comparator.comparing(Product::getTotalSold, Comparator.reverseOrder());
            case LEAST_SOLD_FIRST:
                return Comparator.comparing(Product::getTotalSold);
            case LOWEST_PRICE_FIRST:
                return Comparator.comparing(Product::getPrice);
            case HIGHEST_PRICE_FIRST:
                return Comparator.comparing(Product::getPrice, Comparator.reverseOrder());
        }
        return null;
    }
}

public class OnlineShopTest {

    public static void main(String[] args) {
        OnlineShop onlineShop = new OnlineShop();
        double totalAmount = 0.0;
        Scanner sc = new Scanner(System.in);
        String line;
        while (sc.hasNextLine()) {
            line = sc.nextLine();
            String[] parts = line.split("\\s+");
            if (parts[0].equalsIgnoreCase("addproduct")) {
                String category = parts[1];
                String id = parts[2];
                String name = parts[3];
                LocalDateTime createdAt = LocalDateTime.parse(parts[4]);
                double price = Double.parseDouble(parts[5]);
                onlineShop.addProduct(category, id, name, createdAt, price);
            } else if (parts[0].equalsIgnoreCase("buyproduct")) {
                String id = parts[1];
                int quantity = Integer.parseInt(parts[2]);
                try {
                    totalAmount += onlineShop.buyProduct(id, quantity);
                } catch (ProductNotFoundException e) {
                    System.out.println(e.getMessage());
                }
            } else {
                String category = parts[1];
                if (category.equalsIgnoreCase("null"))
                    category=null;
                String comparatorString = parts[2];
                int pageSize = Integer.parseInt(parts[3]);
                COMPARATOR_TYPE comparatorType = COMPARATOR_TYPE.valueOf(comparatorString);
                printPages(onlineShop.listProducts(category, comparatorType, pageSize));
            }
        }
        System.out.println("Total revenue of the online shop is: " + totalAmount);

    }

    private static void printPages(List<List<Product>> listProducts) {
        for (int i = 0; i < listProducts.size(); i++) {
            System.out.println("PAGE " + (i + 1));
            listProducts.get(i).forEach(System.out::println);
        }
    }
}

