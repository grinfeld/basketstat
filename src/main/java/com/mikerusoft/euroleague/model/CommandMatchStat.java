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
    private Command command;
    private boolean isHome;

    private int score;
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
    private int secondChancePoints;

    private int attempts1;
    private int attempts2;
    private int attempts3;
    private int points1;
    private int points2;
    private int points3;

    private List<CommandQuarterStat> quarterStats;

    public int rebounds() {
        return reboundsDefense + reboundsOffense;
    }

    public double point1ratio() {
        if (attempts1 == 0)
            return 0;
        if (points1 == 0)
            return 0;

        return Math.round((points1/(double)attempts1) * 100);
    }

    public double point2ratio() {
        if (attempts2 == 0)
            return 0;
        if (points2 == 0)
            return 0;

        return Math.round((points2/(double)attempts2) * 100);
    }

    public double point3ratio() {
        if (attempts3 == 0)
            return 0;
        if (points3 == 0)
            return 0;

        return Math.round((points3/(double)attempts3) * 100);
    }
}
