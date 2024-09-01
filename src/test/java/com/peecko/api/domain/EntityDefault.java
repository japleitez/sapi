package com.peecko.api.domain;

import com.peecko.api.domain.enumeration.Lang;
import com.peecko.api.utils.Common;

public abstract class EntityDefault {
    public static final String NAME = "default name";
    public static final String USERNAME = "defaul.namet@gmail.com";
    public static final String PASSWORD = "default";
    public static final String LANGUAGE = Lang.EN.name();
    public static final String LICENSE = "free";
    public static final Integer PERIOD = Common.currentPeriod();
    public static final Long CUSTOMER_ID = 1L;
    public static final String PHONE_MODEL = "default-model";
    public static final String OS_VERSION = "default-version";
    public static final String DEVICE_ID = "default-device";

    public static final String JTI = "jti";

}
