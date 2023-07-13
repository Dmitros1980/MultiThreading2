import java.util.*;

public class Main_A {
    final static String letters = "RLRFR";
    final static int quantityThreads = 1000;
    final static int routeLengtn = 100;
    public static final Map<Integer, Integer> sizeToFreq = new HashMap<>();

    public static void main(String[] args) throws InterruptedException {
        List<Thread> threadList = new ArrayList<>();
        Thread print = new Thread(() -> {
            while (!Thread.interrupted()) {
                synchronized (sizeToFreq) {
                    try {
                        sizeToFreq.wait();
                    } catch (InterruptedException e) {
                        return;
                    }
                    printLeader();
                }
            }
        });
        print.start();
        for (int i = 0; i < quantityThreads; i++) {
            threadList.add(getThread());
        }
        for (Thread thread : threadList) {
            thread.start();
        }

        print.join();
    }


    public static String generateRoute(String letters, int length) {
        Random random = new Random();
        StringBuilder route = new StringBuilder();
        for (int i = 0; i < length; i++) {
            route.append(letters.charAt(random.nextInt(letters.length())));
        }
        return route.toString();
    }

    public static void printLeader() {
        Map.Entry<Integer, Integer> max = sizeToFreq
                .entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .get();
        System.out.println("Самое частое количество повторений " + max.getKey()
                + " (встретилось " + max.getValue() + " раз)");
    }

    public static Thread getThread() {
        return new Thread(() -> {
            String route = generateRoute(letters, routeLengtn);
            int frequency = (int) route.chars()
                    .filter(right ->right == 'R')
                    .count();

            synchronized (sizeToFreq) {
                if (sizeToFreq.containsKey(frequency)) {
                    sizeToFreq.put(frequency, sizeToFreq.get(frequency) + 1);
                } else {
                    sizeToFreq.put(frequency, 1);
                }
                sizeToFreq.notify();
            }
        });

    }

}
