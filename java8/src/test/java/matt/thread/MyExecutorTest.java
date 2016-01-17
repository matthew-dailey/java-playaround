package matt.thread;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

import org.junit.Before;
import org.junit.Test;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

public class MyExecutorTest {

  private static final ConcurrentLinkedQueue<String> MESSAGES = new ConcurrentLinkedQueue<>();

  @Before
  public void setup() {
    MESSAGES.clear();
  }

  public static class MyCallable implements Callable<String> {
    @Override
    public String call() throws Exception {
      Thread.sleep(100);
      return String.valueOf(System.currentTimeMillis());
    }
  }

  @Test
  public void executorTest() {
    ListeningExecutorService executor =
            MoreExecutors.listeningDecorator(
                    Executors.newFixedThreadPool(100));

    // we're making a Future whose main computation will sleep, then finish.
    // we'll also attach callbacks for success and failure to edit the synchronous
    // MESSAGES queue
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

    String answer = "";
    try {
      MESSAGES.add("Sleeping");
      Thread.sleep(150);
      MESSAGES.add("Awaken");
      answer = future.get();
    } catch (InterruptedException | ExecutionException e) {
      fail("Future failed.");
    }

    // since the main thread sleeps longer than the processing thread's sleep,
    // we anticipate the callback to be called before the "Awaken" message here
    assertFalse(answer.isEmpty());
    assertEquals("Sleeping", MESSAGES.poll());
    assertEquals("Success", MESSAGES.poll());
    assertEquals("Awaken", MESSAGES.poll());
    assertTrue(MESSAGES.isEmpty());
  }

}