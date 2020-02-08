package com.mikerusoft.euroleague.controllers;

import com.mikerusoft.euroleague.controllers.model.Aggr;
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
    private int afterDecimal;

    public StatCollector(String commandId, int afterDecimal) {
        this.commandId = commandId;
        this.afterDecimal = afterDecimal;
    }

    @Override
    public Supplier<Aggr> supplier() {
        return () -> Aggr.builder().build();
    }

    @Override
    public BiConsumer<Aggr, Match> accumulator() {
        return (aggregator, match) -> {
            CommandMatchStat stat = null;
            CommandMatchStat ops = null;
            if (isHomeCommandInMatch(commandId, match)) {
                stat = match.getHomeCommand();
                ops = match.getAwayCommand();
            } else if (isAwayCommandInMatch(commandId, match)) {
                stat = match.getAwayCommand();
                ops = match.getHomeCommand();
            }
            if (stat == null || ops == null) {
                return;
            }
            boolean hasOvertime = match.isHasOvertime();
            if (hasOvertime) {
                aggregator.setOvertimes(aggregator.getOvertimes() + 1);
            }
            if (isEmptyTrimmed(aggregator.getCommandName())) {
                aggregator.setCommandName(stat.getCommand().getCommandName());
            }

            aggregate(aggregator, stat, ops);
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
                        .assists(avg(stat.getAssists(), size, afterDecimal))
                        .reboundsDefense(avg(stat.getReboundsDefense(), size, afterDecimal))
                        .reboundsOffense(avg(stat.getReboundsOffense(), size, afterDecimal))
                        .foulsDefense(avg(stat.getFoulsDefense(), size, afterDecimal))
                        .more10Points(avg(stat.getMore10Points(), size, afterDecimal))
                        .scoreStart5Score(avg(stat.getScoreStart5Score(), size, afterDecimal))
                        .scoreBenchScore(avg(stat.getScoreBenchScore(), size, afterDecimal))
                        .steals(avg(stat.getSteals(), size, afterDecimal))
                        .turnovers(avg(stat.getTurnovers(), size, afterDecimal))
                        .secondChancePoints(avg(stat.getSecondChancePoints(), size, afterDecimal))
                        .points1(avg(stat.getPoints1(), size, afterDecimal))
                        .attempts1(avg(stat.getAttempts1(), size, afterDecimal))
                        .points2(avg(stat.getPoints2(), size, afterDecimal))
                        .attempts2(avg(stat.getAttempts2(), size, afterDecimal))
                        .points3(avg(stat.getPoints3(), size, afterDecimal))
                        .attempts3(avg(stat.getAttempts3(), size, afterDecimal))
                        .playerMaxPointsName(removeLastComma(stat.getPlayerMaxPointsName()))
                        .maxLeadQuarter(removeLastComma(stat.getMaxLeadQuarter()))
                        .scoreIn(avg(stat.getScoreIn(), size, afterDecimal))
                        .scoreOut(avg(stat.getScoreOut(), size, afterDecimal))
                        .quarterScoreIn(
                            Optional.ofNullable(stat.getQuarterScoreIn()).orElseGet(StatCollector::initQuarterScores)
                            .stream().map(d -> avg(d, size, afterDecimal)).collect(Collectors.toList())
                        )
                        .quarterScoreOut(
                            Optional.ofNullable(stat.getQuarterScoreOut()).orElseGet(StatCollector::initQuarterScores)
                            .stream().map(d -> avg(d, size, afterDecimal)).collect(Collectors.toList())
                        )
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

    private static double avg(double sum, int size, int afterDecimal) {
        double factor = Math.pow(10, afterDecimal);
        if (size == 0 || sum == 0)
            return 0.0D;
        return Math.round((sum/(double)size) * factor) / factor;
    }

    @Override
    public Set<Characteristics> characteristics() {
        return EnumSet.noneOf(Characteristics.class);
    }

    private static void aggregate(Aggr aggregator, CommandMatchStat stat, CommandMatchStat ops) {

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

        aggregatePointsAndAttempts(aggregator, stat);

        if (!isEmptyTrimmed(stat.getPlayerMaxPointsName())) {
            String playerMaxPoints = deNullString(aggregator.getPlayerMaxPointsName()) + stat.getPlayerMaxPointsName() + " (" + stat.getPlayerMaxPointsScore() + "), ";
            aggregator.setPlayerMaxPointsName(playerMaxPoints);
        }

        if (!isEmptyTrimmed(stat.getMaxLeadQuarter())) {
            String maxLead = deNullString(aggregator.getMaxLeadQuarter()) + stat.getMaxLead() + " (" + quarterDisplayName(stat.getMaxLeadQuarter()) + "), ";
            aggregator.setMaxLeadQuarter(maxLead);
        }

        List<Double> scoreInQuarters = quarterScore(aggregator, stat, Aggr::getQuarterScoreIn);
        aggregator.setQuarterScoreIn(scoreInQuarters);
        aggregator.setScoreIn(scoreInQuarters.stream().filter(Objects::nonNull).mapToDouble(d -> d).sum());

        List<Double> scoreOutQuarters = quarterScore(aggregator, ops, Aggr::getQuarterScoreOut);
        aggregator.setQuarterScoreOut(scoreOutQuarters);
        aggregator.setScoreOut(scoreOutQuarters.stream().filter(Objects::nonNull).mapToDouble(d -> d).sum());
    }

    private static void aggregatePointsAndAttempts(Aggr aggregator, CommandMatchStat stat) {
        aggregator.setPoints1(aggregator.getPoints1() + stat.getPoints1());
        aggregator.setAttempts1(aggregator.getAttempts1() + stat.getAttempts1());
        aggregator.setPoints2(aggregator.getPoints2() + stat.getPoints2());
        aggregator.setAttempts2(aggregator.getAttempts2() + stat.getAttempts2());
        aggregator.setPoints3(aggregator.getPoints3() + stat.getPoints3());
        aggregator.setAttempts3(aggregator.getAttempts3() + stat.getAttempts3());
    }

    private static String quarterDisplayName(String quarter) {
        if (isEmptyTrimmed(quarter))
            return "0";

        Quarter q = Quarter.byName(quarter);
        if (q == null)
            return "0";

        return q.getDisplay();
    }

    private static List<Double> quarterScore(Aggr aggregator, CommandMatchStat stat, Function<Aggr, List<Double>> scoreFunc) {
        Map<String, CommandQuarterStat> statToCollect = stat.getQuarterStats().stream()
                .collect(Collectors.toMap(CommandQuarterStat::getQuarter, Function.identity(), (k1, k2) -> k1));

        List<Double> quarterStats = Optional.ofNullable(scoreFunc.apply(aggregator)).orElseGet(StatCollector::initQuarterScores);

        double scoreFirst = Optional.ofNullable(statToCollect.get(Quarter.FIRST.name())).map(CommandQuarterStat::getScore).orElse(0) + quarterStats.get(0);
        quarterStats.set(0, scoreFirst);
        double scoreSecond = Optional.ofNullable(statToCollect.get(Quarter.SECOND.name())).map(CommandQuarterStat::getScore).orElse(0) + quarterStats.get(1);
        quarterStats.set(1, scoreSecond);
        double scoreThird = Optional.ofNullable(statToCollect.get(Quarter.THIRD.name())).map(CommandQuarterStat::getScore).orElse(0) + quarterStats.get(2);
        quarterStats.set(2, scoreThird);
        double scoreFourth = Optional.ofNullable(statToCollect.get(Quarter.FOURTH.name())).map(CommandQuarterStat::getScore).orElse(0) + quarterStats.get(3);
        quarterStats.set(3, scoreFourth);

        return quarterStats;
    }

    private static List<Double> initQuarterScores() {
        return new ArrayList<>(Arrays.asList(0D, 0D, 0D, 0D));
    }

    private static boolean isAwayCommandInMatch(String commandId, Match m) {
        return m.getAwayCommand().getCommand().getId().equals(commandId);
    }

    private static boolean isHomeCommandInMatch(String commandId, Match m) {
        return m.getHomeCommand().getCommand().getId().equals(commandId);
    }
}
