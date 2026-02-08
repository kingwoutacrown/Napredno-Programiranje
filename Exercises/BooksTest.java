import java.util.*;
import java.util.stream.Collectors;

public class BooksTest {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        scanner.nextLine();
        BookCollection booksCollection = new BookCollection();
        Set<String> categories = fillCollection(scanner, booksCollection);
        System.out.println("=== PRINT BY CATEGORY ===");
        for (String category : categories) {
            System.out.println("CATEGORY: " + category);
            booksCollection.printByCategory(category);
        }
        System.out.println("=== TOP N BY PRICE ===");
        print(booksCollection.getCheapestN(n));
    }

    static void print(List<Book> books) {
        for (Book book : books) {
            System.out.println(book);
        }
    }

    static TreeSet<String> fillCollection(Scanner scanner,
                                          BookCollection collection) {
        TreeSet<String> categories = new TreeSet<String>();
        while (scanner.hasNext()) {
            String line = scanner.nextLine();
            String[] parts = line.split(":");
            Book book = new Book(parts[0], parts[1], Float.parseFloat(parts[2]));
            collection.addBook(book);
            categories.add(parts[1]);
        }
        return categories;
    }
}

// Вашиот код овде
class Book {
    private String title;
    private String category;
    private float price;
    public Book(String title, String category, float price) {
        this.title = title;
        this.category = category;
        this.price = price;
    }
    public String getTitle() { return this.title; }
    public String getCategory() { return this.category; }
    public float getPrice() { return this.price; }
    @Override
    public String toString() {
        return String.format("%s (%s) %.2f",title,category,price);
    }
}

class BookCollection {
    List<Book> booksList;
    public BookCollection() {
        booksList= new ArrayList<>();
    }
    public void addBook(Book book) {
        booksList.add(book);
    }
    public void printByCategory(String category) {
        //System.out.println("CATEGORY: "+ category);
        booksList.stream().filter(b -> b.getCategory().equalsIgnoreCase(category))
                .sorted(
                        Comparator.comparing(
                                Book::getTitle
                        )
                                .thenComparing(
                                        Book::getPrice
                                )
                ).forEach(System.out::println);
    }

    public List<Book> getCheapestN(int n) {
        if(booksList.size() < n)
            return booksList;
        else {
            return booksList.stream().sorted(
                    Comparator.comparing(
                            Book::getPrice
                    ).thenComparing(Book::getTitle)
            ).limit(n).collect(Collectors.toList());
        }
    }
}