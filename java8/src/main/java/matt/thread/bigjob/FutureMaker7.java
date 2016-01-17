package matt.thread.bigjob;

import java.util.List;
import java.util.concurrent.Callable;

import org.apache.commons.lang.StringUtils;

import com.google.common.util.concurrent.AsyncFunction;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;

/** FutureMaker7 implements FutureMaker pretty verbosely using Java7 syntax */
public class FutureMaker7 implements FutureMaker {

  private ListeningExecutorService executor;

  public FutureMaker7(ListeningExecutorService executor) {
    this.executor = executor;
  }

  public ListenableFuture<String> createFirstStep(List<String> idChunk) {
    return executor.submit(new Callable<String>() {
      @Override
      public String call() throws Exception {
        return StringUtils.join(idChunk, "");
      }
    });
  }

  public ListenableFuture<String> createSecondStep(String s) {
    return executor.submit(new Callable<String>() {
      @Override
      public String call() throws Exception {
        return s.toUpperCase();
      }
    });
  }

  public AsyncFunction<String, String> createFirstToSecondFunction() {
    return new AsyncFunction<String, String>() {
      @Override
      public ListenableFuture<String> apply(String s) throws Exception {
        return createSecondStep(s);
      }
    };
  }
}
