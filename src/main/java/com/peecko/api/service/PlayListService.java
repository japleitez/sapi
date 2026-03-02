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
import jakarta.transaction.Transactional;
import java.time.Instant;
import java.util.*;

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

    @Transactional
    public void addVideoToPlaylist(Long playlistId, String videoCode) {
        PlayList playList = playListRepo.findById(playlistId).orElseThrow(() -> new RuntimeException("Playlist not found"));
        Video video = videoRepo.findByCode(videoCode).orElseThrow(() -> new RuntimeException("Video not found"));
        if (videoItemRepo.findByPlayListAndVideo(playList, video).isPresent()) {
            return; // Video already exists
        }
        VideoItem newVideoItem = new VideoItem();
        newVideoItem.setVideo(video);
        newVideoItem.setPlayList(playList);
        VideoItem bottomVideoItem = findBottom(playList).orElse(null);
        if (bottomVideoItem == null) {
            // empty playlist
            newVideoItem.setPrevious(null);
            newVideoItem.setNext(null);
        } else {
            insertBelow(newVideoItem, bottomVideoItem);
        }
        videoItemRepo.save(newVideoItem);
        int counter = Math.toIntExact(videoItemRepo.countByPlaylist(playlistId));
        playList.setCounter(counter);
        playListRepo.save(playList);
    }

    @Transactional
    public void moveVideoToTop(Long playListId, String codeToMove) {
        PlayList playlist = playListRepo.findById(playListId).orElseThrow(() -> new RuntimeException("Playlist not found"));

        Video video = videoRepo.findByCode(codeToMove).orElseThrow(() -> new RuntimeException("Video not found"));

        VideoItem itemToMove = videoItemRepo.findByPlayListAndVideo(playlist, video).orElseThrow(() -> new RuntimeException("Video Item not found"));

        if (itemToMove.getPrevious() == null) {
            // video already at the top
            return;
        }
        detachNode(itemToMove);
        VideoItem currentTop = findTop(playlist).orElse(null);
        if (currentTop == null) {
            // playlist was empty -> item becomes the only node
            itemToMove.setPrevious(null);
            itemToMove.setNext(null);
        } else {
            // insert before current top
            itemToMove.setPrevious(null);
            itemToMove.setNext(currentTop);
            currentTop.setPrevious(itemToMove);
        }
        videoItemRepo.save(itemToMove);
        if (currentTop != null) {
            videoItemRepo.save(currentTop);
        }
    }

    @Transactional
    public void moveVideoBelowTarget(Long playlistId, String codeToMove, String codeTarget) {
        PlayList playlist = playListRepo.findById(playlistId).orElseThrow(() -> new RuntimeException("Playlist not found"));
        Video videoToMove = videoRepo.findByCode(codeToMove).orElseThrow(() -> new RuntimeException("Video(toMove) not found"));
        Video videoTarget = videoRepo.findByCode(codeTarget).orElseThrow(() -> new RuntimeException("Video(target) not found") );
        VideoItem itemToMove = videoItemRepo.findByPlayListAndVideo(playlist, videoToMove).orElseThrow(() -> new RuntimeException("Video Item not found"));
        VideoItem itemTarget = videoItemRepo.findByPlayListAndVideo(playlist, videoTarget).orElseThrow(() -> new RuntimeException("Video Item not found"));
        detachNode(itemToMove);
        insertBelow(itemToMove, itemTarget);
        videoItemRepo.save(itemToMove);
        videoItemRepo.save(itemTarget);
    }

    public void removeVideosFromPlaylist(Long playlistId, List<String> videoCodes) {
        if (videoCodes == null || videoCodes.isEmpty()) {
            return;
        }
        PlayList playList = playListRepo.findById(playlistId).orElseThrow(() -> new RuntimeException("Playlist not found " + playlistId));
        List<VideoItem> itemsToRemove = videoItemRepo.findByPlayListAndVideoCodeIn(playList, videoCodes);
        if (itemsToRemove.isEmpty()) {
            return;
        }
        for (VideoItem item : itemsToRemove) {
            detachNode(item);
        }
        videoItemRepo.deleteAll(itemsToRemove);
        int counter = Math.toIntExact(videoItemRepo.countByPlaylist(playlistId));
        playList.setCounter(counter);
    }

    public boolean existsById(Long playlistId) {
        return playListRepo.existsById(playlistId);
    }

    public boolean existsPlayListByName(ApsUser apsUser, String name) {
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
        PlayListDTO playListDTO = PlayListMapper.toPlayListDTO(playList);
        if (!playList.getVideoItems().isEmpty()) {
            List<String> videoCodes = playList.getVideoItems().stream().map(VideoItem::getCode).toList();
            Set<Video> videos = videoRepo.findByCodes(videoCodes);
            Set<Long> favIds = userFavoriteVideoRepo.findVideoIdsByApsUserId(apsUserId);
            List<VideoItemDTO> videoItemDTOs = playList
                    .getVideoItems()
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

    private Optional<VideoItem> findTop(PlayList playlist) {
        return videoItemRepo.findFirstVideoItemOfPlaylist(playlist.getId());
    }

    private Optional<VideoItem> findBottom(PlayList playlist) {
        return videoItemRepo.findLastVideoItemOfPlaylist(playlist.getId());
    }

    private void detachNode(VideoItem toDetach) {
        VideoItem prev = toDetach.getPrevious();
        VideoItem next = toDetach.getNext();
        if (prev != null) {
            prev.setNext(next);
            videoItemRepo.save(prev);
        }
        if (next != null) {
            next.setPrevious(prev);
            videoItemRepo.save(next);
        }
        toDetach.setPrevious(null);
        toDetach.setNext(null);
    }

    private void insertBelow(VideoItem toInsert, VideoItem target) {
        VideoItem oldNext = target.getNext();
        target.setNext(toInsert);
        toInsert.setPrevious(target);
        toInsert.setNext(oldNext);
        if (oldNext != null) {
            oldNext.setPrevious(toInsert);
            videoItemRepo.save(oldNext);
        }
    }
}