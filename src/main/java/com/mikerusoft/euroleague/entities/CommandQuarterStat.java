package com.mikerusoft.euroleague.entities;

import com.mikerusoft.euroleague.model.Quarter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "command_match_by_types")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class CommandQuarterStat {
    @Id
    @Column(name = "command_match_by_type_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Enumerated(EnumType.STRING)
    private Quarter quarter;

    private int points1;
    private int attempts1;
    private int points2;
    private int attempts2;
    private int points3;
    private int attempts3;
}
