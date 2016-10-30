## Usage

To just run the microbenchmarks, you can skip unit tests, and just run the produced `benchmarks.jar`

```shell
mvn clean package \
    -DskipTests -am -pl benchmark \
    && java -jar benchmark/target/original-matt-benchmark-1.0-SNAPSHOT-shaded.jar
```

# Benchmark writeups

## BranchingBenchmark

This benchmark tests whether using if-else, switch, or a Supplier class is faster when
going through **the same branch** in succession.

The initial implementation (commit f36fa67) found that the Supplier method was fastest, but not by
a significant amount.  This seems to make sense because no branching instructions are necessary,
but I thought the branch predictor would do a good job of being right because the first conditional
would always be true.  I thought the virtual function call on the Supplier would take longer than the
branch instruction, but the data does not suggest that.

```
Benchmark                               Mode     Cnt     Score    Error   Units
BranchingBenchmark.benchmarkIfElse     thrpt      20    26.237 ±  1.771  ops/us
BranchingBenchmark.benchmarkSupplier   thrpt      20    28.025 ±  0.580  ops/us
BranchingBenchmark.benchmarkSwitch     thrpt      20    25.528 ±  0.212  ops/us
NoOpBenchmark.noop                     thrpt      20  3574.203 ± 25.902  ops/us
BranchingBenchmark.benchmarkIfElse      avgt      20     0.035 ±  0.001   us/op
BranchingBenchmark.benchmarkSupplier    avgt      20     0.035 ±  0.001   us/op
BranchingBenchmark.benchmarkSwitch      avgt      20     0.039 ±  0.001   us/op
NoOpBenchmark.noop                      avgt      20    ≈ 10⁻⁴            us/op
```

It also seems plausible that a majority of this time is spent constructing the object, rather than
going through the branching instructions, so the next set of benchmarks sought to remove the object construction.

