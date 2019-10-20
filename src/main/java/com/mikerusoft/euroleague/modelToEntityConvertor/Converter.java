package com.mikerusoft.euroleague.modelToEntityConvertor;

import com.mikerusoft.euroleague.entities.mysql.*;
import com.mikerusoft.euroleague.model.Quarter;

import java.util.*;
import java.util.stream.Collectors;

public class Converter {

    private Converter() {}

    public static Command convert(com.mikerusoft.euroleague.model.Command source) {
        if (source == null)
            return null;
        return Command.builder().id(source.getId()).commandName(source.getCommandName()).build();
    }

    public static Tournament convert(com.mikerusoft.euroleague.model.Tournament source) {
        if (source == null)
            return null;
        return Tournament.builder().id(source.getId()).tournName(source.getTournName()).build();
    }

    public static com.mikerusoft.euroleague.model.Command convert(Command source) {
        if (source == null)
            return null;
        return com.mikerusoft.euroleague.model.Command.builder()
                .id(source.getId()).commandName(source.getCommandName()).build();
    }

    public static com.mikerusoft.euroleague.model.Tournament convert(Tournament source) {
        if (source == null)
            return null;
        return com.mikerusoft.euroleague.model.Tournament.builder()
                .id(source.getId()).tournName(source.getTournName()).build();
    }

    public static Result convert(com.mikerusoft.euroleague.model.Result source) {
        if (source == null)
            return null;
        return Result.builder()
            .id(source.getId())
            .date(source.getDate())
            .attempts1Points(source.getAttempts1Points())
            .attempts2Points(source.getAttempts2Points())
            .attempts3Points(source.getAttempts3Points())
            .homeMatch(source.isHomeMatch())
            .scored1Points(source.getScored1Points())
            .scored2Points(source.getScored2Points())
            .scored3Points(source.getScored3Points())
            .scoreIn(source.getScoreIn())
            .scoreOut(source.getScoreOut())
            .season(source.getSeason())
            .command(convert(source.getCommand()))
            .tournament(convert(source.getTournament()))
        .build();
    }

    public static com.mikerusoft.euroleague.model.Result convert(Result source) {
        if (source == null)
            return null;
        return com.mikerusoft.euroleague.model.Result.builder()
            .id(source.getId())
            .date(source.getDate())
            .attempts1Points(source.getAttempts1Points())
            .attempts2Points(source.getAttempts2Points())
            .attempts3Points(source.getAttempts3Points())
            .homeMatch(source.isHomeMatch())
            .scored1Points(source.getScored1Points())
            .scored2Points(source.getScored2Points())
            .scored3Points(source.getScored3Points())
            .scoreIn(source.getScoreIn())
            .scoreOut(source.getScoreOut())
            .season(source.getSeason())
            .command(convert(source.getCommand()))
            .tournament(convert(source.getTournament()))
        .build();
    }

    public static com.mikerusoft.euroleague.model.CommandQuarterStat convert(CommandQuarterStat stat) {
        if (stat == null)
            return null;
        return com.mikerusoft.euroleague.model.CommandQuarterStat.builder()
                .id(stat.getId())
                .attempts1(stat.getAttempts1())
                .points1(stat.getPoints1())
                .attempts2(stat.getAttempts2())
                .points2(stat.getPoints2())
                .attempts3(stat.getAttempts3())
                .points3(stat.getPoints3())
                .quarter(stat.getQuarter().getDisplay())
            .build();
    }

    public static CommandQuarterStat convert(com.mikerusoft.euroleague.model.CommandQuarterStat stat) {
        if (stat == null)
            return null;
        return CommandQuarterStat.builder()
                .id(stat.getId())
                .attempts1(stat.getAttempts1())
                .points1(stat.getPoints1())
                .attempts2(stat.getAttempts2())
                .points2(stat.getPoints2())
                .attempts3(stat.getAttempts3())
                .points3(stat.getPoints3())
                .quarter(Quarter.byDisplayName(stat.getQuarter()))
            .build();
    }

