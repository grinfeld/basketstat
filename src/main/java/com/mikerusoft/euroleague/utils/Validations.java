package com.mikerusoft.euroleague.utils;

import com.mikerusoft.euroleague.model.*;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.stream.Stream;

import static com.mikerusoft.euroleague.utils.Utils.assertNotNull;
import static com.mikerusoft.euroleague.utils.Utils.isEmptyTrimmed;

public class Validations {
    private Validations() {}

    public static void validateMatch(@ModelAttribute("currentMatch") Match match) {
        assertNotNull(match);
        validateTournament(match.getTournament());
        validateCommand(match.getHomeCommand(), "Home Command couldn't be empty");
        validateCommand(match.getAwayCommand(), "Away Command couldn't be empty");
        validateNotTheSameCommand(match.getHomeCommand().getCommand(), match.getAwayCommand().getCommand());
        validateMaxLead(match.getHomeCommand(), match.getAwayCommand());
        validateScore(match.getHomeCommand(), match.getAwayCommand());
    }

    private static void validateScore(CommandMatchStat homeCommand, CommandMatchStat awayCommand) {
        if (homeCommand.getScore() <= 0 && awayCommand.getScore() <= 0) {
            throw new IllegalArgumentException("Score should be positive");
        }

        int homeScore = homeCommand.getQuarterStats().stream().mapToInt(Validations::sumPoints).sum();
        if (homeScore != homeCommand.getScore()) {
            throw new IllegalArgumentException("Home team: Sum of all quarters score should be equal to game score");
        }

        int awayScore = awayCommand.getQuarterStats().stream().mapToInt(Validations::sumPoints).sum();
        if (awayScore != awayCommand.getScore()) {
            throw new IllegalArgumentException("Away team: Sum of all quarters score should be equal to game score");
        }

        boolean hasNegativeHome = homeCommand.getQuarterStats().stream()
            .flatMap(m -> Stream.of(m.getPoints1(), m.getPoints2(), m.getPoints3()))
            .mapToInt(i -> i).filter(i -> i >= 0).findAny().isPresent();
        if (hasNegativeHome) {
            throw new IllegalArgumentException("Home team: any score/attempts field shouldn't be negative");
        }

        boolean hasNegativeAway = awayCommand.getQuarterStats().stream()
            .flatMap(m -> Stream.of(m.getPoints1(), m.getPoints2(), m.getPoints3()))
            .mapToInt(i -> i).filter(i -> i >= 0).findAny().isPresent();
        if (hasNegativeAway) {
            throw new IllegalArgumentException("Away team: any score/attempts field shouldn't be negative");
        }

    }

    private static int sumPoints(CommandQuarterStat m) {
        return m.getPoints1() + m.getPoints2() * 2 + m.getPoints3() * 3;
    }

    private static void validateTournament(Tournament tournament) {
        assertNotNull(tournament, "Tournament couldn't be empty");
        if (isEmptyTrimmed(tournament.getId()) && isEmptyTrimmed(tournament.getTournName())) {
            throw new IllegalArgumentException("Tournament couldn't be empty");
        }
    }

    private static void validateMaxLead(CommandMatchStat homeCommand, CommandMatchStat awayCommand) {
        int homeMaxLead = homeCommand.getMaxLead();
        String homeMaxLeadQuarter = homeCommand.getMaxLeadQuarter();
        if (homeMaxLead > 0 && isEmptyTrimmed(homeMaxLeadQuarter)) {
            throw new IllegalArgumentException("Home team Quarter max lead shouldn't be empty");
        }

        int awayMaxLead = awayCommand.getMaxLead();
        String awayMaxLeadQuarter = awayCommand.getMaxLeadQuarter();
        if (awayMaxLead > 0 && isEmptyTrimmed(awayMaxLeadQuarter)) {
            throw new IllegalArgumentException("Away Team Quarter max lead shouldn't be empty");
        }
    }

    private static void validateNotTheSameCommand(Command command1, Command command2) {
        String message = "Home and Away commands should be different";
        if (!isEmptyTrimmed(command1.getId()) && !isEmptyTrimmed(command2.getId())) {
            if (command1.getId().trim().equalsIgnoreCase(command2.getId().trim())) {
                throw new IllegalArgumentException(message);
            }
        } else if (isEmptyTrimmed(command1.getId()) && isEmptyTrimmed(command2.getId())) {
            if (Utils.deNull(command1.getCommandName()).trim().equalsIgnoreCase(Utils.deNull(command2.getCommandName()).trim())) {
                throw new IllegalArgumentException(message);
            }
        }
    }

    private static void validateCommand(CommandMatchStat commandStat, String message) {
        assertNotNull(commandStat, message);
        Command command = commandStat.getCommand();
        assertNotNull(command, message);

        if (isEmptyTrimmed(command.getId()) && isEmptyTrimmed(command.getCommandName())) {
            throw new NullPointerException(message);
        }
    }
}
