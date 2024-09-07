package com.peecko.api.web.rest;

import com.peecko.api.domain.dto.Help;
import com.peecko.api.domain.dto.LanguageDTO;
import com.peecko.api.domain.dto.NotificationDTO;
import com.peecko.api.domain.enumeration.Lang;
import com.peecko.api.security.Login;
import com.peecko.api.service.*;
import com.peecko.api.utils.Common;
import com.peecko.api.web.payload.response.LanguageResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/account")
public class AccountResource extends BaseResource {

    final HelpService helpService;
    final AccountService accountService;
    final ApsUserService apsUserService;
    final LanguageService languageService;
    final NotificationService notificationService;

    public AccountResource(HelpService helpService, AccountService accountService, ApsUserService apsUserService, LanguageService languageService, NotificationService notificationService) {
        this.helpService = helpService;
        this.accountService = accountService;
        this.apsUserService = apsUserService;
        this.languageService = languageService;
        this.notificationService = notificationService;
    }

    /**
     * Get the list of available languages.
     */
    @GetMapping("/languages")
    public ResponseEntity<LanguageResponse> getLanguages() {
        List<LanguageDTO> languages = languageService.findActiveLanguages();
        return ResponseEntity.ok(new LanguageResponse(Login.getUserLanguage().name(), languages));
    }

    /**
     * Set user's language.
     */
    @PutMapping("/languages/{lang}")
    public ResponseEntity<Void> setUserLanguage(@PathVariable("lang")  String lang) {
        apsUserService.setUserLanguage(getUsername(), Lang.fromString(lang));
        return ResponseEntity.ok().build();
    }

    /**
     * Get user's notifications for the current period.
     */
    @GetMapping("/notifications")
    public ResponseEntity<List<NotificationDTO>> getNotifications() {
        List<NotificationDTO> list = notificationService.getNotificationsForUserAndPeriod(Login.getUser(), Common.currentPeriod());
        return ResponseEntity.ok(list);
    }

    /**
     * Add a notification as viewed.
     */
    @PutMapping("/notifications/{id}")
    public ResponseEntity<Void> addViewedNotification(@PathVariable Long id) {
        notificationService.addViewedNotification(Login.getUserId(), id);
        return ResponseEntity.ok().build();
    }

    /**
     * Get the list of questions & answers (help).
     */
    @GetMapping("/help")
    public ResponseEntity<List<Help>> getHelp() {
        List<Help> helpList = helpService.findByLang(Login.getUserLanguage());
        return ResponseEntity.ok(helpList);
    }
}
