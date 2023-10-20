package com.peecko.api.repository;

import com.peecko.api.domain.*;
import com.peecko.api.utils.Common;
import com.peecko.api.utils.SponsorUtils;
import com.peecko.api.web.payload.request.SignInRequest;
import com.peecko.api.web.payload.request.SignOutRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static com.peecko.api.utils.Common.MAX_ALLOWED;

@Component
public class UserRepository {

    public static final HashMap<String, User> REPO = new HashMap<>();

    public static final HashMap<String, Set<Device>> DEVICES = new HashMap<>();

    public static final HashMap<String, PinCode> PIN_CODES = new HashMap<>();

    public static final Set<String> INVALID_JWT = new HashSet<>();

    public static final Set<String> INVALID_LICENSE = new HashSet<>();

    public static final Set<Role> DEFAULT_ROLES = new HashSet<>();

    static final List<String> SPONSORS = SponsorUtils.getSponsors();

    static final Map<String, Membership> MEMBERSHIPS = new HashMap<>();

    public static final HashMap<String, List<Playlist>> PLAYLISTS = new HashMap<>();

    static final DateTimeFormatter CUSTOM_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    static final DateTimeFormatter DAY_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    static {
        DEFAULT_ROLES.add(Role.USER);
    }

    public static boolean isValidLicense(String license) {
        return StringUtils.hasLength(license) && license.length() == 20 && license.startsWith("1111") && !INVALID_LICENSE.contains(license);
    }

    public static boolean isActiveLicense(String license) {
        return !INVALID_LICENSE.contains(license);
    }

    public static void deactivateLicense(String license) {
        if (StringUtils.hasText(license)) {
            if (!INVALID_LICENSE.contains(license)) {
                INVALID_LICENSE.add(license);
            }
        }
    }

    public Optional<User> findByUsername(String username) {
        if (REPO.containsKey(username)) {
            return Optional.of(REPO.get(username));
        } else {
            return Optional.empty();
        }
    }

    public boolean hasActiveLicense(String username) {
        if (REPO.containsKey(username)) {
            User user = REPO.get(username);
            return UserRepository.isValidLicense(user.license());
        }
        return false;
    }

    public boolean existsByUsername(String username) {
        return REPO.containsKey(username);
    }

    public void save(User user) {
        user.roles(DEFAULT_ROLES);
        REPO.put(user.username(), user);
    }

    private int sponsorIndex = 0;

    public Membership retrieveMembership(String license) {
        Membership membership = MEMBERSHIPS.get(license);
        if (membership == null) {
            int index = sponsorIndex < SPONSORS.size() - 1? sponsorIndex: 0;
            String sponsor = SPONSORS.get(index);
            LocalDate expire = LocalDate.now().plusMonths(3);
            membership = new Membership();
            membership.setLicense(license);
            membership.setSponsor(sponsor);
            membership.setExpiration(expire.format(DAY_FORMATTER));
            MEMBERSHIPS.put(license, membership);
            sponsorIndex = sponsorIndex + 1;
        }
        return membership;
    }

    public Set<Device> getUserDevices(String username) {
        if (DEVICES.containsKey(username)) {
            return DEVICES.get(username);
        } else {
            return new HashSet<>();
        }
    }

    public static boolean isInstallationExceeded(int current) {
        return current > MAX_ALLOWED;
    }

    public void addDevice(SignInRequest signInRequest, String jwt) {
        String username = signInRequest.getUsername();
        Set<Device> userDevices = getUserDevices(username);
        Device device = Common.mapToDevice(signInRequest);
        if (!userDevices.contains(device)) {
            LocalDateTime ldt = LocalDateTime.now();
            device.setInstalled(ldt.format(CUSTOM_FORMATTER));
            device.setJwt(jwt);
            userDevices.add(device);
            DEVICES.put(username, userDevices);
        }
    }

