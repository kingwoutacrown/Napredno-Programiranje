import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

class Book {
    private String isbn;
    private String title;
    private String author;
    private int year;
    private int availableCopies;
    private int totalBorrowings;

    public Book(String isbn, String title, String author, int year) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.year = year;
        this.availableCopies = 1;
        this.totalBorrowings = 0;
    }

    public void addTotalBorrowings() {
        this.totalBorrowings += 1;
    }

    public int getAvailableCopies() {
        return availableCopies;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public int getYear() {
        return year;
    }

    public String getIsbn() {
        return isbn;
    }

    boolean findBook(String isbn) {
        return Objects.equals(this.isbn, isbn);
    }

    public void addAvailableCopies() {
        this.availableCopies += 1;
    }

    public void subtractAvailableCopies() {
        this.availableCopies -= 1;
    }

    public int getTotalBorrowings() {
        return totalBorrowings;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Book book = (Book) o;
        return Objects.equals(isbn, book.isbn);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(isbn);
    }
}

class Member {
    private String id;
    private String fullName;
    private int numberOfBorrowedBooks;
    private int totalNumberOfBorrowedBooks;

    public Member(String id, String fullName) {
        this.id = id;
        this.fullName = fullName;
        this.numberOfBorrowedBooks = 0;
        this.totalNumberOfBorrowedBooks = 0;
    }

    boolean findMember(String id) {
        return this.id.equals(id);
    }

    public String getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public void addTotalNumberOfBorrowedBooks() {
        this.totalNumberOfBorrowedBooks += 1;
    }

    public void addNumberOfBorrowedBooks() {
        this.numberOfBorrowedBooks += 1;
    }

    public void subtractNumberOfBorrowedBooks() {
        this.numberOfBorrowedBooks -= 1;
    }

    public int getNumberOfBorrowedBooks() {
        return numberOfBorrowedBooks;
    }

    public int getTotalNumberOfBorrowedBooks() {
        return totalNumberOfBorrowedBooks;
    }
}

class LibrarySystem {
    private String name;
    private Set<Book> books;
    private Set<Member> members;
    Map<Member, List<Book>> bookMap;
    Map<Book, List<Member>> queue;

    public LibrarySystem(String name) {
        this.name = name;
        books = new LinkedHashSet<>();
        members = new LinkedHashSet<>();
        bookMap = new HashMap<>();
        queue = new HashMap<>();
    }

    void registerMember(String id, String fullName) {
        members.add(new Member(id, fullName));
    }

    public void addBook(String isbn, String title, String author, int year) {
        for (Book b : books) {
            if (b.findBook(isbn)) {
                b.addAvailableCopies();
                return;
            }
        }
        books.add(new Book(isbn, title, author, year));
    }

    public void borrowBook(String memberId, String isbn) {
        Member member = members.stream()
                .filter(m -> m.getId().equals(memberId))
                .findFirst()
                .orElse(null);
        if (member == null) return;

        Book book = books.stream()
                .filter(b -> b.getIsbn().equals(isbn))
                .findFirst()
                .orElse(null);
        if (book == null) return;

        if (book.getAvailableCopies() > 0) {
            bookMap.computeIfAbsent(member, k -> new ArrayList<>()).add(book);
            member.addNumberOfBorrowedBooks();
            book.addTotalBorrowings();
            member.addTotalNumberOfBorrowedBooks();
            book.subtractAvailableCopies();
        } else {
            queue.computeIfAbsent(book, k -> new ArrayList<>()).add(member);
        }
    }

    public void returnBook(String memberId, String isbn) {
        Member member = members.stream()
                .filter(m -> m.getId().equals(memberId))
                .findFirst()
                .orElse(null);

        if (member == null) return;

        Book book = bookMap.get(member).stream().filter(b -> b.getIsbn().equals(isbn)).findFirst().orElse(null);
        if (book == null || !book.getIsbn().equals(isbn)) return;

        bookMap.get(member).remove(book);
        member.subtractNumberOfBorrowedBooks();

        List<Member> waiting = queue.get(book);

        if (waiting == null || waiting.isEmpty()) {
            book.addAvailableCopies();
        } else {
            Member next = waiting.removeFirst();
            if (waiting.isEmpty()) queue.remove(book);

            bookMap.computeIfAbsent(next, k -> new ArrayList<>()).add(book);
            book.addTotalBorrowings();
            next.addNumberOfBorrowedBooks();
            next.addTotalNumberOfBorrowedBooks();
        }
    }

    public void printMembers() {
        members.stream().
                sorted(Comparator.comparing(Member::getNumberOfBorrowedBooks, Comparator.reverseOrder())
                        .thenComparing(Member::getFullName))
                .forEach(member -> System.out.printf("%s (%s) - borrowed now: %d, total borrows: %d\n",
                        member.getFullName(),
                        member.getId(),
                        member.getNumberOfBorrowedBooks(),
                        member.getTotalNumberOfBorrowedBooks()));
    }

    public void printBooks() {
        books.stream().sorted(Comparator.comparing(Book::getTotalBorrowings, Comparator.reverseOrder())
                        .thenComparing(Book::getYear))
                .forEach(book ->
                        System.out.printf("%s - \"%s\" by %s (%d), available: %d, total borrows: %d\n",
                                book.getIsbn(),
                                book.getTitle(),
                                book.getAuthor(),
                                book.getYear(),
                                book.getAvailableCopies(),
                                book.getTotalBorrowings()));
    }

