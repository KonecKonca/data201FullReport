package com.epam.hadoopadvanced.reducer;

import com.epam.hadoopadvanced.util.CommonConstant;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * The type Hotel reducer.
 */
public class HotelReducer {

    /**
     * Chooses top 3 most popular hotels between couples.
     *
     * @param stream stream of data processed by {@link com.epam.hadoopadvanced.mapper.HotelMapper}
     * @return the HashMap with 3 most popular hotels between couples
     */
    public Map<String, Long> reduce(Stream<String> stream) {

    return stream
            .map(l -> l.split(CommonConstant.KEY_VALUE_SEPARATOR))
            // group by composite key
            .collect(Collectors.groupingBy(
                    r -> r[0],
                    Collectors.summingLong(r -> Long.parseLong(r[1]))
                  )
            )
            .entrySet()
            .stream()
            // sort by distinct order
            .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
            // leave only top 3
            .limit(3)
            .collect(HashMap::new, (map, entry) -> map.put(entry.getKey(), entry.getValue()), Map::putAll);
  }

}
