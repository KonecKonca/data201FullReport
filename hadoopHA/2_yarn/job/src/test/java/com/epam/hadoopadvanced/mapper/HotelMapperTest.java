package com.epam.hadoopadvanced.mapper;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;
import org.junit.Assert;
import org.junit.Test;

public class HotelMapperTest {

  private HotelMapper mapper = new HotelMapper();

  @Test
  public void mapSampleDataTest() {
    Map<String, Long> result = mapper.map(Stream.of(
        "0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23",
        ".,.,.,.,.,.,.,.,.,.,..,..,..,2 ,..,..,..,..,..,..,21,50,62,..",
        ".,.,.,.,.,.,.,.,.,.,..,..,..,2 ,..,..,..,..,..,..,21,50,62,..",
        ".,.,.,.,.,.,.,.,.,.,..,..,..,2 ,..,..,..,..,..,..,21,50,14,..",
        ".,.,.,.,.,.,.,.,.,.,..,..,..,1 ,..,..,..,..,..,..,21,50,14,..",
        ".,.,.,.,.,.,.,.,.,.,..,..,..,1 ,..,..,..,..,..,..,21,50,14,..",
        ".,.,.,.,.,.,.,.,.,.,..,..,..,3 ,..,..,..,..,..,..,21,50,67,.."
    ));
    Map<String, Long> expected = new HashMap<>();
    expected.put("21,50,14", 1L);
    expected.put("21,50,62", 2L);

    Assert.assertEquals(expected, result);
  }
  @Test
  public void mapDataWithWhitespacesTest() {
    Map<String, Long> result = mapper.map(Stream.of(
        "0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23",
        ".,.,.,.,.,.,.,.,.,.,..,..,..,2,..,..,..,..,..,.., 21,50,62,..",
        ".,.,.,.,.,.,.,.,.,.,..,..,..,2 ,..,..,..,..,..,..,21 ,50,62,..",
        ".,.,.,.,.,.,.,.,.,.,..,..,.., 2 ,..,..,..,..,..,.., 21 ,50,62,.."
    ));
    Map<String, Long> expected = new HashMap<>();
    expected.put("21,50,62", 3L);

    Assert.assertEquals(expected, result);
  }

  @Test
  public void mapNoCouplesHotelsTest() {
    Map<String, Long> result = mapper.map(Stream.of(
        "0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23",
        ".,.,.,.,.,.,.,.,.,.,..,..,..,1 ,..,..,..,..,..,..,21,50,62,..",
        ".,.,.,.,.,.,.,.,.,.,..,..,..,3 ,..,..,..,..,..,..,21,50,62,..",
        ".,.,.,.,.,.,.,.,.,.,..,..,..,4 ,..,..,..,..,..,..,21,50,14,..",
        ".,.,.,.,.,.,.,.,.,.,..,..,..,1 ,..,..,..,..,..,..,21,50,14,..",
        ".,.,.,.,.,.,.,.,.,.,..,..,..,1 ,..,..,..,..,..,..,21,50,14,..",
        ".,.,.,.,.,.,.,.,.,.,..,..,..,3 ,..,..,..,..,..,..,21,50,67,.."
    ));

    Assert.assertTrue(result.isEmpty());
  }

  @Test
  public void mapEmptyStreamTest() {
    Assert.assertTrue(mapper.map(Stream.empty()).isEmpty());
  }


}