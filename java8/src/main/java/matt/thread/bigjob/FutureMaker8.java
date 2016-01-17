package matt.thread.bigjob;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.google.common.util.concurrent.AsyncFunction;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;

/** FutureMaker8 implements FutureMaker succinctly using Java8 syntax */
public class FutureMaker8 implements FutureMaker {

  private ListeningExecutorService executor;

  public FutureMaker8(ListeningExecutorService executor) {
    this.executor = executor;
  }

  public ListenableFuture<String> createFirstStep(List<String> idChunk) {
    return executor.submit(() -> StringUtils.join(idChunk, ""));
  }

  public ListenableFuture<String> createSecondStep(String s) {
    return executor.submit(() -> s.toUpperCase());
  }

  public AsyncFunction<String, String> createFirstToSecondFunction() {
    return ((String s) -> createSecondStep(s));
  }
}
