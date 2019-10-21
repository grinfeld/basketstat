package com.mikerusoft.euroleague.entities.mongo;

import com.mikerusoft.euroleague.model.Quarter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(builderClassName = "Builder", toBuilder = true)
public class CommandMatchStat {
    @Indexed private Command command;
    private int score;
    private int reboundsDefense;
    private int reboundsOffense;
    private int assists;
    private int foulsDefense;
    private int more10Points;
    private String playerMaxPointsName;
    private int playerMaxPointsScore;
    private int maxLead;
    private Quarter maxLeadQuarter;
    private int scoreStart5Score;
    private int scoreBenchScore;
    private int steals;
    private int turnovers;
    private int secondChanceAttempt;

    private List<CommandQuarterStat> quarterStat;
}
