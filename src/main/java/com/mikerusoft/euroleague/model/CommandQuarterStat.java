package com.mikerusoft.euroleague.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class CommandQuarterStat {
    private int id;
    private String quarter;

    private int points1;
    private int attempts1;
    private int points2;
    private int attempts2;
    private int points3;
    private int attempts3;
}
