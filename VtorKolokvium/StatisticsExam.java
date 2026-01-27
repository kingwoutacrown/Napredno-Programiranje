import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;


public class StatisticsExam {

    public static void main(String[] args) throws Exception {

        StatisticsService service = new StatisticsService();

        int k;
        Scanner scanner = new Scanner(System.in);
        k = scanner.nextInt();


        List<Callable<String>> tasks = new ArrayList<>();

        /* ------------------------------------------------------------
           PHASE 1: Concurrent writers
           ------------------------------------------------------------ */

        int added = 0;
        int avg = 0;
        int min = 0;
        int max = 0;

        int expectedMin = 10;
        int expectedMax = 10;

        for (int i = 1; i < k*100; i++) {
            int value = i * 10;
            tasks.add(new SubmitNumberTask(service, value));
            expectedMax = Math.max(expectedMax, value);
            added++;
        }

        /* ------------------------------------------------------------
           PHASE 2: Concurrent readers (should run in parallel)
           ------------------------------------------------------------ */

        for (int i = 0; i < k*5; i++) {
            tasks.add(new GetAverageTask(service));
            avg++;
            tasks.add(new GetMinTask(service));
            min++;
            tasks.add(new GetMaxTask(service));
            max++;
        }

        /* ------------------------------------------------------------
           PHASE 3: Interleaved read/write (critical part)
           ------------------------------------------------------------ */

        for (int i = 100; i <= k*200; i += 10) {
            tasks.add(new SubmitNumberTask(service, i));
            added++;
            expectedMax = Math.max(expectedMax, i);
            tasks.add(new GetAverageTask(service));
            avg++;
            tasks.add(new GetMinTask(service));
            min++;
            tasks.add(new GetMaxTask(service));
            max++;
        }

        /* ------------------------------------------------------------
           EXECUTION
           ------------------------------------------------------------ */



        List<Future<String>> results = ConcurrentService.submitAll(6, tasks);


        List<String> finalResults = new ArrayList<>();
        for (Future<String> f : results) {
            try{
                finalResults.add(f.get());
            } catch (Exception e){
                System.out.println(e.getMessage());
            }
        }

        int numberAddedMessage = 0, minInvoked = 0, maxInvoked = 0, averageInvoked = 0;

        for (String finalResult : finalResults) {
            if (finalResult.startsWith("AVERAGE")) {
                averageInvoked++;
            }
            if  (finalResult.startsWith("MIN")) {
                minInvoked++;
            }
            if (finalResult.startsWith("MAX")) {
                maxInvoked++;
            }
            if (finalResult.contains("Total numbers: ")) {
                numberAddedMessage++;
            }
        }

        if (minInvoked!=min){
            System.out.println("GetMinTask was not invoked the correct number of times");
        }

        if (maxInvoked!=max){
            System.out.println("GetMaxTask was not invoked the correct number of times");
        }

        if (averageInvoked!=avg){
            System.out.println("GetAverageTask was not invoked the correct number of times");
        }

        if (numberAddedMessage!=added) {
            System.out.println("Number of added tasks was not invoked the correct number of times");
        }

        /* ------------------------------------------------------------
           BASIC SANITY CHECKS (NO assert, exam-safe)
           ------------------------------------------------------------ */

        int finalCount = service.getCount();



        if (finalCount != added) {
            throw new RuntimeException(
                    String.format("ERROR: Expected %d numbers, but got %d", added, finalCount)
            );
        }

        if (service.getMin() != expectedMin) {
            throw new RuntimeException(
                    "ERROR: Expected MIN = " + expectedMin
            );
        }

        if (service.getMax() != expectedMax) {
            throw new RuntimeException(
                    "ERROR: Expected MAX = " + expectedMax
            );
        }

        System.out.println("✔ FINAL CHECKS PASSED");
    }
}

class StatisticsService {
    private List<Integer> integers;
    Lock lock;
    public StatisticsService() {
        integers = new ArrayList<>();
        lock = new ReentrantLock();
    }
    public void addInteger(int integer) {
        lock.lock();
        try {
            integers.add(integer);
        }
        finally {
            lock.unlock();
        }
    }
    public int getCount() {
        lock.lock();
        try {
            return integers.size();
        } finally {
            lock.unlock();
        }
    }
    public Double getMin() {
        lock.lock();
        try {
            return (double) integers.stream().min(Comparator.comparing(Integer::intValue)).orElse(0);
        }
        finally {
            lock.unlock();
        }
    }
    public Double getMax() {
        lock.lock();
        try {
            return (double) integers.stream().max(Comparator.comparing(Integer::intValue)).orElse(0);
        }
        finally {
            lock.unlock();
        }
    }
    public Double getAverage() {
        lock.lock();
        try {
            return integers.stream().mapToInt(Integer::intValue).average().orElse(0.0);
        }
        finally {
            lock.unlock();
        }
    }
}

class SubmitNumberTask implements Callable<String> {
    private StatisticsService service;
    private int number;
    public SubmitNumberTask(StatisticsService service, int number) { this.service = service; this.number = number; }

    @Override
    public String call() throws Exception {
        service.addInteger(number);
        return String.format("NUMBER %d ADDED. Total numbers: %d",number,service.getCount());
    }
}

class GetAverageTask implements Callable<String> {
    private StatisticsService service;
    public GetAverageTask(StatisticsService service) {
        this.service = service;
    }

    @Override
    public String call() throws Exception {
        return String.format("AVERAGE: %.2f", service.getAverage());
    }
}

class GetMinTask implements Callable<String> {
    private StatisticsService service;
    public GetMinTask(StatisticsService service) {
        this.service = service;
    }

    @Override
    public String call() throws Exception {
        return String.format("MIN: %.2f", service.getMin());
    }
}

class GetMaxTask implements Callable<String> {
    private StatisticsService service;
    public GetMaxTask(StatisticsService service) {
        this.service = service;
    }

    @Override
    public String call() throws Exception {
        return String.format("MAX: %.2f", service.getMax());
    }
}

class ConcurrentService {
    public static List<Future<String>> submitAll(int numberOfThreads, List<Callable<String>> callables) throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
        List<Future<String>> futures = new ArrayList<>();
        callables.forEach(c -> {
            futures.add(executor.submit(c));
        });
        executor.shutdown();
        return futures;
    }
}