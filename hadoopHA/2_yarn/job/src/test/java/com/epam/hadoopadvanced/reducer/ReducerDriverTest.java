package com.epam.hadoopadvanced.reducer;

import org.junit.Assert;
import org.junit.Test;

public class ReducerDriverTest {

  @Test
  public void initEmptyArgsTest() {
    ReducerDriver reducerDriver = new ReducerDriver();
    boolean result = reducerDriver.init(new String[]{});

    Assert.assertFalse(result);
  }

  @Test
  public void initValidArgumentsTest() {
    ReducerDriver reducerDriver = new ReducerDriver();
    boolean result = reducerDriver.init(new String[]{"-o", "output/file"});

    Assert.assertTrue(result);
  }

  @Test
  public void initMapperArgsTest() {
    ReducerDriver reducerDriver = new ReducerDriver();
    boolean result = reducerDriver.init(new String[]{"-o", "output/file", "reducer argument"});

    Assert.assertTrue(result);
  }

  @Test
  public void initInvalidFlagTest() {
    ReducerDriver reducerDriver = new ReducerDriver();
    boolean result = reducerDriver.init(new String[]{"-o", "output/file", "-s", "1", "-end_line", "2", "-i", "file"});

    Assert.assertFalse(result);
  }

}