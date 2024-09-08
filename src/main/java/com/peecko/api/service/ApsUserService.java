package com.peecko.api.service;

import com.peecko.api.domain.ApsDevice;
import com.peecko.api.domain.ApsMembership;
import com.peecko.api.domain.ApsUser;
import com.peecko.api.domain.Customer;
import com.peecko.api.domain.dto.DeviceDTO;
import com.peecko.api.domain.enumeration.Lang;
import com.peecko.api.domain.mapper.ApsDeviceMapper;
import com.peecko.api.repository.ApsDeviceRepo;
import com.peecko.api.repository.ApsMembershipRepo;
import com.peecko.api.repository.ApsUserRepo;
import com.peecko.api.repository.CustomerRepo;
import com.peecko.api.web.payload.response.UserProfileResponse;
import com.peecko.api.utils.Common;
import com.peecko.api.utils.NameUtils;
import com.peecko.api.web.payload.request.SignInRequest;
import com.peecko.api.web.payload.request.SignUpRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ApsUserService {

    final ApsUserRepo apsUserRepo;
    final ApsDeviceRepo apsDeviceRepo;
    final CustomerRepo customerRepo;
    final ApsMembershipRepo apsMembershipRepo;
    final PasswordEncoder passwordEncoder;

    public static final int MAX_NUMBER_DEVICES = 3;

    public ApsUserService(ApsUserRepo apsUserRepo, ApsDeviceRepo apsDeviceRepo, CustomerRepo customerRepo, ApsMembershipRepo apsMembershipRepo, PasswordEncoder passwordEncoder) {
        this.apsUserRepo = apsUserRepo;
        this.apsDeviceRepo = apsDeviceRepo;
        this.customerRepo = customerRepo;
        this.apsMembershipRepo = apsMembershipRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public boolean authenticated(String username, String password) {
        return apsUserRepo.findByUsername(username)
                .filter(apsUser -> passwordEncoder.matches(password, apsUser.getPassword()))
                .isPresent();
    }

    public boolean exists(String username) {
        return apsUserRepo.existsByUsername(username.toLowerCase());
    }

    public boolean doesNotExist(String username) {
        return !apsUserRepo.existsByUsername(username.toLowerCase());
    }

    @Transactional
    public void setUserActive(String username, boolean active) {
        ApsUser apsUser = apsUserRepo.findByUsername(username).orElseThrow();
        apsUser.active(active);
        apsUserRepo.save(apsUser);
    }

    @Transactional
    public void setUserLanguage(String username, Lang lang) {
        ApsUser apsUser = apsUserRepo.findByUsername(username).orElseThrow();
        apsUser.language(lang);
        apsUser.updated(Instant.now());
        apsUserRepo.save(apsUser);
    }

    @Transactional
    public void signUp(SignUpRequest request) {
        ApsUser apsUser = new ApsUser();
        apsUser.username(request.username().toLowerCase());
        apsUser.name(NameUtils.toCamelCase(request.name()));
        apsUser.language(Lang.fromString(request.language()));
        apsUser.password(passwordEncoder.encode(request.password()));
        apsUser.active(false);
        apsUser.license(null);
        apsUser.created(Instant.now());
        apsUser.updated(Instant.now());
        apsUser.usernameVerified(false);
        apsUserRepo.save(apsUser);
    }

    @Transactional
    public UserProfileResponse signIn(SignInRequest request) {
        return apsUserRepo.findByUsernameWithDevices(request.username())
                .map(apsUser -> {
                    apsUser.addApsDevice(ApsDeviceMapper.toApsDevice(request));
                    apsUserRepo.save(apsUser);
                    return buildProfileResponse(apsUser);
                }).orElseGet(UserProfileResponse::new);
    }

    public UserProfileResponse getProfile(String username) {
        return apsUserRepo.findByUsernameWithDevices(username.toLowerCase())
                .map(this::buildProfileResponse)
                .orElse(buildProfileNotFound(username));
    }

    private UserProfileResponse buildProfileNotFound(String username) {
        UserProfileResponse notFound = new UserProfileResponse();
        notFound.setUsername(username);
        return notFound;
    }

    private UserProfileResponse buildProfileResponse(ApsUser apsUser) {
        int currentPeriod = Common.currentPeriod();
        int deviceCount = apsUser.getApsDevices().size();
        UserProfileResponse response = new UserProfileResponse();
        response.setToken(apsUser.getJwt());
        response.setName(apsUser.getName());
        response.setUsername(apsUser.getUsername());
        response.setEmailVerified(response.isEmailVerified());
        response.setDevicesExceeded(deviceCount > MAX_NUMBER_DEVICES);
        response.setDevicesCount(deviceCount);
        response.setDevicesMax(MAX_NUMBER_DEVICES);
        response.setMembership(apsUser.getLicense());
        ApsMembership apsMembership = apsMembershipRepo.findByUsernameAndPeriod(apsUser.getUsername(), currentPeriod).orElse(null);
        if (Objects.nonNull(apsMembership)) {
            response.setMembershipActivated(true);
            response.setMembershipExpiration(Common.lastDayOfMonthAsString());
            Customer customer = customerRepo.findById(apsMembership.getCustomerId()).orElse(null);
            if (Objects.nonNull(customer)) {
                response.setMembershipSponsor(customer.getName());
                response.setMembershipSponsorLogo(customer.getLogo());
            }
        }
        return response;
    }

    @Transactional
    public int signOut(String username, String deviceId) {
        ApsUser apsUser = apsUserRepo.findByUsernameWithDevices(username).orElse(null);
        if (Objects.nonNull(apsUser)) {
            ApsDevice toRemove = apsUser.getApsDevices()
                    .stream()
                    .filter(device -> deviceId.equals(device.getDeviceId()))
                    .findFirst()
                    .orElse(null);
            if (Objects.nonNull(toRemove)) {
                apsUser.removeApsDevice(toRemove);
                apsUserRepo.save(apsUser);
            }
            return apsUser.getApsDevices().size();
        }
        return 0;
    }

    public List<DeviceDTO> getUserDevicesAsDTO(String username) {
        return apsUserRepo.findByUsernameWithDevices(username.toLowerCase())
                .map(apsUser -> apsUser.getApsDevices().stream().map(ApsDeviceMapper::deviceDTO).collect(Collectors.toList()))
                .orElse(Collections.emptyList());
    }

    @Transactional
    public void updateUserPassword(String username, String password) {
        ApsUser apsUser = apsUserRepo.findByUsername(username).orElseThrow();
        apsUser.password(passwordEncoder.encode(password));
        apsUser.updated(Instant.now());
        apsUserRepo.save(apsUser);
    }

    @Transactional
    public void updateUserName(String username, String name) {
        ApsUser apsUser = apsUserRepo.findByUsername(username).orElseThrow();
        apsUser.name(name);
        apsUser.updated(Instant.now());
        apsUserRepo.save(apsUser);
    }

    public boolean passwordDoesNotMatch(String username, String password) {
        return apsUserRepo.findByUsername(username.toLowerCase())
                .filter(apsUser -> passwordEncoder.matches(password, apsUser.getPassword()))
                .isEmpty();
    }

}
