package matt.thread;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

import org.junit.Test;

import com.google.common.util.concurrent.AsyncFunction;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

public class AsyncFunctionsTest {

  @Test
  public void testAll() throws ExecutionException, InterruptedException {

    final String input = "hello";

    // can express as a simple lambda in Java 8
    // final MyWorker worker = () -> input;
    MyWorker worker = () -> input;

    ListeningExecutorService executor =
            MoreExecutors.listeningDecorator(
                    Executors.newFixedThreadPool(1));

    // guava executors can execute a Callable, so let's do it
    Callable<String> downloadCallable = new Callables.DownloadCallable(worker);
    ListenableFuture<String> downloadFuture = executor.submit(downloadCallable);

    AsyncFunction<String, String> downloadAsyncFunction =
            new AsyncFunctions.DownloadAsyncFunction(executor);

    ListenableFuture<String> finalFuture = Futures.transform(downloadFuture, downloadAsyncFunction, executor);
    final String result = finalFuture.get();
    assertEquals(result, "hello is done");

  }

}