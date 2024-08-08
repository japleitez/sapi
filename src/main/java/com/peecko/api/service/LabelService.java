package com.peecko.api.service;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class LabelService {
    private static final Map<String, String> LABELS = new HashMap<>();

    static {
        LABELS.put("greeting", "Here is your daily dose of Fitness and Wellness.");
    }
    public String getLabel(String name) {
        return LABELS.getOrDefault(name, name);
    }
}
