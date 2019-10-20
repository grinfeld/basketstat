package com.mikerusoft.euroleague.entities.mysql;

import com.mikerusoft.euroleague.model.Quarter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
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
    private Integer id;
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

    @Column(name = "rebounds_defense")
    @Min(0L)
    @Max(199L)
    private int reboundsDefense;
    @Column(name = "rebounds_offense")
    @Min(0L)
    @Max(199L)
    private int reboundsOffense;
    @Min(0L)
    @Max(199L)
    private int assists;
    @Column(name = "fouls_defense")
    @Min(0L)
    @Max(199L)
    private int foulsDefense;
    @Column(name = "more_10_points")
    @Min(0L)
    @Max(199L)
    private int more10Points;
    @Column(name = "player_max_points_name")
    private String playerMaxPointsName;
    @Column(name = "player_max_points_score")
    @Min(0L)
    @Max(199L)
    private int playerMaxPointsScore;
    @Column(name = "max_lead")
    private int maxLead;
    @Enumerated(EnumType.STRING)
    @Column(name = "max_lead_quarter")
    private Quarter maxLeadQuarter;
    @Min(0L)
    @Max(199L)
    @Column(name = "score_start5_score")
    private int scoreStart5Score;
    @Min(0L)
    @Max(199L)
    @Column(name = "score_bench_score")
    private int scoreBenchScore;
    @Min(0L)
    @Max(199L)
    private int steals;
    @Min(0L)
    @Max(199L)
    private int turnovers;
    @Column(name = "second_chance_attempt")
    private int secondChanceAttempt;

    @OneToMany
    @JoinColumn(name = "command_stats_id")
    private List<CommandQuarterStat> quarterStats;
}
