import java.util.ArrayList;
import java.util.List;

class Song {
    private String title;
    private String artist;
    public Song(String title,String artist) {
        this.title=title;
        this.artist=artist;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }
    @Override
    public String toString() {
        return "Song{title="+title+", artist="+artist+"}";
    }
}

interface IPlayerState {
    public void pressPlay();
    public void pressStop();
    public void pressFWD();
    public void pressREW();
}
class MP3Player {
    private List<Song> songs;
    private int currentSong;
    private IPlayerState playingState;
    private IPlayerState pausedState;
    private IPlayerState stoppedState;
    private IPlayerState state;
    public MP3Player(List<Song> songs) {
        this.songs=songs;
        this.currentSong= 0;
        this.playingState = new PlayingState(this);
        this.pausedState = new PausedState(this);
        this.stoppedState = new StoppedState(this);
        this.state = stoppedState;
    }

    public void pressPlay() {
        state.pressPlay();
    }

    public void pressStop() {
        state.pressStop();
    }

    public void pressFWD() {
        state.pressFWD();
    }

    public void pressREW() {
        state.pressREW();
    }

    void nextSong() {
        currentSong = (currentSong + 1) % songs.size();
    }

    void previousSong() {
        currentSong = (currentSong -1 + songs.size()) % songs.size();
    }

    void reset() {
        currentSong =0;
    }

    void printCurrentSong() {
        System.out.println(songs.get(currentSong));
    }

    public int getCurrentSongIndex() {
        return currentSong;
    }

    public void setState(IPlayerState state) {
        this.state = state;
    }

    public IPlayerState getPlayingState() {
        return this.playingState;
    }

    public IPlayerState getStoppedState() {
        return this.stoppedState;
    }

    public IPlayerState getPausedState() {
        return this.pausedState;
    }

    @Override
    public String toString() {
        return "MP3Player{currentSong = " + currentSong + ", songList = " + songs + "}";
    }
}

class PlayingState implements IPlayerState {
    private MP3Player player;
    public PlayingState(MP3Player player) {
        this.player = player;
    }

    @Override
    public void pressPlay() {
        System.out.println("Song is already playing");
    }

    @Override
    public void pressStop() {
        System.out.println("Song " + player.getCurrentSongIndex() + " is paused");
        player.setState(player.getPausedState());
    }

    @Override
    public void pressFWD() {
        System.out.println("Forward...");
        player.nextSong();
        player.setState(player.getPausedState());
    }

    @Override
    public void pressREW() {
        System.out.println("Reward...");
        player.previousSong();
        player.setState(player.getPausedState());
    }
}

class PausedState implements IPlayerState {

    private MP3Player player;

    public PausedState(MP3Player player) {
        this.player = player;
    }

    @Override
    public void pressPlay() {
        System.out.println("Song " + player.getCurrentSongIndex() + " is playing");
        player.setState(player.getPlayingState());
    }

    @Override
    public void pressStop() {
        System.out.println("Songs are stopped");
        player.reset();
        player.setState(player.getStoppedState());
    }

    @Override
    public void pressFWD() {
        System.out.println("Forward...");
        player.nextSong();
    }

    @Override
    public void pressREW() {
        System.out.println("Reward...");
        player.previousSong();
    }
}

class StoppedState implements IPlayerState {
    private MP3Player player;
    public StoppedState(MP3Player player) {
        this.player=player;
    }

    @Override
    public void pressPlay() {
        System.out.println("Song " + player.getCurrentSongIndex() + " is playing");
        player.setState(player.getPlayingState());
    }

    @Override
    public void pressStop() {
        System.out.println("Songs are already stopped");
    }

    @Override
    public void pressFWD() {
        System.out.println("Forward...");
        player.nextSong();
    }

    @Override
    public void pressREW() {
        System.out.println("Reward...");
        player.previousSong();
    }


}

public class PatternTest {
    public static void main(String args[]) {
        List<Song> listSongs = new ArrayList<Song>();
        listSongs.add(new Song("first-title", "first-artist"));
        listSongs.add(new Song("second-title", "second-artist"));
        listSongs.add(new Song("third-title", "third-artist"));
        listSongs.add(new Song("fourth-title", "fourth-artist"));
        listSongs.add(new Song("fifth-title", "fifth-artist"));
        MP3Player player = new MP3Player(listSongs);


        System.out.println(player.toString());
        System.out.println("First test");


        player.pressPlay();
        player.printCurrentSong();
        player.pressPlay();
        player.printCurrentSong();

        player.pressPlay();
        player.printCurrentSong();
        player.pressStop();
        player.printCurrentSong();

        player.pressPlay();
        player.printCurrentSong();
        player.pressFWD();
        player.printCurrentSong();

        player.pressPlay();
        player.printCurrentSong();
        player.pressREW();
        player.printCurrentSong();


        System.out.println(player.toString());
        System.out.println("Second test");


        player.pressStop();
        player.printCurrentSong();
        player.pressStop();
        player.printCurrentSong();

        player.pressStop();
        player.printCurrentSong();
        player.pressPlay();
        player.printCurrentSong();

        player.pressStop();
        player.printCurrentSong();
        player.pressFWD();
        player.printCurrentSong();

        player.pressStop();
        player.printCurrentSong();
        player.pressREW();
        player.printCurrentSong();


        System.out.println(player.toString());
        System.out.println("Third test");


        player.pressFWD();
        player.printCurrentSong();
        player.pressFWD();
        player.printCurrentSong();

        player.pressFWD();
        player.printCurrentSong();
        player.pressPlay();
        player.printCurrentSong();

        player.pressFWD();
        player.printCurrentSong();
        player.pressStop();
        player.printCurrentSong();

        player.pressFWD();
        player.printCurrentSong();
        player.pressREW();
        player.printCurrentSong();


        System.out.println(player.toString());
    }
}

//Vasiot kod ovde