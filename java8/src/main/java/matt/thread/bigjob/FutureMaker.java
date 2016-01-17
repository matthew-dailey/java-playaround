package matt.thread.bigjob;

import java.util.List;

import com.google.common.util.concurrent.AsyncFunction;
import com.google.common.util.concurrent.ListenableFuture;

/**
 * A FutureMaker can produce {@link ListenableFuture}s and an {@link AsyncFunction} to connect them together to do some
 * processing on a list of strings
 */
public interface FutureMaker {

  /** The first step takes a List of Strings, and produces one String */
  ListenableFuture<String> createFirstStep(List<String> idChunk);

  /** The second step may transform the input string into another string */
  ListenableFuture<String> createSecondStep(String s);

  /** Create an AsyncFunction linking the other two methods asynchronously */
  AsyncFunction<String, String> createFirstToSecondFunction();
}
