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
import java.util.stream.Collectors;

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

}
