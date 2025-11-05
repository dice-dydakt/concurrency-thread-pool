import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Benchmark utility to compare different Mandelbrot implementations.
 * Runs multiple iterations and collects performance statistics.
 */
public class PerformanceBenchmark {

    public static class BenchmarkResult {
        String implementation;
        int numThreads;
        int tileSize;
        double minTime;
        double maxTime;
        double avgTime;
        double medianTime;
        double speedup;
        double efficiency;

        public BenchmarkResult(String implementation, int numThreads, int tileSize) {
            this.implementation = implementation;
            this.numThreads = numThreads;
            this.tileSize = tileSize;
        }

        public void computeStatistics(double[] times, double sequentialTime) {
            if (times.length == 0) return;

            // Sort for median
            java.util.Arrays.sort(times);

            this.minTime = times[0];
            this.maxTime = times[times.length - 1];
            this.medianTime = times[times.length / 2];

            double sum = 0;
            for (double time : times) {
                sum += time;
            }
            this.avgTime = sum / times.length;

            if (sequentialTime > 0) {
                this.speedup = sequentialTime / avgTime;
                this.efficiency = speedup / numThreads;
            }
        }

        @Override
        public String toString() {
            return String.format("%s (threads=%d%s): avg=%.3fs, min=%.3fs, max=%.3fs, median=%.3fs, speedup=%.2fx, efficiency=%.2f%%",
                    implementation, numThreads,
                    tileSize > 0 ? ", tile=" + tileSize : "",
                    avgTime, minTime, maxTime, medianTime,
                    speedup, efficiency * 100);
        }

        public String toCsv() {
            return String.format("%s,%d,%d,%.3f,%.3f,%.3f,%.3f,%.2f,%.4f",
                    implementation, numThreads, tileSize,
                    avgTime, minTime, maxTime, medianTime,
                    speedup, efficiency);
        }
    }

    public static void runSequentialBenchmark(int width, int height, int maxIter, int warmup, int iterations) {
        System.out.println("\n=== Sequential Baseline Benchmark ===");

        // Warmup
        for (int i = 0; i < warmup; i++) {
            MandelbrotSequential m = new MandelbrotSequential(width, height, maxIter);
            m.generate();
        }

        // Timed runs
        double[] times = new double[iterations];
        for (int i = 0; i < iterations; i++) {
            MandelbrotSequential m = new MandelbrotSequential(width, height, maxIter);
            long start = System.nanoTime();
            m.generate();
            long end = System.nanoTime();
            times[i] = (end - start) / 1_000_000_000.0;
            System.out.printf("  Run %d: %.3f seconds%n", i + 1, times[i]);
        }

        BenchmarkResult result = new BenchmarkResult("Sequential", 1, 0);
        result.computeStatistics(times, times[0]); // Use its own time as baseline
        System.out.println("\nResults: " + result);
    }

    public static BenchmarkResult runRowBasedBenchmark(int width, int height, int maxIter,
                                                        int numThreads, int warmup, int iterations,
                                                        double sequentialTime) {
        System.out.println("\n=== Row-Based Benchmark (threads=" + numThreads + ") ===");

        try {
            // Check if solution exists
            Class<?> clazz = Class.forName("MandelbrotRowBasedSolution");

            // Warmup
            for (int i = 0; i < warmup; i++) {
                Object m = clazz.getConstructor(int.class, int.class, int.class)
                        .newInstance(width, height, maxIter);
                clazz.getMethod("generate", int.class).invoke(m, numThreads);
            }

            // Timed runs
            double[] times = new double[iterations];
            for (int i = 0; i < iterations; i++) {
                Object m = clazz.getConstructor(int.class, int.class, int.class)
                        .newInstance(width, height, maxIter);
                long start = System.nanoTime();
                clazz.getMethod("generate", int.class).invoke(m, numThreads);
                long end = System.nanoTime();
                times[i] = (end - start) / 1_000_000_000.0;
                System.out.printf("  Run %d: %.3f seconds%n", i + 1, times[i]);
            }

            BenchmarkResult result = new BenchmarkResult("RowBased", numThreads, 0);
            result.computeStatistics(times, sequentialTime);
            System.out.println("\nResults: " + result);
            return result;

        } catch (Exception e) {
            System.err.println("Could not run row-based benchmark: " + e.getMessage());
            return null;
        }
    }

