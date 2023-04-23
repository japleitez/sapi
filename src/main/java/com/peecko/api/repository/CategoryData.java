package com.peecko.api.repository;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Data
@Setter @Getter @Accessors(chain = true, fluent = true)
public class CategoryData {
    public String code;
    public String title;
    public String filename;

    public CategoryData(String code, String title, String filename) {
        this.code = code;
        this.title = title;
        this.filename = filename;
    }
}
