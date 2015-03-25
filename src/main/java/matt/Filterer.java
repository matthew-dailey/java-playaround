package matt;

import org.apache.accumulo.core.data.Key;
import org.apache.accumulo.core.data.Value;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * The Filterer is used to filter out exemplars where all of their paths have already been processed.
 */
public class Filterer {

  // take in a set of input paths
  // take in a set of exemplars
  // scan through iterator
  // look at exemplars's paths
  //   if all of the input paths are in the exemplar's paths, ignore the exemplar

  private Set<String> inputPaths;

  public Filterer(Set<String> inputPaths) {
    this.inputPaths = inputPaths;
  }

  public Set<String> filterExemplars(Set<String> exemplarIds, Iterator<Map.Entry<Key, Value>> exemplarAndPathsIterator) {
    Map.Entry<Key, Value> exemplarAndPathEntry;

    PathAggregator aggregator = new PathAggregator(inputPaths, exemplarIds);
    while (exemplarAndPathsIterator.hasNext()) {
      exemplarAndPathEntry = exemplarAndPathsIterator.next();

      final Key key = exemplarAndPathEntry.getKey();
      final String exemplarId = key.getRow().toString();
      final String path = key.getColumnFamily().toString();

      aggregator.removePath(exemplarId, path);
    }

    return aggregator.getRemainingExemplars();
  }

  /**
   * Keeps track of remaining paths for an exemplar, and removes the exemplar when needed.
   * Assumes exemplarIds come in sorted order
   */
  public static class PathAggregator {
    private Set<String> allPaths;

    private Set<String> remaininingExemplars;

    private String currentExemplar;
    private AggregatorState state;

    public PathAggregator(Set<String> allPaths, Set<String> allExemplars) {
      this.allPaths = new TreeSet<String>(allPaths);
      this.remaininingExemplars = new TreeSet<String>(allExemplars);
    }

    /** Remove a path for an exemplar. */
    public void removePath(String exemplarId, String path) {
      if (currentExemplar == null) {
        resetState(exemplarId);
      } else if (!currentExemplar.equals(exemplarId)) {
        // new exemplar, reset state the same way
        resetState(exemplarId);
      }

      state.remove(path);
      if (state.hasNoPathsRemaining()) {
        remaininingExemplars.remove(currentExemplar);
      }
    }

    public Set<String> getRemainingExemplars() {
      return remaininingExemplars;
    }

    private void resetState(String exemplarId) {
      this.currentExemplar = exemplarId;
      state = new AggregatorState(allPaths);
    }

    /** simplified Set of Strings */
    public static class AggregatorState {
      private Set<String> remainingPaths;

      public AggregatorState(Set<String> remainingPaths) {
        this.remainingPaths = new TreeSet<String>(remainingPaths);
      }

      public void remove(String path) {
        remainingPaths.remove(path);
      }

      public boolean hasNoPathsRemaining() {
        return remainingPaths.isEmpty();
      }
    }

  } // PathAggregator
} // Filterer