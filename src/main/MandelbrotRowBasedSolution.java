import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * SOLUTION: Row-based parallel Mandelbrot generator using thread pools.
 * Each task computes one row of pixels.
 */
public class MandelbrotRowBasedSolution {
    private final int width;
    private final int height;
    private final int maxIterations;
    private final double xMin, xMax, yMin, yMax;

    public MandelbrotRowBasedSolution(int width, int height, int maxIterations) {
        this.width = width;
        this.height = height;
        this.maxIterations = maxIterations;
        this.xMin = -2.5;
        this.xMax = 1.0;
        this.yMin = -1.0;
        this.yMax = 1.0;
    }


    /**
     * Task to compute a single row of the Mandelbrot fractal.
     */
    private class RowTask implements Callable<int[]> {
        private final int row;

        public RowTask(int row) {
            this.row = row;
        }

        @Override
        public int[] call() {
            int[] rowData = new int[width];
            double cy = yMin + (yMax - yMin) * row / height;

            for (int px = 0; px < width; px++) {
                double cx = xMin + (xMax - xMin) * px / width;
                double iterations = MandelbrotUtils.computeIterations(cx, cy, maxIterations);
                rowData[px] = MandelbrotUtils.iterationsToColor(iterations, maxIterations);
            }

            return rowData;
        }
    }

    /**
     * Generate the Mandelbrot fractal using a fixed thread pool.
     */
    public BufferedImage generate(int numThreads) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        // Create a fixed thread pool
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);

        try {
            // Create a list to store Future objects
            List<Future<int[]>> futures = new ArrayList<>(height);

            // Submit all row tasks to the executor
            for (int row = 0; row < height; row++) {
                Future<int[]> future = executor.submit(new RowTask(row));
                futures.add(future);
            }

            // Retrieve results from futures and populate the image
            // Note: We process futures in submission order (not completion order)
            // This works because futures[row] corresponds to row number.
            // For better performance with variable-time tasks, consider ExecutorCompletionService
            // (see tile-based solution for an example of completion-order processing)
            for (int row = 0; row < height; row++) {
                int[] rowData = futures.get(row).get(); // Blocking call - waits for THIS specific row
                // Use bulk setRGB for entire row - much faster than pixel-by-pixel!
                image.setRGB(0, row, width, 1, rowData, 0, width);
                //          ^startX ^startY ^w ^h ^data ^offset ^scansize
            }

        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Error during parallel computation: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Shutdown the executor properly
            executor.shutdown();
            try {
                if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                executor.shutdownNow();
            }
        }

        return image;
    }

    public void saveImage(BufferedImage image, String filename) throws IOException {
        File outputFile = new File(filename);
        ImageIO.write(image, "PNG", outputFile);
        System.out.println("Image saved to: " + filename);
    }

    public static void main(String[] args) {
        int width = 1600;
        int height = 1200;
        int maxIterations = 2000;
        int numThreads = Runtime.getRuntime().availableProcessors();

        if (args.length >= 4) {
            width = Integer.parseInt(args[0]);
            height = Integer.parseInt(args[1]);
            maxIterations = Integer.parseInt(args[2]);
            numThreads = Integer.parseInt(args[3]);
        }

        System.out.println("Row-Based Parallel Mandelbrot Generation (SOLUTION)");
        System.out.println("Image size: " + width + "x" + height);
        System.out.println("Max iterations: " + maxIterations);
        System.out.println("Number of threads: " + numThreads);
        System.out.println("----------------------------------------");

        MandelbrotRowBasedSolution mandelbrot = new MandelbrotRowBasedSolution(width, height, maxIterations);

        long startTime = System.nanoTime();
        BufferedImage image = mandelbrot.generate(numThreads);
        long endTime = System.nanoTime();

        double elapsedSeconds = (endTime - startTime) / 1_000_000_000.0;
        System.out.printf("Generation time: %.3f seconds%n", elapsedSeconds);

        try {
            mandelbrot.saveImage(image, "mandelbrot_rowbased_solution_" + numThreads + "threads.png");
        } catch (IOException e) {
            System.err.println("Error saving image: " + e.getMessage());
        }
    }
}
