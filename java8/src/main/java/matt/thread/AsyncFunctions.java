package matt.thread;

import com.google.common.util.concurrent.AsyncFunction;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;

public class AsyncFunctions {

  public static class DownloadAsyncFunction implements AsyncFunction<String, String> {

    private ListeningExecutorService executorService;

    public DownloadAsyncFunction(ListeningExecutorService executorService) {
      this.executorService = executorService;
    }

    @Override
    public ListenableFuture<String> apply(String s) throws Exception {
      return executorService.submit(new Callables.MapReduceCallable(s));
    }
  }
}
