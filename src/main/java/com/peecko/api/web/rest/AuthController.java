package com.peecko.api.web.rest;

import java.util.stream.Collectors;

import com.peecko.api.domain.Device;
import com.peecko.api.domain.User;
import com.peecko.api.security.UserDetailsImpl;
import com.peecko.api.repository.UserRepository;
import com.peecko.api.security.JwtUtils;
import com.peecko.api.utils.Common;
import com.peecko.api.web.payload.request.*;
import com.peecko.api.web.payload.response.InstallationsResponse;
import com.peecko.api.web.payload.response.MessageResponse;
import com.peecko.api.web.payload.response.PinCodeResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import com.peecko.api.web.payload.response.JwtResponse;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    final AuthenticationManager authenticationManager;
    final UserRepository userRepository;
    final PasswordEncoder encoder;
    final JwtUtils jwtUtils;

    public AuthController(AuthenticationManager authenticationManager, UserRepository userRepository, PasswordEncoder encoder, JwtUtils jwtUtils) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.encoder = encoder;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping("/signin")
    public ResponseEntity<?> signInUser(@Valid @RequestBody SignInRequest signInRequest) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(signInRequest.getUsername(), signInRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.toList());

        userRepository.addDevice(signInRequest);

        List<Device> installations = userRepository.getUserDevices(userDetails.getUsername());

        JwtResponse ret = new JwtResponse();
        ret.setToken(jwt);
        ret.setName(userDetails.getName());
        ret.setUsername(userDetails.getUsername());
        ret.setRoles(roles);
        ret.setMaxAllowed(3);
        ret.setInstallations(installations);
        ret.setInstallationsCount(installations.size());
        ret.setAccountStatus("OK"); // OK, ERROR
        ret.setAccountMessage("Account is verified");
        ret.setMembershipStatus("OK"); // OK, ERROR
        ret.setMembershipMessage("Membership is active");
        return ResponseEntity.ok(ret);
    }

    @PostMapping("/signout")
    public ResponseEntity<?> signOutUser(@Valid @RequestBody SignOutRequest signOutRequest) {
        userRepository.removeDevice(signOutRequest);
        return ResponseEntity.ok(new MessageResponse("OK", "User signed out successfully!"));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signUpUser(@Valid @RequestBody SignUpRequest signUpRequest) {
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity
                .badRequest()
                .body(new MessageResponse("ERROR", "Error: Username is already registered!"));
        }
        User user = new User()
            .name(signUpRequest.getName())
            .username(signUpRequest.getUsername())
            .language(signUpRequest.getLanguage().toUpperCase())
            .password(encoder.encode(signUpRequest.getPassword()));

        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("OK", "User registered successfully!"));
    }

    @GetMapping("/installations")
    public ResponseEntity<?> getInstallations() {
        List<Device> installations =userRepository.getUserDevices(getUsername());
        InstallationsResponse response = new InstallationsResponse(3, installations);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/active/{username}")
    public ResponseEntity<?> verifyAccount(@PathVariable String username) {
        if (!StringUtils.hasText(username)) {
            return ResponseEntity.ok(new MessageResponse("ERROR", "Email is required"));
        }
        if (!userRepository.existsByUsername(username)) {
            return ResponseEntity.ok(new MessageResponse("ERROR", "Email is not registered"));
        }
        int num = Common.generateDigit();
        if (num < 5) {
            return ResponseEntity.ok(new MessageResponse("OK", "Email verified successfully!"));
        } else {
            return ResponseEntity.ok(new MessageResponse("ERROR", "Email not verified yet, please try again"));
        }
    }

    @PostMapping("/pincode")
    public ResponseEntity<?> pinCode(@Valid @RequestBody PinCodeRequest request) {
        if (!userRepository.existsByUsername(request.getUsername())) {
            return ResponseEntity.badRequest().build();
        }
        String requestId = userRepository.generatePinCode(request.getUsername());
        PinCodeResponse response = new PinCodeResponse(requestId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/pincode/{requestId}")
    public ResponseEntity<?> pinCodeValidate(@PathVariable String requestId, @Valid @RequestBody PinValidationRequest request) {
        if (userRepository.isPinCodeValid(requestId, request.getPinCode())) {
            return ResponseEntity.ok(new MessageResponse("OK", "Verification code is valid!"));
        } else {
            return ResponseEntity.ok(new MessageResponse("ERROR", "Verification code is invalid"));
        }
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        if (userRepository.isPinCodeValid(request.getRequestId(), request.getPinCode())) {
            String email = userRepository.getPinCode(request.getRequestId()).getEmail();
            User user = userRepository.findByUsername(email).get();
            user.password(encoder.encode(request.getPassword()));
            userRepository.save(user);
            return ResponseEntity.ok(new MessageResponse("OK", "Password changed successfully!"));
        } else {
            return ResponseEntity.ok(new MessageResponse("ERROR", "Cannot change password, token expired or invalid"));
        }
    }

    private String getUsername() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userDetails.getUsername();
    }
}
