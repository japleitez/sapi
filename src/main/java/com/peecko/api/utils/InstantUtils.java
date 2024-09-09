package com.peecko.api.utils;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class InstantUtils {

   public static Instant createInstantFromDays(int daysFromToday) {
      // Get the current Instant
      Instant now = Instant.now();

      // Add the specified number of days to the current Instant
      return now.plus(daysFromToday, ChronoUnit.DAYS);
   }

}
