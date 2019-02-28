package com.mikerusoft.euroleague.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.sql.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "results")
@Builder(builderClassName = "Builder", toBuilder = true)
public class Result {
    @Id @Column(name = "result_id")
    private int id;
    private Date date;
    private int attempts3Points;
    private int scored3Points;
    private int attempts2Points;
    private int scored2Points;
    private int attempts1Points;
    private int scored1Points;
    private int scoreIn;
    private int scoreOut;
    @Size(min = 8, max = 8, message = "Season should be 8 characters, e.g. 20182019")
    @NotNull
    private String season;
    private boolean homeMatch;
    @ManyToOne
    @JoinColumn(name = "commandId", referencedColumnName = "id", nullable = false)
    private Command command;
    @ManyToOne
    @JoinColumn(name = "tournamentId", referencedColumnName = "id", nullable = false)
    private Tournament tournament;
}
