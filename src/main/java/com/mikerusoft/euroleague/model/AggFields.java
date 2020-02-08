package com.mikerusoft.euroleague.model;

import lombok.Getter;

public enum AggFields {
    score("score", "Score"),
    steals("steals", "Steals"),
    reboundsDefense("reboundsDefense", "Defense rebounds"),
    reboundsOffense("reboundsOffense", "Offense rebounds"),
    assists("assists", "Assists"),
    foulsDefense("foulsDefense", "Defense fouls"),
    turnovers("turnovers", "Turnovers")
    ;
    @Getter String fieldName;
    @Getter String displayName;
    AggFields(String fieldName, String displayName) {
        this.fieldName = fieldName;
        this.displayName = displayName;
    }
}
