package matt.thread.bigjob;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.google.common.util.concurrent.AsyncFunction;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;

/** FutureMaker8 implements FutureMaker succinctly using Java8 syntax (pretty much, lambdas) */
public class FutureMaker8 implements FutureMaker {

  private ListeningExecutorService executor;

  public FutureMaker8(ListeningExecutorService executor) {
    this.executor = executor;
  }

  /** Concatenate list to single string */
  public ListenableFuture<String> createFirstStep(List<String> idChunk) {
    return executor.submit(() -> StringUtils.join(idChunk, ""));
  }

  /** Uppercase input string */
  public ListenableFuture<String> createSecondStep(String s) {
    return executor.submit(() -> s.toUpperCase());
  }

  /** Create AsyncFunction that passes input to {@link #createSecondStep(String)} */
  public AsyncFunction<String, String> createFirstToSecondFunction() {
    return ((String s) -> createSecondStep(s));
  }

  /**
   * Combine the other functions in the most lambda way possible and with the most
   * type inference possible, but not necessarily the most readable :/
   */
  public ListenableFuture<String> makeComposed(List<String> idChunk) {
    return Futures.transform(
            executor.submit(
                    () -> StringUtils.join(idChunk, "")),
            (String s) -> s.toUpperCase(),
            executor);
  }
}
