package com.peecko.api.web.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@AllArgsConstructor @Setter @Getter
public class MessageResponse {
    String code;
    String message;
}
