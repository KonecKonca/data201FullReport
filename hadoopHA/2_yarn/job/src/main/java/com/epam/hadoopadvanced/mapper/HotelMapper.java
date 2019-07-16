package com.epam.hadoopadvanced.mapper;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * The type Hotel mapper.
 */
public class HotelMapper{

  /**
   * Extract and count hotels from the
   * stream of Expedia hotel booking events data.
   *
   * @param stream Expedia hotel booking events
   * @return the map with hotel and its booking by couples count
   */
  public Map<String, Long> map(Stream<String> stream) {
    return stream
            .map(l -> l.split(","))
            // leave only couples
            .filter(c -> c[13].trim().equals("2"))
            // leave only necessary fields (composite key)
            .map(c -> String.format("%s,%s,%s", c[20].trim(), c[21].trim(), c[22].trim()))
            // add counts for every unique composite key
            .collect(Collectors.groupingBy(h -> h, Collectors.counting()));
  }

}
