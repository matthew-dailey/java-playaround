## Usage

To just run the microbenchmarks, you can skip unit tests, and just run the produced `benchmarks.jar`

```shell
mvn clean package -DskipTests -am -pl benchmark \
    && java -jar benchmark/target/original-matt-benchmark-1.0-SNAPSHOT-shaded.jar
```

# Benchmark writeups

## BranchingBenchmark

This benchmark tests whether using if-else, switch, or a Supplier class is faster when
going through **the same branch** in succession.

Run with JMH 1.12 on:

```
java version "1.7.0_80"
Java(TM) SE Runtime Environment (build 1.7.0_80-b15)
Java HotSpot(TM) 64-Bit Server VM (build 24.80-b11, mixed mode)
```

### Trial 1

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

### Trial 2

In the supplier implementation, it does seem like a majority of the time is spent in object construction.

```
BranchingBenchmark.benchmarkSupplier                 thrpt      20    21.407 ±  4.346  ops/us
BranchingBenchmark.benchmarkSupplierPreConstructed   thrpt      20   375.652 ±  8.742  ops/us
NoOpBenchmark.noop                                   thrpt      20  3500.235 ± 57.356  ops/us
BranchingBenchmark.benchmarkSupplier                  avgt      20     0.051 ±  0.008   us/op
BranchingBenchmark.benchmarkSupplierPreConstructed    avgt      20     0.003 ±  0.001   us/op
NoOpBenchmark.noop                                    avgt      20    ≈ 10⁻⁴            us/op
```

Then implementing all the benchmarks (at revision a28849f)

```
Benchmark                                            Mode  Cnt     Score    Error   Units
BranchingBenchmark.benchmarkConstructingIfElse      thrpt   20    27.118 ±  0.456  ops/us
BranchingBenchmark.benchmarkConstructingSupplier    thrpt   20    27.909 ±  0.242  ops/us
BranchingBenchmark.benchmarkConstructingSwitch      thrpt   20    24.417 ±  0.515  ops/us
BranchingBenchmark.benchmarkPreConstructedIfElse    thrpt   20   353.047 ±  7.319  ops/us
BranchingBenchmark.benchmarkPreConstructedSupplier  thrpt   20   347.220 ±  4.950  ops/us
BranchingBenchmark.benchmarkPreConstructedSwitch    thrpt   20   164.716 ±  3.349  ops/us
NoOpBenchmark.noop                                  thrpt   20  3107.719 ± 55.770  ops/us
BranchingBenchmark.benchmarkConstructingIfElse       avgt   20     0.037 ±  0.001   us/op
BranchingBenchmark.benchmarkConstructingSupplier     avgt   20     0.037 ±  0.001   us/op
BranchingBenchmark.benchmarkConstructingSwitch       avgt   20     0.043 ±  0.001   us/op
BranchingBenchmark.benchmarkPreConstructedIfElse     avgt   20     0.003 ±  0.001   us/op
BranchingBenchmark.benchmarkPreConstructedSupplier   avgt   20     0.003 ±  0.001   us/op
BranchingBenchmark.benchmarkPreConstructedSwitch     avgt   20     0.006 ±  0.001   us/op
NoOpBenchmark.noop                                   avgt   20    ≈ 10⁻³            us/op
```

This shows

1. Object construction was taking a majority of the time
2. The if-else and the Supplier show approximately equal performance

Maybe the switch statement is missing some optimization for Strings in this situation?

Also, a possible reason that the Supplier implementation is fast is because JMH isolates each benchmark, and then the JVM
only loads one implementation of `Supplier<HttpRequestBase>`, which means that function calls
through that interface go directly to the object rather than make a virtual function call.
I tried to combat this by having two implementations in the benchmark class, but if each benchmark method only
uses one of those, then it's entirely possible that the forked JVMs only load one implementation each.
