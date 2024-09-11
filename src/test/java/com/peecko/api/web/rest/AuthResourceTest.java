package com.peecko.api.web.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.peecko.api.domain.ApsUser;
import com.peecko.api.domain.enumeration.Lang;
import com.peecko.api.repository.ApsUserRepo;
import com.peecko.api.web.payload.request.SignInRequest;
import com.peecko.api.web.payload.request.SignUpRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.http.MediaType;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc // Automatically configures MockMvc
@Transactional // Rollback the changes after each test
class AuthResourceTest {

   @Autowired
   MockMvc mockMvc;

   @Autowired
   ApsUserRepo apsUserRepo;

   ObjectMapper objectMapper = new ObjectMapper();

   static final String NAME = "John Doe";
   static final String USERNAME = "john@example.com";
   static final String PASSWORD = "s3cr3t";
   static final String EN = Lang.EN.name();

   static final String PHONE_MODEL = "phone-model";
   static final String OS_VERSION = "os-version";
   static final String DEVICE_ID = "device-id";

   @Test
   void signUp() throws Exception {

      String jsonRequest;

      SignUpRequest signUp = new SignUpRequest(NAME, USERNAME, PASSWORD, EN);
      jsonRequest = objectMapper.writeValueAsString(signUp);

      mockMvc.perform(post("/api/auth/signup")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(jsonRequest))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value("OK"))
            .andExpect(jsonPath("$.message").value("Successful sign up"));

      ApsUser user = apsUserRepo.findByUsername(signUp.username()).orElse(null);
      assertNotNull(user);

      SignInRequest signInRequest = new SignInRequest(USERNAME, PASSWORD, PHONE_MODEL, OS_VERSION, DEVICE_ID);
      jsonRequest = objectMapper.writeValueAsString(signInRequest);
      MvcResult result =  mockMvc.perform(post("/api/auth/signin")
                      .contentType(MediaType.APPLICATION_JSON)
                      .content(jsonRequest))
              .andExpect(status().isOk())
              .andExpect(jsonPath("$.name").value(NAME))
              .andExpect(jsonPath("$.username").value(USERNAME))
              .andExpect(jsonPath("$.email-verified").value(true))
              .andExpect(jsonPath("$.devices-exceeded").value(false))
              .andExpect(jsonPath("$.devices-count").value(1))
              .andExpect(jsonPath("$.devices-max").value(3))
              .andExpect(jsonPath("$.membership", is(nullValue())))
              .andExpect(jsonPath("$.membership-activated").value(false))
              .andExpect(jsonPath("$.membership-expiration", blankString()))
              .andExpect(jsonPath("$.membership-sponsor", blankString()))
              .andExpect(jsonPath("$.membership-sponsor-logo", blankString()))
              .andReturn();

      String responseContent = result.getResponse().getContentAsString();

      assertNotNull(responseContent );

   }

}
