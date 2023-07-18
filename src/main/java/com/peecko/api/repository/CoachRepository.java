package com.peecko.api.repository;

import com.peecko.api.domain.Coach;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

public class CoachRepository {

    private static final Map<String, Coach> COACHES = new HashMap<>();

    static {
        COACHES.put("Julia Hernandez", new Coach("Julia Hernandez","https://peecko.com/", "julia.hernandez@gmail.com", "julialux"));
        COACHES.put("Tatiana Mendoza", new Coach("Tatiana Mendoza","https://peecko.com/", "tatiana.mendoza@hotmail.co.uk", ""));
        COACHES.put("Robert Roca", new Coach("Robert Roca","", "", "rocapilateshome"));
        COACHES.put("Tom Hollwege", new Coach("Tom Hollwege","", "julia.hernandez@yahoo.com", "donotome"));
        COACHES.put("Angelica Muller",  new Coach("Angelica Muller","https://peecko.com/", "julia.hernandez@gmail.com", ""));
    }

    public static Coach find(String name) {
        Coach coach = COACHES.get(name);
        if (coach == null) {
            coach = new Coach();
            coach.setName(name);
        }
        return coach;
    }

}
