package com.peecko.api.domain;

import com.peecko.api.utils.Common;

public abstract class EntityDefault {
    public static final String USERNAME = "default@gmail.com";
    public static final String LICENSE = "free";
    public static final Integer PERIOD = Common.currentPeriod();
    public static final Long CUSTOMER_ID = 1L;

}
