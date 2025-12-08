package Lab2;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.Comparator;
import java.util.List;

class Timestamp<T> implements Comparable<Timestamp<T>>{
    final private LocalDateTime time;
    final private T element;
    public Timestamp(LocalDateTime time,T element) {
        this.time = time;
        this.element = element;
    }
    LocalDateTime getTime() {
        return this.time;
    }
    T getElement() {
        return this.element;
    }
    public boolean equals(Object o) {
        if(o == this) {
            return true;
        }
        if(!( o instanceof Timestamp<?>)) {
            return false;
        }
        Timestamp<?> t = (Timestamp<?>) o;
        return this.getTime().equals(t.getTime());
    }
    @Override
    public String toString() {
        return time.toString() +" "+ element.toString();
    }
    public int hashCode() {
        return time.hashCode();
    }

    @Override
    public int compareTo(Timestamp<T> o) {
        return this.time.compareTo(o.time);
    }
}
class Scheduler<T> {
    ArrayList<Timestamp<T>> arrayList;
    T element;
    public Scheduler() {
        arrayList = new ArrayList<>();
    }
    public void add(Timestamp<T> timestamp) {
        arrayList.add(timestamp);
    }
    boolean remove(Timestamp<T> timestamp) {
        return arrayList.remove(timestamp);
    }
    Timestamp<T> next() {
        if(arrayList.isEmpty()) {
            return null;
        }
        LocalDateTime now = LocalDateTime.now();
        ArrayList<Timestamp<T>> arrayList2 = new ArrayList<>();
        for(int i = 0; i < arrayList.size(); i++) {
            if(arrayList.get(i).getTime().isAfter(now)) {
                arrayList2.add(arrayList.get(i));
            }
        }
        if(arrayList2.isEmpty()) {
            return null;
        }
        Timestamp<T> time2 = arrayList2.get(0);
        for(int i=1; i < arrayList2.size(); i++) {
            if(arrayList2.get(i).getTime().isBefore(time2.getTime())) {
                time2 = arrayList2.get(i);
            }
        }
        return time2;
    }
    Timestamp<T> last() {
        if(arrayList.isEmpty()) {
            return null;
        }
        LocalDateTime now = LocalDateTime.now();
        ArrayList<Timestamp<T>> arrayList2 = new ArrayList<>();
        for(int i = 0; i < arrayList.size(); i++) {
            if(arrayList.get(i).getTime().isBefore(now)) {
                arrayList2.add(arrayList.get(i));
            }
        }
        if(arrayList2.isEmpty()) {
            return null;
        }
        Timestamp<T> time2 = arrayList2.get(0);
        for(int i=1; i < arrayList2.size(); i++) {
            if(arrayList2.get(i).getTime().isAfter(time2.getTime())) {
                time2 = arrayList2.get(i);
            }
        }
        return time2;
    }
    List<Timestamp<T>> getAll(LocalDateTime begin, LocalDateTime end) {
        List<Timestamp<T>> list = new ArrayList<>();
        for (Timestamp<T> tTimestamp : arrayList) {
            if (tTimestamp.getTime().isAfter(begin) && tTimestamp.getTime().isBefore(end)) {
                list.add(tTimestamp);
            }
        }
        return list;
    }
}
public class SchedulerTest {

    static final LocalDateTime TIME = LocalDateTime.of(2016, 10, 25, 10, 15);

