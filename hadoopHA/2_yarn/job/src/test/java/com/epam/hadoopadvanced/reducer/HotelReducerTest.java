package com.epam.hadoopadvanced.reducer;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Stream;
import org.junit.Assert;
import org.junit.Test;

public class HotelReducerTest {

  @Test
  public void reduceDifferentValuesTest() {
    Map<String, Long> result = new HotelReducer().reduce(Stream.of(
        "1,1,1\t1",
        "1,2,1\t3",
        "1,1,1\t5",
        "2,2,2\t10",
        "1,2,3\t5"
    ));
    Map<String, Long> expected = new LinkedHashMap<>();
    expected.put("2,2,2", 10L);
    expected.put("1,1,1", 6L);
    expected.put("1,2,3", 5L);

    Assert.assertEquals(expected, result);
  }

  @Test
  public void reduceSameValuesTest() {
    Map<String, Long> result = new HotelReducer().reduce(Stream.of(
        "1,1,1\t1",
        "1,1,1\t5"
    ));
    Map<String, Long> expected = new LinkedHashMap<>();
    expected.put("1,1,1", 6L);

    Assert.assertEquals(expected, result);
  }

  @Test
  public void reduceEmptyStreamTest() {
    Map<String, Long> result = new HotelReducer().reduce(Stream.empty());

    Assert.assertTrue(result.isEmpty());
  }

}