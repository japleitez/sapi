package com.peecko.api.web.rest;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

import com.peecko.api.domain.Device;
import com.peecko.api.domain.User;
import com.peecko.api.security.UserDetailsImpl;
import com.peecko.api.repository.UserRepository;
import com.peecko.api.security.JwtUtils;
import com.peecko.api.utils.EmailUtils;
import com.peecko.api.utils.NameUtils;
import com.peecko.api.utils.PasswordUtils;
import com.peecko.api.web.payload.request.*;
import com.peecko.api.web.payload.response.*;
import jakarta.validation.Valid;
import org.springframework.context.MessageSource;
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

import static com.peecko.api.utils.Common.MAX_ALLOWED;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    final MessageSource messageSource;
    final AuthenticationManager authenticationManager;
    final UserRepository userRepository;
    final PasswordEncoder encoder;
    final JwtUtils jwtUtils;

    public AuthController(MessageSource messageSource, AuthenticationManager authenticationManager, UserRepository userRepository, PasswordEncoder encoder, JwtUtils jwtUtils) {
        this.messageSource = messageSource;
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.encoder = encoder;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signUp(@Valid @RequestBody SignUpRequest signUpRequest) {
        boolean isValidEmail = EmailUtils.isValid(signUpRequest.getUsername());
        if (!isValidEmail) {
            return ResponseEntity.ok(new MessageResponse("ERROR", getMessage("email.valid.nok")));
        }
        boolean isValidPassword = PasswordUtils.isValid(signUpRequest.getPassword());
        if (!isValidPassword) {
            return ResponseEntity.ok(new MessageResponse("ERROR", getMessage("password.valid.nok")));
        }
        boolean isValidName = NameUtils.isValid(signUpRequest.getName());
        if (!isValidName) {
            return ResponseEntity.ok(new MessageResponse("ERROR", getMessage("name.valid.nok")));
        }
        String email = signUpRequest.getUsername().toLowerCase();
        if (userRepository.existsByUsername(email)) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("ERROR", getMessage("email.duplicated")));
        }
        String name = NameUtils.camel(signUpRequest.getName());
        User user = new User()
                .name(name)
                .username(email)
                .language(signUpRequest.getLanguage().toUpperCase())
                .password(encoder.encode(signUpRequest.getPassword()));

        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("OK", getMessage("user.signup.ok")));
    }

    @PostMapping("/signin")
    public ResponseEntity<?> signIn(@Valid @RequestBody SignInRequest req) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        // roles not used but code remains for future reference
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.toList());

        userRepository.addDevice(req, jwt);

        LoginResponse login = userRepository.findByUsername(req.getUsername()).map(this::userToLogin).orElse(new LoginResponse());
        login.setToken(jwt);

        return ResponseEntity.ok(login);
    }

    @PostMapping("/signout")
    public ResponseEntity<?> signOut(@Valid @RequestBody SignOutRequest signOutRequest) {
        userRepository.removeDevice(signOutRequest);
        return ResponseEntity.ok(new MessageResponse("OK", getMessage("user.logoff.ok")));
    }


    @GetMapping("/installations")
    public ResponseEntity<?> getInstallations() {
        List<Device> installations = new ArrayList<>();
        installations.addAll(userRepository.getUserDevices(getUsername()));
        InstallationsResponse response = new InstallationsResponse(MAX_ALLOWED, installations);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/active/{username}")
    public ResponseEntity<?> verifyAccount(@PathVariable String username) {
        if (!StringUtils.hasText(username)) {
            return ResponseEntity.ok(new MessageResponse("ERROR", getMessage("email.required")));
        }
        if (!userRepository.existsByUsername(username)) {
            return ResponseEntity.ok(new MessageResponse("ERROR", getMessage("email.notfound")));
        }
        boolean verified = !username.contains("not.verified@");
        if (verified) {
            User user = userRepository.findByUsername(username).get();
            user.verified(true);
            userRepository.save(user);
            return ResponseEntity.ok(new MessageResponse("OK", getMessage("account.verified.ok")));
        } else {
            return ResponseEntity.ok(new MessageResponse("ERROR", getMessage("account.verified.nok")));
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
            return ResponseEntity.ok(new MessageResponse("OK", getMessage("pin.valid.ok")));
        } else {
            return ResponseEntity.ok(new MessageResponse("ERROR", getMessage("pin.valid.nok")));
        }
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        if (userRepository.isPinCodeValid(request.getRequestId(), request.getPinCode())) {
            boolean isValidPassword = PasswordUtils.isValid(request.getPassword());
            if (!isValidPassword) {
                return ResponseEntity.ok(new MessageResponse("ERROR", "Password is invalid, it must contain only letters, digits and symbols &@?!#$%&"));
            }
            String email = userRepository.getPinCode(request.getRequestId()).getEmail();
            User user = userRepository.findByUsername(email).get();
            user.password(encoder.encode(request.getPassword()));
            userRepository.save(user);
            return ResponseEntity.ok(new MessageResponse("OK", "Password changed successfully!"));
        } else {
            return ResponseEntity.ok(new MessageResponse("ERROR", "Cannot change password, token expired or invalid"));
        }
    }

    @PostMapping("/password/validate")
    public ResponseEntity<?> validatePassword(@Valid @RequestBody PasswordValidationRequest request) {
        boolean isValid = PasswordUtils.isValid(request.getPassword());
        if (isValid) {
            return ResponseEntity.ok(new MessageResponse("OK", "Password is valid!"));
        } else {
            return ResponseEntity.ok(new MessageResponse("ERROR", "Password is invalid, it must be 6 characters minimum of letters, digits and symbols &@?!#$%&"));
        }
    }

    @PostMapping("/username/validate")
    public ResponseEntity<?> validateUsername(@Valid @RequestBody EmailValidationRequest request) {
        boolean isValid = EmailUtils.isValid(request.getUsername());
        if (isValid) {
            return ResponseEntity.ok(new MessageResponse("OK", "Email is valid!"));
        } else {
            return ResponseEntity.ok(new MessageResponse("ERROR", "Email is invalid!"));
        }
    }

    @PostMapping("/name/validate")
    public ResponseEntity<?> validateName(@Valid @RequestBody NameValidationRequest request) {
        boolean isValid = NameUtils.isValid(request.getName());
        if (isValid) {
            return ResponseEntity.ok(new MessageResponse("OK", "Name is valid!"));
        } else {
            return ResponseEntity.ok(new MessageResponse("ERROR", "Name is invalid, it must contain 2 words minimum without symbols"));
        }
    }

    @PostMapping("/profile")
    public ResponseEntity<?> getProfile(@Valid @RequestBody EmailValidationRequest request) {
        return ResponseEntity.ok(privateProfile(request.getUsername()));
    }

    private LoginResponse privateProfile(String username) {
        LoginResponse notFound = new LoginResponse();
        notFound.setUsername(username);
        notFound.setEmailVerified(false);
        notFound.setMembershipActivated(false);
        return userRepository.findByUsername(username).map(this::userToLogin).orElse(notFound);
    }

    private LoginResponse userToLogin(User user) {
        int devicesCount = userRepository.getUserDevices(user.username()).size();
        LoginResponse login = new LoginResponse();
        login.setUsername(user.username());
        login.setName(user.name());
        login.setEmailVerified(user.verified());
        login.setDevicesCount(devicesCount);
        login.setMembershipActivated(UserRepository.isValidLicense(user.license()));
        login.setDevicesExceeded(UserRepository.isInstallationExceeded(devicesCount));
        return login;
    }

    @PutMapping("/deactivate/{license}")
    public ResponseEntity<?> deactivateMembership(@PathVariable String license) {
        if (!StringUtils.hasLength(license) && license.length() != 20) {
            return ResponseEntity.ok(new MessageResponse("ERROR", "License must be 20 char length"));
        }
        UserRepository.deactivateLicense(license);
        return ResponseEntity.ok(new MessageResponse("OK", "License inactivated successfully!"));
    }


    private String getUsername() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userDetails.getUsername();
    }

    private String getMessage(String code) {
        return messageSource.getMessage(code, null, Locale.ENGLISH);
    }

}
