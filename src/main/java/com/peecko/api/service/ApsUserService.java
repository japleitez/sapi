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
import com.peecko.api.web.payload.request.UpdateUserRequest;
import com.peecko.api.web.payload.request.SignInRequest;
import com.peecko.api.web.payload.request.SignOutRequest;
import com.peecko.api.web.payload.request.SignUpRequest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ApsUserService {

    private final ApsUserRepo apsUserRepo;
    private final CustomerRepo customerRepo;
    private final ApsMembershipRepo apsMembershipRepo;
    private final PasswordEncoder passwordEncoder;
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

    public Long findIdByUsername(String username) {
        return apsUserRepo.findByUsername(username).map(ApsUser::getId).orElse(null);
    }

    public Lang findLangByUsername(String username) {
        return apsUserRepo.findByUsername(username).map(ApsUser::getLanguage).orElse(Lang.EN);
    }

    public boolean authenticate(String username, String password) {
        Optional<ApsUser> optionalApsUser = apsUserRepo.findByUsername(username);
        if (optionalApsUser.isPresent()) {
            ApsUser apsUser = optionalApsUser.get();
            return passwordEncoder.matches(password, apsUser.getPassword());
        }
        return false;
    }

    public boolean exists(String username) {
        return apsUserRepo.existsByUsername(username.toLowerCase());
    }

    public Locale getUserLocale(String username) {
        Lang lang = apsUserRepo.findByUsername(username.toLowerCase()).map(ApsUser::getLanguage).orElse(Lang.EN);
        return Locale.forLanguageTag(lang.name());
    }

    public void setUserActive(String username) {
        Optional<ApsUser> optional = apsUserRepo.findByUsername(username.toLowerCase());
        if (optional.isPresent()) {
            ApsUser apsUser = optional.get();
            apsUser.setActive(true);
            apsUserRepo.save(apsUser);
        }
    }

    public void setUserLanguage(String username, String langCode) {
        Optional<ApsUser> optional = apsUserRepo.findByUsername(username.toLowerCase());
        if (optional.isPresent()) {
            ApsUser apsUser = optional.get();
            apsUser.language(Lang.fromString(langCode));
            apsUserRepo.save(apsUser);
        }
    }

    public UserDTO signUp(SignUpRequest request) {
        ApsUser apsUser = new ApsUser();
        apsUser.username(request.getUsername().toLowerCase());
        apsUser.name(NameUtils.camel(request.getName()));
        apsUser.language(Common.toLang(request.getLanguage()));
        apsUser.password(passwordEncoder.encode(request.getPassword()));
        apsUserRepo.save(apsUser);
        return ApsUserMapper.userDTO(apsUser);
    }

    @Transactional
    public UserProfileResponse signIn(SignInRequest request, String jwt) {
        Optional<ApsUser> optional = apsUserRepo.findByUsername(request.getUsername());
        if (optional.isPresent()) {
            ApsUser apsUser = optional.get();
            apsUser.setJwt(jwt);
            apsUser.addApsDevice(Common.toApsDevice(request));
            apsUserRepo.save(apsUser);
            return buildLoginResponse(apsUser);
        }
        return new UserProfileResponse();
    }

    public UserProfileResponse getProfile(String username) {
        UserProfileResponse notFound = new UserProfileResponse();
        notFound.setUsername(username);
        return apsUserRepo.findByUsername(username.toLowerCase())
                .map(this::buildLoginResponse)
                .orElse(notFound);
    }

    private UserProfileResponse buildLoginResponse(ApsUser apsUser) {
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
    public void signOut(SignOutRequest request) {
        String deviceId = request.getDeviceId();
        Optional<ApsUser> optional = apsUserRepo.findByUsername(request.getUsername());
        if (optional.isPresent()) {
            ApsUser apsUser = optional.get();
            apsUser.getApsDevices().stream().filter(device -> deviceId.equals(device.getDeviceId())).findAny().ifPresent(apsUser::removeApsDevice);
            apsUserRepo.save(apsUser);
        }
    }

    public List<DeviceDTO> getDevicesByUsername(String username) {
        Optional<ApsUser> optional = apsUserRepo.findByUsername(username.toLowerCase());
        if (optional.isPresent()) {
            ApsUser apsUser = optional.get();
            return apsUser.getApsDevices().stream()
                    .map(ApsDeviceMapper::deviceDTO)
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    public void updateUserPassword(String username, String password) {
        Optional<ApsUser> optional = apsUserRepo.findByUsername(username.toLowerCase());
        if (optional.isPresent()) {
            ApsUser apsUser = optional.get();
            apsUser.password(passwordEncoder.encode(password));
            apsUserRepo.save(apsUser);
        }
    }

    public void updateUser(UpdateUserRequest request) {
        Optional<ApsUser> optional = apsUserRepo.findByUsername(request.getUsername().toLowerCase());
        if (optional.isPresent()) {
            ApsUser apsUser = optional.get();
            apsUser.name(request.getName());
            apsUserRepo.save(apsUser);
        }
    }

}
