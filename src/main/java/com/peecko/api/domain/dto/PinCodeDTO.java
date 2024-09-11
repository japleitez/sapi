package com.peecko.api.domain.dto;

import lombok.*;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class PinCodeDTO {
    @EqualsAndHashCode.Include
    String requestId;
    String pinCode;
    String email;
    LocalDateTime expireAt;
}
