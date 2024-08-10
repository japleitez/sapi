package com.peecko.api.service;

import com.peecko.api.domain.PinCode;
import com.peecko.api.domain.dto.PinCodeDTO;
import com.peecko.api.domain.enumeration.Language;
import com.peecko.api.domain.mapper.PinCodeMapper;
import com.peecko.api.repository.PinCodeRepo;
import com.peecko.api.service.context.EmailContext;
import com.peecko.api.utils.Common;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

@Service
public class PinCodeService {
    private final PinCodeRepo pinCodeRepo;
    private final EmailService emailService;
    private final MessageSource messageSource;
    private final TemplateEngine templateEngine;

    public PinCodeService(PinCodeRepo pinCodeRepo, EmailService emailService, MessageSource messageSource, TemplateEngine templateEngine) {
        this.pinCodeRepo = pinCodeRepo;
        this.emailService = emailService;
        this.messageSource = messageSource;
        this.templateEngine = templateEngine;
    }

    @Transactional
    public PinCodeDTO generatePinCodeForEmailValidation(String email, String language) {
        PinCode pinCode = new PinCode();
        pinCode.setEmail(email.toLowerCase());
        pinCode.setAgencyEmail("agencyEmail");
        pinCode.setLanguage(language);
        pinCode.setCode(Common.generateRandomDigitString(4));
        pinCode.setExpireAt(LocalDateTime.now().plusMinutes(10));
        pinCodeRepo.save(pinCode);
        TransactionSynchronizationManager.registerSynchronization(new NotifyPinCodeForEmailValidation(pinCode));

        return PinCodeMapper.pinCodeDTO(pinCode);
    }

    public boolean isPinCodeValid(String requestId, String code) {
        UUID uuid = UUID.fromString(requestId);
        return pinCodeRepo.findById(uuid).map(pinCode -> isCodeValid(pinCode, code)).orElse(false);
    }

    private boolean isCodeValid(PinCode pinCode, String code) {
        return code.equals(pinCode.getCode());
    }

    private class NotifyPinCodeForEmailValidation implements TransactionSynchronization {
        private final PinCode pinCode;
        public NotifyPinCodeForEmailValidation(PinCode pinCode) {
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
            context.setFrom(pinCode.getAgencyEmail());
            context.setSubject(resolveSubject());
            context.setText(resolveText());
            return context;
        }

        private String resolveSubject() {
            Locale locale = new Locale(pinCode.getLanguage());
            Object[] args = new Object[] {};
            return messageSource.getMessage("pin.code.email.verification.subject", args, locale);
        }

        private String resolveText() {
            Map<String, Object> variables = new HashMap<>();
            variables.put("code", pinCode.getCode());
            Context context = new Context();
            context.setVariables(variables);
            return templateEngine.process("pin.code.email.verification.html", context);
        }

    }

}
