package com.mikerusoft.euroleague.controllers;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class Aggr {
    private String commandName;
    private int games;

    private int overtimes;

    private double score;
    private double reboundsDefense;
    private double reboundsOffense;
    private double assists;
    private double foulsDefense;
    private double more10Points;
    private String playerMaxPointsName;
    private String maxLeadQuarter;
    private double scoreStart5Score;
    private double scoreBenchScore;
    private double steals;
    private double turnovers;
    private double secondChancePoints;

    private double attempts1;
    private double attempts2;
    private double attempts3;
    private double points1;
    private double points2;
    private double points3;

    public double rebounds() {
        return reboundsDefense + reboundsOffense;
    }

    public double point1ratio() {
        if (attempts1 == 0)
            return 0;
        if (points1 == 0)
            return 0;

        return Math.round((points1/(double)attempts1) * 10000)/100d;
    }

    public double point2ratio() {
        if (attempts2 == 0)
            return 0;
        if (points2 == 0)
            return 0;

        return Math.round((points2/(double)attempts2) * 10000)/100d;
    }

    public double point3ratio() {
        if (attempts3 == 0)
            return 0;
        if (points3 == 0)
            return 0;

        return Math.round((points3/(double)attempts3) * 10000)/100d;
    }

}
