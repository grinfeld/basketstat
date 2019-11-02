package com.mikerusoft.euroleague.modelToEntityConvertor;

import com.mikerusoft.euroleague.entities.mysql.*;
import com.mikerusoft.euroleague.model.Quarter;
import com.mikerusoft.euroleague.utils.Utils;
import org.bson.types.ObjectId;

import java.sql.Date;
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
                .id(Utils.deNullString(source.getId(), ObjectId::toHexString)).tournName(source.getName()).build();
    }

    public static com.mikerusoft.euroleague.entities.mongo.Tournament convertM(com.mikerusoft.euroleague.model.Tournament source) {
        if (source == null)
            return null;
        return com.mikerusoft.euroleague.entities.mongo.Tournament.builder()
                .id(Utils.deNullObject(source, s -> s != null && !isEmptyTrimmed(s.getId()) ? new ObjectId(s.getId()) : null))
                .name(Utils.deNullString(source, s -> s != null && !isEmptyTrimmed(s.getTournName()) ? s.getTournName() : null)).build();
    }

    public static com.mikerusoft.euroleague.model.Command convertM(com.mikerusoft.euroleague.entities.mongo.Command source) {
        if (source == null)
            return null;
        return com.mikerusoft.euroleague.model.Command.builder()
                .id(Utils.deNullString(source.getId(), ObjectId::toHexString)).commandName(source.getName()).build();
    }

    public static com.mikerusoft.euroleague.entities.mongo.Command convertM(com.mikerusoft.euroleague.model.Command source) {
        if (source == null)
            return null;
        return com.mikerusoft.euroleague.entities.mongo.Command.builder()
                .id(Utils.deNullObject(source, s -> s != null && !isEmptyTrimmed(s.getId()) ? new ObjectId(s.getId()) : null))
                .name(Utils.deNullString(source, s -> s != null && !isEmptyTrimmed(s.getCommandName()) ? s.getCommandName() : null)).build();
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
            .date(Optional.ofNullable(source.getDate()).map(d -> new Date(d.getTime())).orElse(null))
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

    public static com.mikerusoft.euroleague.model.Match convertM(com.mikerusoft.euroleague.entities.mongo.Match source) {
        if (source == null)
            return null;
        return com.mikerusoft.euroleague.model.Match.builder()
                .season(source.getSeason())
                .date(source.getDate())
                .tournament(convertM(source.getTournament()))
                .hasOvertime(source.isHasOvertime())
                .awayCommand(convertM(source.getAwayCommand()))
                .homeCommand(convertM(source.getHomeCommand()))
                .id(Utils.deNullString(source.getId(), ObjectId::toHexString))
            .build();
    }

    public static com.mikerusoft.euroleague.entities.mongo.Match convertM(com.mikerusoft.euroleague.model.Match source) {
        if (source == null)
            return null;
        return com.mikerusoft.euroleague.entities.mongo.Match.builder()
                .season(source.getSeason())
                .date(source.getDate())
                .hasOvertime(source.isHasOvertime())
                .tournament(convertM(source.getTournament()))
                .awayCommand(convertM(source.getAwayCommand()))
                .homeCommand(convertM(source.getHomeCommand()))
                .id(!isEmptyTrimmed(source.getId()) ? new ObjectId(source.getId()) : null)
            .build();
    }

    public static com.mikerusoft.euroleague.model.CommandQuarterStat convertM(com.mikerusoft.euroleague.entities.mongo.CommandQuarterStat source) {
        if (source == null)
            return null;
        return com.mikerusoft.euroleague.model.CommandQuarterStat.builder()
                .score(source.getScore())
                .quarter(source.getQuarter().name())
            .build();
    }

    public static com.mikerusoft.euroleague.entities.mongo.CommandQuarterStat convertM(com.mikerusoft.euroleague.model.CommandQuarterStat source) {
        if (source == null)
            return null;
        return com.mikerusoft.euroleague.entities.mongo.CommandQuarterStat.builder()
                .score(source.getScore())
                .quarter(Quarter.valueOf(source.getQuarter()))
            .build();
    }

    public static com.mikerusoft.euroleague.model.CommandMatchStat convertM(com.mikerusoft.euroleague.entities.mongo.CommandMatchStat source) {
        if (source == null)
            return null;
        return com.mikerusoft.euroleague.model.CommandMatchStat.builder()
                .command(convertM(source.getCommand()))
                .score(source.getScore())
                .reboundsDefense(source.getReboundsDefense())
                .reboundsOffense(source.getReboundsOffense())
                .assists(source.getAssists())
                .foulsDefense(source.getFoulsDefense())
                .more10Points(source.getMore10Points())
                .playerMaxPointsName(source.getPlayerMaxPointsName())
                .playerMaxPointsScore(source.getPlayerMaxPointsScore())
                .maxLead(source.getMaxLead())
                .maxLeadQuarter(source.getMaxLead() > 0 && source.getMaxLeadQuarter() != null ? source.getMaxLeadQuarter().name() : null)
                .scoreStart5Score(source.getScoreStart5Score())
                .scoreBenchScore(source.getScoreBenchScore())
                .steals(source.getSteals())
                .turnovers(source.getTurnovers())
                .secondChancePoints(source.getSecondChancePoints())
                .attempts1(source.getAttempts1())
                .attempts2(source.getAttempts2())
                .attempts3(source.getAttempts3())
                .points1(source.getPoints1())
                .points2(source.getPoints2())
                .points3(source.getPoints3())
                .quarterStats(
                    Optional.ofNullable(source.getQuarterStats()).orElseGet(ArrayList::new)
                    .stream().sorted(Comparator.comparingInt(o -> o.getQuarter().getOrder()))
                    .map(Converter::convertM).collect(Collectors.toList())
                )
            .build();
    }

    public static com.mikerusoft.euroleague.entities.mongo.CommandMatchStat convertM(com.mikerusoft.euroleague.model.CommandMatchStat source) {
        if (source == null)
            return null;
        return com.mikerusoft.euroleague.entities.mongo.CommandMatchStat.builder()
                .command(convertM(source.getCommand()))
                .score(source.getScore())
                .reboundsDefense(source.getReboundsDefense())
                .reboundsOffense(source.getReboundsOffense())
                .assists(source.getAssists())
                .foulsDefense(source.getFoulsDefense())
                .more10Points(source.getMore10Points())
                .playerMaxPointsName(source.getPlayerMaxPointsName())
                .playerMaxPointsScore(source.getPlayerMaxPointsScore())
                .maxLead(source.getMaxLead())
                .maxLeadQuarter(Quarter.valueOf(source.getMaxLeadQuarter()))
                .scoreStart5Score(source.getScoreStart5Score())
                .scoreBenchScore(source.getScoreBenchScore())
                .steals(source.getSteals())
                .turnovers(source.getTurnovers())
                .secondChancePoints(source.getSecondChancePoints())
                .attempts1(source.getAttempts1())
                .attempts2(source.getAttempts2())
                .attempts3(source.getAttempts3())
                .points1(source.getPoints1())
                .points2(source.getPoints2())
                .points3(source.getPoints3())
                .quarterStats(
                    Optional.ofNullable(source.getQuarterStats()).orElseGet(ArrayList::new)
                        .stream().map(Converter::convertM)
                        .sorted(Comparator.comparingInt(o -> o.getQuarter().getOrder()))
                        .collect(Collectors.toList())
            ).build();
    }
}
