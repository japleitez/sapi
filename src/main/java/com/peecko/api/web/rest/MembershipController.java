package com.peecko.api.web.rest;

import com.peecko.api.domain.dto.Membership;
import com.peecko.api.domain.dto.UserDTO;
import com.peecko.api.repository.fake.UserRepository;
import com.peecko.api.web.payload.request.ActivationRequest;
import com.peecko.api.web.payload.response.MessageResponse;
import jakarta.validation.Valid;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Locale;

import static com.peecko.api.utils.Common.ERROR;
import static com.peecko.api.utils.Common.OK;

@RestController
@RequestMapping("/api/membership")
public class MembershipController extends BaseController {

    final MessageSource messageSource;
    final UserRepository userRepository;

    public MembershipController(MessageSource messageSource, UserRepository userRepository) {
        this.messageSource = messageSource;
        this.userRepository = userRepository;
    }

    @PutMapping("/activate")
    public ResponseEntity<?> activate(@Valid @RequestBody ActivationRequest activationRequest) {
        String license = activationRequest.getLicense();
        if (!StringUtils.hasLength(license) && license.length() != 20) {
            return ResponseEntity.ok(new MessageResponse(ERROR, message("membership.valid.nok")));
        }
        if (UserRepository.isValidLicense(license)) {
            Membership membership = userRepository.retrieveMembership(license);
            UserDTO userDTO = getActiveUser(userRepository);
            userDTO.license(license);
            userDTO.membership(membership);
            userRepository.save(userDTO);
            return ResponseEntity.ok(new MessageResponse(OK, message("membership.activate.ok")));
        } else {
            return ResponseEntity.ok(new MessageResponse(ERROR, message("membership.activate.nok")));
        }
    }

    private String message(String code) {
        return messageSource.getMessage(code, null, Locale.ENGLISH);
    }

}
