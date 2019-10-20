package com.mikerusoft.euroleague.entities.mysql;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.sql.Date;

@Entity
@Table(name = "results")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(builderClassName = "Builder", toBuilder = true)
public class Result {
    @Id @Column(name = "result_id")
    @GeneratedValue(
        strategy= GenerationType.IDENTITY
    )
    private Integer id;
    private Date date;
    @Min(0L)
    @Max(199L)
    private Integer attempts3Points;
    @Min(0L)
    @Max(199L)
    private Integer scored3Points;
    @Min(0L)
    @Max(199L)
    private Integer attempts2Points;
    @Min(0L)
    @Max(199L)
    private Integer scored2Points;
    @Min(0L)
    @Max(199L)
    private Integer attempts1Points;
    @Min(0L)
    @Max(199L)
    private Integer scored1Points;
    @Min(0L)
    @Max(199L)
    private Integer scoreIn;
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
