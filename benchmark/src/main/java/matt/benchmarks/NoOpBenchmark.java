package matt.benchmarks;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

/** Baseline for benchmarking */
@State(Scope.Thread)
public class NoOpBenchmark {

  @Benchmark
  public void noop() {}
}
