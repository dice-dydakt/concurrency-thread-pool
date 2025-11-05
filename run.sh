#!/bin/bash
# Run script for Thread Pools Lab

if [ $# -lt 1 ]; then
    echo "Usage: ./run.sh <program> [args...]"
    echo ""
    echo "Available programs:"
    echo "  sequential           - Run sequential baseline"
    echo "  rowbased            - Run row-based solution"
    echo "  tilebased           - Run tile-based solution"
    echo "  forkjoin            - Run ForkJoin solution"
    echo "  compare             - Compare all approaches"
    echo "  benchmark           - Run performance benchmark"
    echo ""
    echo "Examples:"
    echo "  ./run.sh sequential"
    echo "  ./run.sh rowbased 800 600 1000 4"
    echo "  ./run.sh tilebased 800 600 1000 4 50"
    echo "  ./run.sh forkjoin 800 600 1000 5000"
    echo "  ./run.sh compare"
    exit 1
fi

# Build if needed
if [ ! -d "bin" ]; then
    echo "Building project..."
    ./build.sh
    echo ""
fi

PROGRAM=$1
shift

case $PROGRAM in
    sequential)
        java -cp bin MandelbrotSequential "$@"
        ;;
    rowbased)
        java -cp bin MandelbrotRowBasedSolution "$@"
        ;;
    tilebased)
        java -cp bin MandelbrotTileBasedSolution "$@"
        ;;
    benchmark)
        java -cp bin PerformanceBenchmark "$@"
        ;;
    *)
        echo "Unknown program: $PROGRAM"
        echo "Run './run.sh' without arguments to see usage."
        exit 1
        ;;
esac
