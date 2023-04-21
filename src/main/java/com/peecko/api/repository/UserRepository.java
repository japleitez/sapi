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

    private static final String DEFAULT_NAME = "Peter Cash";

    private static final String DEFAULT_USERNAME = "peter@legend.com";

    private static final String DEFAULT_LICENSE = "AAAA0000111122223333";

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
        user.license(DEFAULT_LICENSE);
        repo.put(user.username(), user);
    }

}