    public static BenchmarkResult runTileBasedBenchmark(int width, int height, int maxIter,
                                                         int numThreads, int tileSize,
                                                         int warmup, int iterations,
                                                         double sequentialTime) {
        System.out.println("\n=== Tile-Based Benchmark (threads=" + numThreads + ", tile=" + tileSize + ") ===");

        try {
            // Check if solution exists
            Class<?> clazz = Class.forName("MandelbrotTileBasedSolution");

            // Warmup
            for (int i = 0; i < warmup; i++) {
                Object m = clazz.getConstructor(int.class, int.class, int.class)
                        .newInstance(width, height, maxIter);
                clazz.getMethod("generate", int.class, int.class).invoke(m, numThreads, tileSize);
            }

            // Timed runs
            double[] times = new double[iterations];
            for (int i = 0; i < iterations; i++) {
                Object m = clazz.getConstructor(int.class, int.class, int.class)
                        .newInstance(width, height, maxIter);
                long start = System.nanoTime();
                clazz.getMethod("generate", int.class, int.class).invoke(m, numThreads, tileSize);
                long end = System.nanoTime();
                times[i] = (end - start) / 1_000_000_000.0;
                System.out.printf("  Run %d: %.3f seconds%n", i + 1, times[i]);
            }

            BenchmarkResult result = new BenchmarkResult("TileBased", numThreads, tileSize);
            result.computeStatistics(times, sequentialTime);
            System.out.println("\nResults: " + result);
            return result;

        } catch (Exception e) {
            System.err.println("Could not run tile-based benchmark: " + e.getMessage());
            return null;
        }
    }

    public static void saveResultsToCsv(java.util.List<BenchmarkResult> results, String filename) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.println("Implementation,Threads,TileSize,AvgTime,MinTime,MaxTime,MedianTime,Speedup,Efficiency");
            for (BenchmarkResult result : results) {
                if (result != null) {
                    writer.println(result.toCsv());
                }
            }
            System.out.println("\nResults saved to: " + filename);
        } catch (IOException e) {
            System.err.println("Error saving results: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        int width = 1600;
        int height = 1200;
        int maxIter = 2000;
        int warmupRuns = 2;
        int benchmarkRuns = 5;

        System.out.println("Mandelbrot Performance Benchmark");
        System.out.println("=================================");
        System.out.println("Image size: " + width + "x" + height);
        System.out.println("Max iterations: " + maxIter);
        System.out.println("Warmup runs: " + warmupRuns);
        System.out.println("Benchmark runs: " + benchmarkRuns);

        java.util.List<BenchmarkResult> allResults = new java.util.ArrayList<>();

        // Run sequential baseline
        System.out.println("\n--- Phase 1: Sequential Baseline ---");
        MandelbrotSequential seqM = new MandelbrotSequential(width, height, maxIter);

        // Warmup
        for (int i = 0; i < warmupRuns; i++) {
            seqM.generate();
        }

        // Measure
        double[] seqTimes = new double[benchmarkRuns];
        for (int i = 0; i < benchmarkRuns; i++) {
            long start = System.nanoTime();
            seqM.generate();
            long end = System.nanoTime();
            seqTimes[i] = (end - start) / 1_000_000_000.0;
            System.out.printf("  Run %d: %.3f seconds%n", i + 1, seqTimes[i]);
        }

        BenchmarkResult seqResult = new BenchmarkResult("Sequential", 1, 0);
        seqResult.computeStatistics(seqTimes, seqTimes[0]);
        System.out.println("\nResults: " + seqResult);
        allResults.add(seqResult);

        double sequentialTime = seqResult.avgTime;

        // Run parallel benchmarks with different thread counts
        System.out.println("\n--- Phase 2: Row-Based Parallel ---");
        int maxThreads = Runtime.getRuntime().availableProcessors();
        int[] threadCounts = {1, 2, 4, maxThreads, maxThreads * 2};

        for (int threads : threadCounts) {
            if (threads <= maxThreads * 2) {
                BenchmarkResult result = runRowBasedBenchmark(width, height, maxIter,
                        threads, warmupRuns, benchmarkRuns, sequentialTime);
                if (result != null) allResults.add(result);
            }
        }

        // Run tile-based benchmarks
        System.out.println("\n--- Phase 3: Tile-Based Parallel ---");
        int[] tileSizes = {25, 50, 100};

        for (int tileSize : tileSizes) {
            BenchmarkResult result = runTileBasedBenchmark(width, height, maxIter,
                    maxThreads, tileSize, warmupRuns, benchmarkRuns, sequentialTime);
            if (result != null) allResults.add(result);
        }

        // Summary
        System.out.println("\n\n=== BENCHMARK SUMMARY ===");
        for (BenchmarkResult result : allResults) {
            System.out.println(result);
        }

        // Save to CSV
        saveResultsToCsv(allResults, "benchmark_results.csv");

        // Analysis hints
        System.out.println("\n=== Analysis Questions ===");
        System.out.println("1. What is the speedup with " + maxThreads + " threads?");
        System.out.println("2. What happens when you use more threads than cores?");
        System.out.println("3. How does tile size affect performance?");
        System.out.println("4. What is the parallel efficiency at different thread counts?");
        System.out.println("5. Which approach (row vs tile) performs better and why?");
    }
}
