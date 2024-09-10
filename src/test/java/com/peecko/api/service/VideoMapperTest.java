package com.peecko.api.service;

import com.peecko.api.domain.*;
import com.peecko.api.domain.dto.CategoryDTO;
import com.peecko.api.domain.dto.VideoDTO;
import com.peecko.api.domain.enumeration.Intensity;
import com.peecko.api.domain.enumeration.Lang;
import com.peecko.api.domain.enumeration.Player;
import com.peecko.api.repository.LabelRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class VideoMapperTest {

   @Autowired
   LabelService labelService;

   @Autowired
   LabelRepo labelRepo;

   @Autowired
   VideoMapper videoMapper;

   @Autowired
   VideoService videoService;

   Label yoga = null;
   Label pilates = null;
   Label intensityLow = null;
   Label audienceAll = null;
   Label all = null;
   Label relax = null;
   Label energy = null;

   @BeforeEach
   void setUp() {
      videoService.clearAllCaches();
      labelRepo.deleteAll();
   }

   @Test
   void toCategoryDTO() {
      // Given
      createLabels();

      VideoCategory category = createBaseVideoCategory();
      category.setLabel("yoga");

      Coach coach = createBaseCoach();

      List<Video> videos = new ArrayList<>();
      videos.add(createBaseVideo(Lang.EN, coach, category));
      videos.add(createBaseVideo(Lang.FR, coach, category));

      // When
      CategoryDTO dto = videoMapper.toCategoryDTO(category, videos, Lang.EN);

      // Then
      assertEquals(dto.getCode(), category.getCode());
      assertEquals(dto.getTitle(), yoga.getText());
      assertEquals(2, dto.getVideos().size());
   }

   @Test
   void toCategoryDTOWithEmptyVideos() {
      // Given
      createLabels();

      VideoCategory category = createBaseVideoCategory();
      category.setLabel("pilates");

      // When
      CategoryDTO dto = videoMapper.toCategoryDTO(category, new ArrayList<>(), Lang.EN);

      // Then
      assertEquals(dto.getCode(), category.getCode());
      assertEquals(dto.getTitle(), pilates.getText());
      assertTrue(dto.getVideos().isEmpty());
   }

   @Test
   void toVideoDtoNoTranslation() {
      // Given
      Video video = createBaseVideo(Lang.EN, createBaseCoach(), createBaseVideoCategory());

      // When
      VideoDTO dto = videoMapper.toVideoDTO(video, Lang.EN);

      // Then
      assertEquals(dto.getCode(), video.getCode());
      assertEquals(dto.getCategory(), video.getVideoCategory().getCode());
      assertEquals(dto.getTitle(), video.getTitle());
      assertEquals(dto.getDuration(), String.valueOf(video.getDuration()));
      assertEquals(dto.getImage(), video.getThumbnail());
      assertEquals(dto.getUrl(), video.getUrl());
      assertEquals(dto.getDescription(), video.getDescription());
      assertEquals(dto.getPlayer(), video.getPlayer().name());
      assertEquals(dto.isFavorite(), video.isFavorite());
      assertEquals(dto.getAudience(), "video.audience." + video.getAudience()); // no translation
      assertEquals(dto.getIntensity(), "video.intensity." + video.getIntensity().name().toLowerCase()); // no translation
      assertEquals(dto.getTags(), convertTagsToList(video.getTags(), "video.tag.")); // no translation
      assertEquals(dto.getCoach(), video.getCoach().getName());
      assertEquals(dto.getResume(), video.getCoach().getResume());
      assertEquals(dto.getCoachWebsite(), video.getCoach().getWebsite());
      assertEquals(dto.getCoachEmail(), video.getCoach().getEmail());
      assertEquals(dto.getCoachInstagram(), video.getCoach().getInstagram());
   }

   @Test
   void toVideoDTOWithTranslations() {
      // Given
      createLabels();
      Video video = createBaseVideo(Lang.EN, createBaseCoach(), createBaseVideoCategory());
      video.setIntensity(Intensity.LOW);
      video.setAudience("all");
      video.setTags("all, relax, energy");

      // When
      VideoDTO dto = videoMapper.toVideoDTO(video, Lang.EN);

      // Then
      assertEquals(dto.getIntensity(), intensityLow.getText());
      assertEquals(dto.getAudience(), audienceAll.getText());
      assertEquals(dto.getTags(), convertTagsToList(all.getText() + ", " + relax.getText() + ", " + energy.getText(), null));

   }

   @Test
   void toVideoDTOWithNullCoach() {
      // Given
      Video video = createBaseVideo(Lang.FR, null, createBaseVideoCategory());

      // When
      VideoDTO dto = videoMapper.toVideoDTO(video, Lang.FR);

      // Then
      assertNull(dto.getCoach());
      assertNull(dto.getResume());
      assertNull(dto.getCoachWebsite());
      assertNull(dto.getCoachEmail());
      assertNull(dto.getCoachInstagram());
   }

   @Test
   void toVideoDTOWithNullAudience() {
      // Given
      Video video = createBaseVideo(Lang.FR, null, createBaseVideoCategory());
      video.setAudience(null);

      // When
      VideoDTO dto = videoMapper.toVideoDTO(video, Lang.FR);

      // Then
      assertNull(dto.getAudience());
   }

   @Test
   void toVideoDTOWithNullIntensity() {
      // Given
      Video video = createBaseVideo(Lang.FR, null, createBaseVideoCategory());
      video.setIntensity(null);

      // When
      VideoDTO dto = videoMapper.toVideoDTO(video, Lang.FR);

      // Then
      assertNull(dto.getIntensity());
   }

   @Test
   void toVideoDTOWithNullTags() {
      // Given
      Video video = createBaseVideo(Lang.FR, null, createBaseVideoCategory());
      video.setTags(null);

      // When
      VideoDTO dto = videoMapper.toVideoDTO(video, Lang.FR);

      // Then
      assertNull(dto.getTags());
   }

   /**
    * Utilities
    * ----------------------------------------------------------------
    */

   private Video createBaseVideo(Lang lang, Coach coach, VideoCategory videoCategory) {
      Video video  = new Video();
      video.setCoach(coach);
      video.setLanguage(lang);
      video.setVideoCategory(videoCategory);
      video.setCode(EntityDefault.VIDEO_CODE);
      video.setTitle(EntityDefault.VIDEO_TITLE);
      video.setDuration(EntityDefault.VIDEO_DURATION);
      video.setThumbnail(EntityDefault.VIDEO_THUMBNAIL);
      video.setUrl(EntityDefault.VIDEO_URL);
      video.setDescription(EntityDefault.VIDEO_DESCRIPTION);
      video.setAudience(EntityDefault.VIDEO_AUDIENCE_ALL);
      video.setIntensity(Intensity.LOW);
      video.setTags(EntityDefault.VIDEO_TAGS);
      video.setPlayer(Player.PEECKO);
      return video;
   }

   private VideoCategory createBaseVideoCategory() {
      VideoCategory category = new VideoCategory();
      category.setCode(EntityDefault.VIDEO_CATEGORY_CODE);
      category.setTitle(EntityDefault.VIDEO_CATEGORY_TITLE);
      category.setLabel(EntityDefault.VIDEO_CATEGORY_LABEL);
      return category;
   }

   private Coach createBaseCoach() {
      Coach coach = new Coach();
      coach.setName(EntityDefault.COACH_NAME);
      coach.setResume(EntityDefault.COACH_RESUME);
      coach.setWebsite(EntityDefault.COACH_WEBSITE);
      coach.setEmail(EntityDefault.COACH_EMAIL);
      coach.setInstagram(EntityDefault.COACH_INSTAGRAM);
      return coach;
   }

   private List<String> convertTagsToList(String codes, String prefix) {
      if (!StringUtils.hasText(codes)) {
         return null;
      }
      List<String> list = new ArrayList<>();
      String[] array = codes.split(",");
      for(String code: array) {
         code = code.trim();
         if (prefix == null) {
            list.add(code);
         } else {
            list.add(prefix + code);
         }
      }
      return list;
   }

   private void createLabels() {
      yoga = labelRepo.save(new Label(Lang.EN, "video.category.yoga", "Yoga"));
      pilates = labelRepo.save(new Label(Lang.EN, "video.category.pilates", "Pilates"));

      intensityLow = labelRepo.save(new Label(Lang.EN, "video.intensity.low", "Low"));
      audienceAll = labelRepo.save(new Label(Lang.EN, "video.audience.all", "All"));

      all = labelRepo.save(new Label(Lang.EN, "video.tag.all", "All"));
      relax = labelRepo.save(new Label(Lang.EN, "video.tag.relax", "Relax"));
      energy = labelRepo.save(new Label(Lang.EN, "video.tag.energy", "Energy"));
   }

}
