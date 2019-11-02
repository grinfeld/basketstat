package com.mikerusoft.euroleague.controllers;

import com.mikerusoft.euroleague.model.*;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import static com.mikerusoft.euroleague.utils.Utils.*;

public class StatCollector implements Collector<Match, Aggr, Aggr> {

    private String commandId;
    private int size = 0;

    public StatCollector(String commandId) {
        this.commandId = commandId;
    }

    @Override
    public Supplier<Aggr> supplier() {
        return () -> Aggr.builder()
                //.quarterStats(initialCommandStats())
            //.command(Command.builder().id(commandId).build())
                .build();

    }

    @Override
    public BiConsumer<Aggr, Match> accumulator() {
        return (aggregator, match) -> {
            CommandMatchStat stat = null;
            if (isHomeCommandInMatch(commandId, match)) {
                stat = match.getHomeCommand();
            } else if (isAwayCommandInMatch(commandId, match)) {
                stat = match.getAwayCommand();
            }
            if (stat == null) {
                return;
            }
            boolean hasOvertime = match.isHasOvertime();
            if (hasOvertime) {
                aggregator.setOvertimes(aggregator.getOvertimes() + 1);
            }
            if (isEmptyTrimmed(aggregator.getCommandName())) {
                aggregator.setCommandName(stat.getCommand().getCommandName());
            }
            aggregate(aggregator, stat);
            size++;
            aggregator.setGames(size);
        };
    }

    @Override
    public BinaryOperator<Aggr> combiner() {
        return (aggr, stat) -> {
            throw new IllegalArgumentException("ooopps");
        };
    }

    @Override
    public Function<Aggr, Aggr> finisher() {
        return new Function<Aggr, Aggr>() {

            @Override
            public Aggr apply(Aggr commandMatchStat) {
                Aggr stat = commandMatchStat.toBuilder().build();

                Aggr aggregation = Aggr.builder()
                        .commandName(stat.getCommandName())
                        .games(stat.getGames())
                        .overtimes(stat.getOvertimes())
                        .assists(avg(stat.getAssists(), size, 1))
                        .reboundsDefense(avg(stat.getReboundsDefense(), size, 1))
                        .reboundsOffense(avg(stat.getReboundsOffense(), size, 1))
                        .foulsDefense(avg(stat.getFoulsDefense(), size, 1))
                        .more10Points(avg(stat.getMore10Points(), size, 1))
                        .scoreStart5Score(avg(stat.getScoreStart5Score(), size, 1))
                        .scoreBenchScore(avg(stat.getScoreBenchScore(), size, 1))
                        .steals(avg(stat.getSteals(), size, 1))
                        .turnovers(avg(stat.getTurnovers(), size, 1))
                        .secondChancePoints(avg(stat.getSecondChancePoints(), size, 1))
                        .points1(avg(stat.getPoints1(), size, 1))
                        .attempts1(avg(stat.getAttempts1(), size, 1))
                        .points2(avg(stat.getPoints2(), size, 1))
                        .attempts2(avg(stat.getAttempts2(), size, 1))
                        .points3(avg(stat.getPoints3(), size, 1))
                        .attempts3(avg(stat.getAttempts3(), size, 1))
                        .playerMaxPointsName(removeLastComma(stat.getPlayerMaxPointsName()))
                        .maxLeadQuarter(removeLastComma(stat.getMaxLeadQuarter()))
                    .build();

                return aggregation;
            }
        };
    }

    private static String removeLastComma(String str) {
        if (isEmptyTrimmed(str))
            return null;
        str = str.trim();

        if (str.length() == 1)
            return null;
        if (str.endsWith(","))
            return str.substring(0, str.length()-1);
        return str;
    }

    private static double avg(double sum, int size, int afterDec) {
        double factor = Math.pow(10, afterDec);
        if (size == 0 || sum == 0)
            return 0.0D;
        return Math.round((sum/(double)size) * factor) / factor;
    }

