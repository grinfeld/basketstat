package com.mikerusoft.euroleague.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true, builderClassName = "Builder")
public class Match {
    private String id;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date date;
    private String season;
    private Tournament tournament;

    private CommandMatchStat awayCommand;
    private CommandMatchStat homeCommand;

    public static Match normalizeMatch(Match match) {
        if (match == null)
            return null;
        List<CommandQuarterStat> homeQuarterStats = match.getHomeCommand().getQuarterStats();
        if (homeQuarterStats.size() == 4){
            homeQuarterStats.add(CommandQuarterStat.builder().quarter(Quarter.OT.name()).build());
            match.getHomeCommand().setQuarterStats(homeQuarterStats);
        }
        List<CommandQuarterStat> awayQuarterStats = match.getAwayCommand().getQuarterStats();
        if (awayQuarterStats.size() == 4){
            awayQuarterStats.add(CommandQuarterStat.builder().quarter(Quarter.OT.name()).build());
            match.getAwayCommand().setQuarterStats(awayQuarterStats);
        }
        return match;
    }
}
