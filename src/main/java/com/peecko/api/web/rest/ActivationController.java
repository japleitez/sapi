package com.peecko.api.web.rest;

import com.peecko.api.domain.User;
import com.peecko.api.repository.UserRepository;
import com.peecko.api.web.payload.request.ActivationRequest;
import com.peecko.api.web.payload.response.MessageResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/membership")
public class ActivationController {

    final UserRepository userRepository;

    public ActivationController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping("/activate")
    public ResponseEntity<?> activate(@Valid @RequestBody ActivationRequest activationRequest) {
        String license = activationRequest.getLicense();
        if (!StringUtils.hasLength(license)) {
            return ResponseEntity.ok(new MessageResponse("ERROR", "License must be provided"));
        }
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.findByUsername(userDetails.getUsername()).get();
        if (user.license().equals(license)) {
            return ResponseEntity.ok(new MessageResponse("OK", "License activated successfully!"));
        } else {
            return ResponseEntity.ok(new MessageResponse("ERROR", "License is not valid"));
        }
    }

}
