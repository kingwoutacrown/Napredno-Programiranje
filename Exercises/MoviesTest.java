import java.util.*;
import java.util.stream.Collectors;

public class MoviesTest {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        MoviesList moviesList = new MoviesList();
        int n = scanner.nextInt();
        scanner.nextLine();
        for (int i = 0; i < n; ++i) {
            String title = scanner.nextLine();
            int x = scanner.nextInt();
            int[] ratings = new int[x];
            for (int j = 0; j < x; ++j) {
                ratings[j] = scanner.nextInt();
            }
            scanner.nextLine();
            moviesList.addMovie(title, ratings);
        }
        scanner.close();
        List<Movie> movies = moviesList.top10ByAvgRating();
        System.out.println("=== TOP 10 BY AVERAGE RATING ===");
        for (Movie movie : movies) {
            System.out.println(movie);
        }
        movies = moviesList.top10ByRatingCoef();
        System.out.println("=== TOP 10 BY RATING COEFFICIENT ===");
        for (Movie movie : movies) {
            System.out.println(movie);
        }
    }
}

// vashiot kod ovde
class Movie {
    private String title;
    private List<Integer> ratings;
    public Movie(String title, int[] ratings) {
        this.title = title;
        this.ratings = Arrays.stream(ratings).boxed().collect(Collectors.toCollection(ArrayList::new));
    }
    public String getTitle() { return this.title; }
    public List<Integer> getRatings() { return this.ratings; }
    @Override
    public String toString() {
        return String.format("%s (%.2f) of %d ratings",title, getAvgRating(), getCount());
    }
    public Double getAvgRating() {
        return ratings.stream().mapToInt(Integer::intValue).average().orElse(0.0);
    }
    public int getCount() {
        return ratings.size();
    }

}

class MoviesList {
    List<Movie> movies;
    public MoviesList() {
        movies = new ArrayList<>();
    }
    public int getMax() {
        return movies.stream().mapToInt(Movie::getCount).max().orElse(0);
    }
    public void addMovie(String title, int[] ratings) {
        movies.add(new Movie(title, ratings));
    }
    public List<Movie> top10ByAvgRating() {
        return movies.stream().sorted(
                Comparator.comparing(
                        Movie::getAvgRating, Comparator.reverseOrder()
                ).thenComparing(Movie::getTitle)
        ).limit(10).collect(Collectors.toList());
    }
    public List<Movie> top10ByRatingCoef() {
        return movies.stream().sorted(
                Comparator.comparing(
                        (Movie m) -> ((m.getAvgRating()*m.getCount())/getMax()), Comparator.reverseOrder()

                )
        ).limit(10).collect(Collectors.toList());
    }
}