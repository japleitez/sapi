package com.peecko.api.repository;

import com.peecko.api.domain.Device;
import com.peecko.api.domain.PinCode;
import com.peecko.api.domain.Role;
import com.peecko.api.domain.User;
import com.peecko.api.utils.Common;
import com.peecko.api.web.payload.request.PinValidationRequest;
import com.peecko.api.web.payload.request.SignInRequest;
import com.peecko.api.web.payload.request.SignOutRequest;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Component
public class UserRepository {

    private static final HashMap<String, User> REPO = new HashMap<>();

    private static final HashMap<String, List<Device>> DEVICES = new HashMap<>();

    private static final HashMap<String, PinCode> PIN_CODES = new HashMap<>();

    private static final Set<Role> DEFAULT_ROLES = new HashSet<>();

    private static final String DEFAULT_NAME = "Peter Cash";

    private static final String DEFAULT_USERNAME = "peter@legend.com";

    private static final String DEFAULT_LICENSE = "AAAA0000111122223333";

    final static DateTimeFormatter CUSTOM_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    static {
        DEFAULT_ROLES.add(Role.USER);
        REPO.put(DEFAULT_USERNAME, new User()
            .name(DEFAULT_NAME)
            .username(DEFAULT_USERNAME)
            .license(DEFAULT_LICENSE)
            .roles(DEFAULT_ROLES));
    }

    public Optional<User> findByUsername(String username) {
        if (REPO.containsKey(username)) {
            return Optional.of(REPO.get(username));
        } else {
            return Optional.empty();
        }
    }

    public boolean existsByUsername(String username) {
        return REPO.containsKey(username);
    }

    public void save(User user) {
        user.roles(DEFAULT_ROLES);
        user.license(DEFAULT_LICENSE);
        REPO.put(user.username(), user);
    }

    public List<Device> getUserDevices(String username) {
        if (DEVICES.containsKey(username)) {
            return DEVICES.get(username);
        } else {
            return new ArrayList<>();
        }
    }

    public void addDevice(SignInRequest signInRequest) {
        String username = signInRequest.getUsername();
        List<Device> userDevices = getUserDevices(username);
        Device device = Common.mapToDevice(signInRequest);
        if (!userDevices.contains(device)) {
            LocalDateTime ldt = LocalDateTime.now();
            device.setInstalled(ldt.format(CUSTOM_FORMATTER));
            userDevices.add(device);
            DEVICES.put(username, userDevices);
        }
    }

    public void removeDevice(SignOutRequest signOutRequest) {
        Device device = new Device();
        device.setDeviceId(signOutRequest.getDeviceId());
        String username = signOutRequest.getUsername();
        List<Device> userDevices = getUserDevices(username);
        if (userDevices.contains(device)) {
            userDevices.remove(device);
            DEVICES.put(username, userDevices);
        }
    }

    public String generatePinCode() {
        String requestId = UUID.randomUUID().toString();
        PinCode pinCode = new PinCode();
        pinCode.setRequestId(requestId);
        pinCode.setPinCode("1234");
        pinCode.setExpireAt(LocalDateTime.now().plus(5, ChronoUnit.MINUTES));
        PIN_CODES.put(requestId, pinCode);
        cleanExpiredPinCodes();
        return requestId;
    }

    public boolean validatePinCode(String requestId, String pinCode) {
        cleanExpiredPinCodes();
        if (PIN_CODES.containsKey(requestId)) {
            PinCode saved = PIN_CODES.get(requestId);
            return saved.getPinCode().equals(pinCode);
        }
        return false;
    }

    private void cleanExpiredPinCodes() {
        LocalDateTime now = LocalDateTime.now();
        PIN_CODES.entrySet().removeIf(entry -> entry.getValue().getExpireAt().isBefore(now));
    }

}
