import java.io.File;

public class DirectorySizeSingle {

    public static long getDirectorySize(File dir) {
        long size = 0;

        File[] files = dir.listFiles();

        if (files != null) {
            for (File file : files) {

                if (file.isFile()) {
                    size += file.length();
                }

                else if (file.isDirectory()) {
                    size += getDirectorySize(file);
                }
            }
        }

        return size;
    }

    public static void main(String[] args) {

        if (args.length == 0) {
            System.out.println("Please provide directory path");
            return;
        }

        File dir = new File(args[0]);

        long startTime = System.currentTimeMillis();

        long size = getDirectorySize(dir);

        long endTime = System.currentTimeMillis();

        System.out.println("Directory Size: " + size + " bytes");
        System.out.println("Execution Time: " + (endTime - startTime) + " ms");
    }
}
