package com.peecko.api.domain;

import com.peecko.api.domain.enumeration.Lang;
import com.peecko.api.utils.Common;

public abstract class EntityDefault {
    public static final String NAME = "default";
    public static final String USERNAME = "default@gmail.com";
    public static final String PASSWORD = "secret";
    public static final String LANGUAGE = Lang.EN.name();
    public static final String LICENSE = "free";
    public static final Integer PERIOD = Common.currentPeriod();
    public static final Long CUSTOMER_ID = 1L;

}
