package matt.thread;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.AsyncFunction;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import org.apache.commons.lang.StringUtils;

import java.nio.Buffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;


public class BigJobService {

  private int maxIdsPerRun;
  private ListeningExecutorService executor;

  public BigJobService(ListeningExecutorService executor, int maxIdsPerRun) {
    this.executor = executor;
    this.maxIdsPerRun = maxIdsPerRun;
  }

  private ListenableFuture<String> createFirstStep(List<String> idChunk) {
    return executor.submit(new Callable<String>() {
      @Override
      public String call() throws Exception {
        return StringUtils.join(idChunk, "");
      }
    });
  }

  private ListenableFuture<String> createSecondStep(String s) {
    return executor.submit(new Callable<String>() {
      @Override
      public String call() throws Exception {
        return s.toUpperCase();
      }
    });
  }

  private AsyncFunction<String, String> createFirstToSecondFunction() {
    return new AsyncFunction<String, String>() {
      @Override
      public ListenableFuture<String> apply(String s) throws Exception {
        return createSecondStep(s);
      }
    };
  }

  public ListenableFuture<List<String>> startJob(List<String> ids) {
    List<List<String>> idChunks = Lists.partition(ids, maxIdsPerRun);

    List<ListenableFuture<String>> futuresList = new ArrayList<>(idChunks.size());
    for (final List<String> idChunk : idChunks) {

      ListenableFuture<String> firstFuture = createFirstStep(idChunk);

      // will return a ListenableFuture that will uppercase the input
      AsyncFunction<String, String> func = createFirstToSecondFunction();

      ListenableFuture<String> composedFuture =
              Futures.transform(firstFuture, func, executor);

      futuresList.add(composedFuture);
    }

    // transform to a single future
    return Futures.allAsList(futuresList);
  }
}
