package com.peecko.api.domain.sorter;

import com.peecko.api.domain.dto.VideoItemDTO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VideoListSorter {
    public static List<VideoItemDTO> sortVideoList(List<VideoItemDTO> videoList) {
        // Create a map of code to VideoItemDTO for quick lookup
        Map<String, VideoItemDTO> map = new HashMap<>();
        for (VideoItemDTO item : videoList) {
            map.put(item.getCode(), item);
        }

        // Find the head of the list (where previous is null)
        VideoItemDTO head = null;
        for (VideoItemDTO item : videoList) {
            if (item.getPrevious() == null) {
                head = item;
                break;
            }
        }

        // Reconstruct the list in order
        List<VideoItemDTO> sortedList = new ArrayList<>();
        VideoItemDTO current = head;
        Integer index = 0;
        while (current != null) {
            current.setIndex(index);
            sortedList.add(current);
            current = map.get(current.getNext());
            index++;
        }

        return sortedList;
    }

}
