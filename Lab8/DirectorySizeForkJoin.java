import java.io.File;
import java.util.concurrent.ForkJoinPool;

public class DirectorySizeForkJoin {

    public static void main(String[] args) {

        if (args.length == 0) {
            System.out.println("Please provide directory path");
            return;
        }

        File dir = new File(args[0]);

        ForkJoinPool pool = new ForkJoinPool();

        long startTime = System.currentTimeMillis();

        DirectorySizeTask task = new DirectorySizeTask(dir);

        long size = pool.invoke(task);
        long endTime = System.currentTimeMillis();

        System.out.println("Directory Size: " + size + " bytes");
        System.out.println("Execution Time: " + (endTime - startTime) + " ms");
    }
}
