package matt.benchmarks;

import com.google.common.base.Supplier;
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

  private static final HttpRequestBase GET = new HttpGet();
  private static final HttpRequestBase HEAD = new HttpHead();
  private static final HttpRequestBase POST = new HttpPost();
  private static final HttpRequestBase PUT = new HttpPut();
  private static final HttpRequestBase DELETE = new HttpDelete();
  private static final HttpRequestBase TRACE = new HttpTrace();
  private static final HttpRequestBase OPTIONS = new HttpOptions();
  private static final HttpRequestBase PATCH = new HttpPatch();

  private static final Supplier<HttpRequestBase> SUPPLIER = new Supplier<HttpRequestBase>() {
    @Override
    public HttpRequestBase get() {
      return new HttpGet();
    }
  };
  private static final Supplier<HttpRequestBase> SUPPLIER_PRE_CONSTRUCTED = new Supplier<HttpRequestBase>() {
    @Override
    public HttpRequestBase get() {
      return GET;
    }
  };

  @Benchmark
  public HttpRequestBase benchmarkConstructingSwitch() {
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
  public HttpRequestBase benchmarkPreConstructedSwitch() {
    HttpRequestBase request;
    switch (HTTP_VERB) {
    case HttpGet.METHOD_NAME:
      request = GET;
      break;
    case HttpHead.METHOD_NAME:
      request = HEAD;
      break;
    case HttpPost.METHOD_NAME:
      request = POST;
      break;
    case HttpPut.METHOD_NAME:
      request = PUT;
      break;
    case HttpDelete.METHOD_NAME:
      request = DELETE;
      break;
    case HttpTrace.METHOD_NAME:
      request = TRACE;
      break;
    case HttpOptions.METHOD_NAME:
      request = OPTIONS;
      break;
    case HttpPatch.METHOD_NAME:
      request = PATCH;
      break;
    default:
      throw new RuntimeException(String.format("%s is not a known HTTP verb", HTTP_VERB));
    }
    return request;
  }

  @Benchmark
  public HttpRequestBase benchmarkConstructingIfElse() {
    HttpRequestBase request;
    if (HTTP_VERB.equals(HttpGet.METHOD_NAME)) {
      request = new HttpGet();
    } else if (HTTP_VERB.equals(HttpHead.METHOD_NAME)) {
      request = new HttpHead();
    } else if (HTTP_VERB.equals(HttpPost.METHOD_NAME)) {
      request = new HttpPost();
    } else if (HTTP_VERB.equals(HttpPut.METHOD_NAME)) {
      request = new HttpPut();
    } else if (HTTP_VERB.equals(HttpDelete.METHOD_NAME)) {
      request = new HttpDelete();
    } else if (HTTP_VERB.equals(HttpTrace.METHOD_NAME)) {
      request = new HttpTrace();
    } else if (HTTP_VERB.equals(HttpOptions.METHOD_NAME)) {
      request = new HttpOptions();
    } else if (HTTP_VERB.equals(HttpPatch.METHOD_NAME)) {
      request = new HttpPatch();
    } else {
      throw new RuntimeException(String.format("%s is not a known HTTP verb", HTTP_VERB));
    }
    return request;
  }

  @Benchmark
  public HttpRequestBase benchmarkPreConstructedIfElse() {
    HttpRequestBase request;
    if (HTTP_VERB.equals(HttpGet.METHOD_NAME)) {
      request = GET;
    } else if (HTTP_VERB.equals(HttpHead.METHOD_NAME)) {
      request = HEAD;
    } else if (HTTP_VERB.equals(HttpPost.METHOD_NAME)) {
      request = POST;
    } else if (HTTP_VERB.equals(HttpPut.METHOD_NAME)) {
      request = PUT;
    } else if (HTTP_VERB.equals(HttpDelete.METHOD_NAME)) {
      request = DELETE;
    } else if (HTTP_VERB.equals(HttpTrace.METHOD_NAME)) {
      request = TRACE;
    } else if (HTTP_VERB.equals(HttpOptions.METHOD_NAME)) {
      request = OPTIONS;
    } else if (HTTP_VERB.equals(HttpPatch.METHOD_NAME)) {
      request = PATCH;
    } else {
      throw new RuntimeException(String.format("%s is not a known HTTP verb", HTTP_VERB));
    }
    return request;
  }

  @Benchmark
  public HttpRequestBase benchmarkConstructingSupplier() {
    return SUPPLIER.get();
  }

  @Benchmark
  public HttpRequestBase benchmarkPreConstructedSupplier() {
    return SUPPLIER_PRE_CONSTRUCTED.get();
  }
}
