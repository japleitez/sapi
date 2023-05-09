package com.peecko.api.repository;

import com.peecko.api.domain.Device;
import com.peecko.api.domain.Role;
import com.peecko.api.domain.User;
import com.peecko.api.utils.Common;
import com.peecko.api.web.payload.request.LoginRequest;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Component
public class UserRepository {

    private static final HashMap<String, User> REPO = new HashMap<>();

    private static final HashMap<String, List<Device>> DEVICES = new HashMap<>();
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

    public void addDevice(LoginRequest loginRequest) {
        String username = loginRequest.getUsername();
        List<Device> userDevices = getUserDevices(username);
        Device device = Common.getDevice(loginRequest);
        if (!userDevices.contains(device)) {
            LocalDateTime ldt = LocalDateTime.now();
            device.setInstalled(ldt.format(CUSTOM_FORMATTER));
            userDevices.add(device);
            DEVICES.put(username, userDevices);
        }
    }

    public List<Device> getUserDevices(String username) {
        if (DEVICES.containsKey(username)) {
            return DEVICES.get(username);
        } else {
            return new ArrayList<>();
        }
    }

}
