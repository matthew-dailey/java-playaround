package matt.java8;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class LambdasTest {

  public static final String MY_NAME_IS = "my name is ";
  public static final String HELLO = "hello";
  public static final String ANSWER = MY_NAME_IS + HELLO;

  @Test
  public void lambdaTest() {
    Function<String, String> func = (String s) -> s + HELLO;
    String result = func.apply(MY_NAME_IS);
    assertEquals(ANSWER, result);

    // brief version of definition
    func = s -> s + HELLO;
    result = func.apply(MY_NAME_IS);
    assertEquals(ANSWER, result);

//    result = ((String s) -> s + HELLO).apply(MY_NAME_IS);
  }

  @Test
  public void lambdaComparatorTest() {
    List<Integer> intList = Arrays.asList(1, 2, 3, 4, 5);
    List<Integer> outList = new ArrayList<>();

    // forEach applies a function to each element in the iterable
    intList.forEach(i -> outList.add(i + 1));
    assertEquals(Arrays.asList(2, 3, 4, 5, 6), outList);

    outList.removeIf(i -> i == 3);
    assertEquals(Arrays.asList(2, 4, 5, 6), outList);

    // use lambda to implement a java.util.Comparator interface, sorting descending
    Collections.sort(intList, (i1, i2) -> i2.compareTo(i1));

    assertEquals(intList, Arrays.asList(5, 4, 3, 2, 1));

    assertFalse(intList.stream().anyMatch(i -> i % 3 == 0 && i % 2 == 0));

    List<String> stringified = intList.stream()
            .map(i -> "" + i).collect(Collectors.toList());
    assertEquals(Arrays.asList("5", "4", "3", "2", "1"), stringified);
  }

  @Test
  public void concatTest() {
    List<String> places = Arrays.asList("Here", "There", "Anywhere");
    String result = places.stream()
            .reduce("", String::concat);
    assertEquals("HereThereAnywhere", result);
  }
}
