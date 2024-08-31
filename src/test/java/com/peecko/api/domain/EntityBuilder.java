package com.peecko.api.domain;

import com.peecko.api.web.payload.request.SignInRequest;
import com.peecko.api.web.payload.request.SignUpRequest;

public abstract class EntityBuilder {

    public static ApsUser buildApsUser() {
        ApsUser apsUser = new ApsUser();
        apsUser.username(EntityDefault.USERNAME);
        return apsUser;
    }

    public static ApsMembership buildApsMembership() {
        ApsMembership apsMembership = new ApsMembership();
        apsMembership.setCustomerId(EntityDefault.CUSTOMER_ID);
        apsMembership.setUsername(EntityDefault.USERNAME);
        apsMembership.setPeriod(EntityDefault.PERIOD);
        apsMembership.setLicense(EntityDefault.LICENSE);
        return apsMembership;
    }

    public static SignUpRequest buildSignUpRequest() {
        return new SignUpRequest(
                EntityDefault.NAME,
                EntityDefault.USERNAME,
                EntityDefault.PASSWORD,
                EntityDefault.LANGUAGE);
    }

    public static SignInRequest buildSignInRequest() {
        return new SignInRequest(
                EntityDefault.USERNAME,
                EntityDefault.PASSWORD,
                EntityDefault.PHONE_MODEL,
                EntityDefault.OS_VERSION,
                EntityDefault.DEVICE_ID
        );
    }
}