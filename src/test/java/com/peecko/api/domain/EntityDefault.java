package com.peecko.api.domain;

import com.peecko.api.domain.enumeration.CustomerState;
import com.peecko.api.domain.enumeration.Lang;
import com.peecko.api.utils.PeriodUtils;

public abstract class EntityDefault {

    /**
     * GENERAL defaults
     */
    public static final String LICENSE = "11112222333344445555";
    public static final Integer PERIOD = PeriodUtils.current();

    /**
     *
     */
    public static final String PLAYLIST_NAME = "best morning workouts";
    public static final Long CUSTOMER_ID = 1L;
    public static final String TITLE = "default-title";
    public static final String VIDEO_CATEGORY_TITLE = "default-video-category";

    public static final String VIDEO_CATEGORY_LABEL = "video.category.default";

    public static final String LABEL = "default.label";
    public static final String MESSAGE = "default-message";
    public static final String VIDEO_CODE = "default-video-code";
    public static final String VIDEO_TITLE = "default-video-title";
    public static final String VIDEO_URL = "https://www.youtube.com/watch?v=dQw4w9WgXcQ";
    public static final String VIDEO_THUMBNAIL = "https://www.google.com/images/272x92dp.png";
    public static final String VIDEO_DESCRIPTION = "default-video-description";
    public static final Integer VIDEO_DURATION = 10;
    public static final String VIDEO_AUDIENCE_ALL = "all";
    public static final String VIDEO_TAG_ALL = "all";
    public static final String VIDEO_TAG_RELAX = "relax";
    public static final String VIDEO_TAG_ENERGY  = "energy";
    public static final String VIDEO_TAGS = VIDEO_TAG_ALL + "," + VIDEO_TAG_RELAX + "," + VIDEO_TAG_ENERGY;
    public static final String VIDEO_CATEGORY_CODE = "VC_CODE";
    public static final String YOGA = "YOGA";
    public static final String PILATES = "PILATES";
    public static final String FLEXIBILITY = "FLEXIBILITY";

    public static final String COACH_NAME = "Chris Heria";
    public static final String COACH_RESUME = "default-resume";
    public static final String COACH_WEBSITE = "default-website";
    public static final String COACH_EMAIL = "default-email";
    public static final String COACH_INSTAGRAM = "default-instagram";

    /**
     * CUSTOMER defaults
     */
    public static final String  CUSTOMER_CODE = "LU-AMAZON";
    public static final String  CUSTOMER_NAME = "AMAZON Luxembourg";
    public static final String  CUSTOMER_COUNTRY = "LU";
    public static final Double  CUSTOMER_VAT_RATE = 0.16;
    public static final CustomerState  CUSTOMER_STATE = CustomerState.ACTIVE;

    /**
     * USER defaults
     */
    public static final String USER_NAME = "John Doe";
    public static final String USER_EMAIL = "john@example.com";
    public static final String USER_PASSWORD = "secret";
    public static final Lang USER_LANG = Lang.EN;
    public static final String LANGUAGE = USER_LANG.name();
    public static final String USER_NEW_PASSWORD = "new" + USER_PASSWORD;
    public static final String USER_RESET_PASSWORD = "reset" + USER_PASSWORD;

    /**
     * DEVICE defaults
     */
    public static final String PHONE_MODEL = "iPhone";
    public static final String OS_VERSION = "iOS 16.7.2";
    public static final String DEVICE_ID = "C99FAE0E-1A09-4A2F-ACE2-49D021F6B2C3";
    public static final String JTI = "a81bc81b-dead-4e5d-abff-90865d1e13b1";


}
