import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Partial exam II 2016/2017
 */

class FootballTable {
    private Map<String, Team> teamMap;
    public FootballTable() {
        teamMap = new HashMap<>();
    }
    public void addGame(String homeTeam, String awayTeam, int homeGoals, int awayGoals) {
        teamMap.computeIfAbsent(homeTeam, Team::new);
        teamMap.computeIfAbsent(awayTeam, Team::new);
        teamMap.get(homeTeam).matchResults(homeGoals,awayGoals);
        teamMap.get(awayTeam).matchResults(awayGoals,homeGoals);
    }
    public void printTable() {
        //"%-19s%5s%5s%5s%5s%5s\n", "Team", "P", "W", "D", "L", "PTS"
        int counter =1;
        StringBuilder sb = new StringBuilder();
        ArrayList<Team> sorted = teamMap.values().stream().sorted(new TeamRankingComparator())
                .collect(Collectors.toCollection(ArrayList::new));
        for(Team t : sorted) {
            sb.append(String.format("%2d. %-15s%5s%5s%5s%5s%5s\n", counter++, t.getName(), t.getTotalMatches(), t.getTotalWins(), t.getTotalDraws(), t.getTotalLosses(), t.getTotalScore()));
        }
        System.out.println(sb);
    }

}

class Team {
    private String name;
    private int totalGoalsScored;
    private int totalGoalsReceived;
    private int totalWins;
    private int totalDraws;
    private int totalLosses;
    public Team(String name) {
        this.name = name;
        totalGoalsScored = 0;
        totalWins = 0;
        totalDraws = 0;
        totalLosses = 0;
    }
    public String getName() { return name;}
    public void addGoals(int goals, int received) {
        totalGoalsScored+= goals;
        totalGoalsReceived+= received;
    }
    public void addWins() {
        totalWins+=1;
    }
    public void addDraws() {
        totalDraws+=1;
    }
    public void addLosses() {
        totalLosses+=1;
    }
    public int getTotalScore() {
        return totalWins*3 + totalDraws;
    }

    public int getTotalWins() {
        return totalWins;
    }

    public int getTotalDraws() {
        return totalDraws;
    }

    public int getTotalLosses() {
        return totalLosses;
    }
    public int getTotalMatches() {
        return totalWins + totalLosses + totalDraws;
    }
    public int getGoalDifference() {
        return totalGoalsScored - totalGoalsReceived;
    }
    public void matchResults(int homeGoals, int awayGoals) {
        addGoals(homeGoals,awayGoals);
        if(homeGoals>awayGoals)
            addWins();
        else if(homeGoals<awayGoals)
            addLosses();
        else
            addDraws();
    }
}

class TeamRankingComparator implements Comparator<Team> {
    @Override
    public int compare(Team a, Team b) {
        return Comparator.comparing(Team::getTotalScore, Comparator.reverseOrder()).thenComparing(Team::getGoalDifference,Comparator.reverseOrder())
                .thenComparing(Team::getName).compare(a,b);
    }
}

public class FootballTableTest {
    public static void main(String[] args) throws IOException {
        FootballTable table = new FootballTable();
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        reader.lines()
                .map(line -> line.split(";"))
                .forEach(parts -> table.addGame(parts[0], parts[1],
                        Integer.parseInt(parts[2]),
                        Integer.parseInt(parts[3])));
        reader.close();
        System.out.println("=== TABLE ===");
        System.out.printf("%-19s%5s%5s%5s%5s%5s\n", "Team", "P", "W", "D", "L", "PTS");
        table.printTable();
    }
}

// Your code here

