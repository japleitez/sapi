package com.peecko.api.utils;

import java.time.LocalDate;

public class PeriodUtils {

   private PeriodUtils() {
      throw new IllegalStateException("Utility class");
   }

   public static int current() {
      LocalDate today = LocalDate.now();
      int year = today.getYear();
      int month = today.getMonthValue();
      return year * 100 + month;
   }

   public static int previous() {
      LocalDate today = LocalDate.now();
      LocalDate previousMonth = today.minusMonths(1);
      return previousMonth.getYear() * 100 + previousMonth.getMonthValue();
   }

}
