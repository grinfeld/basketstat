package com.mikerusoft.euroleague.controllers.model;

import com.mikerusoft.euroleague.model.AggFields;
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
public class TopXFilter {
    private Tournament tournament;
    @lombok.Builder.Default
    private String season = extractSeason(new Date());
    @lombok.Builder.Default
    private int games = 2;
    @lombok.Builder.Default
    private int top = 1;
    @lombok.Builder.Default
    private AggFields field = AggFields.score;

    public boolean hasTournament() { return tournament != null && !isEmptyTrimmed(tournament.getId()); }

    public String getTournamentId() { return hasTournament() ? tournament.getId() : ""; }

    public boolean hasSeason() {
        return !isEmptyTrimmed(season);
    }

    public boolean filterReady() {
        return hasTournament() && hasSeason() && games > 0 && top > 0 && field != null;
    }
}
