package matt.thread.bigjob;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.AsyncFunction;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;


public class BigJobService {

  private ListeningExecutorService executor;
  private FutureMaker futureMaker;
  private int maxIdsPerRun;

  public BigJobService(ListeningExecutorService executor, FutureMaker futureMaker, int maxIdsPerRun) {
    this.executor = executor;
    this.futureMaker = futureMaker;
    this.maxIdsPerRun = maxIdsPerRun;
  }

  public ListenableFuture<List<String>> startJob(List<String> ids) {
    List<List<String>> idChunks = Lists.partition(ids, maxIdsPerRun);

    List<ListenableFuture<String>> futuresList = new ArrayList<>(idChunks.size());
    for (final List<String> idChunk : idChunks) {

      ListenableFuture<String> firstFuture = futureMaker.createFirstStep(idChunk);

      // will return a ListenableFuture that will uppercase the input
      AsyncFunction<String, String> func = futureMaker.createFirstToSecondFunction();

      ListenableFuture<String> composedFuture =
              Futures.transform(firstFuture, func, executor);

      futuresList.add(composedFuture);
    }

    // transform to a single future
    return Futures.allAsList(futuresList);
  }
}
