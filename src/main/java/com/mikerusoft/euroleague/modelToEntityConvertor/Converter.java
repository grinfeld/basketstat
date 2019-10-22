package com.mikerusoft.euroleague.modelToEntityConvertor;

import com.mikerusoft.euroleague.entities.mysql.*;
import com.mikerusoft.euroleague.model.Quarter;
import com.mikerusoft.euroleague.utils.Utils;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Collectors;

public class Converter {

    private Converter() {}

    public static Command convert(com.mikerusoft.euroleague.model.Command source) {
        if (source == null)
            return null;
        return Command.builder().id(Utils.parseIntWithDeNull(source.getId())).commandName(source.getCommandName()).build();
    }

    public static Tournament convert(com.mikerusoft.euroleague.model.Tournament source) {
        if (source == null)
            return null;
        return Tournament.builder().id(Utils.parseIntWithDeNull(source.getId())).tournName(source.getTournName()).build();
    }

    public static com.mikerusoft.euroleague.model.Command convert(Command source) {
        if (source == null)
            return null;
        return com.mikerusoft.euroleague.model.Command.builder()
                .id(Utils.toStringWithDeNull(source.getId())).commandName(source.getCommandName()).build();
    }

    public static com.mikerusoft.euroleague.model.Tournament convertM(com.mikerusoft.euroleague.entities.mongo.Tournament source) {
        if (source == null)
            return null;
        return com.mikerusoft.euroleague.model.Tournament.builder()
                .id(Utils.deNull(source.getId(), ObjectId::toHexString)).tournName(source.getName()).build();
    }

    public static com.mikerusoft.euroleague.entities.mongo.Tournament convertM(com.mikerusoft.euroleague.model.Tournament source) {
        if (source == null)
            return null;
        return com.mikerusoft.euroleague.entities.mongo.Tournament.builder()
                .id(new ObjectId(source.getId())).name(source.getTournName()).build();
    }

    public static com.mikerusoft.euroleague.model.Command convertM(com.mikerusoft.euroleague.entities.mongo.Command source) {
        if (source == null)
            return null;
        return com.mikerusoft.euroleague.model.Command.builder()
                .id(Utils.deNull(source.getId(), ObjectId::toHexString)).commandName(source.getName()).build();
    }

    public static com.mikerusoft.euroleague.entities.mongo.Command convertM(com.mikerusoft.euroleague.model.Command source) {
        if (source == null)
            return null;
        return com.mikerusoft.euroleague.entities.mongo.Command.builder()
                .id(new ObjectId(source.getId())).name(source.getCommandName()).build();
    }

    public static com.mikerusoft.euroleague.model.Tournament convert(Tournament source) {
        if (source == null)
            return null;
        return com.mikerusoft.euroleague.model.Tournament.builder()
                .id(Utils.toStringWithDeNull(source.getId())).tournName(source.getTournName()).build();
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

    public static com.mikerusoft.euroleague.model.CommandQuarterStat convertM(com.mikerusoft.euroleague.entities.mongo.CommandQuarterStat stat) {
        if (stat == null)
            return null;
        return com.mikerusoft.euroleague.model.CommandQuarterStat.builder()
                .attempts1(stat.getAttempts1())
                .points1(stat.getPoints1())
                .attempts2(stat.getAttempts2())
                .points2(stat.getPoints2())
                .attempts3(stat.getAttempts3())
                .points3(stat.getPoints3())
                .quarter(stat.getQuarter().getDisplay())
            .build();
    }

    public static com.mikerusoft.euroleague.entities.mongo.CommandQuarterStat convertM(com.mikerusoft.euroleague.model.CommandQuarterStat stat) {
        if (stat == null)
            return null;
        return com.mikerusoft.euroleague.entities.mongo.CommandQuarterStat.builder()
                .attempts1(stat.getAttempts1())
                .points1(stat.getPoints1())
                .attempts2(stat.getAttempts2())
                .points2(stat.getPoints2())
                .attempts3(stat.getAttempts3())
                .points3(stat.getPoints3())
                .quarter(Quarter.byDisplayName(stat.getQuarter()))
            .build();
    }

    public static com.mikerusoft.euroleague.model.CommandMatchStat convertM(com.mikerusoft.euroleague.entities.mongo.CommandMatchStat stat) {
        if (stat == null)
            return null;
        return com.mikerusoft.euroleague.model.CommandMatchStat.builder()
                .command(convertM(stat.getCommand()))
                .score(stat.getScore())
                .reboundsDefense(stat.getReboundsDefense())
                .reboundsOffense(stat.getReboundsOffense())
                .assists(stat.getAssists())
                .foulsDefense(stat.getFoulsDefense())
                .more10Points(stat.getMore10Points())
                .playerMaxPointsName(stat.getPlayerMaxPointsName())
                .playerMaxPointsScore(stat.getPlayerMaxPointsScore())
                .maxLead(stat.getMaxLead())
                .maxLeadQuarter(stat.getMaxLeadQuarter().getDisplay())
                .scoreStart5Score(stat.getScoreStart5Score())
                .scoreBenchScore(stat.getScoreBenchScore())
                .steals(stat.getSteals())
                .turnovers(stat.getTurnovers())
                .secondChanceAttempt(stat.getSecondChanceAttempt())
                .quarterStats(
                    Optional.ofNullable(stat.getQuarterStats()).orElseGet(ArrayList::new)
                    .stream().sorted(Comparator.comparingInt(o -> o.getQuarter().getOrder()))
                    .map(Converter::convertM).collect(Collectors.toList())
                )
            .build();
    }

    public static com.mikerusoft.euroleague.entities.mongo.CommandMatchStat convertM(com.mikerusoft.euroleague.model.CommandMatchStat stat) {
        if (stat == null)
            return null;
        return com.mikerusoft.euroleague.entities.mongo.CommandMatchStat.builder()
                .command(convertM(stat.getCommand()))
                .score(stat.getScore())
                .reboundsDefense(stat.getReboundsDefense())
                .reboundsOffense(stat.getReboundsOffense())
                .assists(stat.getAssists())
                .foulsDefense(stat.getFoulsDefense())
                .more10Points(stat.getMore10Points())
                .playerMaxPointsName(stat.getPlayerMaxPointsName())
                .playerMaxPointsScore(stat.getPlayerMaxPointsScore())
                .maxLead(stat.getMaxLead())
                .maxLeadQuarter(Quarter.byDisplayName(stat.getMaxLeadQuarter()))
                .scoreStart5Score(stat.getScoreStart5Score())
                .scoreBenchScore(stat.getScoreBenchScore())
                .steals(stat.getSteals())
                .turnovers(stat.getTurnovers())
                .secondChanceAttempt(stat.getSecondChanceAttempt())
                .quarterStats(
                        Optional.ofNullable(stat.getQuarterStats()).orElseGet(ArrayList::new)
                                .stream().map(Converter::convertM)
                                .sorted(Comparator.comparingInt(o -> o.getQuarter().getOrder()))
                                .collect(Collectors.toList())
                )
                .build();
    }



}
