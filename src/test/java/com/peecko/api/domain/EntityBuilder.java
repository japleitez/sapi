package com.peecko.api.domain;

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

}
