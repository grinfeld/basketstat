package com.mikerusoft.euroleague.modelToEntityConvertor;

import com.mikerusoft.euroleague.entities.mysql.*;
import com.mikerusoft.euroleague.model.Quarter;
import com.mikerusoft.euroleague.utils.Utils;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.mikerusoft.euroleague.utils.Utils.isEmptyTrimmed;

public class Converter {

    private Converter() {}

    public static Command convert(com.mikerusoft.euroleague.model.Command source) {
        if (source == null)
            return null;
        return Command.builder().id(Utils.parseIntWithEmptyToNull(source.getId())).commandName(source.getCommandName()).build();
    }

    public static Tournament convert(com.mikerusoft.euroleague.model.Tournament source) {
        if (source == null)
            return null;
        return Tournament.builder().id(Utils.parseIntWithEmptyToNull(source.getId())).tournName(source.getTournName()).build();
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
                .id(Utils.deNullObject(source, s -> s != null && isEmptyTrimmed(s.getId()) ? new ObjectId(s.getId()) : null))
                .name(Utils.deNull(source, s -> s != null && !isEmptyTrimmed(s.getTournName()) ? s.getTournName() : null)).build();
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
                .id(Utils.deNullObject(source, s -> s != null && isEmptyTrimmed(s.getId()) ? new ObjectId(s.getId()) : null))
                .name(Utils.deNull(source, s -> s != null && !isEmptyTrimmed(s.getCommandName()) ? s.getCommandName() : null)).build();
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

    public static com.mikerusoft.euroleague.model.Match convertM(com.mikerusoft.euroleague.entities.mongo.Match stat) {
        if (stat == null)
            return null;
        return com.mikerusoft.euroleague.model.Match.builder()
                .season(stat.getSeason())
                .date(stat.getDate())
                .tournament(convertM(stat.getTournament()))
                .awayCommand(convertM(stat.getAwayCommand()))
                .homeCommand(convertM(stat.getHomeCommand()))
                .id(Utils.deNull(stat.getId(), ObjectId::toHexString))
            .build();
    }

    public static com.mikerusoft.euroleague.entities.mongo.Match convertM(com.mikerusoft.euroleague.model.Match source) {
        if (source == null)
            return null;
        return com.mikerusoft.euroleague.entities.mongo.Match.builder()
                .season(source.getSeason())
                .date(source.getDate())
                .tournament(convertM(source.getTournament()))
                .awayCommand(convertM(source.getAwayCommand()))
                .homeCommand(convertM(source.getHomeCommand()))
                .id(Utils.deNullObject(source, s -> s != null && !isEmptyTrimmed(s.getId()) ? new ObjectId(s.getId()) : null))
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
                .quarter(Quarter.valueOf(stat.getQuarter()))
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
                ).build();
    }
}
