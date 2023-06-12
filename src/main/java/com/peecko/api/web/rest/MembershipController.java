package com.peecko.api.web.rest;

import com.peecko.api.domain.User;
import com.peecko.api.repository.UserRepository;
import com.peecko.api.web.payload.request.ActivationRequest;
import com.peecko.api.web.payload.response.MessageResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/membership")
public class MembershipController extends BaseController {

    final UserRepository userRepository;

    public MembershipController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PutMapping("/activate")
    public ResponseEntity<?> activate(@Valid @RequestBody ActivationRequest activationRequest) {
        String license = activationRequest.getLicense();
        if (!StringUtils.hasLength(license) && license.length() != 20) {
            return ResponseEntity.ok(new MessageResponse("ERROR", "License must be 20 char length"));
        }
        if (UserRepository.DEFAULT_LICENSE.equals(license)) {
            User user = getActiveUser(userRepository);
            user.license(license);
            userRepository.save(user);
            return ResponseEntity.ok(new MessageResponse("OK", "License activated successfully!"));
        } else {
            return ResponseEntity.ok(new MessageResponse("ERROR", "License is invalid or expired."));
        }
    }

}
