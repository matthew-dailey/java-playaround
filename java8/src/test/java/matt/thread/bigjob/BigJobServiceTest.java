package matt.thread.bigjob;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

import org.junit.Test;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

public class BigJobServiceTest {

  private void testBigJobService(ListeningExecutorService executor,
                                 FutureMaker futureMaker, int maxIdsPerRun) throws ExecutionException, InterruptedException {
    BigJobService service = new BigJobService(executor, futureMaker, maxIdsPerRun);

    List<String> ids = Arrays.asList("a", "b", "c");
    ListenableFuture<List<String>> list = service.startJob(ids);

    final List<String> results = list.get();
    // a and b should be in first run, c in last
    // results should be concatenated then upper-cased
    assertEquals("AB", results.get(0));
    assertEquals("C", results.get(1));
  }

  @Test
  public void submitJob_test() throws ExecutionException, InterruptedException {
    ListeningExecutorService executor =
            MoreExecutors.listeningDecorator(
                    Executors.newFixedThreadPool(1));

    final int maxIdsPerRun = 2;

    // run this test with the java7 and java8 implementations, and show they are
    // functionally the same
    testBigJobService(executor, new FutureMaker7(executor), maxIdsPerRun);
    testBigJobService(executor, new FutureMaker8(executor), maxIdsPerRun);
  }

}