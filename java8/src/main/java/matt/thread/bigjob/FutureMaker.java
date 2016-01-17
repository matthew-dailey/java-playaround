package matt.thread.bigjob;

import java.util.List;

import com.google.common.util.concurrent.AsyncFunction;
import com.google.common.util.concurrent.ListenableFuture;

/**
 * A FutureMaker can produce {@link ListenableFuture}s and an {@link AsyncFunction} to connect them together to do some
 * processing on a list of strings
 */
public interface FutureMaker {

  ListenableFuture<String> createFirstStep(List<String> idChunk);

  ListenableFuture<String> createSecondStep(String s);

  AsyncFunction<String, String> createFirstToSecondFunction();
}
