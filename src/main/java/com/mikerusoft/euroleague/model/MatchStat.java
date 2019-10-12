package com.mikerusoft.euroleague.model;

import com.mikerusoft.euroleague.entities.Command;
import com.mikerusoft.euroleague.entities.Match;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Optional;
import java.util.function.Function;

@Entity
@Table(name = "match_to_command_stats")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(builderClassName = "Builder", toBuilder = true)
public class MatchStat {
    @Id
    @Column(name = "command_stats_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private int commandId;

    @ManyToOne
    @JoinColumn(name = "matchId", referencedColumnName = "match_id", nullable = false)
    private Match match;

    public boolean isHome() {
        return filterCommandByMatchLocation(Match::getHomeCommand);
    }

    public boolean isAway() {
        return filterCommandByMatchLocation(Match::getAwayCommand);
    }

    private boolean filterCommandByMatchLocation(Function<Match, Command> getCommand) {
        return Optional.of(match).map(getCommand).map(Command::getId).filter(id -> id == commandId).isPresent();
    }
}
