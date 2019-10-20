package com.mikerusoft.euroleague.entities.mysql;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Date;

@Entity
@Table(name = "matches")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(builderClassName = "Builder", toBuilder = true)
public class Match {
    @Id
    @Column(name = "match_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Date date;
    private String season;
    @Column(name = "score_home")
    private int scoreHome;
    @Column(name = "score_away")
    private int scoreAway;

    @ManyToOne
    @JoinColumn(name = "tournamentId", referencedColumnName = "id", nullable = false)
    private Tournament tournament;

    @ManyToOne
    @JoinColumn(name = "home_command_id", referencedColumnName = "id", nullable = false)
    private Command homeCommand;

    @ManyToOne
    @JoinColumn(name = "away_command_id", referencedColumnName = "id", nullable = false)
    private Command awayCommand;


}
