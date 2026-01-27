import java.time.LocalDateTime;
import java.util.*;

class Article {

    private final String category;
    private final String author;
    private final String content;
    private final LocalDateTime timestamp;

    public Article(String category, String author, String content, LocalDateTime timestamp) {
        this.category = category;
        this.author = author;
        this.content = content;
        this.timestamp = timestamp;
    }

    public String getCategory() {
        return category;
    }

    public String getAuthor() {
        return author;
    }

    public String getContent() {
        return content;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}


public class NewsSystemTest {

    public static void main(String[] args) {

        // Hardcoded categories and authors
        List<String> categories = List.of(
                "Technology", "Sports", "Politics", "Health", "Science",
                "Business", "Education", "Culture", "Travel", "Entertainment"
        );

        List<String> authors = List.of(
                "MartinFowler", "JohnDoe", "AliceSmith", "BobBrown", "JaneMiller"
        );

        NewsSystem system = new NewsSystem(categories, authors);

        Scanner sc = new Scanner(System.in);

        while (sc.hasNextLine()) {
            String line = sc.nextLine().trim();
            if (line.isEmpty()) continue;

            String[] parts = line.split("\\s+", 2);
            String command = parts[0];

            switch (command) {

                case "ADD_USER":
                    system.addUser(parts[1]);
                    break;

                case "SUBSCRIBE_CATEGORY": {
                    String[] p = parts[1].split("\\s+");
                    system.subscribeUserToCategory(p[0], p[1]);
                    break;
                }

                case "UNSUBSCRIBE_CATEGORY": {
                    String[] p = parts[1].split("\\s+");
                    system.unsubscribeUserFromCategory(p[0], p[1]);
                    break;
                }

                case "SUBSCRIBE_AUTHOR": {
                    String[] p = parts[1].split("\\s+");
                    system.subscribeUserToAuthor(p[0], p[1]);
                    break;
                }

                case "UNSUBSCRIBE_AUTHOR": {
                    String[] p = parts[1].split("\\s+");
                    system.unsubscribeUserFromAuthor(p[0], p[1]);
                    break;
                }

                case "PUBLISH": {
                    // format:
                    // PUBLISH <category> <author> <timestamp> <content>
                    String[] p = parts[1].split("\\s+", 4);
                    Article article = new Article(
                            p[0],
                            p[1],
                            p[3],
                            LocalDateTime.parse(p[2])
                    );
                    system.publishArticle(article);
                    break;
                }

                case "PRINT":
                    system.printNewsForUser(parts[1]);
                    break;
            }
        }
    }
}

interface Observer {
    void update(Article article);
}

interface Subject {
    void registerObserver(Observer observer);
    void notifyObservers(Article article);
}

class User implements Observer {
    private String username;
    private Set<String> categories = new HashSet<>();
    private Set<String> authors = new HashSet<>();
    private List<Article> inbox = new ArrayList<>();

    public User(String username) {
        this.username = username;
    }

    public String getUsername() {
        return this.username;
    }

    public void subscribeCategory(String category) {
        categories.add(category);
    }
    public void unsubscribeCategory(String category) {
        categories.remove(category);
    }

    public void subscribeAuthor(String author) {
        authors.add(author);
    }

    public void unsubscribeAuthor(String author) {
        authors.remove(author);
    }

    @Override
    public void update(Article article) {
        if(categories.contains(article.getCategory()) || authors.contains(article.getAuthor())) {
            inbox.add(article);
        }
    }

    public void printNews() {
        inbox.stream().sorted(
                Comparator.comparing(Article::getTimestamp)
        ).forEach(a -> {
            System.out.println("["+a.getTimestamp()+"] " + a.getAuthor() + " - " + a.getCategory());
            System.out.println(a.getContent());
        });
    }
}

class NewsSystem implements Subject {
    private Set<String> categories;
    private Set<String> authors;
    private Map<String, User> users;
    private List<Observer> observers;
    public NewsSystem(List<String> categoryNames, List<String> authorNames) {
        this.categories = new HashSet<>(categoryNames);
        this.authors = new HashSet<>(authorNames);
        this.users = new HashMap<>();
        this.observers = new ArrayList<>();
    }

    public void addUser(String username) {
        User user = new User(username);
        users.put(username, user);
        registerObserver(user);
    }

    public void subscribeUserToCategory(String username, String categoryName) {
        if(categories.contains(categoryName)) {
            users.get(username).subscribeCategory(categoryName);
        }
    }

    public void unsubscribeUserFromCategory(String username, String categoryName) {
        users.get(username).unsubscribeCategory(categoryName);
    }

    public void subscribeUserToAuthor(String username, String authorName) {
        if(authors.contains(authorName))
            users.get(username).subscribeAuthor(authorName);
    }

    public void unsubscribeUserFromAuthor(String username, String authorName) {
        users.get(username).unsubscribeAuthor(authorName);
    }

    public void publishArticle(Article article) {
        notifyObservers(article);
    }

    public void printNewsForUser(String username) {
        System.out.println("News for user: " + username);
        users.get(username).printNews();
    }

    @Override
    public void registerObserver(Observer observer) {
        observers.add(observer);
    }

    @Override
    public void notifyObservers(Article article) {
        for(Observer observer: observers) {
            observer.update(article);
        }
    }
}