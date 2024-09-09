package com.peecko.api.domain;

import com.peecko.api.domain.enumeration.CustomerState;
import com.peecko.api.domain.enumeration.Intensity;
import com.peecko.api.domain.enumeration.Lang;
import com.peecko.api.domain.enumeration.Player;
import com.peecko.api.web.payload.request.SignInRequest;
import com.peecko.api.web.payload.request.SignUpRequest;
import org.checkerframework.checker.units.qual.A;

import java.time.LocalDate;

public abstract class EntityBuilder {

    public static ApsUser buildApsUser() {
        ApsUser apsUser = new ApsUser();
        apsUser.name(EntityDefault.NAME);
        apsUser.username(EntityDefault.USERNAME);
        apsUser.setLanguage(EntityDefault.LANG);
        return apsUser;
    }

    public static Customer buildCustomer() {
        Customer customer = new Customer();
        customer.setCode(EntityDefault.USERNAME);
        customer.setName(EntityDefault.NAME);
        customer.setCountry(EntityDefault.COUNTRY);
        customer.setState(CustomerState.ACTIVE);
        customer.vatRate(EntityDefault.VAT_RATE);
        customer.setLicense(EntityDefault.LICENSE);
        return customer;
    }

    public static ApsMembership buildApsMembership(String username, Long customerId) {
        ApsMembership apsMembership = new ApsMembership();
        apsMembership.setCustomerId(customerId);
        apsMembership.setUsername(username);
        apsMembership.setPeriod(EntityDefault.PERIOD);
        apsMembership.setLicense(EntityDefault.LICENSE);
        return apsMembership;
    }

    public static Notification buildNotification(Customer customer) {
        Notification notification = new Notification();
        notification.setCustomer(customer);
        notification.setTitle(EntityDefault.TITLE);
        notification.setMessage(EntityDefault.MESSAGE);
        notification.setLanguage(EntityDefault.LANG);
        notification.setVideoUrl(EntityDefault.VIDEO_URL);
        notification.setImageUrl(EntityDefault.IMAGE_URL);
        notification.setStarts(LocalDate.now());
        notification.setExpires(LocalDate.now().plusDays(10));
        return notification;
    }

    public static VideoCategory buildVideoCategory() {
        VideoCategory category = new VideoCategory();
        category.setCode(EntityDefault.VIDEO_CATEGORY_CODE);
        category.setTitle(EntityDefault.TITLE);
        category.setLabel(EntityDefault.LABEL);
        return category;
    }

    public static Video buildVideo(String videoCode, VideoCategory videoCategory) {
        Video video = new Video();
        video.setVideoCategory(videoCategory);
        video.setCode(videoCode);
        video.setTitle(EntityDefault.TITLE);
        video.setLanguage(EntityDefault.LANG);
        video.setUrl(EntityDefault.VIDEO_URL);
        video.setPlayer(Player.PEECKO);
        video.setIntensity(Intensity.BEGINNER);
        video.setDuration(EntityDefault.VIDEO_DURATION);
        return video;
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
