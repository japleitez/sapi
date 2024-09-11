package com.peecko.api.service;

import com.peecko.api.domain.ApsUser;
import com.peecko.api.domain.EntityBuilder;
import com.peecko.api.repository.ApsUserRepo;
import com.peecko.api.security.UserDetailsImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class UserDetailsServiceImplTest {

   @Autowired
   ApsUserRepo apsUserRepo;

   @Autowired
   UserDetailsServiceImpl userDetailsServiceImpl;

   @Test
   void loadUserByUsername() {
      // Given
      ApsUser apsUser = EntityBuilder.buildApsUser();
      apsUserRepo.save(apsUser);

      // When
      UserDetailsImpl userDetails = (UserDetailsImpl) userDetailsServiceImpl.loadUserByUsername(apsUser.getUsername());

      // Then
      assertNotNull(userDetails);
      assertEquals(apsUser.getName(), userDetails.getName());
      assertEquals(apsUser.getUsername(), userDetails.getUsername());
      assertEquals(apsUser.getPassword(), userDetails.getPassword());
   }

   @Test
   void loadUserByUsernameNotFound() {
      // Then
      assertThrows(UsernameNotFoundException.class, () -> userDetailsServiceImpl.loadUserByUsername("unknown"));
   }

}
