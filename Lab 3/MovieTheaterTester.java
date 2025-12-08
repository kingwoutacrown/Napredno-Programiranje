import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

import static java.lang.Integer.parseInt;


public class MovieTheaterTester {
    public static void main(String[] args) {
        MovieTheater mt = new MovieTheater();
        try {
            mt.readMovies(System.in);
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return;
        }
        System.out.println("SORTING BY RATING");
        mt.printByRatingAndTitle();
        System.out.println("\nSORTING BY GENRE");
        mt.printByGenreAndTitle();
        System.out.println("\nSORTING BY YEAR");
        mt.printByYearAndTitle();
    }
}
class Movie {
    private String title;
    private String genre;
    private int year;
    private double avgRating;

    public Movie(String title, String genre, int year, double avgRating) {
        this.title = title;
        this.genre = genre;
        this.year = year;
        this.avgRating = avgRating;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public double getAvgRating() {
        return avgRating;
    }

    public void setAvgRating(double avgRating) {
        this.avgRating = avgRating;
    }

    @Override
    public String toString() {
        return String.format("%s, %s, %d, %.2f", title, genre, year, avgRating);
    }
}
class MovieTheater {
    private ArrayList<Movie> movies;
    private Map<String, Map<String, Double>> userRatings = new HashMap<>();
    public void readMovies(InputStream in) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String line;
        line = reader.readLine();
        int n = parseInt(line);
        movies = new ArrayList<>(n);
        for(int i = 0; i < n; i++) {
            line = reader.readLine();
            String title = line;
            line = reader.readLine();
            String genre = line;
            line = reader.readLine();
            int year = parseInt(line);
            line = reader.readLine();
            String[] line2 = line.split(" ");
            int sum = 0;
            for (String s : line2) {
                sum += parseInt(s);
            }
            double avg = (double) sum /line2.length;
            Movie m = new Movie(title, genre, year, avg);
            movies.add(m);
        }
    }
    public void printByGenreAndTitle() {
        movies.stream().sorted(Comparator.comparing(Movie::getTitle)).sorted(Comparator.comparing(Movie::getGenre)).forEach(System.out::println);
    }
    public void printByYearAndTitle() {
        movies.stream().sorted(Comparator.comparing(Movie::getTitle)).sorted(Comparator.comparing(Movie::getYear)).forEach(System.out::println);
    }
    public void printByRatingAndTitle() {
        movies.stream().sorted(Comparator.comparing(Movie::getTitle)).sorted(Comparator.comparing(Movie::getAvgRating).reversed()).forEach(System.out::println);
    }
    public Map<String,Movie> bestMovieByGenre() {
        Map<String,Movie> bestMovie;
        /*movies.stream().
                sorted(Comparator.comparing(Movie::getGenre).reversed()).
                sorted((Comparator.comparing(Movie::getAvgRating))).limit(1)
                .map(Map.of())
                */
        bestMovie = new HashMap<>();
        bestMovie.put(movies.get(0).getGenre(),movies.get(0));
        for (Movie movie : movies) {
            if (bestMovie.containsKey(movie.getGenre())) {
                if (bestMovie.get(movie.getGenre()).getAvgRating() < movie.getAvgRating()) {
                    bestMovie.replace(movie.getGenre(), movie);
                }
            } else {
                bestMovie.put(movie.getGenre(), movie);
            }
        }
        return bestMovie;
    }
    public void printBestMovieByGenre() {

        Map<String,Movie> bestMovie = bestMovieByGenre();
        bestMovie.entrySet().stream().sorted(Map.Entry.comparingByKey()).forEach(movie -> System.out.println(movie.getKey() + ":" + movie.getValue()));

    }
    public void addUserRating(String movieTitle, String userId, double rating) {
        Map<String, Double> usermap = new HashMap<>();
        usermap.put(userId,rating);
        if(userRatings.get(movieTitle).isEmpty())
        {
            userRatings.put(movieTitle,usermap);
        }
        else {
            userRatings.replace(movieTitle,usermap);
        }
    }
}