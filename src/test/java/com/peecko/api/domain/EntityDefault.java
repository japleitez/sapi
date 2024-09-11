package com.peecko.api.domain;

import com.peecko.api.domain.enumeration.Intensity;
import com.peecko.api.domain.enumeration.Lang;
import com.peecko.api.utils.Common;
import com.peecko.api.utils.PeriodUtils;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.security.core.parameters.P;

public abstract class EntityDefault {
    public static final String PLAYLIST_NAME = "default-playlist-name";
    public static final String NAME = "Default Name";
    public static final String USERNAME = "defaul.name@mail.com";
    public static final String PASSWORD = "default-password";
    public static final Lang LANG = Lang.EN;
    public static final String COUNTRY = "UK";
    public static final String LANGUAGE = LANG.name();
    public static final String LICENSE = "default-license";
    public static final Integer PERIOD = PeriodUtils.current();
    public static final Long CUSTOMER_ID = 1L;
    public static final String PHONE_MODEL = "default-model";
    public static final String OS_VERSION = "default-version";
    public static final String DEVICE_ID = "default-device";
    public static final String JTI = "jti";
    public static final Double VAT_RATE = 0.16;
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

    public static final String VIDEO_INTENSITY_LOW = "low";
    public static final String VIDEO_INTENSITY_MODERATE = "moderate";
    public static final String VIDEO_INTENSITY_HIGH = "high";

    public static final String VIDEO_TAG_ALL = "all";
    public static final String VIDEO_TAG_RELAX = "relax";
    public static final String VIDEO_TAG_ENERGY  = "energy";
    public static final String VIDEO_TAGS = VIDEO_TAG_ALL + "," + VIDEO_TAG_RELAX + "," + VIDEO_TAG_ENERGY;

    public static final String VIDEO_CATEGORY_CODE = "VC_CODE";
    public static final String YOGA = "YOGA";
    public static final String PILATES = "PILATES";
    public static final String FLEXIBILITY = "FLEXIBILITY";

    public static final String COACH_NAME = "default-name";
    public static final String COACH_RESUME = "default-resume";
    public static final String COACH_WEBSITE = "default-website";
    public static final String COACH_EMAIL = "default-email";
    public static final String COACH_INSTAGRAM = "default-instagram";


}
