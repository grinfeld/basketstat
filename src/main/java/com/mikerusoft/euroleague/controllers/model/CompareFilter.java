package com.mikerusoft.euroleague.controllers.model;

import com.mikerusoft.euroleague.model.Command;
import com.mikerusoft.euroleague.model.Place;
import com.mikerusoft.euroleague.model.Tournament;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

import static com.mikerusoft.euroleague.utils.Utils.extractSeason;
import static com.mikerusoft.euroleague.utils.Utils.isEmptyTrimmed;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(builderClassName = "Builder")
public class CompareFilter {
    private Tournament tournament;
    private Command command1;
    private Command command2;
    @lombok.Builder.Default
    private String season = extractSeason(new Date());
    @lombok.Builder.Default
    private int records = 1;
    @lombok.Builder.Default
    private String matchPlace = Place.all.name();

    public boolean hasTournament() {
        return tournament != null && !isEmptyTrimmed(tournament.getId());
    }

    public boolean hasCommand1() {
        return command1 != null && !isEmptyTrimmed(command1.getId());
    }

    public boolean hasCommand2() {
        return command2 != null && !isEmptyTrimmed(command2.getId());
    }

    public boolean hasSeason() {
        return !isEmptyTrimmed(season);
    }

    public boolean filterReady() {
        return hasTournament() && hasCommand1() && hasCommand2() && hasSeason() && records > 0;
    }

    public String getCommand1Id() {
        return hasCommand1() ? command1.getId() : "";
    }

    public String getCommand2Id() {
        return hasCommand2() ? command2.getId() : "";
    }

    public String getTournamentId() {
        return hasTournament() ? tournament.getId() : "";
    }
}