    public static com.mikerusoft.euroleague.model.Match convert(Match match) {
        if (match == null)
            return null;
        return com.mikerusoft.euroleague.model.Match.builder()
                .id(match.getId())
                .awayCommand(convert(match.getAwayCommand().toBuilder().build()))
                .homeCommand(convert(match.getHomeCommand().toBuilder().build()))
                .date(Optional.ofNullable(match.getDate()).map(d -> new Date(d.getTime())).orElse(null))
                .tournament(convert(match.getTournament().toBuilder().build()))
                .scoreAway(match.getScoreHome())
                .scoreAway(match.getScoreAway())
                .season(match.getSeason())
            .build();
    }

    public static Match convert(com.mikerusoft.euroleague.model.Match match) {
        if (match == null)
            return null;
        return Match.builder()
                .id(match.getId())
                .awayCommand(convert(match.getAwayCommand().toBuilder().build()))
                .homeCommand(convert(match.getHomeCommand().toBuilder().build()))
                .date(Optional.ofNullable(match.getDate()).map(d -> new java.sql.Date(d.getTime())).orElse(null))
                .tournament(convert(match.getTournament().toBuilder().build()))
                .scoreAway(match.getScoreHome())
                .scoreAway(match.getScoreAway())
                .season(match.getSeason())
            .build();
    }

    public static com.mikerusoft.euroleague.model.CommandMatchStat convert(CommandMatchStat stat) {
        if (stat == null)
            return null;
        return com.mikerusoft.euroleague.model.CommandMatchStat.builder()
                .id(stat.getId())
                .commandId(stat.getCommandId())
                .match(convert(stat.getMatch()))
                .isHome(stat.isHome())
                .assists(stat.getAssists())
                .steals(stat.getSteals())
                .turnovers(stat.getTurnovers())
                .foulsDefense(stat.getFoulsDefense())
                .maxLead(stat.getMaxLead())
                .maxLeadQuarter(Optional.ofNullable(stat.getMaxLeadQuarter()).map(Enum::name).orElse(null))
                .more10Points(stat.getMore10Points())
                .playerMaxPointsName(stat.getPlayerMaxPointsName())
                .playerMaxPointsScore(stat.getPlayerMaxPointsScore())
                .reboundsDefense(stat.getReboundsDefense())
                .reboundsOffense(stat.getReboundsOffense())
                .scoreBenchScore(stat.getScoreBenchScore())
                .scoreStart5Score(stat.getScoreStart5Score())
                .secondChanceAttempt(stat.getSecondChanceAttempt())
                .quarterStats(
                        Optional.ofNullable(stat.getQuarterStats()).orElseGet(ArrayList::new)
                        .stream().map(Converter::convert).filter(Objects::nonNull)
                        .sorted(Comparator.comparingInt(o -> Quarter.byDisplayName(o.getQuarter()).getOrder()))
                    .collect(Collectors.toList())
                )
            .build();
    }

    public static CommandMatchStat convert(com.mikerusoft.euroleague.model.CommandMatchStat stat) {
        return CommandMatchStat.builder()
                .id(stat.getId())
                .commandId(stat.getCommandId())
                .match(convert(stat.getMatch()))
                .assists(stat.getAssists())
                .steals(stat.getSteals())
                .turnovers(stat.getTurnovers())
                .foulsDefense(stat.getFoulsDefense())
                .maxLead(stat.getMaxLead())
                .maxLeadQuarter(Optional.ofNullable(stat.getMaxLeadQuarter()).map(Quarter::byDisplayName).orElse(null))
                .more10Points(stat.getMore10Points())
                .playerMaxPointsName(stat.getPlayerMaxPointsName())
                .playerMaxPointsScore(stat.getPlayerMaxPointsScore())
                .reboundsDefense(stat.getReboundsDefense())
                .reboundsOffense(stat.getReboundsOffense())
                .scoreBenchScore(stat.getScoreBenchScore())
                .scoreStart5Score(stat.getScoreStart5Score())
                .secondChanceAttempt(stat.getSecondChanceAttempt())
                .quarterStats(
                        Optional.ofNullable(stat.getQuarterStats()).orElseGet(ArrayList::new)
                        .stream().map(Converter::convert).filter(Objects::nonNull)
                    .collect(Collectors.toList())
                )
            .build();
    }
}
