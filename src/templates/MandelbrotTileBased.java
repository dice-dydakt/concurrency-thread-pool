import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.*;

/**
 * Tile-based parallel Mandelbrot generator using thread pools.
 * The image is divided into rectangular tiles for better load balancing.
 *
 * STUDENT TODO: Complete the implementation
 */
public class MandelbrotTileBased {
    private final int width;
    private final int height;
    private final int maxIterations;
    private final double xMin, xMax, yMin, yMax;

    public MandelbrotTileBased(int width, int height, int maxIterations) {
        this.width = width;
        this.height = height;
        this.maxIterations = maxIterations;
        this.xMin = -2.5;
        this.xMax = 1.0;
        this.yMin = -1.0;
        this.yMax = 1.0;
    }


    /**
     * Represents a rectangular tile in the image.
     */
    private static class Tile {
        final int startX, startY;
        final int endX, endY;

        public Tile(int startX, int startY, int endX, int endY) {
            this.startX = startX;
            this.startY = startY;
            this.endX = endX;
            this.endY = endY;
        }
    }

    /**
     * Task to compute a rectangular tile of the Mandelbrot fractal.
     *
     * TODO: Implement this Callable to compute a tile
     * - Return type should store both pixel data and tile position
     * - Consider using a wrapper class or 2D array
     */
    private class TileTask implements Callable<TileResult> {
        private final Tile tile;

        public TileTask(Tile tile) {
            this.tile = tile;
        }

        @Override
        public TileResult call() {
            // TODO: Implement tile computation
            // Calculate the width and height of this tile
            // Compute iterations for each pixel in the tile
            // Return the results with position information

            return null; // REPLACE THIS
        }
    }

    /**
     * Helper class to store tile computation results.
     */
    private static class TileResult {
        final Tile tile;
        final int[][] pixelData;

        public TileResult(Tile tile, int[][] pixelData) {
            this.tile = tile;
            this.pixelData = pixelData;
        }
    }

    /**
     * Generate the Mandelbrot fractal using tile-based parallelization.
     *
     * TODO: Implement this method
     * 1. Divide the image into tiles (experiment with tile size)
     * 2. Create an ExecutorService
     * 3. Submit TileTask for each tile
     * 4. Collect results and populate the image
     * 5. Properly shutdown the executor
     *
     * @param numThreads Number of threads in the pool
     * @param tileSize Size of each tile (both width and height)
     */
    public BufferedImage generate(int numThreads, int tileSize) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        // TODO: Implement tile-based parallel generation

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
        int tileSize = 50; // Default tile size

        if (args.length >= 5) {
            width = Integer.parseInt(args[0]);
            height = Integer.parseInt(args[1]);
            maxIterations = Integer.parseInt(args[2]);
            numThreads = Integer.parseInt(args[3]);
            tileSize = Integer.parseInt(args[4]);
        }

        System.out.println("Tile-Based Parallel Mandelbrot Generation");
        System.out.println("Image size: " + width + "x" + height);
        System.out.println("Max iterations: " + maxIterations);
        System.out.println("Number of threads: " + numThreads);
        System.out.println("Tile size: " + tileSize + "x" + tileSize);
        System.out.println("----------------------------------------");

        MandelbrotTileBased mandelbrot = new MandelbrotTileBased(width, height, maxIterations);

        long startTime = System.nanoTime();
        BufferedImage image = mandelbrot.generate(numThreads, tileSize);
        long endTime = System.nanoTime();

        double elapsedSeconds = (endTime - startTime) / 1_000_000_000.0;
        System.out.printf("Generation time: %.3f seconds%n", elapsedSeconds);

        try {
            mandelbrot.saveImage(image, "mandelbrot_tilebased_" + numThreads + "threads_tile" + tileSize + ".png");
        } catch (IOException e) {
            System.err.println("Error saving image: " + e.getMessage());
        }
    }
}
