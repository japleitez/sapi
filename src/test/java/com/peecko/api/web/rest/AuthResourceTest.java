package com.peecko.api.web.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.peecko.api.domain.ApsUser;
import com.peecko.api.repository.ApsUserRepo;
import com.peecko.api.web.payload.request.SignUpRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.http.MediaType;

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

   ObjectMapper mapper = new ObjectMapper();

   @Test
   void signUp() throws Exception {

      SignUpRequest request = new SignUpRequest("John", "john@example.com", "password", "en");
      String jsonRequest = mapper.writeValueAsString(request);

      mockMvc.perform(post("/api/auth/signup")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(jsonRequest))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value("OK"))
            .andExpect(jsonPath("$.message").value("Successful sign up"));

      ApsUser user = apsUserRepo.findByUsername(request.username()).orElse(null);

      assertNotNull(user);

   }

}
