import java.util.*;

public class StaduimTest {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        scanner.nextLine();
        String[] sectorNames = new String[n];
        int[] sectorSizes = new int[n];
        String name = scanner.nextLine();
        for (int i = 0; i < n; ++i) {
            String line = scanner.nextLine();
            String[] parts = line.split(";");
            sectorNames[i] = parts[0];
            sectorSizes[i] = Integer.parseInt(parts[1]);
        }
        Stadium stadium = new Stadium(name);
        stadium.createSectors(sectorNames, sectorSizes);
        n = scanner.nextInt();
        scanner.nextLine();
        for (int i = 0; i < n; ++i) {
            String line = scanner.nextLine();
            String[] parts = line.split(";");
            try {
                stadium.buyTicket(parts[0], Integer.parseInt(parts[1]),
                        Integer.parseInt(parts[2]));
            } catch (SeatNotAllowedException e) {
                System.out.println("SeatNotAllowedException");
            } catch (SeatTakenException e) {
                System.out.println("SeatTakenException");
            }
        }
        stadium.showSectors();
    }
}

class Sector {
    private String name;
    private int numberOfSeats;
    private List<Integer> availabilityList;
    private int type;
    public Sector(String name, int numberOfSeats) {
        this.name = name;
        this.numberOfSeats = numberOfSeats;
        availabilityList = new ArrayList<>();
        type = -1;
    }

    public void addSeat(int seat, int type) throws SeatTakenException, SeatNotAllowedException {
        if(availabilityList.contains(seat)) {
            throw new SeatTakenException();
        }
        if (this.type == -1 && type != 0) {
            this.type = type;
        }
        if (this.type != -1 && type != 0 && type != this.type) {
            throw new SeatNotAllowedException();
        }
        availabilityList.add(seat);
    }


    public String getName() {
        return this.name;
    }

    public int getAvailableNumberOfSeats() {
        return numberOfSeats-availabilityList.size();
    }

    public double getFillPercentage() {
        return availabilityList.size()*100.0/numberOfSeats;
    }
    @Override
    public String toString() {
        return String.format("%s\t%d/%d\t%.1f%%", name,getAvailableNumberOfSeats(),numberOfSeats,getFillPercentage());
    }
}

class Stadium {
    private String name;
    Map<String,Sector> sectorMap;
    public Stadium(String name) {
        this.name = name;
        sectorMap = new HashMap<>();
    }
    public void createSectors(String[] sectorNames, int[] sizes) {
        for(int i=0;i<sectorNames.length;i++) {
            sectorMap.put(sectorNames[i], new Sector(sectorNames[i],sizes[i]));
        }
    }

    public void buyTicket(String sectorName, int seat, int type) throws SeatTakenException, SeatNotAllowedException {
        Sector sector = sectorMap.get(sectorName);
        sector.addSeat(seat,type);
    }

    public void showSectors() {
        sectorMap.values().stream().sorted(
                Comparator.comparing(
                        Sector::getAvailableNumberOfSeats,Comparator.reverseOrder()
                ).thenComparing(
                        Sector::getName
                )
        ).forEach(System.out::println);
    }
}

class SeatTakenException extends Exception {
    public SeatTakenException() {
        super();
    }
}

class SeatNotAllowedException extends Exception {
    public SeatNotAllowedException() {
        super();
    }
}