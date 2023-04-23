package com.peecko.api.web.payload.request;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Setter @Getter
public class ChangePasswordRequest {
    String password;
    String newPassword;
}
