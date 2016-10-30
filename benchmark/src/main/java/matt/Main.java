package matt;

import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

/**
 * Overrides org.openjdk.jmh.main with some opinionated defaults
 */
public class Main {

  public static void main(String[] args) throws RunnerException {
    Options opt = new OptionsBuilder()
      .include("matt.benchmarks.*")
      .forks(1)
      .mode(Mode.All)
      .timeUnit(TimeUnit.MICROSECONDS)
      .shouldFailOnError(true)
      .build();

    new Runner(opt).run();
  }
}
