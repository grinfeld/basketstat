package com.mikerusoft.euroleague.model;

import com.mikerusoft.euroleague.entities.Command;
import com.mikerusoft.euroleague.entities.Match;
import com.mikerusoft.euroleague.entities.Tournament;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Optional;

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
        return Optional.of(match).map(Match::getHomeCommand).map(Command::getId).filter(id -> id == commandId).isPresent();
    }

    public boolean isAway() {
        return Optional.of(match).map(Match::getAwayCommand).map(Command::getId).filter(id -> id == commandId).isPresent();
    }
}