    public void removeDevice(SignOutRequest signOutRequest) {
        Device deviceParam = new Device();
        deviceParam.setDeviceId(signOutRequest.getDeviceId());
        String username = signOutRequest.getUsername();
        Set<Device> userDevices = getUserDevices(username);
        Optional<Device> optionalDevice =  userDevices.stream().filter(deviceParam::equals).findAny();
        if (optionalDevice.isPresent()) {
            Device device = optionalDevice.get();
            userDevices.remove(device);
            DEVICES.put(username, userDevices);
            INVALID_JWT.add(device.getJwt());
        }
    }

    public static boolean isInvalidJwt(String jwt) {
        return INVALID_JWT.contains(jwt);
    }

    public String generatePinCode(String email) {
        cleanExpiredPinCodes();
        String requestId = UUID.randomUUID().toString();
        PinCode pinCode = new PinCode();
        pinCode.setRequestId(requestId);
        pinCode.setPinCode("1234");
        pinCode.setEmail(email);
        pinCode.setExpireAt(LocalDateTime.now().plus(5, ChronoUnit.MINUTES));
        PIN_CODES.put(requestId, pinCode);
        return requestId;
    }

    public boolean isPinCodeValid(String requestId, String pinCode) {
        cleanExpiredPinCodes();
        boolean isValid = false;
        if (PIN_CODES.containsKey(requestId)) {
            PinCode saved = PIN_CODES.get(requestId);
            isValid = saved.getPinCode().equals(pinCode);
        }
        return isValid;
    }

    public PinCode getPinCode(String requestId) {
        return PIN_CODES.get(requestId);
    }

    private void cleanExpiredPinCodes() {
        LocalDateTime now = LocalDateTime.now();
        PIN_CODES.entrySet().removeIf(entry -> entry.getValue().getExpireAt().isBefore(now));
    }

    /**
     * Playlist
     * ------------------------------------------------------------------------------------*/

    private static Long PLAYLIST_NEXT_ID = 0L;

    private static Long nextPlaylistId() {
        return ++PLAYLIST_NEXT_ID;
    }

    public List<IdName> getPlaylists(String username) {
        List<IdName> list = new ArrayList<>();
        if (PLAYLISTS.containsKey(username)) {
            list = PLAYLISTS.get(username).stream().map(p -> new IdName(p.getId(), p.getName())).toList();
        }
        return list;
    }

    public Optional<Playlist> getPlaylist(String username, Long id) {
        Optional<Playlist> optionalPlaylist = PLAYLISTS.get(username).stream().filter(p -> id.equals(p.getId())).findAny();
        if (optionalPlaylist.isPresent()) {
            List<VideoItem> videoItems = optionalPlaylist.get().getVideoItems();
            optionalPlaylist.get().setVideoItems(sortVideoItems(videoItems));
        }
        return optionalPlaylist;
    }

    public Optional<Playlist> createPlaylist(String username, String listName) {
        Playlist playlist = new Playlist(username, nextPlaylistId(), listName, new ArrayList<>());
        List<Playlist> userPlaylists = PLAYLISTS.get(username);
        if (userPlaylists == null) {
            userPlaylists = new ArrayList<>();
        }
        userPlaylists.add(playlist);
        PLAYLISTS.put(username, userPlaylists);
        return Optional.of(playlist);
    }

    public Playlist addPlaylistVideoItem(String username, Long listId, Video video) {
        Playlist playlist = getPlaylist(username, listId).get();
        if (video == null) {
            return playlist;
        }
        VideoItem last = getLastVideoItem(playlist);
        VideoItem toAdd = new VideoItem();
        toAdd.setCode(video.getCode());
        // toAdd.setVideo(video); TODO restore when test is fine
        if (last != null) {
            last.setNext(video.getCode());
            toAdd.setPrevious(last.getCode());
        }
        playlist.getVideoItems().add(toAdd);
        playlist.setVideoItems(sortVideoItems(playlist.getVideoItems()));
        return playlist;
    }

