package com.peecko.api.web.rest;

import com.peecko.api.domain.PinCode;
import com.peecko.api.domain.dto.DeviceDTO;
import com.peecko.api.domain.enumeration.Verification;
import com.peecko.api.security.JwtUtils;
import com.peecko.api.security.Login;
import com.peecko.api.service.ApsUserService;
import com.peecko.api.service.InvalidJwtService;
import com.peecko.api.service.PinCodeService;
import com.peecko.api.service.response.UserProfileResponse;
import com.peecko.api.utils.*;
import com.peecko.api.web.payload.request.*;
import com.peecko.api.web.payload.response.InstallationsResponse;
import com.peecko.api.web.payload.response.Message;
import com.peecko.api.web.payload.response.PinCodeResponse;
import jakarta.validation.Valid;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

import static com.peecko.api.utils.Common.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthResource extends BaseResource {

    final JwtUtils jwtUtils;
    final PasswordEncoder encoder;
    final MessageSource messageSource;
    final ApsUserService apsUserService;
    final PinCodeService pinCodeService;
    final InvalidJwtService invalidJwtService;
    final AuthenticationManager authenticationManager;

    public AuthResource(JwtUtils jwtUtils, PasswordEncoder encoder, MessageSource messageSource, ApsUserService apsUserService, PinCodeService pinCodeService, InvalidJwtService invalidJwtService, AuthenticationManager authenticationManager) {
        this.jwtUtils = jwtUtils;
        this.encoder = encoder;
        this.messageSource = messageSource;
        this.apsUserService = apsUserService;
        this.pinCodeService = pinCodeService;
        this.invalidJwtService = invalidJwtService;
        this.authenticationManager = authenticationManager;
    }

    /**
     * Sign up new user.
     */
    @PostMapping("/signup")
    public ResponseEntity<?> signUp(@Valid @RequestBody SignUpRequest request) {
        String language = request.getLanguage();
        if (EmailValidator.isNotValid(request.getUsername())) {
            return ResponseEntity.ok(message(ERROR,"email.valid.nok", language));
        }
        if (PasswordValidator.isNotValid(request.getPassword())) {
            return ResponseEntity.ok(message(ERROR,"password.valid.ok", language));
        }
        if (NameValidator.isNotValid(request.getName())) {
            return ResponseEntity.ok(message(ERROR,"name.valid.nok", language));
        }
        if (apsUserService.exists(request.getUsername())) {
            return ResponseEntity.badRequest().body(message(ERROR,"email.duplicated", language));
        }
        apsUserService.signUp(request);
        return ResponseEntity.ok(message(OK,"user.signup.ok", language));
    }

    /**
     * Sign in user.
     */
    @PostMapping("/signin")
    public ResponseEntity<UserProfileResponse> signIn(@Valid @RequestBody SignInRequest request) {
        if (apsUserService.authenticated(request.getUsername(), request.getPassword())) {
            return ResponseEntity.ok(apsUserService.signIn(request));
        } else {
            return ResponseEntity.ok(new UserProfileResponse());
        }
    }

    /**
     * Sing out user.
     */
    @PostMapping("/signout")
    public ResponseEntity<?> signOut(@Valid @RequestBody SignOutRequest request, @RequestHeader("Authorization") String authHeader) {
        String jti = jwtUtils.getJtiFromAuthHeader(authHeader);
        if (jti != null) {
            invalidJwtService.invalidateJwt(jti);
        }
        apsUserService.signOut(request);
        return ResponseEntity.ok(message(OK,"user.logoff.ok"));
    }

    /**
     * Get list of user's devices where user has signed in.
     */
    @GetMapping("/installations")
    public ResponseEntity<?> getDevices() {
        List<DeviceDTO> list = apsUserService.getUserDevicesAsDTO(getUsername());
        return ResponseEntity.ok(new InstallationsResponse(MAX_ALLOWED, list));
    }

    /**
     * Set user as active.
     */
    @GetMapping("/active/{username}")
    public ResponseEntity<?> activateUser(@PathVariable String username) {
        if (!StringUtils.hasText(username)) {
            return ResponseEntity.ok(message(ERROR,"email.required"));
        }
        if (apsUserService.doesNotExist(username)) {
            return ResponseEntity.ok(message(ERROR,"email.notfound"));
        }
        apsUserService.setUserActive(username, true);
        return ResponseEntity.ok(message(OK,"email.verified.ok"));
    }

    /**
     * Generate pin code to reset password.
     */
    @PostMapping("/pincode")
    public ResponseEntity<?> generatePinCode(@Valid @RequestBody PinCodeRequest request) {
        if (apsUserService.doesNotExist(request.getUsername())) {
            return ResponseEntity.badRequest().build();
        }
        String requestId = pinCodeService.generatePinCode(request, Verification.RESET_PASSWORD);
        return ResponseEntity.ok(new PinCodeResponse(requestId));
    }

    /**
     * Validate pin code associated with the specified request id.
     */
    @PutMapping("/pincode/{requestId}")
    public ResponseEntity<?> isPinCodeValid(@PathVariable String requestId, @Valid @RequestBody PinValidationRequest request) {
        if (pinCodeService.isPinCodeValid(requestId, request.getCode())) {
            return ResponseEntity.ok(message(OK,"pin.valid.ok"));
        } else {
            return ResponseEntity.ok(message(ERROR,"pin.valid.nok"));
        }
    }

    /**
     * Reset user's password.
     * Note: the user forgot its password and the pin code is used to reset it
     */
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        if (PasswordValidator.isNotValid(request.getPassword())) {
            return ResponseEntity.ok(message(ERROR,"password.valid.nok"));
        }
        if (Common.isNotValidUUID(request.getRequestId())) {
            return ResponseEntity.ok(message(ERROR,"requestId.valid.nok"));
        }
        PinCode pinCode = pinCodeService.findByRequestId(request.getRequestId());
        if (pinCode != null) {
            if (Objects.equals(pinCode.getCode(), request.getPinCode())) {
                apsUserService.updateUserPassword(pinCode.getEmail(), request.getPassword());
                return ResponseEntity.ok(message(OK,"password.change.ok"));
            }
        }
        return ResponseEntity.ok(message(ERROR,"password.change.nok"));
    }

    /**
     * Change user's password.
     * Note: the user is currently signed in so no need of pin code
     */
    @PostMapping("/change-password")
    public ResponseEntity<?> updateUserPassword(@Valid @RequestBody UpdatePasswordRequest request) {
        String username = request.getUsername();
        if (!StringUtils.hasText(username)) {
            return ResponseEntity.ok(message(ERROR,"email.required"));
        }
        if (apsUserService.doesNotExist(username)) {
            return ResponseEntity.ok(message(ERROR,"email.notfound"));
        }
        if (apsUserService.passwordDoesNotMatch(username, request.getCurrent())) {
            return ResponseEntity.ok(message(ERROR,"password.old.mismatch"));
        }
        if (PasswordValidator.isNotValid(request.getPassword())) {
            return ResponseEntity.ok(message(ERROR,"password.valid.nok"));
        }
        apsUserService.updateUserPassword(username, request.getPassword());
        return ResponseEntity.ok(message(OK,"password.change.ok"));
    }

    /**
     * Change user's name.
     */
    @PostMapping("/change-personal-info")
    public ResponseEntity<?> updateUserInfo(@Valid @RequestBody UpdateUserRequest request) {
        if (!StringUtils.hasText(request.getUsername())) {
            return ResponseEntity.ok(message(ERROR,"email.required"));
        }
        if (apsUserService.doesNotExist(request.getUsername())) {
            return ResponseEntity.ok(message(ERROR,"email.notfound"));
        }
        if (NameValidator.isNotValid(request.getName())) {
            return ResponseEntity.ok(message(ERROR,"name.valid.nok"));
        }
        apsUserService.updateUserName(request);
        return ResponseEntity.ok(message(OK,"user.info.change.ok"));
    }

    /**
     * Validate password.
     */
    @PostMapping("/password/validate")
    public ResponseEntity<?> validatePassword(@Valid @RequestBody PasswordValidationRequest request) {
        if (PasswordValidator.isValid(request.getPassword())) {
            return ResponseEntity.ok(message(OK,"password.valid.ok"));
        } else {
            return ResponseEntity.ok(message(ERROR,"password.valid.nok"));
        }
    }

    /**
     * Validate email.
     */
    @PostMapping("/username/validate")
    public ResponseEntity<?> validateUsername(@Valid @RequestBody EmailValidationRequest request) {
        if (EmailValidator.isValidEmail(request.getUsername())) {
            return ResponseEntity.ok(message(OK,"email.valid.ok"));
        } else {
            return ResponseEntity.ok(message(ERROR,"email.valid.nok"));
        }
    }

    /**
     * Validate name.
     */
    @PostMapping("/name/validate")
    public ResponseEntity<?> validateName(@Valid @RequestBody NameValidationRequest request) {
        if (NameValidator.isValid(request.getName())) {
            return ResponseEntity.ok(message(OK,"name.valid.ok"));
        } else {
            return ResponseEntity.ok(message(ERROR,"name.valid.nok"));
        }
    }

    /**
     * Get user profile.
     */
    @PostMapping("/profile")
    public ResponseEntity<?> getProfile(@Valid @RequestBody EmailValidationRequest request) {
        UserProfileResponse response = apsUserService.getProfile(request.getUsername());
        return ResponseEntity.ok(response);
    }

    private Message message(String code, String key) {
        String message = messageSource.getMessage(key, null, Login.getUserLocale());
        return new Message(code, message);
    }

    private Message message(String code, String key, String lang) {
        String message = messageSource.getMessage(key, null, Locale.forLanguageTag(lang));
        return new Message(code, message);
    }


}
