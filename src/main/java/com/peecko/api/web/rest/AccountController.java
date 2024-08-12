package com.peecko.api.web.rest;

import com.peecko.api.domain.dto.NotificationDTO;
import com.peecko.api.domain.dto.UserDTO;
import com.peecko.api.repository.fake.HelpRepository;
import com.peecko.api.repository.fake.LanguageRepository;
import com.peecko.api.repository.fake.NotificationRepository;
import com.peecko.api.repository.fake.UserRepository;
import com.peecko.api.service.AccountService;
import com.peecko.api.web.payload.response.LanguageResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/account")
public class AccountController extends BaseController {

    final UserRepository userRepository;

    final HelpRepository helpRepository;

    final LanguageRepository languageRepository;

    final NotificationRepository notificationRepository;

    final PasswordEncoder encoder;

    final AccountService accountService;

    public AccountController(UserRepository userRepository, HelpRepository helpRepository, LanguageRepository languageRepository, NotificationRepository notificationRepository, PasswordEncoder encoder, AccountService accountService) {
        this.userRepository = userRepository;
        this.helpRepository = helpRepository;
        this.languageRepository = languageRepository;
        this.notificationRepository = notificationRepository;
        this.encoder = encoder;
        this.accountService = accountService;
    }

    @GetMapping("/notifications")
    public ResponseEntity<?> getNotifications() {
        return ResponseEntity.ok(accountService.getNotifications(getUsername()));
    }

    /**
     * NOT YET IMPLEMENTED
     */

    @GetMapping("/help")
    public ResponseEntity<?> getHelp() {
        return ResponseEntity.ok(helpRepository.getHelp());
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

    private String getUsername() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userDetails.getUsername();
    }

}
