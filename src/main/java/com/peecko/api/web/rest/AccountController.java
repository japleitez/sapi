package com.peecko.api.web.rest;

import com.peecko.api.domain.dto.Help;
import com.peecko.api.domain.dto.LanguageDTO;
import com.peecko.api.domain.enumeration.Lang;
import com.peecko.api.service.AccountService;
import com.peecko.api.service.ApsUserService;
import com.peecko.api.service.LanguageService;
import com.peecko.api.web.payload.response.LanguageResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/account")
public class AccountController extends BaseController {

    final AccountService accountService;
    final ApsUserService apsUserService;
    final LanguageService languageService;

    public AccountController(AccountService accountService, ApsUserService apsUserService, LanguageService languageService) {
        this.accountService = accountService;
        this.apsUserService = apsUserService;
        this.languageService = languageService;
    }

    @GetMapping("/notifications")
    public ResponseEntity<?> getNotifications() {
        return ResponseEntity.ok(accountService.getNotifications(getUsername()));
    }

    @PutMapping("/notifications/{id}")
    public ResponseEntity<?> updateNotification(@PathVariable Long id) {
        accountService.addNotificationItem(getApsUserId(), id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/help")
    public ResponseEntity<?> getHelp() {
        List<Help> helpByLang = accountService.findHelpByLang(getApsUserLang());
        return ResponseEntity.ok(helpByLang);
    }

    @GetMapping("/languages")
    public ResponseEntity<?> getLanguages() {
        String selected = getApsUserLang().name();
        List<LanguageDTO> activeLanguages = languageService.findActiveLanguages();
        return ResponseEntity.ok(new LanguageResponse(selected, activeLanguages));
    }

    @PutMapping("/languages/{lang}")
    public ResponseEntity<?> setLanguage(@PathVariable("lang")  String lang) {
        apsUserService.setUserLanguage(getUsername(), lang);
        return ResponseEntity.ok().build();
    }

    private Lang getApsUserLang() {
        return apsUserService.findLangByUsername(getUsername());
    }

    private Long getApsUserId() {
        return apsUserService.findIdByUsername(getUsername());
    }

}
