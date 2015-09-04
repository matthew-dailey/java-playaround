package matt.thread;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class MyExecutorTest {

  private static final ConcurrentLinkedQueue<String> MESSAGES = new ConcurrentLinkedQueue<>();

  @Before
  public void setup() {
    MESSAGES.clear();
  }

  public static class MyCallable implements Callable<String> {
    @Override
    public String call() throws Exception {
      Thread.sleep(1000);
      return String.valueOf(System.currentTimeMillis());
    }
  }

  @Test
  public void executorTest() {
    ListeningExecutorService executor =
            MoreExecutors.listeningDecorator(
                    Executors.newFixedThreadPool(100));

    ListenableFuture<String> future = executor.submit(new MyCallable());
    Futures.addCallback(future, new FutureCallback<String>() {
      @Override
      public void onSuccess(String s) {
        MESSAGES.add("Success");
      }

      @Override
      public void onFailure(Throwable throwable) {
        MESSAGES.add("Whoops...");
      }
    });

    try {
      MESSAGES.add("Sleeping");
      Thread.sleep(2000);
      MESSAGES.add("Awaken");
      future.get();
    } catch (InterruptedException | ExecutionException e) {
      fail("Future failed.");
    }

    assertEquals("Sleeping", MESSAGES.poll());
    assertEquals("Success", MESSAGES.poll());
    assertEquals("Awaken", MESSAGES.poll());
    assertTrue(MESSAGES.isEmpty());
  }

}