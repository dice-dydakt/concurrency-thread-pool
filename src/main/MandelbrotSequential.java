import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Sequential (single-threaded) Mandelbrot fractal generator.
 * This serves as the baseline for performance comparison.
 */
public class MandelbrotSequential {
    private final int width;
    private final int height;
    private final int maxIterations;
    private final double xMin, xMax, yMin, yMax;

    public MandelbrotSequential(int width, int height, int maxIterations) {
        this.width = width;
        this.height = height;
        this.maxIterations = maxIterations;

        // Default view of the Mandelbrot set
        this.xMin = -2.5;
        this.xMax = 1.0;
        this.yMin = -1.0;
        this.yMax = 1.0;
    }


    /**
     * Generate the Mandelbrot fractal image.
     * @return BufferedImage containing the fractal
     */
    public BufferedImage generate() {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int py = 0; py < height; py++) {
            for (int px = 0; px < width; px++) {
                // Map pixel coordinates to complex plane
                double cx = xMin + (xMax - xMin) * px / width;
                double cy = yMin + (yMax - yMin) * py / height;

                double iterations = MandelbrotUtils.computeIterations(cx, cy, maxIterations);
                int color = MandelbrotUtils.iterationsToColor(iterations, maxIterations);

                image.setRGB(px, py, color);
            }
        }

        return image;
    }

    /**
     * Save the generated image to a file.
     */
    public void saveImage(BufferedImage image, String filename) throws IOException {
        File outputFile = new File(filename);
        ImageIO.write(image, "PNG", outputFile);
        System.out.println("Image saved to: " + filename);
    }

    public static void main(String[] args) {
        int width = 1600;
        int height = 1200;
        int maxIterations = 2000;

        // Parse command line arguments if provided
        if (args.length >= 3) {
            width = Integer.parseInt(args[0]);
            height = Integer.parseInt(args[1]);
            maxIterations = Integer.parseInt(args[2]);
        }

        System.out.println("Sequential Mandelbrot Generation");
        System.out.println("Image size: " + width + "x" + height);
        System.out.println("Max iterations: " + maxIterations);
        System.out.println("----------------------------------------");

        MandelbrotSequential mandelbrot = new MandelbrotSequential(width, height, maxIterations);

        long startTime = System.nanoTime();
        BufferedImage image = mandelbrot.generate();
        long endTime = System.nanoTime();

        double elapsedSeconds = (endTime - startTime) / 1_000_000_000.0;
        System.out.printf("Generation time: %.3f seconds%n", elapsedSeconds);

        try {
            mandelbrot.saveImage(image, "mandelbrot_sequential.png");
        } catch (IOException e) {
            System.err.println("Error saving image: " + e.getMessage());
        }
    }
}
