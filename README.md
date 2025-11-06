# Thread Pools Lab: Mandelbrot Fractal Generation

## Overview

This lab teaches concurrent programming concepts using thread pools (`ExecutorService`) by implementing parallel versions of a Mandelbrot fractal generator. 

**Topics Covered:**
- Thread pool creation and management
- Task decomposition strategies
- Parallel performance analysis

## Project Structure

```
concurrency-thread-pools/
├── build.sh                    # Build script
├── run.sh                      # Run script
│
├── src/                        # Source code
│   ├── main/                   # Complete programs (ready to run & study)
│   │   ├── MandelbrotSequential.java          # Sequential baseline
│   │   ├── MandelbrotRowBasedSolution.java    # Row-based decomposition
│   │   └── PerformanceBenchmark.java          # Benchmark utility
│   ├── templates/              # Student implementation tasks (with TODOs)
│   │   └── MandelbrotTileBased.java           # Tile-based decomposition template
    └── utils/                  # Shared utilities
        └── MandelbrotUtils.java               # Computation & coloring
```

## Quick Start

### Setup

1. **Clone or download this lab directory**
2. **Ensure Java 8+ is installed:**
   ```bash
   java -version
   javac -version
   ```
3. **Build the project:**
   ```bash
   chmod +x build.sh run.sh
   ./build.sh
   ```

### Quick Start

**Run programs using the convenience script:**

```bash
# Sequential baseline
./run.sh sequential

# Row-based solution (study this first!)
./run.sh rowbased 800 600 1000 4

# Tile-based solution (implement this!)
./run.sh tilebased 800 600 1000 4 50

# Run comprehensive benchmark
./run.sh benchmark
```

**Arguments:**
- width, height: Image dimensions
- maxIterations: Maximum iterations per pixel
- numThreads: Thread pool size
- tileSize: Tile dimensions (tile-based only)

### Manual Compilation (Alternative)

If you prefer to compile manually:

```bash
# Compile utilities first
javac -d bin src/utils/MandelbrotUtils.java

# Compile main programs
javac -cp bin -d bin src/main/MandelbrotSequential.java

# Compile solutions
javac -cp bin -d bin solutions/MandelbrotRowBasedSolution.java
javac -cp bin -d bin solutions/MandelbrotTileBasedSolution.java

# Run (example)
java -cp bin MandelbrotSequential
java -cp bin MandelbrotRowBasedSolution 800 600 1000 4
```

## Customization Options

### Parameter Variations

Adjust complexity by changing default parameters:

```java
// Faster (for testing)
int width = 400;
int height = 300;
int maxIterations = 500;

// Standard (default)
int width = 800;
int height = 600;
int maxIterations = 1000;

// More intensive (better differentiation)
int width = 1600;
int height = 1200;
int maxIterations = 2000;
```

## References and Resources

- [Java Concurrency in Practice](https://jcip.net/) by Goetz et al.
- [Java ExecutorService Tutorial](https://docs.oracle.com/javase/tutorial/essential/concurrency/executors.html)
- [Amdahl's Law](https://en.wikipedia.org/wiki/Amdahl%27s_law)
- [The Mandelbrot Set](https://en.wikipedia.org/wiki/Mandelbrot_set)

## License

This lab material is provided for educational purposes. Feel free to modify and adapt for your courses.


## Contact

Bartosz Balis, balis at agh edu pl
