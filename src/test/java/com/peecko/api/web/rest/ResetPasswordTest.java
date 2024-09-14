package com.peecko.api.web.rest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.peecko.api.domain.ApsUser;
import com.peecko.api.domain.EntityBuilder;
import com.peecko.api.domain.EntityDefault;
import com.peecko.api.domain.PinCode;
import com.peecko.api.repository.ApsUserRepo;
import com.peecko.api.repository.PinCodeRepo;
import com.peecko.api.web.payload.request.PinCodeRequest;
import com.peecko.api.web.payload.request.PinValidationRequest;
import com.peecko.api.web.payload.request.ResetPasswordRequest;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
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
    public void resetPassword() throws Exception {
        ApsUser apsUser = EntityBuilder.buildApsUser();
        apsUserRepo.save(apsUser);

        // request PinCode
        PinCodeRequest request = new PinCodeRequest(apsUser.getUsername());
        String jsonRequest = objectMapper.writeValueAsString(request);

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

        // validate PinCode
        PinCode pinCode = pinCodeRepo.findById(UUID.fromString(requestId)).orElseThrow();
        PinValidationRequest validationRequest = new PinValidationRequest(pinCode.getCode());
        jsonRequest = objectMapper.writeValueAsString(validationRequest);
        mockMvc.perform(put("/api/auth/pincode/{requestId}", requestId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is("OK")))
                .andExpect(jsonPath("$.message", is("Verification code is valid")));

        // reset password
        String newPassword = "123456789"; // not yet encoded
        ResetPasswordRequest resetPasswordRequest = new ResetPasswordRequest(requestId, pinCode.getCode(), newPassword);
        jsonRequest = objectMapper.writeValueAsString(resetPasswordRequest);
        mockMvc.perform(post("/api/auth/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is("OK")))
                .andExpect(jsonPath("$.message", is("Password changed successfully")));

        // check password
        apsUser = apsUserRepo.findByUsername(apsUser.getUsername()).orElseThrow();
        assertTrue(passwordEncoder.matches(newPassword, apsUser.getPassword()));
    }

}
