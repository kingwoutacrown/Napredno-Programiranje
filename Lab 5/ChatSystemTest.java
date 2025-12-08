import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.Scanner;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

public class ChatSystemTest {

    public static void main(String[] args) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, NoSuchRoomException {
        Scanner jin = new Scanner(System.in);
        int k = jin.nextInt();
        if ( k == 0 ) {
            ChatRoom cr = new ChatRoom(jin.next());
            int n = jin.nextInt();
            for ( int i = 0 ; i < n ; ++i ) {
                k = jin.nextInt();
                if ( k == 0 ) cr.addUser(jin.next());
                if ( k == 1 ) cr.removeUser(jin.next());
                if ( k == 2 ) System.out.println(cr.hasUser(jin.next()));
            }
            //System.out.println("");
            System.out.println(cr.toString());
            n = jin.nextInt();
            if ( n == 0 ) return;
            ChatRoom cr2 = new ChatRoom(jin.next());
            for ( int i = 0 ; i < n ; ++i ) {
                k = jin.nextInt();
                if ( k == 0 ) cr2.addUser(jin.next());
                if ( k == 1 ) cr2.removeUser(jin.next());
                if ( k == 2 ) cr2.hasUser(jin.next());
            }
            System.out.println(cr2.toString());
        }
        if ( k == 1 ) {
            ChatSystem cs = new ChatSystem();
            Method mts[] = cs.getClass().getMethods();
            while ( true ) {
                String cmd = jin.next();
                if ( cmd.equals("stop") ) break;
                if ( cmd.equals("print") ) {
                    System.out.println(cs.getRoom(jin.next())+"\n");continue;
                }
                for ( Method m : mts ) {
                    if ( m.getName().equals(cmd) ) {
                        String params[] = new String[m.getParameterTypes().length];
                        for ( int i = 0 ; i < params.length ; ++i ) params[i] = jin.next();
                        m.invoke(cs,(Object[]) params);
                    }
                }
            }
        }
    }
}
class ChatRoom {
    private String name;
    private Set<String> users;
    ChatRoom(String name) {
        this.name = name;
        this.users = new TreeSet<>();
    }
    String getName() {
        return this.name;
    }
    Set<String> getUsers() {
        return this.users;
    }
    void addUser(String user) {
        this.users.add(user);
    }
    void removeUser(String user) {
        if(this.users.contains(user)) this.users.remove(user);
    }
    boolean hasUser(String user) {
        return this.users.contains(user);
    }
    int numUsers() {
        return this.users.size();
    }
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.name).append("\n");
        if( users.isEmpty() ) return sb.append("EMPTY").toString();
        for ( String user : users ) {
            sb.append(user).append("\n");
        }
        return sb.toString();
    }
}
class ChatSystem {
    private Map<String, ChatRoom> rooms;
    private Set<String> users;
    public ChatSystem() {
        this.rooms = new TreeMap<>();
        this.users = new TreeSet<>();
    }
    void addRoom(String roomName) {
        ChatRoom cr = new ChatRoom(roomName);
        rooms.put(roomName, cr);
    }
    void removeRoom(String roomName) {
        rooms.remove(roomName);
    }
    ChatRoom getRoom(String roomName) throws NoSuchRoomException {
        if( rooms.containsKey(roomName) ) {
            return rooms.get(roomName);
        }
        else {
            throw new NoSuchRoomException(roomName);
        }
    }
    void register(String user) {
        users.add(user);
        Optional<ChatRoom> optional = rooms.values()
                .stream()
                .min(Comparator.comparing(ChatRoom::numUsers)
                        .thenComparing(ChatRoom::getName));
        optional.ifPresent(cr -> cr.addUser(user));
    }
    void registerAndJoin(String user, String roomName) throws NoSuchRoomException {
        if( !rooms.containsKey(roomName) ) {
            throw new NoSuchRoomException(roomName);
        }
        users.add(user);
        ChatRoom cr = rooms.get(roomName);
        cr.addUser(user);
    }
    void joinRoom(String user, String roomName) throws NoSuchUserException, NoSuchRoomException {
        if( rooms.containsKey(roomName) ) {
            if( users.contains(user) ) {
                rooms.get(roomName).addUser(user);
            }
            else {
                throw new NoSuchUserException(user);
            }
        }
        else {
            throw new NoSuchRoomException(roomName);
        }
    }
    void leaveRoom(String user, String roomName) throws NoSuchUserException, NoSuchRoomException {
        if( rooms.containsKey(roomName) ) {
            if( users.contains(user) ) {
                rooms.get(roomName).removeUser(user);
            }
            else {
                throw new NoSuchUserException(user);
            }
        }
        else {
            throw new NoSuchRoomException(roomName);
        }
    }
    void followFriend(String username, String friend_username) throws NoSuchUserException{
        if(!users.contains(friend_username)) {
            throw new NoSuchUserException(friend_username);
        }
        if(!users.contains(username)) {
            throw new NoSuchUserException(username);
        }
        rooms.values().stream().filter(room -> room.hasUser(friend_username)).forEach(room -> {
            room.addUser(username);
        });
    }
    Map<String, Set<String>> getAllRoomsByUser() {
        return rooms.values().stream().flatMap( c -> c.getUsers().stream().map(v -> Map.entry(v,c.getName())))
                .collect(Collectors.groupingBy(
                        Map.Entry::getKey,
                        Collectors.mapping(Map.Entry::getValue, Collectors.toSet())
                ));
//        Map<String, Set<String>> map = new HashMap<>();
//        for(ChatRoom cr : rooms.values()) {
//            for(String user : cr.getUsers()) {
//                map.computeIfAbsent(user, k -> new HashSet<>()).add(cr.getName());
//            }
//        }
//        return map;
    }
    Map<ChatRoom, Integer> getChatRoomStatistics() {
        return rooms.values().stream().collect(
                Collectors.toMap(
                        Function.identity(),
                        ChatRoom::numUsers,
                        Integer::sum,
                        () -> new TreeMap<>(Comparator.comparing(ChatRoom::getName))
                )
        );
    }
//    Map<String, Set<String>> getAllRoomsByUser() {
//        return rooms.values().stream().flatMap(room -> room.getUsers().stream()
//                .map( r -> Map.entry(r, room.getName())))
//                .collect(Collectors.groupingBy(
//                        Map.Entry::getKey,
//                        Collectors.mapping(Map.Entry::getValue, Collectors.toSet()))
//                );
//    }

}
class NoSuchRoomException extends Exception {
    public NoSuchRoomException(String roomName) {
        super(roomName);
    }
}
class NoSuchUserException extends Exception {
    public NoSuchUserException(String user) {
        super(user);
    }
}