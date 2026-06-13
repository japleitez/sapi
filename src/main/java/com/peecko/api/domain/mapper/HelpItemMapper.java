package com.peecko.api.domain.mapper;

import com.peecko.api.domain.HelpItem;
import com.peecko.api.domain.dto.Help;

public class HelpItemMapper {

   private HelpItemMapper() {
       throw new IllegalStateException("Utility class");
   }
    public static Help help(HelpItem item) {
        return new Help(item.getQuestion(), item.getAnswer());
    }

}
