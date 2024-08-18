package com.peecko.api.service;

import com.peecko.api.domain.PinCode;
import com.peecko.api.domain.dto.PinCodeDTO;
import com.peecko.api.domain.dto.UserDTO;
import com.peecko.api.domain.enumeration.Verification;
import com.peecko.api.domain.mapper.PinCodeMapper;
import com.peecko.api.repository.PinCodeRepo;
import com.peecko.api.service.context.EmailContext;
import com.peecko.api.utils.Common;
import com.peecko.api.web.payload.request.PinCodeRequest;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class PinCodeService {
    private final PinCodeRepo pinCodeRepo;
    private final EmailService emailService;
    private final MessageSource messageSource;
    private final TemplateEngine templateEngine;
    private final ApsUserService apsUserService;

    public PinCodeService(PinCodeRepo pinCodeRepo, EmailService emailService, MessageSource messageSource, TemplateEngine templateEngine, ApsUserService apsUserService) {
        this.pinCodeRepo = pinCodeRepo;
        this.emailService = emailService;
        this.messageSource = messageSource;
        this.templateEngine = templateEngine;
        this.apsUserService = apsUserService;
    }

    public PinCode findByRequestId(UUID uuid) {
        return pinCodeRepo.findById(uuid).orElse(null);
    }

    @Transactional
    public String generatePinCode(PinCodeRequest request, Verification verification) {
        UserDTO user = apsUserService.findByUsernameOrElseThrow(request.getUsername());
        PinCode pinCode = new PinCode();
        pinCode.setLanguage(user.language());
        pinCode.setEmail(request.getUsername().toLowerCase());
        pinCode.setCode(Common.randomDigitsAsString(4));
        pinCode.setExpireAt(LocalDateTime.now().plusMinutes(10));
        pinCode.setVerification(verification);
        pinCodeRepo.save(pinCode);
        TransactionSynchronizationManager.registerSynchronization(new NotifyPinCode(pinCode));
        return pinCode.getRequestId().toString();
    }

    public boolean isPinCodeValid(String requestId, String code) {
        UUID uuid = UUID.fromString(requestId);
        return pinCodeRepo.findById(uuid).map(pinCode -> isCodeValid(pinCode, code)).orElse(false);
    }

    private boolean isCodeValid(PinCode pinCode, String code) {
        return code.equals(pinCode.getCode());
    }

    private class NotifyPinCode implements TransactionSynchronization {
        private final PinCode pinCode;
        public NotifyPinCode(PinCode pinCode) {
            this.pinCode = pinCode;
        }

        @Override
        public void afterCommit() {
            EmailContext context = buildEmailContext();
            emailService.sendEmail(context);
        }

        private EmailContext buildEmailContext() {
            EmailContext context =  new EmailContext();
            context.setTo(pinCode.getEmail());
            context.setSubject(resolveSubject());
            context.setText(resolveText());
            return context;
        }

        private String resolveSubject() {
            Locale locale = new Locale(pinCode.getLanguage());
            Object[] args = new Object[] {};
            return messageSource.getMessage("pin.code.email.reset.password.subject", args, locale);
        }

        private String resolveText() {
            Map<String, Object> variables = new HashMap<>();
            variables.put("code", pinCode.getCode());
            Context context = new Context();
            context.setVariables(variables);
            return templateEngine.process(getTemplateName(), context);
        }

        private String getTemplateName() {
            return switch (pinCode.getLanguage().toUpperCase()) {
                case "FR" -> "pin_code_email_reset_password_fr.html";
                case "DE" -> "pin_code_email_reset_password_de.html";
                case "ES" -> "pin_code_email_reset_password_es.html";
                default -> "pin_code_email_reset_password.html";
            };
        }
    }

}
