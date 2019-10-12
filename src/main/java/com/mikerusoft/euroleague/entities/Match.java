package com.mikerusoft.euroleague.entities;

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
    private int id;
    private Date date;
    private String season;
    private int scoreHome;
    private int scoreAway;

    @ManyToOne
    @JoinColumn(name = "tournamentId", referencedColumnName = "id", nullable = false)
    private Tournament tournament;

    @ManyToOne
    @JoinColumn(name = "homeCommandId", referencedColumnName = "id", nullable = false)
    private Command homeCommand;

    @ManyToOne
    @JoinColumn(name = "awayCommandId", referencedColumnName = "id", nullable = false)
    private Command awayCommand;
}
