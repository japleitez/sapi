package com.peecko.api.domain.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@Setter
@Getter
@Accessors(chain = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class PinCode {
    @EqualsAndHashCode.Include
    String requestId;
    String pinCode;
    String email;
    LocalDateTime expireAt;
}
