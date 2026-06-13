package com.peecko.api.utils;

import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TagUtils {

   private TagUtils() {
      throw new IllegalStateException("Utility class");
   }

   public static List<String> convertToList(String tags) {
      if (!StringUtils.hasText(tags)) {
         return new ArrayList<>();
      }
      return Arrays.stream(tags.split(","))
            .map(String::trim)
            .filter(tag -> !tag.isEmpty())
            .toList();
   }

}