    public Playlist removePlaylistVideoItem(String username, Long listId, String videoCode) {
        Playlist playlist = getPlaylist(username, listId).get();
        Optional<VideoItem> optionalVideoItem = playlist.getVideoItems().stream().filter(v -> videoCode.equals(v.getCode())).findAny();
        if (optionalVideoItem.isPresent()) {
            List<VideoItem> cleaned = new ArrayList<>();
            VideoItem toRemove = optionalVideoItem.get();
            if (playlist.getVideoItems().size() > 1) {
                VideoItem previous = null;
                VideoItem next = null;
                if (StringUtils.hasText(toRemove.getPrevious())) {
                    previous = playlist.getVideoItems().stream().filter(v -> toRemove.getPrevious().equals(v.getCode())).findAny().orElse(null);
                }
                if (StringUtils.hasText(toRemove.getNext())) {
                    next = playlist.getVideoItems().stream().filter(v -> toRemove.getNext().equals(v.getCode())).findAny().orElse(null);
                }
                if (previous != null && next != null) {
                    previous.setNext(next.getCode());
                    next.setPrevious(previous.getCode());
                } else if (previous == null && next != null) {
                    next.setPrevious(null);
                } else if (previous != null && next == null) {
                    previous.setNext(null);
                }
                cleaned = playlist.getVideoItems().stream().filter(v -> !videoCode.equals(v.getCode())).toList();
            }
            playlist.setVideoItems(sortVideoItems(cleaned));
        }
        return playlist;
    }

    public Playlist movePlaylistVideoItem(String username, Long listId, String videoCode, String newPreviousVideoCode) {
        boolean moved = false;
        Playlist playlist = getPlaylist(username, listId).get();

        VideoItem current = getVideoItem(playlist, videoCode);
        VideoItem previous = getVideoItem(playlist, current.getPrevious());
        VideoItem next = getVideoItem(playlist, current.getNext());

        if (newPreviousVideoCode.equals(current.getPrevious())) {
            playlist.setVideoItems(sortVideoItems(playlist.getVideoItems()));
            return playlist;
        }

        try {
            previous.setNext(current.getNext());
            if (next != null) {
                next.setPrevious(previous.getCode());
            }

            if ("top".equalsIgnoreCase(newPreviousVideoCode)) {
                VideoItem first = getFirstVideoItem(playlist);
                first.setPrevious(current.getCode());
                current.setPrevious(null);
                current.setNext(first.getCode());
            } else {
                VideoItem newPrevious = getVideoItem(playlist, newPreviousVideoCode);
                current.setPrevious(newPrevious.getCode());
                current.setNext(newPrevious.getNext());
                VideoItem newNext = getVideoItem(playlist, newPrevious.getNext());
                newNext.setPrevious(current.getCode());
                newPrevious.setNext(current.getCode());
            }

            moved = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (moved) {
                playlist.setVideoItems(sortVideoItems(playlist.getVideoItems()));
            }
        }
        return playlist;
    }

    private VideoItem getVideoItem(Playlist playlist, String code) {
        if (!StringUtils.hasText(code)) {
            return null;
        }
        return playlist.getVideoItems().stream().filter(v -> code.equals(v.getCode())).findAny().orElse(null);
    }

    private VideoItem getFirstVideoItem(Playlist playlist) {
        return playlist.getVideoItems().stream().filter(v -> !StringUtils.hasText(v.getPrevious())).findAny().orElse(null);
    }
    private VideoItem getLastVideoItem(Playlist playlist) {
        return playlist.getVideoItems().stream().filter(v -> !StringUtils.hasText(v.getNext())).findAny().orElse(null);
    }

    public boolean playlistExistsByName(String username, String playlistName) {
        if (!StringUtils.hasText(playlistName)) {
            return false;
        }
        if (PLAYLISTS.containsKey(username)) {
            return PLAYLISTS.get(username).stream().anyMatch(playlist -> playlistName.equals(playlist.getName()));
        }
        return false;
    }

    private List<VideoItem> sortVideoItems(List<VideoItem> sources) {
        if (sources.isEmpty()) {
            return sources;
        }
        List<VideoItem> sorted =  new ArrayList<>();
        VideoItem item = sources.stream().filter(v -> !StringUtils.hasText(v.getPrevious())).findAny().get();
        sorted.add(item);
        while (StringUtils.hasText(item.getNext())) {
            String next = item.getNext();;
            item = sources.stream().filter(v -> next.equals(v.getCode())).findAny().get();
            sorted.add(item);
        }
        return sorted;
    }

}
