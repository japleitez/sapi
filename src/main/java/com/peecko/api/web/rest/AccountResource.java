package com.peecko.api.web.rest;

import com.peecko.api.domain.ApsUser;
import com.peecko.api.domain.dto.Help;
import com.peecko.api.domain.dto.LanguageDTO;
import com.peecko.api.domain.dto.NotificationDTO;
import com.peecko.api.security.Login;
import com.peecko.api.service.AccountService;
import com.peecko.api.service.ApsUserService;
import com.peecko.api.service.LanguageService;
import com.peecko.api.web.payload.response.LanguageResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/account")
public class AccountResource extends BaseResource {

    final AccountService accountService;
    final ApsUserService apsUserService;
    final LanguageService languageService;

    public AccountResource(AccountService accountService, ApsUserService apsUserService, LanguageService languageService) {
        this.accountService = accountService;
        this.apsUserService = apsUserService;
        this.languageService = languageService;
    }

    @GetMapping("/notifications")
    public ResponseEntity<List<NotificationDTO>> getNotifications() {
        return ResponseEntity.ok(accountService.getNotifications(getUsername()));
    }

    @PutMapping("/notifications/{id}")
    public ResponseEntity<Void> updateNotification(@PathVariable Long id) {
        ApsUser apsUser = Login.getUser();
        accountService.addNotificationItem(apsUser.getId(), id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/help")
    public ResponseEntity<List<Help>> getHelp() {
        ApsUser apsUser = Login.getUser();
        List<Help> helpList = accountService.findHelpByLang(apsUser.getLanguage());
        return ResponseEntity.ok(helpList);
    }

    @GetMapping("/languages")
    public ResponseEntity<LanguageResponse> getLanguages() {
        String language = Login.getUser().getLanguage().name();
        List<LanguageDTO> activeLanguages = languageService.findActiveLanguages();
        return ResponseEntity.ok(new LanguageResponse(language, activeLanguages));
    }

    @PutMapping("/languages/{lang}")
    public ResponseEntity<Void> setLanguage(@PathVariable("lang")  String lang) {
        apsUserService.setUserLanguage(getUsername(), lang);
        return ResponseEntity.ok().build();
    }

}
