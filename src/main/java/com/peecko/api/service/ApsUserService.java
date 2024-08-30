package com.peecko.api.service;

import com.peecko.api.domain.ApsMembership;
import com.peecko.api.domain.ApsUser;
import com.peecko.api.domain.Customer;
import com.peecko.api.domain.dto.DeviceDTO;
import com.peecko.api.domain.dto.UserDTO;
import com.peecko.api.domain.enumeration.Lang;
import com.peecko.api.domain.mapper.ApsDeviceMapper;
import com.peecko.api.domain.mapper.ApsUserMapper;
import com.peecko.api.repository.ApsMembershipRepo;
import com.peecko.api.repository.ApsUserRepo;
import com.peecko.api.repository.CustomerRepo;
import com.peecko.api.service.response.UserProfileResponse;
import com.peecko.api.utils.Common;
import com.peecko.api.utils.NameUtils;
import com.peecko.api.web.payload.request.SignInRequest;
import com.peecko.api.web.payload.request.SignUpRequest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ApsUserService {

    final ApsUserRepo apsUserRepo;
    final CustomerRepo customerRepo;
    final ApsMembershipRepo apsMembershipRepo;
    final PasswordEncoder passwordEncoder;

    public static final int MAX_NUMBER_DEVICES = 3;

    public ApsUserService(ApsUserRepo apsUserRepo, CustomerRepo customerRepo, ApsMembershipRepo apsMembershipRepo, PasswordEncoder passwordEncoder) {
        this.apsUserRepo = apsUserRepo;
        this.customerRepo = customerRepo;
        this.apsMembershipRepo = apsMembershipRepo;
        this.passwordEncoder = passwordEncoder;
    }

    public UserDTO findByUsernameOrElseThrow(String username) {
        ApsUser apsUser = apsUserRepo.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found " + username));
        return ApsUserMapper.userDTO(apsUser);
    }

    public ApsUser findByUsername(String username) {
        return apsUserRepo.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found " + username));
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

    public void setUserActive(String username, boolean active) {
        apsUserRepo.setActive(username.toLowerCase(), active);
    }

    public void setUserLanguage(String username, Lang lang) {
        apsUserRepo.setLanguage(username, lang);
    }

    public void signUp(SignUpRequest request) {
        ApsUser apsUser = new ApsUser();
        apsUser.username(request.username().toLowerCase());
        apsUser.name(NameUtils.toCamelCase(request.name()));
        apsUser.language(Lang.fromString(request.language()));
        apsUser.password(passwordEncoder.encode(request.password()));
        apsUserRepo.save(apsUser);
    }

    @Transactional
    public UserProfileResponse signIn(SignInRequest request) {
        return apsUserRepo.findByUsername(request.username())
                .map(apsUser -> {
                    apsUser.addApsDevice(ApsDeviceMapper.toApsDevice(request));
                    apsUserRepo.save(apsUser);
                    return buildProfileResponse(apsUser);
                }).orElseGet(UserProfileResponse::new);
    }

    public UserProfileResponse getProfile(String username) {
        return apsUserRepo.findByUsername(username.toLowerCase())
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
    public void signOut(String username, String deviceId) {
        apsUserRepo.findByUsername(username).ifPresent(apsUser -> {
                    apsUser.getApsDevices().removeIf(device -> deviceId.equals(device.getDeviceId()));
                    apsUserRepo.save(apsUser);
                });
    }

    public List<DeviceDTO> getUserDevicesAsDTO(String username) {
        return apsUserRepo.findByUsername(username.toLowerCase())
                .map(apsUser -> apsUser.getApsDevices().stream().map(ApsDeviceMapper::deviceDTO).collect(Collectors.toList()))
                .orElse(Collections.emptyList());
    }

    public void updateUserPassword(String username, String password) {
        apsUserRepo.setPassword(username.toLowerCase(), passwordEncoder.encode(password));
    }

    public void updateUserName(String username, String name) {
        apsUserRepo.setName(username, name);
    }

    public boolean passwordDoesNotMatch(String username, String password) {
        return !apsUserRepo.findByUsername(username.toLowerCase())
                .filter(apsUser -> passwordEncoder.matches(password, apsUser.getPassword()))
                .isPresent();
    }

}
