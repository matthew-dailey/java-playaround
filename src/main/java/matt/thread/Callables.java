package matt.thread;

import java.util.concurrent.Callable;

public class Callables {

  public static class DownloadCallable implements Callable<String> {
    private MyWorker worker;
    public DownloadCallable(MyWorker worker) {
      this.worker = worker;
    }

    @Override
    public String call() throws Exception {
      return worker.doWork();
    }
  }

  public static class MapReduceCallable implements Callable<String> {
    private String input;

    public MapReduceCallable(String input) {
      this.input = input;
    }

    @Override
    public String call() throws Exception {
      return input + " is done";
    }
  }
}
