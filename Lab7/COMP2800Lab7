import java.io.*;
import java.nio.file.*;
import java.util.concurrent.locks.*;

public class COMP2800Lab7 {

    static int totalWhitespace = 0;
    static Lock lock = new ReentrantLock();

    // No threads
    public static int countWhitespaceSingle(String filePath) throws Exception {

        int count = 0;
        FileReader fr = new FileReader(filePath);
        int ch;

        while ((ch = fr.read()) != -1) {
            if (Character.isWhitespace((char) ch)) {
                count++;
            }
        }

        fr.close();
        return count;
    }

    // Worker thread class
    static class Worker extends Thread {

        String text;

        Worker(String text) {
            this.text = text;
        }

        public void run() {

            int localCount = 0;

            for (char c : text.toCharArray()) {
                if (Character.isWhitespace(c)) {
                    localCount++;
                }
            }

            lock.lock();
            try {
                totalWhitespace += localCount;
            } finally {
                lock.unlock();
            }
        }
    }

    // W threads
    public static int countWhitespaceThreads(String filePath, int numThreads) throws Exception {

        String content = new String(Files.readAllBytes(Paths.get(filePath)));

        int length = content.length() / numThreads;

        Worker[] threads = new Worker[numThreads];

        for (int i = 0; i < numThreads; i++) {

            int start = i * length;
            int end = (i == numThreads - 1) ? content.length() : start + length;

            threads[i] = new Worker(content.substring(start, end));
            threads[i].start();
        }

        for (Worker t : threads) {
            t.join();
        }

        return totalWhitespace;
    }

    public static void main(String[] args) throws Exception {

        String filePath = "input.txt";

        // Task 1 timing
        long start1 = System.currentTimeMillis();
        int result1 = countWhitespaceSingle(filePath);
        long end1 = System.currentTimeMillis();

        System.out.println("Single Thread Count: " + result1);
        System.out.println("Single Thread Time: " + (end1 - start1) + " ms");

        // Task 2 timing
        long start2 = System.currentTimeMillis();
        int result2 = countWhitespaceThreads(filePath, 4);
        long end2 = System.currentTimeMillis();

        System.out.println("Multithread Count: " + result2);
        System.out.println("Multithread Time: " + (end2 - start2) + " ms");
    }
}
