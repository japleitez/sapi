package com.peecko.api.web.payload.response;

import com.peecko.api.domain.dto.CategoryDTO;

import java.util.List;

public record LibraryResponse(String greeting, List<CategoryDTO> categories) {
}
