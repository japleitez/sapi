package com.peecko.api.utils;

import com.peecko.api.domain.Label;

import java.util.Arrays;
import java.util.stream.Collectors;

public class LabelUtils {

    public static String concatCodes(Label... labels) {
        return Arrays.stream(labels)
                .map(Label::getCode)
                .collect(Collectors.joining(","));
    }

}
