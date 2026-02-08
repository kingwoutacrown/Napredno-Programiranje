import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;
import java.time.LocalDateTime;

public class FrontPageTest {
    public static void main(String[] args) {
        // Reading
        Scanner scanner = new Scanner(System.in);
        String line = scanner.nextLine();
        String[] parts = line.split(" ");
        Category[] categories = new Category[parts.length];
        for (int i = 0; i < categories.length; ++i) {
            categories[i] = new Category(parts[i]);
        }
        int n = scanner.nextInt();
        scanner.nextLine();
        FrontPage frontPage = new FrontPage(categories);

        for (int i = 0; i < n; ++i) {
            String title = scanner.nextLine();
            int min = scanner.nextInt();
            scanner.nextLine();
            LocalDateTime date = LocalDateTime.now().minusMinutes(min);
            String text = scanner.nextLine();
            int categoryIndex = scanner.nextInt();
            scanner.nextLine();
            TextNewsItem tni = new TextNewsItem(title, date, categories[categoryIndex], text);
            frontPage.addNewsItem(tni);
        }

        n = scanner.nextInt();
        scanner.nextLine();
        for (int i = 0; i < n; ++i) {
            String title = scanner.nextLine();
            int min = scanner.nextInt();
            scanner.nextLine();
            LocalDateTime date = LocalDateTime.now().minusMinutes(min);
            String url = scanner.nextLine();
            int views = scanner.nextInt();
            scanner.nextLine();
            int categoryIndex = scanner.nextInt();
            scanner.nextLine();
            MediaNewsItem mni = new MediaNewsItem(title, date, categories[categoryIndex], url, views);
            frontPage.addNewsItem(mni);
        }
        // Execution
        String category = scanner.nextLine();
        System.out.println(frontPage);
        for(Category c : categories) {
            System.out.println(frontPage.listByCategory(c).size());
        }
        try {
            System.out.println(frontPage.listByCategoryName(category).size());
        } catch(CategoryNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }
}

// Vasiot kod ovde

abstract class NewsItem {
    private String title;
    private LocalDateTime dateOfPublishing;
    private Category category;
    public NewsItem(String title, LocalDateTime dateOfPublishing, Category category) {
        this.title = title;
        this.dateOfPublishing = dateOfPublishing;
        this.category = category;
    }
    public String getTitle() { return this.title; }
    public LocalDateTime getDateOfPublishing() { return this.dateOfPublishing; }
    public Category getCategory() { return this.category; }
    abstract String getTeaser();
    public int getMinutes() {
        return (int) Duration.between(getDateOfPublishing(),LocalDateTime.now()).toMinutes();
    }
}

class Category implements Comparable<Category> {
    private String name;
    public Category(String name) {
        this.name = name;
    }
    public String getName() { return this.name; }

    @Override
    public int compareTo(Category o) {
        return this.name.compareTo(o.getName());
    }
}

class TextNewsItem extends NewsItem {
    private String text;
    public TextNewsItem(String title, LocalDateTime dateOfPublishing, Category category, String text) {
        super(title,dateOfPublishing,category);
        this.text = text;
    }
    public String getText() {
        if(this.text.length()<=80) {
            return this.text;
        } else {
            return this.text.substring(0,80);
        }
    }

    @Override
    String getTeaser() {
        return String.format("%s\n%d\n%s", getTitle(), getMinutes(), getText());
    }
}

class MediaNewsItem extends NewsItem {
    private String url;
    private int viewedTimes;
    public MediaNewsItem(String title, LocalDateTime dateOfPublishing, Category category, String url, int viewedTimes) {
        super(title,dateOfPublishing,category);
        this.url = url;
        this.viewedTimes = viewedTimes;
    }
    public String getUrl() { return this.url; }
    public int getViewedTimes() { return this.viewedTimes; }

    @Override
    String getTeaser() {
        return String.format("%s\n%d\n%s\n%d", getTitle(), getMinutes(), getUrl(), getViewedTimes());
    }
}

class FrontPage {
    private List<NewsItem> newsItemList;
    private Map<String,Category> categoriesMap;
    public FrontPage(Category[] categories) {
        categoriesMap = new HashMap<>();
        Arrays.stream(categories).forEach(c -> {
            categoriesMap.put(c.getName(),c);
        });
        newsItemList = new ArrayList<>();
    }
    public void addNewsItem(NewsItem newsItem) {
        newsItemList.add(newsItem);
    }
    public List<NewsItem> listByCategory(Category category) {
        return newsItemList.stream().filter(n -> n.getCategory().compareTo(category)==0)
                .collect(Collectors.toList());
    }

    public List<NewsItem> listByCategoryName(String category) throws CategoryNotFoundException {
        if(!categoriesMap.containsKey(category)) {
            throw new CategoryNotFoundException(category);
        }
        Category category1 = categoriesMap.get(category);
        return newsItemList.stream().filter(n -> n.getCategory().compareTo(category1) == 0)
                .collect(Collectors.toList());
    }
    @Override
    public String toString() {
        return newsItemList.stream().map(NewsItem::getTeaser).collect(Collectors.joining("\n")) + "\n";
    }
}

class CategoryNotFoundException extends Exception {
    public CategoryNotFoundException(String message) {
        super(String.format("Category %s was not found",message));
    }
}
