package com.peecko.api.web.rest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.peecko.api.domain.*;
import com.peecko.api.domain.enumeration.Lang;
import com.peecko.api.domain.enumeration.Player;
import com.peecko.api.repository.*;
import com.peecko.api.service.LabelService;
import com.peecko.api.service.VideoService;
import com.peecko.api.utils.InstantUtils;
import com.peecko.api.utils.NameUtils;
import com.peecko.api.web.payload.request.ActivationRequest;
import com.peecko.api.web.payload.request.SignInRequest;
import com.peecko.api.web.payload.request.SignUpRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.http.MediaType;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

   @Autowired
   CustomerRepo customerRepo;

   @Autowired
   ApsMembershipRepo apsMembershipRepo;

   @Autowired
   VideoRepo videoRepo;

   @Autowired
   LabelRepo labelRepo;

   @Autowired
   LabelService labelService;

   @Autowired
   CacheManager cacheManager;

   @Autowired
   TodayVideoRepo todayVideoRepo;

   @Autowired
   VideoCategoryRepo videoCategoryRepo;

   @Autowired
   UserFavoriteVideoRepo userFavoriteVideoRepo;

   @Autowired
   VideoService videoService;

   // data references

   VideoCategory yogaCategory = null;
   VideoCategory pilatesCategory = null;
   VideoCategory flexibilityCategory = null;

   Video yoga1En = null;
   Video yoga2En = null;
   Video pilates1En = null;
   Video pilates2En = null;
   Video pilates3En = null;
   Video pilates4En = null;
   Video pilates5En = null;
   Video pilatesFrench = null;
   Video pilatesArchived = null;

   Label yoga = null;
   Label pilates = null;
   Label flexibility = null;

   Label all = null;
   Label relax = null;
   Label energy = null;
   Label strength = null;

   Instant created = InstantUtils.createInstantFromDays(-20);
   LocalDate released = LocalDate.now().minusDays(10);
   LocalDate archived = LocalDate.now().minusDays(5);

   List<Video> videosYoga = null;
   List<Video> videosPilates = null;

   List<Video> allVideos = null;
   Set<Long> activeAllEnIds = null;
   Set<Long> activeAllFrIds = null;
   Set<Long> activeYogaEnIds = null;
   Set<Long> activePilatesEnIds = null;

   ObjectMapper objectMapper = new ObjectMapper();

   static final String EN = Lang.EN.name();

   @BeforeEach
   void setUp() {
      videoService.clearAllCaches();
      createVideos();
   }

   @Test
   void signUp() throws Exception {

      String jsonRequest;

      SignUpRequest signUp = new SignUpRequest(EntityDefault.USER_NAME, EntityDefault.USER_EMAIL, EntityDefault.USER_PASSWORD, EN);
      jsonRequest = objectMapper.writeValueAsString(signUp);

      mockMvc.perform(post("/api/auth/signup")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(jsonRequest))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value("OK"))
            .andExpect(jsonPath("$.message").value("Successful sign up"));

      ApsUser user = apsUserRepo.findByUsername(signUp.username()).orElse(null);
      assertNotNull(user);

      SignInRequest signInRequest = new SignInRequest(
            EntityDefault.USER_EMAIL,
            EntityDefault.USER_PASSWORD,
            EntityDefault.PHONE_MODEL,
            EntityDefault.OS_VERSION,
            EntityDefault.DEVICE_ID);

      jsonRequest = objectMapper.writeValueAsString(signInRequest);
      MvcResult result =  mockMvc.perform(post("/api/auth/signin")
                      .contentType(MediaType.APPLICATION_JSON)
                      .content(jsonRequest))
              .andExpect(status().isOk())
              .andExpect(jsonPath("$.name").value(EntityDefault.USER_NAME))
              .andExpect(jsonPath("$.username").value(EntityDefault.USER_EMAIL))
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

      String jsonResponse = result.getResponse().getContentAsString();
      assertNotNull(jsonResponse);

      JsonNode jsonNode = objectMapper.readTree(jsonResponse);
      String token = jsonNode.get("token").asText();
      assertNotNull(token);


      Customer customer = EntityBuilder.buildCustomer();
      customerRepo.saveAndFlush(customer);

      ApsMembership apsMembership = EntityBuilder.buildApsMembership(signUp.username(), customer.getId());
      apsMembershipRepo.saveAndFlush(apsMembership);

      ActivationRequest activationRequest = new ActivationRequest(apsMembership.getLicense(), EntityDefault.DEVICE_ID);
      jsonRequest = objectMapper.writeValueAsString(activationRequest);

      mockMvc.perform(put("/api/membership/activate")
                  .contentType(MediaType.APPLICATION_JSON)
                  .header("Authorization", "Bearer " + token)
            .content(jsonRequest))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value("OK"))
            .andExpect(jsonPath("$.message").value("Membership number activated successfully"));

      mockMvc.perform(get("/api/videos/today")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.greeting", notNullValue()))
            .andExpect(jsonPath("$.tags.length()", not(empty())))
            .andExpect(jsonPath("$.videos.length()", is(activeAllEnIds.size())));

   }


   private void createVideos() {

      // reference video list
      videosYoga = new ArrayList<>();
      videosPilates = new ArrayList<>();

      // Video Category Labels
      yoga = labelRepo.save(new Label(Lang.EN, "video.category.yoga", "Yoga"));
      pilates = labelRepo.save(new Label(Lang.EN, "video.category.pilates", "Pilates"));
      flexibility = labelRepo.save(new Label(Lang.EN, "video.category.flexibility", "Flexibility"));

      // Video Tag Labels
      all = labelRepo.save(new Label(Lang.EN, "video.tag.all", "All"));
      relax = labelRepo.save(new Label(Lang.EN, "video.tag.relax", "Relax"));
      energy = labelRepo.save(new Label(Lang.EN, "video.tag.energy", "Energy"));
      strength = labelRepo.save(new Label(Lang.EN, "video.tag.strength", "Strength"));

      // Video Categories
      yogaCategory = createVideoCategory(EntityDefault.YOGA, yoga);
      videoCategoryRepo.save(yogaCategory);


      pilatesCategory = createVideoCategory(EntityDefault.PILATES, pilates);
      videoCategoryRepo.save(pilatesCategory);

      flexibilityCategory = createVideoCategory(EntityDefault.FLEXIBILITY, flexibility);
      flexibilityCategory.setArchived(archived);
      videoCategoryRepo.save(flexibilityCategory);

      // YOGA Videos
      yoga1En = createVideo(yogaCategory, "1010", "all, relax");
      videoRepo.save(yoga1En);
      videosYoga.add(yoga1En);

      yoga2En = createVideo(yogaCategory, "1020", "all, relax");
      videoRepo.save(yoga2En);
      videosYoga.add(yoga2En);

      // Pilates Videos
      pilates1En = createVideo(pilatesCategory, "2010", "all, energy, strength");
      videoRepo.save(pilates1En);
      videosPilates.add(pilates1En);

      pilates2En = createVideo(pilatesCategory, "2020", "all, energy, strength");
      videoRepo.save(pilates2En);
      videosPilates.add(pilates2En);

      pilates3En = createVideo(pilatesCategory, "2030", "all, energy, strength");
      videoRepo.save(pilates3En);
      videosPilates.add(pilates3En);

      pilates4En = createVideo(pilatesCategory, "2040", "all, energy, strength");
      videoRepo.save(pilates4En);
      videosPilates.add(pilates4En);

      pilates5En = createVideo(pilatesCategory, "2050", "all, energy, strength");
      videoRepo.save(pilates5En);
      videosPilates.add(pilates5En);

      pilatesFrench = createVideo(pilatesCategory, "2060", "all, energy, strength");
      pilatesFrench.setLanguage(Lang.FR); // FR
      videoRepo.save(pilatesFrench);
      videosPilates.add(pilatesFrench);

      pilatesArchived = createVideo(pilatesCategory, "2070", "all, energy, strength");
      pilatesArchived.setArchived(archived); // Archived
      videoRepo.save(pilatesArchived);
      videosPilates.add(pilatesArchived);

      // set references about the videos
      allVideos = videoRepo.findAll();
      activeAllEnIds = allVideos.stream().filter(v -> v.getLanguage() == Lang.EN && v.getArchived() == null).map(Video::getId).collect(Collectors.toSet());
      activeAllFrIds = allVideos.stream().filter(v -> v.getLanguage() == Lang.FR && v.getArchived() == null).map(Video::getId).collect(Collectors.toSet());
      activeYogaEnIds = allVideos.stream()
            .filter(v -> v.getLanguage() == Lang.EN && v.getArchived() == null && v.getVideoCategory().getCode().equals(yogaCategory.getCode()))
            .map(Video::getId).collect(Collectors.toSet());
      activePilatesEnIds = allVideos.stream()
            .filter(v -> v.getLanguage() == Lang.EN && v.getArchived() == null && v.getVideoCategory().getCode().equals(pilatesCategory.getCode()))
            .map(Video::getId).collect(Collectors.toSet());

      TodayVideo todayVideoEn = new TodayVideo(Lang.EN, LocalDate.now(), activeAllEnIds);
      todayVideoRepo.save(todayVideoEn);

   }

   private VideoCategory createVideoCategory(String code, Label label) {
      VideoCategory category = new VideoCategory();
      category.setCode(code);
      category.setTitle(NameUtils.toCamelCase(code));
      category.setLabel(label.getCode());
      category.setCreated(created);
      category.setReleased(released);
      category.setArchived(null);
      return category;
   }

   private Video createVideo(VideoCategory category, String number, String tags) {
      Video video = new Video();
      video.setVideoCategory(category);
      video.setCode("VIDEO_" + number);
      video.setTitle("Video #" + number);
      video.setDescription("Morning workout #" + number);
      video.setFilename(number + ".mov");
      video.setLanguage(Lang.EN);
      video.setPlayer(Player.PEECKO);
      video.setThumbnail("http://peecko/thumbnail/" + number);
      video.setUrl("http://peecko/video/" + number);
      video.setCreated(created);
      video.setReleased(released);
      video.setArchived(null);
      video.setDuration(Integer.valueOf(number));
      video.setTags(tags);
      return video;
   }

}
