package com.mikerusoft.euroleague.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static com.mikerusoft.euroleague.utils.Utils.isEmptyTrimmed;

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
    private boolean hasOvertime;

    private CommandMatchStat awayCommand;
    private CommandMatchStat homeCommand;

    public boolean isAwayCommand(String commandId) {
        if (isEmptyTrimmed(commandId) || awayCommand == null)
            return false;
        return Optional.of(awayCommand).map(CommandMatchStat::getCommand).map(Command::getId)
                .filter(s -> s.equals(commandId)).isPresent();
    }

    public boolean isHomeCommand(String commandId) {
        if (isEmptyTrimmed(commandId) || homeCommand == null)
            return false;
        return Optional.of(homeCommand).map(CommandMatchStat::getCommand).map(Command::getId)
                .filter(s -> s.equals(commandId)).isPresent();
    }

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
