package matt;

import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.assertEquals;

public class PathAggregatorTest {

  @Test
  public void removePath_test() {
    Filterer.PathAggregator aggregator = new Filterer.PathAggregator(TestHelpers.inputPaths, TestHelpers.allExemplars);

    Set<String> remaining = aggregator.getRemainingExemplars();
    assertEquals(remaining, TestHelpers.allExemplars);

    aggregator.removePath("ex1", "2004");
    aggregator.removePath("ex1", "2000");
    aggregator.removePath("ex1", "2001");
    aggregator.removePath("ex1", "2002");
    aggregator.removePath("ex1", "2003");

    remaining = aggregator.getRemainingExemplars();
    assertEquals(TestHelpers.allExemplars.size() - 1, remaining.size());
  }
}
