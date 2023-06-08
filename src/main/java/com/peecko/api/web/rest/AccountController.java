package com.peecko.api.web.rest;

import com.peecko.api.domain.User;
import com.peecko.api.repository.HelpRepository;
import com.peecko.api.repository.LanguageRepository;
import com.peecko.api.repository.NotificationRepository;
import com.peecko.api.repository.UserRepository;
import com.peecko.api.web.payload.request.ChangePasswordRequest;
import com.peecko.api.web.payload.response.LanguageResponse;
import com.peecko.api.web.payload.response.MessageResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/account")
public class AccountController extends BaseController {

    final UserRepository userRepository;

    final HelpRepository helpRepository;

    final LanguageRepository languageRepository;

    final NotificationRepository notificationRepository;

    final PasswordEncoder encoder;

    public AccountController(UserRepository userRepository, HelpRepository helpRepository, LanguageRepository languageRepository, NotificationRepository notificationRepository, PasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.helpRepository = helpRepository;
        this.languageRepository = languageRepository;
        this.notificationRepository = notificationRepository;
        this.encoder = encoder;
    }

    @GetMapping("/help")
    public ResponseEntity<?> getHelp() {
        return ResponseEntity.ok(helpRepository.getHelp());
    }

    @GetMapping("/notifications")
    public ResponseEntity<?> getNotifications() {
        return ResponseEntity.ok(notificationRepository.getNotifications());
    }

    @GetMapping("/languages")
    public ResponseEntity<?> getLanguages() {
        return ResponseEntity.ok(new LanguageResponse(getLanguage(), languageRepository.getLanguages()));
    }

    private String getLanguage() {
        return getActiveLanguage(userRepository);
    }

}
