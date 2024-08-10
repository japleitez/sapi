package com.peecko.api.web.rest;

import java.util.List;
import java.util.Locale;

import com.peecko.api.domain.dto.DeviceDTO;
import com.peecko.api.domain.dto.Membership;
import com.peecko.api.domain.dto.PinCodeDTO;
import com.peecko.api.domain.dto.UserDTO;
import com.peecko.api.repository.fake.UserRepository;
import com.peecko.api.security.JwtUtils;
import com.peecko.api.service.ApsUserService;
import com.peecko.api.service.PinCodeService;
import com.peecko.api.service.response.LoginResponse;
import com.peecko.api.service.TokenBlacklistService;
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

import static com.peecko.api.utils.Common.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController extends BaseController {

    private final MessageSource messageSource;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final JwtUtils jwtUtils;
    private final TokenBlacklistService tokenBlacklistService;
    private final ApsUserService apsUserService;
    private final PinCodeService pinCodeService;

    public AuthController(MessageSource messageSource, AuthenticationManager authenticationManager, UserRepository userRepository, PasswordEncoder encoder, JwtUtils jwtUtils, TokenBlacklistService tokenBlacklistService, ApsUserService apsUserService, PinCodeService pinCodeService) {
        this.messageSource = messageSource;
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.encoder = encoder;
        this.jwtUtils = jwtUtils;
        this.tokenBlacklistService = tokenBlacklistService;
        this.apsUserService = apsUserService;
        this.pinCodeService = pinCodeService;
    }

    @PostMapping("/signin")
    public ResponseEntity<LoginResponse> signIn(@Valid @RequestBody SignInRequest request) {
        boolean authenticated = apsUserService.authenticate(request.getUsername(), request.getPassword());
        if (authenticated) {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtils.generateJwtToken(authentication);
            LoginResponse response = apsUserService.signIn(request, jwt);
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.ok(new LoginResponse());
        }
    }

    @PostMapping("/signout")
    public ResponseEntity<?> signOut(@Valid @RequestBody SignOutRequest signOutRequest, @RequestHeader("Authorization") String authHeader) {
        tokenBlacklistService.invalidateToken(authHeader);
        apsUserService.signOut(signOutRequest);
        return ResponseEntity.ok(new MessageResponse(OK, message("user.logoff.ok")));
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
        if (apsUserService.exists(signUpRequest.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse(ERROR, message("email.duplicated")));
        }
        UserDTO userDTO = apsUserService.signUp(signUpRequest);
        return ResponseEntity.ok(new MessageResponse(OK, message("user.signup.ok", userDTO)));
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
        apsUserService.activateUser(username);
        return ResponseEntity.ok(new MessageResponse(OK, message("email.verified.ok")));
    }

    @PostMapping("/pincode")
    public ResponseEntity<?> generatePinCode(@Valid @RequestBody PinCodeRequest request) {
        if (!apsUserService.exists(request.getUsername())) {
            return ResponseEntity.badRequest().build();
        }
        UserDTO user = apsUserService.findByUsernameOrElseThrow(request.getUsername());
        PinCodeDTO pinCode = pinCodeService.generatePinCode(request.getUsername(), user.language());
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
                apsUserService.updatePassword(pinCode.getEmail(), request.getPassword());
                return ResponseEntity.ok(new MessageResponse(OK, message("password.change.ok")));
            }
        }
        return ResponseEntity.ok(new MessageResponse(ERROR, message("password.change.nok")));
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
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
        apsUserService.updatePassword(username, request.getPassword());
        return ResponseEntity.ok(new MessageResponse(OK, message("password.change.ok")));
    }

    /**
     * -------------------------------------------------------------------------------------
     * TO IMPLEMENT
     * -------------------------------------------------------------------------------------
     */


    @PostMapping("/change-personal-info")
    public ResponseEntity<?> changeUserInfo(@Valid @RequestBody ChangeUserInfoRequest request) {
        if (!StringUtils.hasText(request.getUsername())) {
            return ResponseEntity.ok(new MessageResponse(ERROR, message("email.required")));
        }
        if (!userRepository.existsByUsername(request.getUsername())) {
            return ResponseEntity.ok(new MessageResponse(ERROR, message("email.notfound")));
        }
        UserDTO userDTO = userRepository.findByUsername(request.getUsername()).get();
        boolean isValid = NameUtils.isValid(request.getName());
        if (isValid) {
            userDTO.name(request.getName());
            userRepository.save(userDTO);
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

    private LoginResponse userToLogin(UserDTO userDTO) {
        int devicesCount = userRepository.getUserDevices(userDTO.username()).size();
        LoginResponse login = new LoginResponse();
        login.setUsername(userDTO.username());
        login.setName(userDTO.name());
        login.setEmailVerified(userDTO.verified());
        login.setDevicesCount(devicesCount);
        login.setDevicesExceeded(UserRepository.isInstallationExceeded(devicesCount));
        if (userDTO.membership() != null) {
            Membership membership = userDTO.membership();
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

    private String message(String code, UserDTO userDTO) {
        String lang = resolveLanguage(userDTO.language());
        Locale locale = Locale.forLanguageTag(lang);
        return messageSource.getMessage(code, null, locale);
    }

}
