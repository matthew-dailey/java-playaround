package matt;

import org.apache.accumulo.core.data.Key;
import org.apache.accumulo.core.data.Value;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class TestHelpers {

  public static final Value EMPTY_VALUE = new Value("".getBytes());
  public static List<Map.Entry<Key, Value>> emptyRecords;
  public static List<Map.Entry<Key, Value>> filterOut2;

  public static Set<String> inputPaths = new TreeSet<String>();
  public static Set<String> allExemplars = new TreeSet<String>();
  static {
    emptyRecords = new ArrayList<Map.Entry<Key, Value>>();

    filterOut2 = new ArrayList<Map.Entry<Key, Value>>(emptyRecords);
    filterOut2.add(new AbstractMap.SimpleEntry<Key, Value>(new Key("ex2", "2000", "colqual"), EMPTY_VALUE));
    filterOut2.add(new AbstractMap.SimpleEntry<Key, Value>(new Key("ex2", "2001", "colqual"), EMPTY_VALUE));
    filterOut2.add(new AbstractMap.SimpleEntry<Key, Value>(new Key("ex2", "2002", "colqual"), EMPTY_VALUE));
    filterOut2.add(new AbstractMap.SimpleEntry<Key, Value>(new Key("ex2", "2003", "colqual"), EMPTY_VALUE));
    filterOut2.add(new AbstractMap.SimpleEntry<Key, Value>(new Key("ex2", "2004", "colqual"), EMPTY_VALUE));

    inputPaths.add("2000");
    inputPaths.add("2001");
    inputPaths.add("2002");
    inputPaths.add("2003");
    inputPaths.add("2004");

    allExemplars.add("ex1");
    allExemplars.add("ex2");
    allExemplars.add("ex3");
    allExemplars.add("ex4");
    allExemplars.add("ex5");
  }
}
