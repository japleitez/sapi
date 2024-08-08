package com.peecko.api.web.rest;

import java.util.ArrayList;
import java.util.Locale;

import com.peecko.api.domain.dto.Device;
import com.peecko.api.domain.dto.Membership;
import com.peecko.api.domain.dto.User;
import com.peecko.api.repository.fake.UserRepository;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.peecko.api.utils.Common.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController extends BaseController {

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
            return ResponseEntity.ok(new MessageResponse(ERROR, message("email.valid.nok")));
        }
        boolean isValidPassword = PasswordUtils.isValid(signUpRequest.getPassword());
        if (!isValidPassword) {
            return ResponseEntity.ok(new MessageResponse(ERROR, message("password.valid.ok")));
        }
        boolean isValidName = NameUtils.isValid(signUpRequest.getName());
        if (!isValidName) {
            return ResponseEntity.ok(new MessageResponse(ERROR, message("name.valid.nok")));
        }
        String email = signUpRequest.getUsername().toLowerCase();
        if (userRepository.existsByUsername(email)) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse(ERROR, message("email.duplicated")));
        }
        String name = NameUtils.camel(signUpRequest.getName());
        User user = new User()
                .name(name)
                .username(email)
                .language(signUpRequest.getLanguage().toUpperCase())
                .password(encoder.encode(signUpRequest.getPassword()));

        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse(OK, message("user.signup.ok", user)));
    }

    @PostMapping("/signin")
    public ResponseEntity<?> signIn(@Valid @RequestBody SignInRequest req) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);
        userRepository.addDevice(req, jwt);

        LoginResponse login = userRepository.findByUsername(req.getUsername()).map(this::userToLogin).orElse(new LoginResponse());
        login.setToken(jwt);

        return ResponseEntity.ok(login);
    }

    @PostMapping("/signout")
    public ResponseEntity<?> signOut(@Valid @RequestBody SignOutRequest signOutRequest) {
        userRepository.removeDevice(signOutRequest);
        return ResponseEntity.ok(new MessageResponse(OK, message("user.logoff.ok")));
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
            return ResponseEntity.ok(new MessageResponse(ERROR, message("email.required")));
        }
        if (!userRepository.existsByUsername(username)) {
            return ResponseEntity.ok(new MessageResponse(ERROR, message("email.notfound")));
        }
        boolean verified = !username.contains("not.verified@");
        if (verified) {
            User user = userRepository.findByUsername(username).get();
            user.verified(true);
            userRepository.save(user);
            return ResponseEntity.ok(new MessageResponse(OK, message("email.verified.ok")));
        } else {
            return ResponseEntity.ok(new MessageResponse(ERROR, message("email.verified.nok")));
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
            return ResponseEntity.ok(new MessageResponse(OK, message("pin.valid.ok")));
        } else {
            return ResponseEntity.ok(new MessageResponse(ERROR, message("pin.valid.nok")));
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        if (userRepository.isPinCodeValid(request.getRequestId(), request.getPinCode())) {
            boolean isValidPassword = PasswordUtils.isValid(request.getPassword());
            if (!isValidPassword) {
                return ResponseEntity.ok(new MessageResponse(ERROR, message("password.valid.nok")));
            }
            String email = userRepository.getPinCode(request.getRequestId()).getEmail();
            User user = userRepository.findByUsername(email).get();
            user.password(encoder.encode(request.getPassword()));
            userRepository.save(user);
            return ResponseEntity.ok(new MessageResponse(OK, message("password.change.ok")));
        } else {
            return ResponseEntity.ok(new MessageResponse(ERROR, message("password.change.nok")));
        }
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        String username = request.getUsername();
        if (!StringUtils.hasText(username)) {
            return ResponseEntity.ok(new MessageResponse(ERROR, message("email.required")));
        }
        if (!userRepository.existsByUsername(username)) {
            return ResponseEntity.ok(new MessageResponse(ERROR, message("email.notfound")));
        }
        User user = userRepository.findByUsername(username).get();
        if (!encoder.matches(request.getCurrent(), user.password())) {
            return ResponseEntity.ok(new MessageResponse(ERROR, message("password.old.mismatch")));
        }
        boolean isValidPassword = PasswordUtils.isValid(request.getPassword());
        if (!isValidPassword) {
            return ResponseEntity.ok(new MessageResponse(ERROR, message("password.valid.nok")));
        }
        user.password(encoder.encode(request.getPassword()));
        userRepository.save(user);
        return ResponseEntity.ok(new MessageResponse(OK, message("password.change.ok")));
    }

    @PostMapping("/change-personal-info")
    public ResponseEntity<?> changeUserInfo(@Valid @RequestBody ChangeUserInfoRequest request) {
        if (!StringUtils.hasText(request.getUsername())) {
            return ResponseEntity.ok(new MessageResponse(ERROR, message("email.required")));
        }
        if (!userRepository.existsByUsername(request.getUsername())) {
            return ResponseEntity.ok(new MessageResponse(ERROR, message("email.notfound")));
        }
        User user = userRepository.findByUsername(request.getUsername()).get();
        boolean isValid = NameUtils.isValid(request.getName());
        if (isValid) {
            user.name(request.getName());
            userRepository.save(user);
            return ResponseEntity.ok(new MessageResponse(OK, message("user.info.change.ok")));
        } else {
            return ResponseEntity.ok(new MessageResponse(ERROR, message("name.valid.nok")));
        }
    }

    @PostMapping("/password/validate")
    public ResponseEntity<?> validatePassword(@Valid @RequestBody PasswordValidationRequest request) {
        boolean isValid = PasswordUtils.isValid(request.getPassword());
        if (isValid) {
            return ResponseEntity.ok(new MessageResponse(OK, message("password.valid.ok")));
        } else {
            return ResponseEntity.ok(new MessageResponse(ERROR, message("password.valid.nok")));
        }
    }

    @PostMapping("/username/validate")
    public ResponseEntity<?> validateUsername(@Valid @RequestBody EmailValidationRequest request) {
        boolean isValid = EmailUtils.isValid(request.getUsername());
        if (isValid) {
            return ResponseEntity.ok(new MessageResponse(OK, message("email.valid.ok")));
        } else {
            return ResponseEntity.ok(new MessageResponse(ERROR, message("email.valid.nok")));
        }
    }

    @PostMapping("/name/validate")
    public ResponseEntity<?> validateName(@Valid @RequestBody NameValidationRequest request) {
        boolean isValid = NameUtils.isValid(request.getName());
        if (isValid) {
            return ResponseEntity.ok(new MessageResponse(OK, message("name.valid.ok")));
        } else {
            return ResponseEntity.ok(new MessageResponse(ERROR, message("name.valid.nok")));
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
        login.setDevicesExceeded(UserRepository.isInstallationExceeded(devicesCount));
        if (user.membership() != null) {
            Membership membership = user.membership();
            login.setMembership(membership.getLicense());
            login.setMembershipSponsor(membership.getSponsor());
            login.setMembershipSponsorLogo(membership.getLogo());
            login.setMembershipExpiration(membership.getExpiration());
            login.setMembershipActivated(UserRepository.isActiveLicense(membership.getLicense()));
        } else {

        }
        return login;
    }

    @PutMapping("/deactivate/{license}")
    public ResponseEntity<?> deactivateMembership(@PathVariable String license) {
        if (!StringUtils.hasLength(license) && license.length() != 20) {
            return ResponseEntity.ok(new MessageResponse(ERROR, message("membership.valid.nok")));
        }
        UserRepository.deactivateLicense(license);
        return ResponseEntity.ok(new MessageResponse(OK, message("membership.deactivate.ok")));
    }


    private String getUsername() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userDetails.getUsername();
    }

    private String message(String code) {
        Locale locale = geActiveLocale(userRepository);
        return messageSource.getMessage(code, null, locale);
    }

    private String message(String code, User user) {
        String lang = resolveLanguage(user.language());
        Locale locale = Locale.forLanguageTag(lang);
        return messageSource.getMessage(code, null, locale);
    }

}
