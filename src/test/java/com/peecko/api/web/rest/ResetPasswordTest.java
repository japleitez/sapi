package com.peecko.api.web.rest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.peecko.api.domain.ApsUser;
import com.peecko.api.domain.EntityDefault;
import com.peecko.api.domain.PinCode;
import com.peecko.api.repository.ApsUserRepo;
import com.peecko.api.repository.PinCodeRepo;
import com.peecko.api.web.payload.request.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.util.UUID;


@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@AutoConfigureMockMvc // Automatically configures MockMvc
public class ResetPasswordTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ApsUserRepo apsUserRepo;

    @Autowired
    PinCodeRepo pinCodeRepo;

    @Autowired
    PasswordEncoder passwordEncoder;

    ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void resetAndChangePassword() throws Exception {

        // Sign Up
        SignUpRequest signUpRequest = new SignUpRequest(
                EntityDefault.USER_NAME,
                EntityDefault.USER_EMAIL,
                EntityDefault.USER_PASSWORD,
                EntityDefault.LANGUAGE);
        String jsonRequest = objectMapper.writeValueAsString(signUpRequest);
        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("OK"))
                .andExpect(jsonPath("$.message").value("Successful sign up"));
        ApsUser apsUser = apsUserRepo.findByUsername(EntityDefault.USER_EMAIL).orElse(null);
        assertNotNull(apsUser);

        // User forgot its password and wants to reset it
        // step1 : request PinCode which is sent to user's email and receive requestId
        PinCodeRequest pinCodeRequest = new PinCodeRequest(apsUser.getUsername());
        jsonRequest = objectMapper.writeValueAsString(pinCodeRequest);
        MvcResult result = mockMvc.perform(post("/api/auth/pincode")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.request-id", is(notNullValue())))
                .andReturn();
        String jsonResponse = result.getResponse().getContentAsString();
        JsonNode jsonNode = objectMapper.readTree(jsonResponse);
        String requestId = jsonNode.get("request-id").asText();
        assertNotNull(requestId);

        // step2 : Validate PinCode
        PinCode pinCode = pinCodeRepo.findById(UUID.fromString(requestId)).orElseThrow();
        PinValidationRequest validationRequest = new PinValidationRequest(pinCode.getCode());
        jsonRequest = objectMapper.writeValueAsString(validationRequest);
        mockMvc.perform(put("/api/auth/pincode/{requestId}", requestId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is("OK")))
                .andExpect(jsonPath("$.message", is("Verification code is valid")));

        // step3 : Reset password
        ResetPasswordRequest resetPasswordRequest = new ResetPasswordRequest(
                requestId,
                pinCode.getCode(),
                EntityDefault.USER_RESET_PASSWORD);
        jsonRequest = objectMapper.writeValueAsString(resetPasswordRequest);
        mockMvc.perform(post("/api/auth/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is("OK")))
                .andExpect(jsonPath("$.message", is("Password changed successfully")));
        apsUser = apsUserRepo.findByUsername(apsUser.getUsername()).orElseThrow();
        assertTrue(passwordEncoder.matches(EntityDefault.USER_RESET_PASSWORD, apsUser.getPassword()));

        // Sign In
        SignInRequest signInRequest = new SignInRequest(
                EntityDefault.USER_EMAIL,
                EntityDefault.USER_RESET_PASSWORD,
                EntityDefault.PHONE_MODEL,
                EntityDefault.OS_VERSION,
                EntityDefault.DEVICE_ID);
        jsonRequest = objectMapper.writeValueAsString(signInRequest);
        result = mockMvc.perform(post("/api/auth/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andReturn();

        // Read token
        jsonResponse = result.getResponse().getContentAsString();
        jsonNode = objectMapper.readTree(jsonResponse);
        String token = jsonNode.get("token").asText();
        assertNotNull(token);

        // Update password: user knows its password and wants to change it
        UpdatePasswordRequest updatePasswordRequest = new UpdatePasswordRequest(
                EntityDefault.USER_EMAIL,
                EntityDefault.USER_RESET_PASSWORD, // old password
                EntityDefault.USER_NEW_PASSWORD // new password
        );

        jsonRequest = objectMapper.writeValueAsString(updatePasswordRequest);
        mockMvc.perform(post("/api/auth/change-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is("OK")))
                .andExpect(jsonPath("$.message", is("Password changed successfully")));

        apsUser = apsUserRepo.findByUsername(EntityDefault.USER_EMAIL).orElseThrow();
        assertTrue(passwordEncoder.matches(EntityDefault.USER_NEW_PASSWORD, apsUser.getPassword()));
    }

}
