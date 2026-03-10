import java.io.File;
import java.util.concurrent.RecursiveTask;

public class DirectorySizeTask extends RecursiveTask<Long> {

    private File dir;

    public DirectorySizeTask(File dir) {
        this.dir = dir;
    }

    @Override
    protected Long compute() {

        long size = 0;

        File[] files = dir.listFiles();

        if (files == null) return 0L;

        java.util.List<DirectorySizeTask> tasks = new java.util.ArrayList<>();

        for (File file : files) {

            if (file.isFile()) {
                size += file.length();
            }

            else if (file.isDirectory()) {
                DirectorySizeTask task = new DirectorySizeTask(file);

                task.fork();   // run in parallel
                tasks.add(task);
            }
        }

        for (DirectorySizeTask task : tasks) {
            size += task.join();  // wait for result
        }

        return size;
    }
}
