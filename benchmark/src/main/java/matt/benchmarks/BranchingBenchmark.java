package matt.benchmarks;

import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpOptions;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpTrace;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

@State(Scope.Thread)
public class BranchingBenchmark {

  private static final String HTTP_VERB = "GET";

  @Benchmark
  public HttpRequestBase benchmarkSwitch() {
    HttpRequestBase request;
    switch (HTTP_VERB) {
    case HttpGet.METHOD_NAME:
      request = new HttpGet();
      break;
    case HttpHead.METHOD_NAME:
      request = new HttpHead();
      break;
    case HttpPost.METHOD_NAME:
      request = new HttpPost();
      break;
    case HttpPut.METHOD_NAME:
      request = new HttpPut();
      break;
    case HttpDelete.METHOD_NAME:
      request = new HttpDelete();
      break;
    case HttpTrace.METHOD_NAME:
      request = new HttpTrace();
      break;
    case HttpOptions.METHOD_NAME:
      request = new HttpOptions();
      break;
    case HttpPatch.METHOD_NAME:
      request = new HttpPatch();
      break;
    default:
      throw new RuntimeException(String.format("%s is not a known HTTP verb", HTTP_VERB));
    }
    return request;
  }

  @Benchmark
  public HttpRequestBase benchmarkIfElse() {
    HttpRequestBase request;
    if (HTTP_VERB.equals(HttpGet.METHOD_NAME)) {
      request = new HttpGet();
    } else if (HTTP_VERB.equals(HttpHead.METHOD_NAME)) {
      request = new HttpHead();
    } else if (HTTP_VERB.equals(HttpPost.METHOD_NAME)) {
      request = new HttpPost();
//    case HttpPut.METHOD_NAME:
//      request = new HttpPut();
//      break;
//    case HttpDelete.METHOD_NAME:
//      request = new HttpDelete();
//      break;
//    case HttpTrace.METHOD_NAME:
//      request = new HttpTrace();
//      break;
//    case HttpOptions.METHOD_NAME:
//      request = new HttpOptions();
//      break;
//    case HttpPatch.METHOD_NAME:
//      request = new HttpPatch();
//      break;
    } else {
      throw new RuntimeException(String.format("%s is not a known HTTP verb", HTTP_VERB));
    }
    return request;
}
}
