package com.mikerusoft.euroleague.model;

import lombok.Getter;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum Quarter {
    FIRST ("1st", 1),
    SECOND ("2nd", 2),
    THIRD ("3rd", 3),
    FOURTH ("4th", 4),
    OT ("OT", 5);

    @Getter private String display;
    @Getter private int order;

    Quarter(String display, int order) {
        this.display = display;
        this.order = order;
    }

    private static final Map<String, Quarter> byDisplay = Stream.of(Quarter.values())
            .collect(Collectors.toMap(Quarter::getDisplay, Function.identity(), (k1, k2) -> k1));

    public static Quarter byDisplayName(String display) {
        return byDisplay.get(display);
    }
}
