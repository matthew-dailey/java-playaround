package matt;

import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.assertEquals;

public class FiltererTest {

  @Test
  public void filterExemplars_test() {

    Filterer filterer = new Filterer(TestHelpers.inputPaths);

    Set<String> results;

    // should not filter anything out
    results = filterer.filterExemplars(TestHelpers.allExemplars, TestHelpers.emptyRecords.iterator());
    assertEquals(TestHelpers.allExemplars.size(), results.size());

    // should filter out exemplar "ex2"
    results = filterer.filterExemplars(TestHelpers.allExemplars, TestHelpers.filterOut2.iterator());
    assertEquals(TestHelpers.allExemplars.size() - 1, results.size());

  }
}
