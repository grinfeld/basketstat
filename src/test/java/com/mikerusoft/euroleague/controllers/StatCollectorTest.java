package com.mikerusoft.euroleague.controllers;

import com.mikerusoft.euroleague.controllers.model.Aggr;
import com.mikerusoft.euroleague.model.*;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Date;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class StatCollectorTest {

    @Test
    void whenReceiving2Matches_expectedAggregatedIntoCommandMatchStat() {
        Match match1 = Match.builder()
                .date(new Date())
                .season("201920120")
                .tournament(Tournament.builder().id("1234").tournName("tourn").build())
                .homeCommand(CommandMatchStat.builder().assists(5).maxLead(5).maxLeadQuarter(Quarter.FIRST.name())
                        .quarterStats(Collections.singletonList(CommandQuarterStat.builder().quarter(Quarter.FIRST.name()).score(5).build()))
                        .command(Command.builder().commandName("team1").id("11111").build())
                        .points1(1)
                        .attempts1(2)
                        .points2(1)
                        .attempts2(2)
                        .points3(1)
                        .attempts3(2)
                    .build())
                .awayCommand(CommandMatchStat.builder().assists(10).maxLead(1).maxLeadQuarter(Quarter.SECOND.name())
                        .command(Command.builder().commandName("team2").id("22222").build())
                        .quarterStats(Collections.singletonList(CommandQuarterStat.builder().quarter(Quarter.FIRST.name()).score(10).build()))
                        .points1(1)
                        .attempts1(2)
                        .points2(1)
                        .attempts2(2)
                        .points3(1)
                        .attempts3(2)
                    .build())
            .build();
        Match match2 = Match.builder()
                .date(new Date())
                .season("201920120")
                .tournament(Tournament.builder().id("1234").tournName("tourn").build())
                .homeCommand(CommandMatchStat.builder().assists(5).maxLead(5).maxLeadQuarter(Quarter.FIRST.name())
                        .quarterStats(Collections.singletonList(CommandQuarterStat.builder().quarter(Quarter.FIRST.name()).score(3).build()))
                        .command(Command.builder().commandName("team3").id("333333").build())
                        .points1(1)
                        .attempts1(2)
                        .points2(1)
                        .attempts2(2)
                        .points3(1)
                        .attempts3(2)
                    .build())
                .awayCommand(CommandMatchStat.builder().assists(10).maxLead(1).maxLeadQuarter(Quarter.THIRD.name())
                        .quarterStats(Collections.singletonList(CommandQuarterStat.builder().quarter(Quarter.FIRST.name()).score(12).build()))
                        .command(Command.builder().commandName("team1").id("11111").build())
                        .points1(2)
                        .attempts1(4)
                        .points2(1)
                        .attempts2(4)
                        .points3(2)
                        .attempts3(3)
                    .build())
            .build();

        Aggr collect = Stream.of(match1, match2).collect(new StatCollector("11111", 1));

        assertThat(collect).isNotNull()
            .hasFieldOrPropertyWithValue("commandName", "team1")
            .hasFieldOrPropertyWithValue("assists", 7.5D)
            .hasFieldOrPropertyWithValue("points1", 1.5D)
            .hasFieldOrPropertyWithValue("attempts1", 3D)
            .hasFieldOrPropertyWithValue("points2", 1D)
            .hasFieldOrPropertyWithValue("attempts2", 3D)
            .hasFieldOrPropertyWithValue("points3", 1.5D)
            .hasFieldOrPropertyWithValue("attempts3", 2.5D)
            .hasFieldOrPropertyWithValue("maxLeadQuarter", "5 (1st), 1 (3rd)")
        ;

        assertThat(collect.getQuarterScoreIn()).isNotNull().hasSize(4).containsExactly(8.5, 0.0, 0.0, 0.0);
        assertThat(collect.getQuarterScoreOut()).isNotNull().hasSize(4).containsExactly(6.5, 0.0, 0.0, 0.0);

    }
}