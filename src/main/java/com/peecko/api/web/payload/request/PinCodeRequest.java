package com.peecko.api.web.payload.request;

import com.peecko.api.domain.enumeration.Verification;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Setter @Getter
public class PinCodeRequest {
    String username;
}