    public static void main(String[] args) {
        Scanner jin = new Scanner(System.in);
        int k = jin.nextInt();
        if (k == 0) { //test Timestamp with String
            Timestamp<String> t = new Timestamp<>(TIME, jin.next());
            System.out.println(t);
            System.out.println(t.getTime());
            System.out.println(t.getElement());
        }
        if (k == 1) { //test Timestamp with ints
            Timestamp<Integer> t1 = new Timestamp<>(TIME, jin.nextInt());
            System.out.println(t1);
            System.out.println(t1.getTime());
            System.out.println(t1.getElement());
            Timestamp<Integer> t2 = new Timestamp<>(TIME.plusDays(10), jin.nextInt());
            System.out.println(t2);
            System.out.println(t2.getTime());
            System.out.println(t2.getElement());
            System.out.println(t1.compareTo(t2));
            System.out.println(t2.compareTo(t1));
            System.out.println(t1.equals(t2));
            System.out.println(t2.equals(t1));
        }
        if (k == 2) {//test Timestamp with String, complex
            Timestamp<String> t1 = new Timestamp<>(ofEpochMS(jin.nextLong()), jin.next());
            System.out.println(t1);
            System.out.println(t1.getTime());
            System.out.println(t1.getElement());
            Timestamp<String> t2 = new Timestamp<>(ofEpochMS(jin.nextLong()), jin.next());
            System.out.println(t2);
            System.out.println(t2.getTime());
            System.out.println(t2.getElement());
            System.out.println(t1.compareTo(t2));
            System.out.println(t2.compareTo(t1));
            System.out.println(t1.equals(t2));
            System.out.println(t2.equals(t1));
        }
        if (k == 3) { //test Scheduler with String
            Scheduler<String> scheduler = new Scheduler<>();
            LocalDateTime now = LocalDateTime.now();
            scheduler.add(new Timestamp<>(now.minusHours(2), jin.next()));
            scheduler.add(new Timestamp<>(now.minusHours(1), jin.next()));
            scheduler.add(new Timestamp<>(now.minusHours(4), jin.next()));
            scheduler.add(new Timestamp<>(now.plusHours(2), jin.next()));
            scheduler.add(new Timestamp<>(now.plusHours(4), jin.next()));
            scheduler.add(new Timestamp<>(now.plusHours(1), jin.next()));
            scheduler.add(new Timestamp<>(now.plusHours(5), jin.next()));
            System.out.println(scheduler.next().getElement());
            System.out.println(scheduler.last().getElement());
            List<Timestamp<String>> result = scheduler.getAll(now.minusHours(3), now.plusHours(4).plusMinutes(15));
            String out = result.stream()
                    .sorted()
                    .map(Timestamp::getElement)
                    .collect(Collectors.joining(", "));
            System.out.println(out);
        }
        if (k == 4) {//test Scheduler with ints complex
            Scheduler<Integer> scheduler = new Scheduler<>();
            int counter = 0;
            ArrayList<Timestamp<Integer>> forRemoval = new ArrayList<>();
            while (jin.hasNextLong()) {
                Timestamp<Integer> ti = new Timestamp<>(ofEpochMS(jin.nextLong()), jin.nextInt());
                if ((counter & 7) == 0) {
                    forRemoval.add(ti);
                }
                scheduler.add(ti);
                ++counter;
            }
            jin.next();

            while (jin.hasNextLong()) {
                LocalDateTime left = ofEpochMS(jin.nextLong());
                LocalDateTime right = ofEpochMS(jin.nextLong());
                List<Timestamp<Integer>> res = scheduler.getAll(left, right);
                Collections.sort(res);
                System.out.println(left + " <: " + print(res) + " >: " + right);
            }
            System.out.println("test");
            List<Timestamp<Integer>> res = scheduler.getAll(ofEpochMS(0), ofEpochMS(Long.MAX_VALUE));
            Collections.sort(res);
            System.out.println(print(res));
            forRemoval.forEach(scheduler::remove);
            res = scheduler.getAll(ofEpochMS(0), ofEpochMS(Long.MAX_VALUE));
            Collections.sort(res);
            System.out.println(print(res));
        }
    }

    private static LocalDateTime ofEpochMS(long ms) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(ms), ZoneId.systemDefault());
    }

    private static <T> String print(List<Timestamp<T>> res) {
        if (res == null || res.isEmpty()) return "NONE";
        return res.stream()
                .map(each -> each.getElement().toString())
                .collect(Collectors.joining(", "));
    }

}

// vashiot kod ovde

