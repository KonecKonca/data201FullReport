package com.epam.hadoopadvanced.mapper;

import org.junit.Assert;
import org.junit.Test;

public class MapperDriverTest {

  @Test
  public void initEmptyArgsTest() {
    MapperDriver mapperDriver = new MapperDriver();
    boolean result = mapperDriver.init(new String[]{});

    Assert.assertFalse(result);
  }

  @Test
  public void initPartialArgsTest() {
    MapperDriver mapperDriver = new MapperDriver();
    boolean result = mapperDriver.init(new String[]{"-s", "1"});

    Assert.assertFalse(result);
  }

  @Test
  public void initValidArgumentsTest() {
    MapperDriver mapperDriver = new MapperDriver();
    boolean result = mapperDriver.init(new String[]{"-s", "1", "-end_line", "2", "-i", "file"});

    Assert.assertTrue(result);
  }

  @Test
  public void initMapperArgsTest() {
    MapperDriver mapperDriver = new MapperDriver();
    boolean result = mapperDriver.init(new String[]{"-s", "1", "-end_line", "2", "-i", "file", "mapper argument"});

    Assert.assertTrue(result);
  }

  @Test
  public void initInvalidFlagTest() {
    MapperDriver mapperDriver = new MapperDriver();
    boolean result = mapperDriver.init(new String[]{"-a", "invalid", "-s", "1", "-end_line", "2", "-i", "file"});

    Assert.assertFalse(result);
  }

}