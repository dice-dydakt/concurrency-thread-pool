#!/bin/bash
# Build script for Thread Pools Lab

set -e  # Exit on error

echo "Building Thread Pools Lab..."
echo "=============================="

# Create bin directory if it doesn't exist
mkdir -p bin

# Compile utilities
echo "Compiling utilities..."
javac -d bin src/utils/MandelbrotUtils.java

# Compile main programs
echo "Compiling main programs..."
javac -cp bin -d bin src/main/MandelbrotSequential.java
javac -cp bin -d bin src/main/MandelbrotRowBasedSolution.java
javac -cp bin -d bin src/main/PerformanceBenchmark.java

# Compile templates (may have TODOs, so might fail - that's okay)
echo "Compiling student templates..."
javac -cp bin -d bin src/templates/MandelbrotTileBased.java 2>/dev/null || echo "  (MandelbrotTileBased has TODOs - skipped)"

# Compile solutions (instructor reference)
echo "Compiling instructor solutions..."
javac -cp bin -d bin solutions/MandelbrotTileBasedSolution.java
javac -cp bin -d bin solutions/MandelbrotForkJoinSolution.java
javac -cp bin -d bin solutions/CompareAllApproaches.java

echo ""
echo "Build complete!"
echo "Compiled classes are in: bin/"
