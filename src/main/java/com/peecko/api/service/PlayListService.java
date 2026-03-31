package com.peecko.api.service;

import com.peecko.api.domain.ApsUser;
import com.peecko.api.domain.PlayList;
import com.peecko.api.domain.PlayListItem;
import com.peecko.api.domain.Video;
import com.peecko.api.domain.dto.IdName;
import com.peecko.api.domain.dto.PlayListDTO;
import com.peecko.api.domain.dto.VideoDTO;
import com.peecko.api.domain.dto.VideoItemDTO;
import com.peecko.api.domain.mapper.PlayListMapper;
import com.peecko.api.security.Login;
import com.peecko.api.repository.PlayListRepo;
import com.peecko.api.repository.UserFavoriteVideoRepo;
import com.peecko.api.repository.PlayListItemRepo;
import com.peecko.api.repository.VideoRepo;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PlayListService {

    final VideoMapper videoMapper;
    final PlayListRepo playListRepo;
    final PlayListItemRepo playListItemRepo;
    final VideoRepo videoRepo;
    final UserFavoriteVideoRepo userFavoriteVideoRepo;


    public PlayListService(VideoMapper videoMapper, PlayListRepo playListRepo, PlayListItemRepo playListItemRepo, VideoRepo videoRepo, UserFavoriteVideoRepo userFavoriteVideoRepo) {
        this.videoMapper = videoMapper;
        this.playListRepo = playListRepo;
        this.playListItemRepo = playListItemRepo;
        this.videoRepo = videoRepo;
        this.userFavoriteVideoRepo = userFavoriteVideoRepo;
    }

    @Transactional
    public void addVideoAtBottom(Long playListId, String videoCode) {
        PlayList playList = playListRepo.findById(playListId).orElseThrow(() -> new RuntimeException("PlayList not found with id: " + playListId));
        Video video = videoRepo.findByCode(videoCode).orElseThrow(() -> new RuntimeException("Video not found with code: " + videoCode));
        if (playListItemRepo.findByPlayListIdAndVideoCode(playListId, videoCode).isPresent()) {
            throw new RuntimeException("Video already exists in the PlayList");
        }
        int nextPosition = playListItemRepo.findMaxPositionByPlayListId(playListId).orElse(-1) + 1;
        PlayListItem item = new PlayListItem(playList, video).code(videoCode).position(nextPosition);
        playListItemRepo.save(item);
        playListRepo.updateCounter(playListId, nextPosition + 1);
    }

    @Transactional
    public void moveVideoToTop(Long playListId, String videoCode) {
        List<PlayListItem> items = playListItemRepo.findByPlayListIdOrderByPositionAsc(playListId);
        if (items.isEmpty()) return;
        PlayListItem toMove = findItemByCode(items, videoCode);

        if (items.get(0).equals(toMove)) return; // already at top

        items.remove(toMove);
        items.add(0, toMove);
        reassignPositions(items);
        playListItemRepo.saveAll(items);
    }

    /**
     * move dragged video immediately AFTER the target video (standard drag-and-drop reorder)
     * @param playListId
     * @param videoCodeToMove
     * @param targetVideoCode
     */
    @Transactional
    public void dragVideoAfter(Long playListId, String videoCodeToMove, String targetVideoCode) {
        List<PlayListItem> items = playListItemRepo.findByPlayListIdOrderByPositionAsc(playListId);
        if (items.isEmpty()) return;

        PlayListItem toMove = findItemByCode(items, videoCodeToMove);
        PlayListItem target = findItemByCode(items, targetVideoCode);

        if (toMove.equals(target)) return;

        items.remove(toMove);
        int targetIndex = items.indexOf(target);
        int insertIndex = targetIndex + 1;
        items.add(insertIndex, toMove);

        reassignPositions(items);
        playListItemRepo.saveAll(items);
    }

    private PlayListItem findItemByCode(List<PlayListItem> items, String videoCode) {
        return items.stream()
                .filter(item -> item.getCode().equals(videoCode))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Video with code " + videoCode + " not found in this PlayList"));
    }

    private void reassignPositions(List<PlayListItem> items) {
        for (int i = 0; i < items.size(); i++) {
            items.get(i).setPosition(i);
        }
    }

    @Transactional
    public void removeVideoFromPlayList(Long playListId, String videoCode) {
        List<PlayListItem> items = playListItemRepo.findByPlayListIdOrderByPositionAsc(playListId);
        if (items.isEmpty()) {
            return;
        }

        PlayListItem itemToRemove = findItemByCode(items, videoCode);

        items.remove(itemToRemove);
        reassignPositions(items);

        playListItemRepo.delete(itemToRemove);
        playListItemRepo.saveAll(items);
        playListRepo.updateCounter(playListId, items.size());
    }

    @Transactional
    public void removeVideosFromPlaylist(Long playListId, List<String> videoCodes) {
        if (videoCodes == null || videoCodes.isEmpty()) {
            return;
        }

        List<PlayListItem> items = playListItemRepo.findByPlayListIdOrderByPositionAsc(playListId);
        if (items.isEmpty()) {
            return;
        }

        // Convert videoCodes to a Set for O(1) lookup
        Set<String> codesToRemove = videoCodes.stream()
                .filter(code -> code != null && !code.trim().isEmpty())
                .collect(Collectors.toSet());

        if (codesToRemove.isEmpty()) {
            return;
        }

        // Filter out items to be removed
        List<PlayListItem> remainingItems = items.stream()
                .filter(item -> !codesToRemove.contains(item.getCode()))
                .collect(Collectors.toList());

        // If nothing changed, do nothing
        if (remainingItems.size() == items.size()) {
            return;
        }

        // Reassign positions to remaining items
        reassignPositions(remainingItems);

        // Delete items that should be removed
        List<PlayListItem> itemsToDelete = items.stream()
                .filter(item -> codesToRemove.contains(item.getCode()))
                .collect(Collectors.toList());

        playListItemRepo.deleteAll(itemsToDelete);

        // Save updated positions for remaining items
        if (!remainingItems.isEmpty()) {
            playListItemRepo.saveAll(remainingItems);
        }
        playListRepo.updateCounter(playListId, remainingItems.size());
    }

    public boolean existsById(Long playlistId) {
        return playListRepo.existsById(playlistId);
    }

    public boolean existsPlayListByName(ApsUser apsUser, String name) {
        return playListRepo.findByApsUserAndName(apsUser, name).isPresent();
    }

    @Transactional
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

    @Transactional
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
        Set<Long> favIds = userFavoriteVideoRepo.findVideoIdsByApsUserId(apsUserId);
        List<VideoItemDTO> items = buildVideoItemDTOs(playList.getId(), favIds);
        playListDTO.getVideoItemDTOS().addAll(items);
        return playListDTO;
    }

    /**
     * Gets ordered PlayList items where each item knows its previous and next video code
     * @return the doubly-linked list representation of the PlayList
     */
    private List<VideoItemDTO> buildVideoItemDTOs(Long playListId, Set<Long> favIds) {
        List<PlayListItem> items = playListItemRepo.findByPlayListIdWithVideoOrderByPositionAsc(playListId);
        if (items.isEmpty()) {
            return List.of();
        }
        List<VideoItemDTO> nodes = new ArrayList<>(items.size());
        for (int i = 0; i < items.size(); i++) {
            PlayListItem currentItem = items.get(i);
            Video video = currentItem.getVideo();
            String prevCode = (i > 0) ? items.get(i - 1).getCode() : null;
            String nextCode = (i < items.size() - 1) ? items.get(i + 1).getCode() : null;

            video.setFavorite(favIds.contains(video.getId()));
            VideoDTO videoDTO = videoMapper.toVideoDTO(video, Login.getUserLanguage());
            VideoItemDTO node = new VideoItemDTO(i, prevCode, video.getCode(), nextCode, videoDTO);
            nodes.add(node);
        }
        return nodes;

    }

}
