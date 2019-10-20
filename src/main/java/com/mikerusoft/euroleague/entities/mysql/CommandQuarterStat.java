package com.mikerusoft.euroleague.entities.mysql;

import com.mikerusoft.euroleague.model.Quarter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Entity
@Table(name = "command_match_by_types")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true, builderClassName = "Builder")
public class CommandQuarterStat {
    @Id
    @Column(name = "command_match_by_type_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Enumerated(EnumType.STRING)
    private Quarter quarter;
    @Min(0L)
    @Max(199L)
    private int points1;
    @Min(0L)
    @Max(199L)
    private int attempts1;
    @Min(0L)
    @Max(199L)
    private int points2;
    @Min(0L)
    @Max(199L)
    private int attempts2;
    @Min(0L)
    @Max(199L)
    private int points3;
    @Min(0L)
    @Max(199L)
    private int attempts3;
}
