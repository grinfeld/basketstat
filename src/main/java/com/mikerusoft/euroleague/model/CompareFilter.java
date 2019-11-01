package com.mikerusoft.euroleague.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.mikerusoft.euroleague.utils.Utils.isEmptyTrimmed;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(builderClassName = "Builder")
public class CompareFilter {
    private Tournament tournament;
    private Command command1;
    private Command command2;
    private String season;
    private int records;

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
