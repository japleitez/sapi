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
import com.peecko.api.web.payload.request.CreatePlaylistRequest;
import com.peecko.api.web.payload.request.SignInRequest;
import com.peecko.api.web.payload.request.SignUpRequest;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import org.springframework.http.MediaType;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@AutoConfigureMockMvc // Automatically configures MockMvc
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

   @Autowired
   LanguageRepo languageRepo;

   @Autowired
   HelpItemRepo helpItemRepo;

   @Autowired
   NotificationRepo notificationRepo;

   @Autowired
   PlayListRepo playListRepo;
   ObjectMapper objectMapper = new ObjectMapper();

   // data references

   Customer customer1 = null;
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

   Notification notification1 = null;
   Notification notification2 = null;

   int helpSizeIs2;
   int activeLanguagesSizeIs2;
   int activeNotificationsSizeIs2;
   static final String EN = Lang.EN.name();

   Language languageEn = null;
   Language languageFr = null;

   ApsUser apsUser = null;
   String token = null;

   int activeYogaTopCap = 0;
   int activePilatesTopCap = 0;

   PlayList playList = null;

   @BeforeAll
   public void executeOnceBeforeAllTests() {
      videoService.clearAllCaches();
      createCustomer();
      createHelp();
      createNotifications();
      createLanguages();
      createVideos();
      activeYogaTopCap = Math.min(activeYogaEnIds.size(), VideoService.CATEGORY_VIDEOS_MAX_SIZE);
      activePilatesTopCap = Math.min(activePilatesEnIds.size(), VideoService.CATEGORY_VIDEOS_MAX_SIZE);
   }

   @Test
   @Order(1)
   public void test01SignUp() throws Exception {
      // Given
      SignUpRequest signUp = new SignUpRequest(
            EntityDefault.USER_NAME,
            EntityDefault.USER_EMAIL,
            EntityDefault.USER_PASSWORD, EN);

      // When
      String jsonRequest = objectMapper.writeValueAsString(signUp);
      mockMvc.perform(post("/api/auth/signup")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(jsonRequest))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value("OK"))
            .andExpect(jsonPath("$.message").value("Successful sign up"));

      // Then
      apsUser = apsUserRepo.findByUsername(signUp.username()).orElse(null);
      assertNotNull(apsUser);
   }

   @Test
   @Order(2)
   public void test02ActivateUser() throws Exception {

      // verify email is not active
      apsUser = apsUserRepo.findByUsername(EntityDefault.USER_EMAIL).orElse(null);
      assertNotNull(apsUser);
      assertFalse(apsUser.getActive());

      // activate user email
      mockMvc.perform(get("/api/auth/active/{username}", EntityDefault.USER_EMAIL))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value("OK"))
            .andExpect(jsonPath("$.message").value("Email is verified"));

      // verify email is active
      apsUser = apsUserRepo.findByUsername(EntityDefault.USER_EMAIL).orElse(null);
      assertNotNull(apsUser);
      assertTrue(apsUser.getActive());

   }

   @Test
   @Order(3)
   public void test03SignIn() throws Exception {
      // Given
      SignInRequest signInRequest = new SignInRequest(
            EntityDefault.USER_EMAIL,
            EntityDefault.USER_PASSWORD,
            EntityDefault.PHONE_MODEL,
            EntityDefault.OS_VERSION,
            EntityDefault.DEVICE_ID);

      // When
      String jsonRequest = objectMapper.writeValueAsString(signInRequest);
      MvcResult result = mockMvc.perform(post("/api/auth/signin")
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

      // Then
      String jsonResponse = result.getResponse().getContentAsString();
      JsonNode jsonNode = objectMapper.readTree(jsonResponse);
      token = jsonNode.get("token").asText();
      assertNotNull(token);
   }

   @Test
   @Order(4)
   public void test04ActivateMembership() throws Exception {
      // Given
      ApsMembership apsMembership = EntityBuilder.buildApsMembership(apsUser.getUsername(), customer1.getId());
      apsMembershipRepo.saveAndFlush(apsMembership);

      // When
      ActivationRequest activationRequest = new ActivationRequest(apsMembership.getLicense(), EntityDefault.DEVICE_ID);
      String jsonRequest = objectMapper.writeValueAsString(activationRequest);
      mockMvc.perform(put("/api/membership/activate")
                  .contentType(MediaType.APPLICATION_JSON)
                  .header("Authorization", "Bearer " + token)
                  .content(jsonRequest))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value("OK"))
            .andExpect(jsonPath("$.message").value("Membership number activated successfully"));
   }

   @Test
   @Order(5)
   public void test05GetTodayVideos() throws Exception {
      mockMvc.perform(get("/api/videos/today")
                  .contentType(MediaType.APPLICATION_JSON)
                  .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.greeting", notNullValue()))
            .andExpect(jsonPath("$.tags.length()", not(empty())))
            .andExpect(jsonPath("$.videos.length()", is(activeAllEnIds.size())));
   }

   @Test
   @Order(6)
   public void test06GetCategories() throws Exception {
      mockMvc.perform(get("/api/videos/categories")
                  .contentType(MediaType.APPLICATION_JSON)
                  .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.categories.length()", is(2)))
            .andExpect(jsonPath("$.categories[0].code").value(EntityDefault.PILATES))
            .andExpect(jsonPath("$.categories[0].title").value(pilates.getText()))
            .andExpect(jsonPath("$.categories[0].videos.length()", is(activePilatesTopCap)))
            .andExpect(jsonPath("$.categories[1].code").value(EntityDefault.YOGA))
            .andExpect(jsonPath("$.categories[1].title").value(yoga.getText()))
            .andExpect(jsonPath("$.categories[1].videos.length()", is(activeYogaTopCap)));
   }

   @Test
   @Order(7)
   public void test07GetCategory() throws Exception {
      mockMvc.perform(get("/api/videos/categories/{code}", EntityDefault.YOGA)
                  .contentType(MediaType.APPLICATION_JSON)
                  .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(EntityDefault.YOGA))
            .andExpect(jsonPath("$.title").value(yoga.getText()))
            .andExpect(jsonPath("$.videos.length()", is(activeYogaTopCap)));
   }

   @Test
   @Order(8)
   public void test08GetFavoriteVideos() throws Exception {
      // validate there are no favorite videos
      mockMvc.perform(get("/api/videos/favorites")
                  .contentType(MediaType.APPLICATION_JSON)
                  .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.tags.length()", is(0)))
            .andExpect(jsonPath("$.videos.length()", is(0)));

      // add 1 yoga and 1 pilates video to the user's favorites
      mockMvc.perform(put("/api/videos/favorites/{videoId}", yoga1En.getCode())
                  .contentType(MediaType.APPLICATION_JSON)
                  .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk());

      mockMvc.perform(put("/api/videos/favorites/{videoId}", pilates1En.getCode())
                  .contentType(MediaType.APPLICATION_JSON)
                  .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk());

      // validate there are 2 favorite videos
      mockMvc.perform(get("/api/videos/favorites")
                  .contentType(MediaType.APPLICATION_JSON)
                  .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.tags.length()", not(empty())))
            .andExpect(jsonPath("$.videos.length()", is(2)))
            .andExpect(jsonPath("$.videos[?(@.code == '" + yoga1En.getCode() + "')]", hasSize(1)))
            .andExpect(jsonPath("$.videos[?(@.code == '" + pilates1En.getCode() + "')]", hasSize(1)));

      // validate there 1 favorite vide in the yoga category
      mockMvc.perform(get("/api/videos/categories/{code}", EntityDefault.YOGA)
                  .contentType(MediaType.APPLICATION_JSON)
                  .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(EntityDefault.YOGA))
            .andExpect(jsonPath("$.title").value(yoga.getText()))
            .andExpect(jsonPath("$.videos.length()", is(activeYogaEnIds.size())))
            .andExpect(jsonPath("$.videos[?(@.favorite == true)]", hasSize(1)));

      // validate there 1 favorite video in the pilates category
      mockMvc.perform(get("/api/videos/categories/{code}", EntityDefault.PILATES)
                  .contentType(MediaType.APPLICATION_JSON)
                  .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(EntityDefault.PILATES))
            .andExpect(jsonPath("$.title").value(pilates.getText()))
            .andExpect(jsonPath("$.videos.length()", is(activePilatesEnIds.size())))
            .andExpect(jsonPath("$.videos[?(@.favorite == true)]", hasSize(1)));

      // remove a video from the user's favorite list
      mockMvc.perform(delete("/api/videos/favorites/{videoId}", yoga1En.getCode())
                  .contentType(MediaType.APPLICATION_JSON)
                  .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk());

      // validate the video was removed
      mockMvc.perform(get("/api/videos/favorites")
                  .contentType(MediaType.APPLICATION_JSON)
                  .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.tags.length()", not(empty())))
            .andExpect(jsonPath("$.videos.length()", is(1)))
            .andExpect(jsonPath("$.videos[?(@.code == '" + yoga1En.getCode() + "')]", hasSize(0)))
            .andExpect(jsonPath("$.videos[?(@.code == '" + pilates1En.getCode() + "')]", hasSize(1)));

      // remove all favorites
      mockMvc.perform(delete("/api/videos/favorites")
                  .contentType(MediaType.APPLICATION_JSON)
                  .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk());

      // validate there are no favorite videos
      mockMvc.perform(get("/api/videos/favorites")
                  .contentType(MediaType.APPLICATION_JSON)
                  .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.videos.length()", is(0)));

   }

   @Test
   @Order(9)
   public void test09GetHelp() throws Exception {
      mockMvc.perform(get("/api/account/help")
                  .contentType(MediaType.APPLICATION_JSON)
                  .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()", is(helpSizeIs2)))
            .andExpect(jsonPath("$[0].question", is(notNullValue())))
            .andExpect(jsonPath("$[0].answer", is(notNullValue())))
            .andExpect(jsonPath("$[1].question", is(notNullValue())))
            .andExpect(jsonPath("$[1].answer", is(notNullValue())));
   }

   @Test
   @Order(10)
   public void test10GetNotifications() throws Exception {
      // get notifications
      mockMvc.perform(get("/api/account/notifications")
                  .contentType(MediaType.APPLICATION_JSON)
                  .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()", is(activeNotificationsSizeIs2)))
            .andExpect(jsonPath("$[0].title", is(notNullValue())))
            .andExpect(jsonPath("$[0].message", is(notNullValue())))
            .andExpect(jsonPath("$[0].image", is(notNullValue())))
            .andExpect(jsonPath("$[0].video", is(notNullValue())))
            .andExpect(jsonPath("$[0].date", is(notNullValue())))
            .andExpect(jsonPath("$[0].viewed", is(false)))
            .andExpect(jsonPath("$[1].title", is(notNullValue())))
            .andExpect(jsonPath("$[1].message", is(notNullValue())))
            .andExpect(jsonPath("$[1].image", is(notNullValue())))
            .andExpect(jsonPath("$[1].video", is(notNullValue())))
            .andExpect(jsonPath("$[1].date", is(notNullValue())))
            .andExpect(jsonPath("$[1].viewed", is(false)));

      // set notification 2 as viewed
      mockMvc.perform(put("/api/account/notifications/{id}", notification2.getId())
                  .contentType(MediaType.APPLICATION_JSON)
                  .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk());

      // validate only notification 2 has been viewed
      mockMvc.perform(get("/api/account/notifications")
                  .contentType(MediaType.APPLICATION_JSON)
                  .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()", is(activeNotificationsSizeIs2)))
            .andExpect(jsonPath("$[?(@.id == " + notification1.getId() + " && @.viewed == false)]", hasSize(1)))
            .andExpect(jsonPath("$[?(@.id == " + notification2.getId() + " && @.viewed == true)]", hasSize(1)));
   }

   @Test
   @Order(11)
   public void test11GetLanguages() throws Exception {
      // get active languages ordered by name
      mockMvc.perform(get("/api/account/languages")
                  .contentType(MediaType.APPLICATION_JSON)
                  .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.selected", is(apsUser.getLanguage().name())))
            .andExpect(jsonPath("$.languages.length()", is(activeLanguagesSizeIs2)))
            .andExpect(jsonPath("$.languages[0].code", is(languageEn.getCode())))
            .andExpect(jsonPath("$.languages[0].name", is(languageEn.getName())))
            .andExpect(jsonPath("$.languages[1].code", is(languageFr.getCode())))
            .andExpect(jsonPath("$.languages[1].name", is(languageFr.getName())));

      // change user language to FR
      mockMvc.perform(put("/api/account/languages/{lang}", languageFr.getCode())
                  .contentType(MediaType.APPLICATION_JSON)
                  .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk());

      // verify user language had been changed to FR
      apsUser = apsUserRepo.findById(apsUser.getId()).orElse(null);
      assertNotNull(apsUser);
      assertEquals(languageFr.getCode(), apsUser.getLanguage().name());
   }

   @Test
   @Order(12)
   public void test12GetInstallations() throws Exception {
      // get user profile
      mockMvc.perform(get("/api/auth/installations")
              .contentType(MediaType.APPLICATION_JSON)
              .header("Authorization", "Bearer " + token))
              .andExpect(status().isOk())
              .andExpect(jsonPath("$.max-allowed").value(3))
              .andExpect(jsonPath("$.installations.length()").value(1))
              .andExpect(jsonPath("$.installations[0].device-id").value(EntityDefault.DEVICE_ID))
              .andExpect(jsonPath("$.installations[0].phone-model").value(EntityDefault.PHONE_MODEL))
              .andExpect(jsonPath("$.installations[0].os-version").value(EntityDefault.OS_VERSION))
              .andExpect(jsonPath("$.installations[0].installed-on", is(notNullValue())));
   }

   @Test
   @Order(13)
   public void test13CreatePlaylist() throws Exception {
      CreatePlaylistRequest request = new CreatePlaylistRequest(EntityDefault.PLAYLIST_NAME);
      String jsonRequest = objectMapper.writeValueAsString(request);
      mockMvc.perform(post("/api/videos/playlists")
              .contentType(MediaType.APPLICATION_JSON)
              .content(jsonRequest)
              .header("Authorization", "Bearer " + token))
              .andExpect(status().isOk())
              .andExpect(jsonPath("$.id", is(notNullValue())))
              .andExpect(jsonPath("$.username", is(EntityDefault.USER_EMAIL)))
              .andExpect(jsonPath("$.name", is(EntityDefault.PLAYLIST_NAME)))
              .andExpect(jsonPath("$.videoItems.length()", is(0)));

      playList = playListRepo.findByApsUserAndName(apsUser, EntityDefault.PLAYLIST_NAME).orElse(null);
      assertNotNull(playList);
   }

   @Test
   @Order(14)
   public void test14AddVideoToPlaylist() throws Exception {

      mockMvc.perform(put("/api/videos/playlists/{playListId}/{videoCode}", playList.getId(), pilates1En.getCode())
              .header("Authorization", "Bearer " + token))
              .andExpect(status().isOk());

      mockMvc.perform(put("/api/videos/playlists/{playListId}/{videoCode}", playList.getId(), pilates2En.getCode())
                      .header("Authorization", "Bearer " + token))
              .andExpect(status().isOk());

      mockMvc.perform(put("/api/videos/playlists/{playListId}/{videoCode}", playList.getId(), pilates3En.getCode())
                      .header("Authorization", "Bearer " + token))
              .andExpect(status().isOk());

      mockMvc.perform(put("/api/videos/playlists/{playListId}/{videoCode}", playList.getId(), pilates4En.getCode())
                      .header("Authorization", "Bearer " + token))
              .andExpect(status().isOk());

      mockMvc.perform(get("/api/videos/playlists/{id}", playList.getId())
              .header("Authorization", "Bearer " + token))
              .andExpect(status().isOk())
              .andExpect(jsonPath("$.videoItems.length()", is(4)))
              .andExpect(jsonPath("$.videoItems[0].code", is(pilates1En.getCode())))
              .andExpect(jsonPath("$.videoItems[1].code", is(pilates2En.getCode())))
              .andExpect(jsonPath("$.videoItems[2].code", is(pilates3En.getCode())))
              .andExpect(jsonPath("$.videoItems[3].code", is(pilates4En.getCode())));

   }

   @Test
   @Order(15)
   public void test15MoveVideoInPlaylist() throws Exception {
      String url = "/api/videos/playlists/{playListId}/{videoCode}/drag-beneath/{targetVideoCode}";

      // move pilates 1 under pilates 4 in 3 moves
      mockMvc.perform(put(url, playList.getId(), pilates1En.getCode(), pilates2En.getCode())
              .header("Authorization", "Bearer " + token));
      mockMvc.perform(put(url, playList.getId(), pilates1En.getCode(), pilates3En.getCode())
              .header("Authorization", "Bearer " + token));
      mockMvc.perform(put(url, playList.getId(), pilates1En.getCode(), pilates4En.getCode())
              .header("Authorization", "Bearer " + token));

      mockMvc.perform(get("/api/videos/playlists/{id}", playList.getId())
              .header("Authorization", "Bearer " + token))
              .andExpect(status().isOk())
              .andExpect(jsonPath("$.videoItems.length()", is(4)))
              .andExpect(jsonPath("$.videoItems[0].code", is(pilates2En.getCode())))
              .andExpect(jsonPath("$.videoItems[1].code", is(pilates3En.getCode())))
              .andExpect(jsonPath("$.videoItems[2].code", is(pilates4En.getCode())))
              .andExpect(jsonPath("$.videoItems[3].code", is(pilates1En.getCode())));

      // move pilates 1 back to the top
      mockMvc.perform(put(url, playList.getId(), pilates1En.getCode(), "top")
              .header("Authorization", "Bearer " + token));

      mockMvc.perform(get("/api/videos/playlists/{id}", playList.getId())
              .header("Authorization", "Bearer " + token))
              .andExpect(status().isOk())
              .andExpect(jsonPath("$.videoItems.length()", is(4)))
              .andExpect(jsonPath("$.videoItems[0].code", is(pilates1En.getCode())))
              .andExpect(jsonPath("$.videoItems[1].code", is(pilates2En.getCode())))
              .andExpect(jsonPath("$.videoItems[2].code", is(pilates3En.getCode())))
              .andExpect(jsonPath("$.videoItems[3].code", is(pilates4En.getCode())));

      // move pilates 3 below pilates 4 by repeating the same request 3 times
      mockMvc.perform(put(url, playList.getId(), pilates3En.getCode(), pilates4En.getCode())
              .header("Authorization", "Bearer " + token));
      mockMvc.perform(put(url, playList.getId(), pilates3En.getCode(), pilates4En.getCode())
              .header("Authorization", "Bearer " + token));
      mockMvc.perform(put(url, playList.getId(), pilates3En.getCode(), pilates4En.getCode())
              .header("Authorization", "Bearer " + token));

      mockMvc.perform(get("/api/videos/playlists/{id}", playList.getId())
                      .header("Authorization", "Bearer " + token))
              .andExpect(status().isOk())
              .andExpect(jsonPath("$.videoItems.length()", is(4)))
              .andExpect(jsonPath("$.videoItems[0].code", is(pilates1En.getCode())))
              .andExpect(jsonPath("$.videoItems[1].code", is(pilates2En.getCode())))
              .andExpect(jsonPath("$.videoItems[2].code", is(pilates4En.getCode())))
              .andExpect(jsonPath("$.videoItems[3].code", is(pilates3En.getCode())));
   }

   /**
    * Utilities to create data before the execution of the tests
    */

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
      yogaCategory = createVideoCategory(EntityDefault.YOGA, "yoga");
      videoCategoryRepo.save(yogaCategory);


      pilatesCategory = createVideoCategory(EntityDefault.PILATES, "pilates");
      videoCategoryRepo.save(pilatesCategory);

      flexibilityCategory = createVideoCategory(EntityDefault.FLEXIBILITY, "flexibility");
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

   private void createHelp() {

      // create 2 help items for english (default)
      helpSizeIs2 = 2;
      HelpItem helpItem1En = createHelpItem("What is peecko", "Your best wellness app companion", Lang.EN);
      helpItemRepo.save(helpItem1En);

      HelpItem helpItem2En = createHelpItem("How can I renew my membership?", "Your employer will directly provide the business license to you", Lang.EN);
      helpItemRepo.save(helpItem2En);

      // create 2 help items for french
      HelpItem helpItem1Fr = createHelpItem("What is peecko", "Votre meilleur application de bien-être", Lang.FR);
      helpItemRepo.save(helpItem1Fr);

      HelpItem helpItem2Fr = createHelpItem("How can I renew my membership?", "Votre employeur le contactera directement pour vous", Lang.FR);
      helpItemRepo.save(helpItem2Fr);
   }

   private void createCustomer() {
      customer1 = EntityBuilder.buildCustomer();
      customerRepo.saveAndFlush(customer1);
   }

   private void createNotifications() {
      LocalDate startsToday = LocalDate.now();
      LocalDate startsTomorrow = LocalDate.now().plusDays(1);
      LocalDate expiresInAMonth = startsToday.plusMonths(1);
      LocalDate expiredYesterday = LocalDate.now().minusDays(1);


      // create 2 active notifications
      activeNotificationsSizeIs2 = 2;
      notification1 = createNotification(customer1, Lang.EN, startsToday, expiresInAMonth);
      notificationRepo.save(notification1);

      notification2 = createNotification(customer1, Lang.FR, startsToday, expiresInAMonth);
      notificationRepo.save(notification2);

      // create 2 inactive notifications
      Notification notification3 = createNotification(customer1, Lang.EN, startsToday, expiredYesterday);
      notificationRepo.save(notification3);

      Notification notification4 = createNotification(customer1, Lang.EN, startsTomorrow, expiresInAMonth);
      notificationRepo.save(notification4);
   }

   private void createLanguages() {
      // create 2 active languages
      activeLanguagesSizeIs2 = 2;
      languageEn = createLanguage("EN", "English", true);
      languageRepo.save(languageEn);
      languageFr = createLanguage("FR", "French", true);
      languageRepo.save(languageFr);
      // create 2 inactive languages
      languageRepo.save(createLanguage("ES", "Spanish", false));
      languageRepo.save(createLanguage("DE", "German", false));
   }

   private Language createLanguage(String code, String name, boolean active) {
      Language language = new Language();
      language.setCode(code);
      language.setName(name);
      language.setActive(active);
      return language;
   }

   private VideoCategory createVideoCategory(String code, String label) {
      VideoCategory category = new VideoCategory();
      category.setCode(code);
      category.setTitle(NameUtils.toCamelCase(code));
      category.setLabel(label);
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

   private HelpItem createHelpItem(String question, String answer, Lang lang) {
      HelpItem helpItem = new HelpItem();
      helpItem.setQuestion(question);
      helpItem.setAnswer(answer);
      helpItem.setLang(lang);
      return helpItem;
   }

   private Notification createNotification(Customer customer, Lang lang, LocalDate starts, LocalDate expires) {
      Notification n = new Notification();
      n.setTitle("Test");
      n.setLanguage(lang);
      n.setCustomer(customer);
      n.setStarts(starts);
      n.setExpires(expires);
      n.setMessage("Test");
      n.setVideoUrl("http://peecko/video/1");
      n.setImageUrl("http://peecko/thumbnail/1");
      return n;
   }

}
