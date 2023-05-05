package com.peecko.api.web.rest;

import java.util.stream.Collectors;

import com.peecko.api.domain.User;
import com.peecko.api.security.UserDetailsImpl;
import com.peecko.api.repository.UserRepository;
import com.peecko.api.security.JwtUtils;
import com.peecko.api.utils.Common;
import com.peecko.api.web.payload.request.LoginRequest;
import com.peecko.api.web.payload.request.SignupRequest;
import com.peecko.api.web.payload.request.ValidateUserRequest;
import com.peecko.api.web.payload.response.MessageResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
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
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.toList());

        JwtResponse ret = new JwtResponse();
        ret.setToken(jwt);
        ret.setName(userDetails.getName());
        ret.setUsername(userDetails.getUsername());
        ret.setRoles(roles);
        return ResponseEntity.ok(ret);
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
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

    @GetMapping("/account")
    public ResponseEntity<?> verifyAccount(@Valid @RequestBody ValidateUserRequest request) {
        if (!StringUtils.hasText(request.getEmail())) {
            return ResponseEntity.ok(new MessageResponse("ERROR", "Email is required"));
        }
        Common.sleep(1);
        if (Common.getRandomNum() < 5) {
            return ResponseEntity.ok(new MessageResponse("OK", "Account verified successfully!"));
        } else {
            return ResponseEntity.ok(new MessageResponse("ERROR", "Account not verified yet, please try again"));
        }
    }

}
