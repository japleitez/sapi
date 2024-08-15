package com.peecko.api.web.rest;

import com.peecko.api.domain.dto.DeviceDTO;
import com.peecko.api.domain.dto.PinCodeDTO;
import com.peecko.api.domain.dto.UserDTO;
import com.peecko.api.domain.enumeration.Verification;
import com.peecko.api.security.JwtUtils;
import com.peecko.api.service.ApsUserService;
import com.peecko.api.service.PinCodeService;
import com.peecko.api.service.TokenBlacklistService;
import com.peecko.api.service.response.UserProfileResponse;
import com.peecko.api.utils.EmailUtils;
import com.peecko.api.utils.NameUtils;
import com.peecko.api.utils.PasswordUtils;
import com.peecko.api.web.payload.request.*;
import com.peecko.api.web.payload.response.InstallationsResponse;
import com.peecko.api.web.payload.response.MessageResponse;
import com.peecko.api.web.payload.response.PinCodeResponse;
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
import java.util.Locale;

import static com.peecko.api.utils.Common.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController extends BaseController {

    private final JwtUtils jwtUtils;
    private final PasswordEncoder encoder;
    private final MessageSource messageSource;
    private final ApsUserService apsUserService;
    private final PinCodeService pinCodeService;
    private final TokenBlacklistService tokenBlacklistService;
    private final AuthenticationManager authenticationManager;

    public AuthController(JwtUtils jwtUtils, PasswordEncoder encoder, MessageSource messageSource, ApsUserService apsUserService, PinCodeService pinCodeService, TokenBlacklistService tokenBlacklistService, AuthenticationManager authenticationManager) {
        this.jwtUtils = jwtUtils;
        this.encoder = encoder;
        this.messageSource = messageSource;
        this.apsUserService = apsUserService;
        this.pinCodeService = pinCodeService;
        this.tokenBlacklistService = tokenBlacklistService;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/signin")
    public ResponseEntity<UserProfileResponse> signIn(@Valid @RequestBody SignInRequest request) {
        boolean authenticated = apsUserService.authenticate(request.getUsername(), request.getPassword());
        if (authenticated) {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtils.generateJwtToken(authentication);
            UserProfileResponse response = apsUserService.signIn(request, jwt);
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.ok(new UserProfileResponse());
        }
    }

    @PostMapping("/signout")
    public ResponseEntity<?> signOut(@Valid @RequestBody SignOutRequest request, @RequestHeader("Authorization") String authHeader) {
        tokenBlacklistService.invalidateToken(authHeader);
        apsUserService.signOut(request);
        return ResponseEntity.ok(new MessageResponse(OK, message("user.logoff.ok")));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signUp(@Valid @RequestBody SignUpRequest request) {
        boolean isValidEmail = EmailUtils.isValid(request.getUsername());
        if (!isValidEmail) {
            return ResponseEntity.ok(new MessageResponse(ERROR, message("email.valid.nok")));
        }
        boolean isValidPassword = PasswordUtils.isValid(request.getPassword());
        if (!isValidPassword) {
            return ResponseEntity.ok(new MessageResponse(ERROR, message("password.valid.ok")));
        }
        boolean isValidName = NameUtils.isValid(request.getName());
        if (!isValidName) {
            return ResponseEntity.ok(new MessageResponse(ERROR, message("name.valid.nok")));
        }
        if (apsUserService.exists(request.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse(ERROR, message("email.duplicated")));
        }
        return ResponseEntity.ok(new MessageResponse(OK, message("user.signup.ok", request.getLanguage())));
    }

    @GetMapping("/installations")
    public ResponseEntity<?> getInstallations() {
        List<DeviceDTO> list = apsUserService.getDevicesByUsername(getUsername());
        return ResponseEntity.ok(new InstallationsResponse(MAX_ALLOWED, list));
    }

    @GetMapping("/active/{username}")
    public ResponseEntity<?> activateUser(@PathVariable String username) {
        if (!StringUtils.hasText(username)) {
            return ResponseEntity.ok(new MessageResponse(ERROR, message("email.required")));
        }
        if (!apsUserService.exists(username)) {
            return ResponseEntity.ok(new MessageResponse(ERROR, message("email.notfound")));
        }
        apsUserService.setUserActive(username);
        return ResponseEntity.ok(new MessageResponse(OK, message("email.verified.ok")));
    }

    @PostMapping("/pincode")
    public ResponseEntity<?> generatePinCode(@Valid @RequestBody PinCodeRequest request) {
        if (!apsUserService.exists(request.getUsername())) {
            return ResponseEntity.badRequest().build();
        }
        PinCodeDTO pinCode = pinCodeService.generatePinCode(request, Verification.RESET_PASSWORD);
        PinCodeResponse response = new PinCodeResponse(pinCode.getRequestId());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/pincode/{requestId}")
    public ResponseEntity<?> pinCodeValidate(@PathVariable String requestId, @Valid @RequestBody PinValidationRequest request) {
        if (pinCodeService.isPinCodeValid(requestId, request.getCode())) {
            return ResponseEntity.ok(new MessageResponse(OK, message("pin.valid.ok")));
        } else {
            return ResponseEntity.ok(new MessageResponse(ERROR, message("pin.valid.nok")));
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        if (!PasswordUtils.isValid(request.getPassword())) {
            return ResponseEntity.ok(new MessageResponse(ERROR, message("password.valid.nok")));
        }
        PinCodeDTO pinCode = pinCodeService.findByRequestId(request.getRequestId());
        if (pinCode != null) {
            if (pinCode.getPinCode().equals(request.getPinCode())) {
                apsUserService.updateUserPassword(pinCode.getEmail(), request.getPassword());
                return ResponseEntity.ok(new MessageResponse(OK, message("password.change.ok")));
            }
        }
        return ResponseEntity.ok(new MessageResponse(ERROR, message("password.change.nok")));
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@Valid @RequestBody UpdatePasswordRequest request) {
        String username = request.getUsername();
        if (!StringUtils.hasText(username)) {
            return ResponseEntity.ok(new MessageResponse(ERROR, message("email.required")));
        }
        if (!apsUserService.exists(username)) {
            return ResponseEntity.ok(new MessageResponse(ERROR, message("email.notfound")));
        }
        UserDTO userDTO = apsUserService.findByUsernameOrElseThrow(username);
        if (!encoder.matches(request.getCurrent(), userDTO.password())) {
            return ResponseEntity.ok(new MessageResponse(ERROR, message("password.old.mismatch")));
        }
        boolean isValidPassword = PasswordUtils.isValid(request.getPassword());
        if (!isValidPassword) {
            return ResponseEntity.ok(new MessageResponse(ERROR, message("password.valid.nok")));
        }
        apsUserService.updateUserPassword(username, request.getPassword());
        return ResponseEntity.ok(new MessageResponse(OK, message("password.change.ok")));
    }

    @PostMapping("/change-personal-info")
    public ResponseEntity<?> changeUserInfo(@Valid @RequestBody UpdateUserRequest request) {
        if (!StringUtils.hasText(request.getUsername())) {
            return ResponseEntity.ok(new MessageResponse(ERROR, message("email.required")));
        }
        if (!apsUserService.exists(request.getUsername())) {
            return ResponseEntity.ok(new MessageResponse(ERROR, message("email.notfound")));
        }
        if (!NameUtils.isValid(request.getName())) {
            return ResponseEntity.ok(new MessageResponse(ERROR, message("name.valid.nok")));
        }
        apsUserService.updateUser(request);
        return ResponseEntity.ok(new MessageResponse(OK, message("user.info.change.ok")));
    }

    @PostMapping("/password/validate")
    public ResponseEntity<?> validatePassword(@Valid @RequestBody PasswordValidationRequest request) {
        if (PasswordUtils.isValid(request.getPassword())) {
            return ResponseEntity.ok(new MessageResponse(OK, message("password.valid.ok")));
        } else {
            return ResponseEntity.ok(new MessageResponse(ERROR, message("password.valid.nok")));
        }
    }

    @PostMapping("/username/validate")
    public ResponseEntity<?> validateUsername(@Valid @RequestBody EmailValidationRequest request) {
        if (EmailUtils.isValid(request.getUsername())) {
            return ResponseEntity.ok(new MessageResponse(OK, message("email.valid.ok")));
        } else {
            return ResponseEntity.ok(new MessageResponse(ERROR, message("email.valid.nok")));
        }
    }

    @PostMapping("/name/validate")
    public ResponseEntity<?> validateName(@Valid @RequestBody NameValidationRequest request) {
        if (NameUtils.isValid(request.getName())) {
            return ResponseEntity.ok(new MessageResponse(OK, message("name.valid.ok")));
        } else {
            return ResponseEntity.ok(new MessageResponse(ERROR, message("name.valid.nok")));
        }
    }

    @PostMapping("/profile")
    public ResponseEntity<?> getProfile(@Valid @RequestBody EmailValidationRequest request) {
        UserProfileResponse response = apsUserService.getProfile(request.getUsername());
        return ResponseEntity.ok(response);
    }

    private String getUsername() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userDetails.getUsername();
    }

    private String message(String code) {
        Locale locale = apsUserService.getUserLocale(getUsername());
        return messageSource.getMessage(code, null, locale);
    }

    private String message(String code, String lang) {
        Locale locale = Locale.forLanguageTag(lang);
        return messageSource.getMessage(code, null, locale);
    }

}
