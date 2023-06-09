package com.peecko.api.web.payload.response;

import com.peecko.api.domain.Category;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
@AllArgsConstructor @Setter @Getter
public class LibraryResponse {
    private String greeting;
    private List<Category> categories;
}