    @Override
    public Set<Characteristics> characteristics() {
        return EnumSet.noneOf(Characteristics.class);
    }

    private static Aggr aggregate(Aggr aggregator, CommandMatchStat stat) {
        aggregator.setAssists(aggregator.getAssists() + stat.getAssists());
        aggregator.setReboundsDefense(aggregator.getReboundsDefense() + stat.getReboundsDefense());
        aggregator.setReboundsOffense(aggregator.getReboundsOffense() + stat.getReboundsOffense());
        aggregator.setFoulsDefense(aggregator.getFoulsDefense() + stat.getFoulsDefense());
        aggregator.setMore10Points(aggregator.getMore10Points() + stat.getMore10Points());
        aggregator.setScoreStart5Score(aggregator.getScoreStart5Score() + stat.getScoreStart5Score());
        aggregator.setScoreBenchScore(aggregator.getScoreBenchScore() + stat.getScoreBenchScore());
        aggregator.setSteals(aggregator.getSteals() + stat.getSteals());
        aggregator.setTurnovers(aggregator.getTurnovers() + stat.getTurnovers());
        aggregator.setSecondChancePoints(aggregator.getSecondChancePoints() + stat.getSecondChancePoints());

        aggregator.setPoints1(aggregator.getPoints1() + stat.getPoints1());
        aggregator.setAttempts1(aggregator.getAttempts1() + stat.getAttempts1());
        aggregator.setPoints2(aggregator.getPoints2() + stat.getPoints2());
        aggregator.setAttempts2(aggregator.getAttempts2() + stat.getAttempts2());
        aggregator.setPoints3(aggregator.getPoints3() + stat.getPoints3());
        aggregator.setAttempts3(aggregator.getAttempts3() + stat.getAttempts3());

        if (!isEmptyTrimmed(stat.getPlayerMaxPointsName())) {
            String playerMaxPoints = deNullString(aggregator.getPlayerMaxPointsName()) + stat.getPlayerMaxPointsName() + "(" + stat.getPlayerMaxPointsScore() + "), ";
            aggregator.setPlayerMaxPointsName(playerMaxPoints);
        }

        if (!isEmptyTrimmed(stat.getMaxLeadQuarter())) {
            String maxLead = deNullString(aggregator.getMaxLeadQuarter()) + stat.getMaxLead() + "(" + deNullString(stat.getMaxLeadQuarter()) + "), ";
            aggregator.setMaxLeadQuarter(maxLead);
        }

        Map<String, CommandQuarterStat> statToCollect = stat.getQuarterStats().stream()
                .collect(Collectors.toMap(CommandQuarterStat::getQuarter, Function.identity(), (k1, k2) -> k1));

        /*for (CommandQuarterStat quarterStats : aggregator.getQuarterStats()) {
            int sumScore = quarterStats.getScore() +
                    Optional.ofNullable(statToCollect.get(quarterStats.getQuarter())).map(CommandQuarterStat::getScore).orElse(0);
            quarterStats.setScore(sumScore);
        }*/

        return aggregator;
    }

    private static boolean isAwayCommandInMatch(String commandId, Match m) {
        return m.getAwayCommand().getCommand().getId().equals(commandId);
    }

    private static boolean isHomeCommandInMatch(String commandId, Match m) {
        return m.getHomeCommand().getCommand().getId().equals(commandId);
    }

    private static List<CommandQuarterStat> initialCommandStats() {
        return Arrays.asList(
                CommandQuarterStat.builder().quarter(Quarter.FIRST.name()).build(),
                CommandQuarterStat.builder().quarter(Quarter.SECOND.name()).build(),
                CommandQuarterStat.builder().quarter(Quarter.THIRD.name()).build(),
                CommandQuarterStat.builder().quarter(Quarter.FOURTH.name()).build(),
                CommandQuarterStat.builder().quarter(Quarter.OT.name()).build()
        );
    }
}
