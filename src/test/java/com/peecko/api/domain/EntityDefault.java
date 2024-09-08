package com.peecko.api.domain;

import com.peecko.api.domain.enumeration.Lang;
import com.peecko.api.utils.Common;

public abstract class EntityDefault {

    public static final String PLAYLIST_NAME = "default-playlist-name";
    public static final String NAME = "Default Name";
    public static final String USERNAME = "defaul.namet@gmail.com";
    public static final String PASSWORD = "default-password";
    public static final Lang LANG = Lang.FR;
    public static final String COUNTRY = "FR";
    public static final String LANGUAGE = LANG.name();
    public static final String LICENSE = "default-license";
    public static final Integer PERIOD = Common.currentPeriod();
    public static final Long CUSTOMER_ID = 1L;
    public static final String PHONE_MODEL = "default-model";
    public static final String OS_VERSION = "default-version";
    public static final String DEVICE_ID = "default-device";
    public static final String JTI = "jti";
    public static final Double VAT_RATE = 0.16;
    public static final String TITLE = "default-title";
    public static final String LABEL = "default.label";
    public static final String MESSAGE = "default-message";
    public static final String VIDEO_CODE = "default-video-code";
    public static final String VIDEO_URL = "https://www.youtube.com/watch?v=dQw4w9WgXcQ";
    public static final String IMAGE_URL = "https://www.google.com/images/branding/googlelogo/1x/googlelogo_color_272x92dp.png";

    public static final Integer VIDEO_DURATION = 60;
}
