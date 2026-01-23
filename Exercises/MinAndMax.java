import java.util.Scanner;

class MinMax<T extends Comparable<T>> {
    private T min;
    private T max;
    private int lower;
    private int upper;
    private int total;
    public MinMax() {
    }
    T max() {
        return max;
    }
    T min() {
        return min;
    }
    public void update(T updater) {
        total++;
        if(min == null) {
            min = updater;
            max = updater;
            lower = upper = 1;
            return;
        }
        if(updater.compareTo(min) < 0) {
            min = updater;
            lower = 1;
        }
        else if(updater.compareTo(min) == 0) {
            lower++;
        }
        if(updater.compareTo(max) > 0) {
            max = updater;
            upper = 1;
        }
        else if(updater.compareTo(max) == 0) {
            upper++;
        }
    }
    @Override
    public String toString() {
        int btw = 0;
        if(min.compareTo(max)!= 0) {
            btw = total - lower - upper;
        }
        return String.valueOf(min) + " " + String.valueOf(max) + " " + String.valueOf(btw) + "\n";
    }
}
public class MinAndMax {
    public static void main(String[] args) throws ClassNotFoundException {
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        MinMax<String> strings = new MinMax<String>();
        for(int i = 0; i < n; ++i) {
            String s = scanner.next();
            strings.update(s);
        }
        System.out.println(strings);
        MinMax<Integer> ints = new MinMax<Integer>();
        for(int i = 0; i < n; ++i) {
            int x = scanner.nextInt();
            ints.update(x);
        }
        System.out.println(ints);
    }
}