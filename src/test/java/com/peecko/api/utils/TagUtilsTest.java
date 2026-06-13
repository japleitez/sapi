package com.peecko.api.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class TagUtilsTest {

   @Test
   void convertToList() {
      // Given
      String tags = "apple, banana, , orange, , lemon, , ";

      // When
      List<String> tagList = TagUtils.convertToList(tags);

      // Then
      assertEquals(4, tagList.size());
      assertEquals("apple", tagList.get(0));
      assertEquals("banana", tagList.get(1));
      assertEquals("orange", tagList.get(2));
      assertEquals("lemon", tagList.get(3));
   }

   @ParameterizedTest
   @MethodSource("provideStringsForTest")
   void convertToListWithEmptyValues(String tags) {

      // When
      List<String> tagList = TagUtils.convertToList(tags);

      // Then
      assertTrue(tagList.isEmpty());
   }

   static Stream<String> provideStringsForTest() {
      return Stream.of(null, "", " , ,,,,");
   }

}
