package com.peecko.api.domain;

import lombok.*;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Accessors(chain = true)
public class Notification {
    private Long id;
    private String title;
    private String message;
    private String image;
    private String video;
    private String date;
    private Boolean viewed;
}
