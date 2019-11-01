package com.mikerusoft.euroleague.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class CommandQuarterStat {
    private String quarter;
    private int score;

    public static List<CommandQuarterStat> initialCommandStats() {
        return Arrays.asList(
            builder().quarter(Quarter.FIRST.name()).build(),
            builder().quarter(Quarter.SECOND.name()).build(),
            builder().quarter(Quarter.THIRD.name()).build(),
            builder().quarter(Quarter.FOURTH.name()).build(),
            builder().quarter(Quarter.OT.name()).build()
        );
    }
}
