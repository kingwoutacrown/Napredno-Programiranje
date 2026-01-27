import java.util.*;
import java.util.stream.Collectors;

public class PhoneBookTest {

    public static void main(String[] args) {
        PhoneBook phoneBook = new PhoneBook();
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        scanner.nextLine();
        for (int i = 0; i < n; ++i) {
            String line = scanner.nextLine();
            String[] parts = line.split(":");
            try {
                phoneBook.addContact(parts[0], parts[1]);
            } catch (DuplicateNumberException e) {
                System.out.println(e.getMessage());
            }
        }
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            System.out.println(line);
            String[] parts = line.split(":");
            if (parts[0].equals("NUM")) {
                phoneBook.contactsByNumber(parts[1]);
            } else {
                phoneBook.contactsByName(parts[1]);
            }
        }
    }

}

// Вашиот код овде

class PhoneBook {
    Map<String,Contact> contactMap;
    public PhoneBook() {
        contactMap = new HashMap<>();
    }

    public void addContact(String name, String number) throws DuplicateNumberException {
        if(contactMap.containsKey(number)) {
            throw new DuplicateNumberException("Duplicate number: " + number);
        }
        else {
            contactMap.put(number, new Contact(name,number));
        }
    }

    public void contactsByNumber(String number) {
        List<Contact> newList = contactMap.values().stream().filter(c -> c.getNumber().contains(number))
                .sorted(
                        Comparator.comparing(
                                Contact::getName
                        ).thenComparing(
                                Contact::getNumber
                        )
                ).collect(Collectors.toCollection(ArrayList::new));
        if(newList.isEmpty()) {
            System.out.println("NOT FOUND");
        }
                newList.forEach(
                        System.out::println
                );
    }

    public void contactsByName(String name) {
        List<Contact> newList = contactMap.values().stream().filter(c -> c.getName().equalsIgnoreCase(name))
                .sorted(
                        Comparator.comparing(
                                Contact::getName
                        ).thenComparing(
                                Contact::getNumber
                        )
                ).collect(Collectors.toCollection(ArrayList::new));
        if(newList.isEmpty()) {
            System.out.println("NOT FOUND");
        }
                newList.forEach(
                        System.out::println
                );
    }
}

class Contact {
    private String name;
    private String number;
    public Contact(String name, String number) {
        this.name = name;
        this.number = number;
    }
    public String getName() {
        return this.name;
    }

    public String getNumber() {
        return this.number;
    }
    @Override
    public String toString() {
        return String.format("%s %s", name, number);
    }
}

class DuplicateNumberException extends Exception {
    public DuplicateNumberException(String message) {
        super(message);
    }
}