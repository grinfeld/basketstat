package com.mikerusoft.euroleague.entities.mongo;

import com.mikerusoft.euroleague.model.Quarter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(builderClassName = "Builder", toBuilder = true)
public class CommandQuarterStat {
    private Quarter quarter;
    private int points1;
    private int attempts1;
    private int points2;
    private int attempts2;
    private int points3;
    private int attempts3;
}
