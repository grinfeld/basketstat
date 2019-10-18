package com.mikerusoft.euroleague.entities;

import com.mikerusoft.euroleague.model.Quarter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Entity
@Table(name = "match_to_command_stats")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class CommandMatchStat {
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

    private int rebounds_defense;
    private int rebounds_offense;
    private int assists;
    private int fouls_defense;
    private int more_10_points;
    private String player_max_points_name;
    private int player_max_points_score;
    private int max_lead;
    @Enumerated(EnumType.STRING)
    private Quarter max_lead_quarter;
    private int score_start5_score;
    private int score_bench_score;
    private int steals;
    private int turnovers;

    @OneToMany
    @JoinColumn(name = "command_stats_id")
    private List<CommandQuarterStat> quarterStats;
}