    public void printBookCurrentBorrowers(String isbn) {
        System.out.println(bookMap.entrySet().stream()
                .filter(e -> e.getValue()
                        .stream().anyMatch(b -> b.getIsbn().equals(isbn)))
                .map(Map.Entry::getKey).sorted(Comparator.comparing(Member::getId))
                .map(Member::getId)
                .collect(Collectors.joining(", ")));
        //if(bookMap.isEmpty()) return;
        //ArrayList<String> owners = new ArrayList<>();
//        for(Map.Entry<Member, List<Book>> entry : bookMap.entrySet()) {
//            for(Book b : entry.getValue()) {
//                if(b.getIsbn().equals(isbn)) {
//                    owners.add(entry.getKey().getId());
//                    break;
//                }
//            }
//        }
//        if(owners.isEmpty()) return;
//        System.out.println(owners.stream().sorted().collect(Collectors.joining(", ")));
    }

    public void printTopAuthors() {
        books.stream().collect(Collectors.groupingBy(
                        Book::getAuthor,
                        Collectors.summingInt(Book::getTotalBorrowings)
                )).entrySet().stream()
                .sorted(Comparator.comparing(Map.Entry::getValue, Comparator.reverseOrder()))
                .forEach(b -> {
                    System.out.println(b.getKey() + " - " + b.getValue());
                });
//        Map<String, Integer> map = new TreeMap<>();
//        for(Book book: books) {
//            if(!map.containsKey(book.getAuthor())) {
//                map.put(book.getAuthor(), book.getTotalBorrowings());
//            }
//            else {
//                map.put(book.getAuthor(), map.get(book.getAuthor())+book.getTotalBorrowings());
//            }
//        }
//        map.entrySet().stream().sorted(Comparator.comparing(Map.Entry::getValue, Comparator.reverseOrder())).forEach(entry -> {
//            System.out.printf("%s - %d%n", entry.getKey(), entry.getValue());
//        });
    }

    Map<String, TreeSet<String>> getAuthorsWithBooks() {
        return books.stream()
                .collect(Collectors.groupingBy(Book::getAuthor,
                        HashMap::new,
                        Collectors.mapping(Book::getIsbn, Collectors.toCollection(TreeSet::new))));
    }

    Map<String, Book> getTopBookPerAuthor() {
        return books.stream()
                .collect(Collectors.groupingBy(
                        Book::getAuthor,
                        HashMap::new,
                        Collectors.collectingAndThen(
                                Collectors.maxBy(Comparator.comparing(Book::getTotalBorrowings)),
                                Optional::get)
                ));
    }

    Map<String, Integer> getBooksWaitingListSize() {
        return queue.entrySet().stream()
                .sorted(Map.Entry.<Book, List<Member>>comparingByValue(Comparator.comparingInt(List::size))
                        .thenComparing(e -> e.getKey().getIsbn(), Comparator.reverseOrder()))
                .collect(Collectors.toMap(e -> e.getKey().getIsbn(),
                        e -> e.getValue().size(),
                        (a, b) -> a,
                        LinkedHashMap::new));
    }

    Map<Boolean, List<Member>> getMembersByBorrowActivity() {
        return members.stream().collect(Collectors.groupingBy(
                p -> p.getTotalNumberOfBorrowedBooks() > 0,
                Collectors.toCollection(ArrayList::new)
        ));
    }

    Map<Integer, Set<Member>> getMembersGroupedByBorrowedCount() {
        return members.stream().collect(
                Collectors.groupingBy(
                        Member::getNumberOfBorrowedBooks,
                        () -> new TreeMap<>(Comparator.reverseOrder()),
                        Collectors.toSet()
                )
        );
    }

    Map<Book, Integer> getBooksAndNumberOfBorrowings() {
        return books.stream().collect(Collectors.toMap(
                book -> book,
                Book::getTotalBorrowings,
                (a, b) -> a,
                () -> new TreeMap<>(Comparator.comparing(Book::getTitle))
        ));
    }
//    Map<String, TreeSet<String>> getAuthorsWithBooks() {
//        return books.stream().collect(Collectors.groupingBy(
//                Book::getAuthor,
//                TreeMap::new,
//                Collectors.mapping(Book::getIsbn, Collectors.toCollection(TreeSet::new))
//        ));
//    }
}

public class LibraryTester {
    public static void main(String[] args) {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        try {
            String libraryName = br.readLine();
            //   System.out.println(libraryName); //test
            if (libraryName == null) return;

            libraryName = libraryName.trim();
            LibrarySystem lib = new LibrarySystem(libraryName);

            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.equals("END")) break;
                if (line.isEmpty()) continue;

                String[] parts = line.split(" ");

                switch (parts[0]) {

                    case "registerMember": {
                        lib.registerMember(parts[1], parts[2]);
                        break;
                    }

                    case "addBook": {
                        String isbn = parts[1];
                        String title = parts[2];
                        String author = parts[3];
                        int year = Integer.parseInt(parts[4]);
                        lib.addBook(isbn, title, author, year);
                        break;
                    }

                    case "borrowBook": {
                        lib.borrowBook(parts[1], parts[2]);
                        break;
                    }

                    case "returnBook": {
                        lib.returnBook(parts[1], parts[2]);
                        break;
                    }

                    case "printMembers": {
                        lib.printMembers();
                        break;
                    }

                    case "printBooks": {
                        lib.printBooks();
                        break;
                    }

                    case "printBookCurrentBorrowers": {
                        lib.printBookCurrentBorrowers(parts[1]);
                        break;
                    }

                    case "printTopAuthors": {
                        lib.printTopAuthors();
                        break;
                    }

                    default:
                        break;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
