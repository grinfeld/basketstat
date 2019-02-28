package com.mikerusoft.euroleague.modelToEntityConvertor;

import com.mikerusoft.euroleague.entities.Command;
import com.mikerusoft.euroleague.entities.Result;
import com.mikerusoft.euroleague.entities.Tournament;

public class Converter {

    private Converter() {}

    public static Command convert(com.mikerusoft.euroleague.model.Command source) {
        return Command.builder().id(source.getId()).commandName(source.getCommandName()).build();
    }

    public static Tournament convert(com.mikerusoft.euroleague.model.Tournament source) {
        return Tournament.builder().id(source.getId()).tournName(source.getTournName()).build();
    }

    public static com.mikerusoft.euroleague.model.Command convert(Command source) {
        return com.mikerusoft.euroleague.model.Command.builder()
                .id(source.getId()).commandName(source.getCommandName()).build();
    }

    public static com.mikerusoft.euroleague.model.Tournament convert(Tournament source) {
        return com.mikerusoft.euroleague.model.Tournament.builder()
                .id(source.getId()).tournName(source.getTournName()).build();
    }

    public static Result convert(com.mikerusoft.euroleague.model.Result source) {
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
}
