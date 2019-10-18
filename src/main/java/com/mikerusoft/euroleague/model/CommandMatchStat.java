package com.mikerusoft.euroleague.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class CommandMatchStat {
    private Integer id;
    private int commandId;
    private Match match;
    private boolean isHome;

    private int reboundsDefense;
    private int reboundsOffense;
    private int assists;
    private int foulsDefense;
    private int more10Points;
    private String playerMaxPointsName;
    private int playerMaxPointsScore;
    private int maxLead;
    private String maxLeadQuarter;
    private int scoreStart5Score;
    private int scoreBenchScore;
    private int steals;
    private int turnovers;
    private int secondChanceAttempt;

    private List<CommandQuarterStat> quarterStats;
}
