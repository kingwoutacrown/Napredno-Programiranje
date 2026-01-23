import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.stream.Collectors;

abstract class Archive {
    private int id;
    private LocalDate dateArchived;
    public Archive(int id) {
        this.id = id;
    }
    public void setDateArchived(LocalDate dateArchived) {
        this.dateArchived = dateArchived;
    }
    public int getId() { return this.id; }
    public LocalDate getDateArchived() { return this.dateArchived; }
}

class LockedArchive extends Archive {
    private LocalDate dateToOpen;
    public LockedArchive(int id, LocalDate dateToOpen) {
        super(id);
        this.dateToOpen = dateToOpen;
    }
    public LocalDate getDateToOpen() { return this.dateToOpen; }
}

class SpecialArchive extends Archive {
    private int opened;
    private int maxOpen;
    public SpecialArchive(int id, int maxOpen) {
        super(id);
        opened = 0;
        this.maxOpen = maxOpen;
    }
    public int getMaxOpen() { return this.maxOpen; }
    public boolean open() {
        if(opened >= maxOpen) {
            return false;
        }
        else {
            opened++;
            return true;
        }
    }
}

class ArchiveStore {
    Map<Integer, Archive> archiveMap;
    List<String> logs;
    public ArchiveStore() {
        archiveMap = new HashMap<>();
        logs = new ArrayList<>();
    }
    public void archiveItem(Archive item, LocalDate date) {
        item.setDateArchived(date);
        archiveMap.put(item.getId(),item);
        logs.add(String.format("Item %d archived at %s\n", item.getId(),date.atStartOfDay(ZoneId.of("UTC"))
                .format(DateTimeFormatter.ofPattern(
                        "EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH))));
    }
    public void openItem(int id, LocalDate date) throws NonExistingItemException {
        Archive archive = archiveMap.get(id);
        if(archive == null) {
            throw new NonExistingItemException(String.format("Item with id %d doesn't exist",id));
        }
        if(archive instanceof LockedArchive) {
            LockedArchive locked = (LockedArchive) archive;
            if(date.compareTo(locked.getDateToOpen()) < 0) {
                logs.add(String.format("Item %d cannot be opened before %s\n",id, locked.getDateToOpen()
                        .atStartOfDay(ZoneId.of("UTC"))
                        .format(DateTimeFormatter.ofPattern(
                                "EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH))));
            }
            else {
                logs.add(String.format("Item %d opened at %s\n", id, date
                        .atStartOfDay(ZoneId.of("UTC"))
                        .format(DateTimeFormatter.ofPattern(
                                "EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH))));
            }
        }
        if(archive instanceof SpecialArchive) {
            SpecialArchive special = (SpecialArchive) archive;
            if(!special.open()) {
                logs.add(String.format("Item %d cannot be opened more than %d times\n",special.getId(),special.getMaxOpen()));
            }
            else {
                logs.add(String.format("Item %d opened at %s\n", id, date
                        .atStartOfDay(ZoneId.of("UTC"))
                        .format(DateTimeFormatter.ofPattern(
                                "EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH))));
            }
        }
    }
    public String getLog() {
        return String.join("", logs);
    }
}

class NonExistingItemException extends Exception {
    public NonExistingItemException(String message) {
        super(message);
    }
}

public class ArchiveStoreTest {
    public static void main(String[] args) {
        ArchiveStore store = new ArchiveStore();
        LocalDate date = LocalDate.of(2013,11,7);
        Scanner scanner = new Scanner(System.in);
        scanner.nextLine();
        int n = scanner.nextInt();
        scanner.nextLine();
        scanner.nextLine();
        int i;
        for (i = 0; i < n; ++i) {
            int id = scanner.nextInt();
            long days = scanner.nextLong();
            LocalDate dateToOpen = date.plusDays(days);
            LockedArchive lockedArchive = new LockedArchive(id, dateToOpen);
            store.archiveItem(lockedArchive, date);
        }
        scanner.nextLine();
        scanner.nextLine();
        n = scanner.nextInt();
        scanner.nextLine();
        scanner.nextLine();
        for (i = 0; i < n; ++i) {
            int id = scanner.nextInt();
            int maxOpen = scanner.nextInt();
            SpecialArchive specialArchive = new SpecialArchive(id, maxOpen);
            store.archiveItem(specialArchive, date);
        }
        scanner.nextLine();
        scanner.nextLine();
        while(scanner.hasNext()) {
            int open = scanner.nextInt();
            try {
                store.openItem(open, date);
            } catch(NonExistingItemException e) {
                System.out.println(e.getMessage());
            }
        }
        System.out.println(store.getLog());
    }
}

// вашиот код овде


