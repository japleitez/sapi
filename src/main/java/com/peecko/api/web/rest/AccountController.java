package com.peecko.api.web.rest;

import com.peecko.api.domain.dto.UserDTO;
import com.peecko.api.repository.fake.HelpRepository;
import com.peecko.api.repository.fake.LanguageRepository;
import com.peecko.api.repository.fake.NotificationRepository;
import com.peecko.api.repository.fake.UserRepository;
import com.peecko.api.web.payload.response.LanguageResponse;
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
        String username = getUsername(userRepository);
        return ResponseEntity.ok(notificationRepository.getNotifications(username));
    }

    @PutMapping("/notifications/{id}")
    public ResponseEntity<?> updateNotification(@PathVariable Long id) {
        String username = getUsername(userRepository);
        notificationRepository.updateNotification(username, id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/languages")
    public ResponseEntity<?> getLanguages() {
        return ResponseEntity.ok(new LanguageResponse(getActiveLanguage(), languageRepository.getLanguages()));
    }

    @PutMapping("/languages/{lang}")
    public ResponseEntity<?> setLanguage(@PathVariable("lang")  String lang) {
        UserDTO userDTO = getActiveUser(userRepository);
        userDTO.language(resolveLanguage(lang));
        return ResponseEntity.ok().build();
    }

    private String getActiveLanguage() {
        return getActiveLanguage(userRepository);
    }

}
