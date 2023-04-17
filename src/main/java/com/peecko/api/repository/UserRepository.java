package com.peecko.api.repository;

import com.peecko.api.domain.Role;
import com.peecko.api.domain.User;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Component
public class UserRepository {

    private static final HashMap<String, User> repo = new HashMap<>();

    private static final Set<Role> DEFAULT_ROLES = new HashSet<>();

    private static final String DEFAULT_NAME = "Johnny Cash";

    private static final String DEFAULT_USERNAME = "john@legend.com";

    private static final String DEFAULT_LICENSE = "LU999999999999999999";

    static {
        DEFAULT_ROLES.add(Role.USER);
        repo.put(DEFAULT_USERNAME, new User()
            .name(DEFAULT_NAME)
            .username(DEFAULT_USERNAME)
            .license(DEFAULT_LICENSE)
            .roles(DEFAULT_ROLES));
    }

    public Optional<User> findByUsername(String username) {
        if (repo.containsKey(username)) {
            return Optional.of(repo.get(username));
        } else {
            return Optional.empty();
        }
    }

    public boolean existsByUsername(String username) {
        return repo.containsKey(username);
    }

    public void save(User user) {
        user.roles(DEFAULT_ROLES);
        repo.put(user.username(), user);
    }

}
