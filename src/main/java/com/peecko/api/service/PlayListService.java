package com.peecko.api.service;

import com.peecko.api.domain.ApsUser;
import com.peecko.api.domain.PlayList;
import com.peecko.api.domain.Video;
import com.peecko.api.domain.VideoItem;
import com.peecko.api.domain.dto.IdName;
import com.peecko.api.domain.dto.PlayListDTO;
import com.peecko.api.domain.dto.VideoDTO;
import com.peecko.api.domain.dto.VideoItemDTO;
import com.peecko.api.domain.enumeration.Lang;
import com.peecko.api.domain.mapper.PlayListMapper;
import com.peecko.api.utils.VideoListSorter;
import com.peecko.api.repository.PlayListRepo;
import com.peecko.api.repository.UserFavoriteVideoRepo;
import com.peecko.api.repository.VideoItemRepo;
import com.peecko.api.repository.VideoRepo;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PlayListService {

    final VideoMapper videoMapper;
    final PlayListRepo playListRepo;
    final VideoItemRepo videoItemRepo;
    final VideoRepo videoRepo;
    final UserFavoriteVideoRepo userFavoriteVideoRepo;


    public PlayListService(VideoMapper videoMapper, PlayListRepo playListRepo, VideoItemRepo videoItemRepo, VideoRepo videoRepo, UserFavoriteVideoRepo userFavoriteVideoRepo) {
        this.videoMapper = videoMapper;
        this.playListRepo = playListRepo;
        this.videoItemRepo = videoItemRepo;
        this.videoRepo = videoRepo;
        this.userFavoriteVideoRepo = userFavoriteVideoRepo;
    }

    public boolean existsPlayList(ApsUser apsUser, String name) {
        return playListRepo.findByApsUserAndName(apsUser, name).isPresent();
    }

    public PlayList createPlayList(Long apsUserId, String name) {
        Instant now = Instant.now();
        PlayList playList = new PlayList();
        playList.setApsUser(ApsUser.of(apsUserId));
        playList.setName(name);
        playList.setCounter(0);
        playList.setCreated(now);
        playList.setUpdated(now);
        return playListRepo.save(playList);
    }

    public PlayListDTO toPlayListDTO(PlayList playList) {
        return PlayListMapper.toPlayListDTO(playList);
    }

    public void deletePlayList(Long playlistId) {
        playListRepo.deleteById(playlistId);
    }

    public List<IdName> getPlayListsAsIdNames(ApsUser apsUser) {
        return playListRepo
                .findByApsUser(apsUser)
                .stream()
                .map(PlayListMapper::toIdName)
                .sorted(Comparator.comparing(IdName::getName)).toList();
    }

    public PlayListDTO getPlayListAsDTO(Long playListId, Long apsUserId) {
        PlayList playList = playListRepo.findByIdWithVideoItems(playListId).orElse(null);
        if (playList == null) {
            return null;
        }
        return buildPlayListDTO(playList, apsUserId);
    }

    private PlayListDTO buildPlayListDTO(PlayList playList, Long apsUserId) {
        PlayListDTO playListDTO =  new PlayListDTO();
        playListDTO.setId(playList.getId());
        playListDTO.setName(playList.getName());
        if (!playList.getVideoItems().isEmpty()) {
            List<String> videoCodes = playList.getVideoItems()
                    .stream()
                    .map(VideoItem::getCode).toList();
            Set<Video> videos = videoRepo.findByCodes(videoCodes);
            Set<Long> favIds = userFavoriteVideoRepo.findVideoIdsByApsUserId(apsUserId);
            List<VideoItemDTO> videoItemDTOs = playList.getVideoItems()
                    .stream()
                    .map(videoItem -> buildVideoItemDTO(videoItem, videos, favIds))
                    .filter(Objects::nonNull).toList();
            playListDTO.getVideoItemDTOS().addAll(VideoListSorter.sortVideoList(videoItemDTOs));
        }
        return playListDTO;
    }

    private VideoItemDTO buildVideoItemDTO(VideoItem videoItem, Set<Video> videos, Set<Long> favIds) {
        Video video = videos.stream().filter(v -> v.getCode().equals(videoItem.getCode())).findFirst().orElse(null);
        if (video == null) {
            return null;
        }
        video.setFavorite(favIds.contains(video.getId()));
        VideoItemDTO itemDTO = new VideoItemDTO();
        itemDTO.setCode(videoItem.getCode());
        VideoDTO videoDTO = videoMapper.toVideoDTO(video, Lang.EN); //TODO parametrize language
        itemDTO.setVideo(videoDTO);
        if (videoItem.getPrevious() != null) {
            itemDTO.setPrevious(videoItem.getPrevious().getCode());
        }
        if (videoItem.getNext() != null) {
            itemDTO.setNext(videoItem.getNext().getCode());
        }
        return itemDTO;
    }

    public void moveVideoItemToTop(Long playListId, String movingVideoCode) {
        PlayList playlist =  playListRepo.findByIdWithVideoItems(playListId).orElse(null);
        if (playlist == null || playlist.getVideoItems().isEmpty() || playlist.getVideoItems().size() == 1) {
            return;
        }
        VideoItem videoItem = playlist.getVideoItems()
                .stream()
                .filter(item -> item.getCode().equals(movingVideoCode))
                .findFirst()
                .orElse(null);
        if (videoItem == null) {
            return;
        }
        VideoItem currentTop = playlist.getVideoItems()
                .stream()
                .filter(item -> item.getPrevious() == null)
                .findFirst()
                .orElse(null);
        if (currentTop == null) {
            return;
        }
        if (videoItem.getCode().equals(currentTop.getCode())) {
            return;
        }

        // Detach the video item from its current position
        VideoItem previousVideo = videoItem.getPrevious();
        VideoItem nextVideo = videoItem.getNext();

        // Update the previous video's next pointer
        if (previousVideo != null) {
            previousVideo.setNext(nextVideo);
        }

        // Update the next video's previous pointer
        if (nextVideo != null) {
            nextVideo.setPrevious(previousVideo);
        }

        // Connect the video item to the top
        videoItem.setPrevious(null);
        videoItem.setNext(currentTop);
        currentTop.setPrevious(videoItem);

        videoItemRepo.save(videoItem);
        videoItemRepo.save(currentTop);
        if (previousVideo != null) {
            videoItemRepo.save(previousVideo);
        }
        if (nextVideo != null) {
            videoItemRepo.save(nextVideo);
        }
    }

    public void moveVideoItemBelowAnother(Long playListId, String movingVideoCode, String targetVideoCode) {

        VideoItem movingVideo = videoItemRepo.findByPlayListIdAndCode(playListId, movingVideoCode).orElse(null);
        VideoItem targetVideo = videoItemRepo.findByPlayListIdAndCode(playListId, targetVideoCode).orElse(null);
        if (movingVideo == null || targetVideo == null || movingVideo.getCode().equals(targetVideo.getCode())) {
            return;
        }

        // Disconnect movingVideo from its current position
        VideoItem previousVideo = movingVideo.getPrevious();
        VideoItem nextVideo = movingVideo.getNext();

        if (previousVideo != null) {
            previousVideo.setNext(nextVideo);
        }
        if (nextVideo != null) {
            nextVideo.setPrevious(previousVideo);
        }

        // Insert movingVideo below targetVideo
        VideoItem targetNext = targetVideo.getNext();

        movingVideo.setPrevious(targetVideo);
        movingVideo.setNext(targetNext);

        targetVideo.setNext(movingVideo);
        if (targetNext != null) {
            targetNext.setPrevious(movingVideo);
        }

        // Save changes
        videoItemRepo.save(movingVideo);
        videoItemRepo.save(targetVideo);
        if (previousVideo != null) {
            videoItemRepo.save(previousVideo);
        }
        if (nextVideo != null) {
            videoItemRepo.save(nextVideo);
        }
        if (targetNext != null) {
            videoItemRepo.save(targetNext);
        }

    }

    public void addVideoItemToTop(Long playListId, VideoItem newVideoItem) {

        PlayList playlist =  playListRepo.findByIdWithVideoItems(playListId).orElse(null);
        if (playlist == null) {
            return;
        }

        VideoItem currentTop = null;
        if (!playlist.getVideoItems().isEmpty()) {
            // Get the current top VideoItem
            currentTop = playlist.getVideoItems().stream()
                    .filter(videoItem -> videoItem.getPrevious() == null)
                    .findFirst()
                    .orElse(null);
        }

        // Set the new VideoItem's previous to null as it's going to be the new top
        newVideoItem.setPrevious(null);
        newVideoItem.setNext(currentTop);
        if (currentTop != null) {
            currentTop.setPrevious(newVideoItem);
        }

        playlist.addVideoItem(newVideoItem);
        videoItemRepo.save(newVideoItem);

        if (currentTop != null) {
            videoItemRepo.save(currentTop);
        }
        playListRepo.save(playlist);

    }

    public void addVideoItemToBottom(Long playlistId, VideoItem newVideoItem) {

        PlayList playlist = playListRepo.findByIdWithVideoItems(playlistId).orElse(null);
        if (playlist == null) {
            return;
        }

        VideoItem currentBottom = null;

        if (!playlist.getVideoItems().isEmpty()) {
            // Get the current bottom VideoItem
            currentBottom = playlist.getVideoItems().stream()
                    .filter(videoItem -> videoItem.getNext() == null)
                    .findFirst()
                    .orElse(null);
        }

        // Set the new VideoItem's next to null as it's going to be the new bottom
        newVideoItem.setNext(null);
        newVideoItem.setPrevious(currentBottom);

        if (currentBottom != null) {
            currentBottom.setNext(newVideoItem);
        }

        playlist.addVideoItem(newVideoItem);
        videoItemRepo.save(newVideoItem);

        if (currentBottom != null) {
            videoItemRepo.save(currentBottom);
        }

        playListRepo.save(playlist);
    }

    public void removeVideoItems(Long playListId, List<String> videoCodes) {
        videoCodes.forEach(code -> this.removeVideoItem(playListId, code));
    }

    public void removeVideoItem(Long playListId, String code) {
        VideoItem videoItem = videoItemRepo.findByPlayListIdAndCode(playListId, code).orElse(null);
        if (videoItem == null) {
            return;
        }

        VideoItem previousItem = videoItem.getPrevious();
        VideoItem nextItem = videoItem.getNext();

        // Update the previous item's next reference
        if (previousItem != null) {
            previousItem.setNext(nextItem);
            videoItemRepo.save(previousItem);
        }

        // Update the next item's previous reference
        if (nextItem != null) {
            nextItem.setPrevious(previousItem);
            videoItemRepo.save(nextItem);
        }

        // Remove the video item
        videoItemRepo.delete(videoItem);
    }

    public boolean existsById(Long playListId) {
        return playListRepo.existsById(playListId);
    }
}

