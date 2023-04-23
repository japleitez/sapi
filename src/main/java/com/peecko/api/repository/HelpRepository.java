package com.peecko.api.repository;

import com.peecko.api.domain.Help;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class HelpRepository {
    public static final List<Help> DATA = new ArrayList<>();

    public static final String  Q1 = "What is peecko?";
    public static final String  Q2 = "How can I renew my membership?";
    public static final String  Q3 = "Do you provide 1-to-1 coaching?";

    public static final String A1 = "Peecko is a wellness app sponsored by your Employer. Peecko brings to you short fitness videos that you can perform easily at home";
    public static final String A2 = "Your employer renew your membership automatically every month. Your employer will directly provide the business license to you";
    public static final String A3 = "Some trainers provide personal coaching, the contact information is part of the description of the video";

    static {
        DATA.add(new Help(Q1, A1));
        DATA.add(new Help(Q2, A2));
        DATA.add(new Help(Q3, A3));
    }

    public List<Help> getHelp() {
        return DATA;
    }

}
