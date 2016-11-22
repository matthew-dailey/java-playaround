## Usage

To just run the microbenchmarks, you can skip unit tests, and just run the produced `benchmarks.jar`

```shell
mvn clean package -DskipTests -am -pl benchmark \
    && java -jar benchmark/target/original-matt-benchmark-1.0-SNAPSHOT-shaded.jar
```

# Benchmark writeups

**Please** feel free to peer-review this or any findings.  Like you, I want to find the best answers to these problems.

## BranchingBenchmark

This benchmark tests whether using `if-else`, `switch`, or a Supplier class is faster when
comparing with Strings, and going through **the same** comparison branch multiple times
in succession.  In other words, the expression under the `if` of an `if-elseif-else` ladder
is always true.

Run with JMH 1.12, Mac OS X 10.11.6, Darwin 15.6, with Oracle JDK:

```
java version "1.7.0_80"
Java(TM) SE Runtime Environment (build 1.7.0_80-b15)
Java HotSpot(TM) 64-Bit Server VM (build 24.80-b11, mixed mode)
```

Source in [BranchingBenchmark](src/main/java/matt/benchmarks/BranchingBenchmark.java).

### Hypothesis

Without knowing how `switch` is implemented with Strings, I assume that the `if-else` and `switch` forms should have
similar performance.  Even though the Supplier method requires no branching, I suspect the overhead of a virtual
function call (since I would be calling on an interface rather than concrete implementation) will cause this to
perform more poorly than the other two options.

I initially wanted to put the Conclusions up-front after this Hypothesis, but I think they make more sense following
the trials.  Feel free to [skip to them](#conclusions) anyway if you'd like.


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

Then implementing all the benchmarks (at revision a28849f) with PreConstructed objects:

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
2. The `if-else` and the Supplier show approximately equal performance

Maybe the `switch` statement is missing some optimization for Strings in this situation?

Also, a possible reason that the Supplier implementation is fast is because JMH isolates each benchmark, so the JVM
only loads one implementation of `Supplier<HttpRequestBase>`, which means that function calls
through that interface [go directly to the object](http://stackoverflow.com/a/973531/698839) rather than make a virtual function call.
I tried to combat this by having two implementations in the benchmark class, but if each benchmark method only
uses one of those, then it's entirely possible that the forked JVMs only load one implementation each.

### Trial 3

Do the number of options in the `switch` statement matter?  Benchmarks run against revision 95ad664.

```
Benchmark                                                    Mode  Cnt     Score     Error   Units
BranchingBenchmark.benchmarkConstructingIfElse              thrpt   20    28.160 ±   0.138  ops/us
BranchingBenchmark.benchmarkConstructingIfElseFewOptions    thrpt   20    26.204 ±   3.367  ops/us
BranchingBenchmark.benchmarkConstructingSupplier            thrpt   20    25.067 ±   2.787  ops/us
BranchingBenchmark.benchmarkConstructingSwitch              thrpt   20    25.079 ±   0.213  ops/us
BranchingBenchmark.benchmarkConstructingSwitchFewOptions    thrpt   20    26.703 ±   0.117  ops/us
BranchingBenchmark.benchmarkPreConstructedIfElse            thrpt   20   400.026 ±   2.236  ops/us <--
BranchingBenchmark.benchmarkPreConstructedIfElseFewOptions  thrpt   20   400.145 ±   1.744  ops/us <-- tiny difference
BranchingBenchmark.benchmarkPreConstructedSupplier          thrpt   20   378.510 ±  18.801  ops/us
BranchingBenchmark.benchmarkPreConstructedSwitch            thrpt   20   170.256 ±   3.133  ops/us <--
BranchingBenchmark.benchmarkPreConstructedSwitchFewOptions  thrpt   20   375.343 ±  23.539  ops/us <-- big difference
NoOpBenchmark.noop                                          thrpt   20  3593.874 ± 115.008  ops/us
BranchingBenchmark.benchmarkConstructingIfElse               avgt   20     0.037 ±   0.004   us/op
BranchingBenchmark.benchmarkConstructingIfElseFewOptions     avgt   20     0.036 ±   0.001   us/op
BranchingBenchmark.benchmarkConstructingSupplier             avgt   20     0.035 ±   0.001   us/op
BranchingBenchmark.benchmarkConstructingSwitch               avgt   20     0.041 ±   0.002   us/op
BranchingBenchmark.benchmarkConstructingSwitchFewOptions     avgt   20     0.038 ±   0.001   us/op
BranchingBenchmark.benchmarkPreConstructedIfElse             avgt   20     0.003 ±   0.001   us/op
BranchingBenchmark.benchmarkPreConstructedIfElseFewOptions   avgt   20     0.003 ±   0.001   us/op
BranchingBenchmark.benchmarkPreConstructedSupplier           avgt   20     0.003 ±   0.001   us/op
BranchingBenchmark.benchmarkPreConstructedSwitch             avgt   20     0.006 ±   0.001   us/op
BranchingBenchmark.benchmarkPreConstructedSwitchFewOptions   avgt   20     0.003 ±   0.001   us/op
NoOpBenchmark.noop                                           avgt   20    ≈ 10⁻³             us/op
```

Interestingly, the number of options in a `switch` statement matters, but not in the `if-else` chain.
One possible explanation is that `switch` statements can be compiled into a
[search partition tree](http://stackoverflow.com/a/14089060/698839), which looks like a binary tree
of `if-else` chains.  This happens particularly often for a sparse set of case statements, which is probably
always the case with Strings.

So, if we have a binary-search tree produced by our `switch` statements, it ends up requiring any input
to our `switch` statement go through `log_2(N)` comparisons, where `N` is the number of cases.  For the
`if-else` chain, we always go through exactly one comparison.

However, this seems to contradict what [other](http://stackoverflow.com/questions/6705955/why-switch-is-faster-than-if)
StackOverflow [answers](http://stackoverflow.com/questions/6705955/why-switch-is-faster-than-if) suggest, which is
that `switch` should win on performance.  My next steps should probably be to check out the compiled bytecode to see
how it is being implemented.

### Conclusions

For the case where you expect the `if` of an `if-else` chain to return true (for Strings), I recommend using an
`if-else` chain rather than a `switch` or `Supplier` interface.

* The `switch` statement performance degrades as the number of cases increases.
* The `Supplier` method performs the same as the `if-else` method, but in the case
where additional implementations of `Supplier` exist, it is very likely that the
performance of a virtual function call will affect this method's overall performance

This finding is interesting because it can lead to some form of "artisinal conditionals" where, as a programmer,
you may be able improve your performance by getting statistics on what inputs go through a conditional like this,
and make a decision about the conditional ordering.

### Additional Reading

* Virtual function calls [not so virtual](http://stackoverflow.com/a/973531/698839).
I updated the link to [Oracle's documentation](https://wiki.openjdk.java.net/display/HotSpot/PerformanceTechniques)
in this answer in the comments
* [Why switch is faster than if](http://stackoverflow.com/questions/6705955/why-switch-is-faster-than-if):
because the compiler can usually make a lookup table
* [Switch for String faster than if](http://stackoverflow.com/questions/22110707/how-is-string-in-switch-statement-more-efficient-than-corresponding-if-else-stat):
because the compiler can use the hashcode to assist itself
* [Search partition tree](http://stackoverflow.com/a/14089060/698839):
why `switch` can be slower than `if-else`, especially where the `if` is always true
