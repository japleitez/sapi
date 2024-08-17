package com.peecko.api.service;

import com.peecko.api.domain.ApsUser;
import com.peecko.api.domain.PlayList;
import com.peecko.api.domain.Video;
import com.peecko.api.domain.VideoItem;
import com.peecko.api.domain.dto.IdName;
import com.peecko.api.domain.dto.PlaylistDTO;
import com.peecko.api.domain.dto.VideoDTO;
import com.peecko.api.domain.dto.VideoItemDTO;
import com.peecko.api.domain.mapper.PlayListMapper;
import com.peecko.api.domain.mapper.VideoMapper;
import com.peecko.api.repository.PlayListRepo;
import com.peecko.api.repository.UserFavoriteVideoRepo;
import com.peecko.api.repository.VideoItemRepo;
import com.peecko.api.repository.VideoRepo;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PlayListService {

    final PlayListRepo playListRepo;
    final VideoItemRepo videoItemRepo;
    final VideoRepo videoRepo;
    final UserFavoriteVideoRepo userFavoriteVideoRepo;


    public PlayListService(PlayListRepo playListRepo, VideoItemRepo videoItemRepo, VideoRepo videoRepo, UserFavoriteVideoRepo userFavoriteVideoRepo) {
        this.playListRepo = playListRepo;
        this.videoItemRepo = videoItemRepo;
        this.videoRepo = videoRepo;
        this.userFavoriteVideoRepo = userFavoriteVideoRepo;
    }

    public boolean existsPlayList(ApsUser apsUser, String name) {
        List<PlayList> list = playListRepo.findByApsUserAAndName(apsUser, name);
        return !list.isEmpty();
    }

    public PlaylistDTO createPlayList(ApsUser apsUser, String name) {
        Instant now = Instant.now();
        PlayList playList = new PlayList();
        playList.setCounter(0);
        playList.setApsUser(apsUser);
        playList.setName(name);
        playList.setCreated(now);
        playList.setUpdated(now);
        playList = playListRepo.save(playList);
        return PlayListMapper.playlistDTO(playList);
    }

    public void deletePlayList(Long playlistId) {
        playListRepo.deleteById(playlistId);
    }

    public List<IdName> getPlayListIdNames(ApsUser apsUser) {
        return playListRepo
                .findByApsUser(apsUser)
                .stream()
                .map(PlayListMapper::idName)
                .collect(Collectors.toList());
    }

    public PlaylistDTO getPlayListDTO(Long apsUserId, Long playListId) {
        Optional<PlayList> optionalPlayList = playListRepo.findById(playListId);
        if (optionalPlayList.isPresent()) {
            PlaylistDTO dto =  new PlaylistDTO();
            PlayList playList = optionalPlayList.get();
            dto.setId(playList.getId());
            dto.setName(playList.getName());
            if (!playList.getVideoItems().isEmpty()) {
                Set<Long> favIds = userFavoriteVideoRepo.findVideoIdsByApsUserId(apsUserId);
                List<VideoItemDTO> videoItemDTOS = playList.getVideoItems()
                        .stream()
                        .map(item -> getVideoItemDTO(item, favIds))
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());
                dto.getVideoItemDTOS().addAll(sortVideoItems(videoItemDTOS));
            }
            return dto;
        }
        return null;
    }


    private VideoItemDTO getVideoItemDTO(VideoItem videoItem, Set<Long> favIds) {
        Video video = videoRepo.findByCode(videoItem.getCode()).orElse(null);
        if (video != null) {
            VideoItemDTO itemDTO = new VideoItemDTO();
            itemDTO.setCode(videoItem.getCode());
            VideoDTO videoDTO = VideoMapper.videoDTO(video);
            videoDTO.setFavorite(favIds.contains(video.getId()));
            itemDTO.setVideo(videoDTO);
            if (videoItem.getPrevious() != null) {
                itemDTO.setPrevious(videoItem.getPrevious().getCode());
            }
            if (videoItem.getNext() != null) {
                itemDTO.setNext(videoItem.getNext().getCode());
            }
            return itemDTO;
        }
        return null;

    }


    public PlayList moveVideoItemBelowAnother(Long playlistId, String movingVideoCode, String targetVideoCode) {
        Optional<PlayList> playlistOptional = playListRepo.findById(playlistId);

        if (playlistOptional.isPresent()) {
            PlayList playlist = playlistOptional.get();

            VideoItem movingVideo = videoItemRepo.findById(movingVideoCode).orElse(null);
            VideoItem targetVideo = videoItemRepo.findById(targetVideoCode).orElse(null);

            if (movingVideo != null && targetVideo != null && movingVideo != targetVideo) {
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

                // Save changes to the repository
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

                return playlist;
            }
        }
        return null;
    }

    public PlayList addVideoItemToTop(Long playlistId, VideoItem newVideoItem) {
        Optional<PlayList> playlistOptional = playListRepo.findById(playlistId);

        if (playlistOptional.isPresent()) {
            PlayList playlist = playlistOptional.get();

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

            return playListRepo.save(playlist);
        }

        return null;
    }

    public PlayList addVideoItemToBottom(Long playlistId, VideoItem newVideoItem) {

        Optional<PlayList> playlistOptional = playListRepo.findById(playlistId);

        if (playlistOptional.isPresent()) {
            PlayList playlist = playlistOptional.get();

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

            return playListRepo.save(playlist);
        }

        return null;
    }

    public void removeVideoItems(Long playlistId, List<String> videoCodes) {
        PlayList playList = playListRepo.findById(playlistId).orElse(null);
        if (playList == null) {
            return;
        }
        videoCodes.forEach(code -> this.removeVideoItem(code, playList));
    }

    public void removeVideoItem(String code, PlayList playList) {

        VideoItem videoItem = videoItemRepo.findByCodeAAndPlaylist(code, playList).orElse(null);

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

    private List<VideoItemDTO> sortVideoItems(List<VideoItemDTO> videoItemDTOS) {
        List<VideoItemDTO> sortedList =  new ArrayList<>();
        if (videoItemDTOS == null || videoItemDTOS.isEmpty()) {
            return sortedList;
        }
        Integer index = 0;
        VideoItemDTO videoItemDTO = videoItemDTOS.stream().filter(v -> !StringUtils.hasText(v.getPrevious())).findAny().get();
        videoItemDTO.setIndex(index);
        sortedList.add(videoItemDTO);
        while (StringUtils.hasText(videoItemDTO.getNext())) {
            String next = videoItemDTO.getNext();;
            videoItemDTO = videoItemDTOS.stream().filter(v -> next.equals(v.getCode())).findAny().get();
            index++;
            videoItemDTO.setIndex(index);
            sortedList.add(videoItemDTO);
        }
        return sortedList;
    }

}

