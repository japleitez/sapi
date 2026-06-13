package com.peecko.api.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NameUtilsTest {

   @Test
   void toCamelCase() {
      // Given
      String input = "   john   DOE  SMITH  ";

      // When
      String output = NameUtils.toCamelCase(input);

      // Then
      assertEquals("John Doe Smith", output);
   }

   @Test
   void toCamelCaseWithEmptyInput() {
      // Given
      String input = "   ";

      // When
      String output = NameUtils.toCamelCase(input);

      // Then
      assertEquals("", output);
   }

}
