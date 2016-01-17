package matt.thread;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

import static org.junit.Assert.*;

public class BigJobServiceTest {

  @Test
  public void submitJob_test() throws ExecutionException, InterruptedException {
    ListeningExecutorService executor =
            MoreExecutors.listeningDecorator(
                    Executors.newFixedThreadPool(1));

    int maxIdsPerRun = 2;
    BigJobService service = new BigJobService(executor, maxIdsPerRun);

    List<String> ids = new ArrayList<String>();
    ids.add("a");
    ids.add("b");
    ids.add("c");
    ListenableFuture<List<String>> list = service.startJob(ids);

    final List<String> results = list.get();
    // a and b should be in first run, c in last
    // results should be concatenated then upper-cased
    assertEquals("AB", results.get(0));
    assertEquals("C", results.get(1));
  }

}