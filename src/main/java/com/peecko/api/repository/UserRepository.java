package com.peecko.api.repository;

import com.peecko.api.domain.*;
import com.peecko.api.utils.Common;
import com.peecko.api.web.payload.request.SignInRequest;
import com.peecko.api.web.payload.request.SignOutRequest;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Component
public class UserRepository {

    public static final HashMap<String, User> REPO = new HashMap<>();

    public static final HashMap<String, Set<Device>> DEVICES = new HashMap<>();

    public static final HashMap<String, PinCode> PIN_CODES = new HashMap<>();

    public static final Set<String> INVALID_JWT = new HashSet<>();

    public static final Set<Role> DEFAULT_ROLES = new HashSet<>();

    private static final String DEFAULT_NAME = "Peter Cash";

    public static final String DEFAULT_USERNAME = "peter@legend.com";

    public static final String DEFAULT_LICENSE = "11111111111111111111";

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
        REPO.put(user.username(), user);
    }

    public Set<Device> getUserDevices(String username) {
        if (DEVICES.containsKey(username)) {
            return DEVICES.get(username);
        } else {
            return new HashSet<>();
        }
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
