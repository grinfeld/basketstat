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
    private int id;
    private int commandId;
    private Match match;
    private boolean isHome;

    private int rebounds_defense;
    private int rebounds_offense;
    private int assists;
    private int fouls_defense;
    private int more_10_points;
    private String player_max_points_name;
    private int player_max_points_score;
    private int max_lead;
    private String max_lead_quarter;
    private int score_start5_score;
    private int score_bench_score;
    private int steals;
    private int turnovers;

    private List<CommandQuarterStat> quarterStats;
}
