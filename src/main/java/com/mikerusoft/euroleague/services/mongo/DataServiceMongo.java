package com.mikerusoft.euroleague.services.mongo;

import com.mikerusoft.euroleague.entities.mongo.Command;
import com.mikerusoft.euroleague.entities.mongo.CommandStat;
import com.mikerusoft.euroleague.entities.mongo.Match;
import com.mikerusoft.euroleague.entities.mongo.Tournament;
import com.mikerusoft.euroleague.modelToEntityConvertor.Converter;
import com.mikerusoft.euroleague.repositories.mongo.imperative.CommandMongoRepository;
import com.mikerusoft.euroleague.repositories.mongo.imperative.MatchRepository;
import com.mikerusoft.euroleague.repositories.mongo.imperative.TournamentMongoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.mikerusoft.euroleague.utils.Utils.*;

@Service
public class DataServiceMongo {

    private static final int MAX_NUM_OF_RECORDS = 1000;

    private CommandMongoRepository commandRepository;
    private TournamentMongoRepository tournamentRepository;
    private MatchRepository matchRepository;

    public DataServiceMongo(CommandMongoRepository commandRepository, TournamentMongoRepository tournamentRepository,
                            MatchRepository matchRepository) {
        this.commandRepository = commandRepository;
        this.tournamentRepository = tournamentRepository;
        this.matchRepository = matchRepository;
    }

    private Command createCommand(String command) {
        return commandRepository.save(Command.builder().name(command).build());
    }

    public com.mikerusoft.euroleague.model.Command insertCommand(String command) {
        return Converter.convertM(createCommand(command));
    }

    public com.mikerusoft.euroleague.model.Command getCommand(String id) {
        return Converter.convertM(commandRepository.findById(id).orElse(null));
    }

    private Tournament createTournament(String tournament) {
        return tournamentRepository.save(Tournament.builder().name(tournament).build());
    }

    public com.mikerusoft.euroleague.model.Tournament insertTournament(String tournament) {
        return Converter.convertM(createTournament(tournament));
    }

    public com.mikerusoft.euroleague.model.Tournament getTournament(String id) {
        return Converter.convertM(tournamentRepository.findById(id).orElse(null));
    }

    public Match createMatch(Match match) {

        match = match.toBuilder().build();

        Tournament tournament = match.getTournament();
        assertNotNull(tournament);
        if (tournament.getId() == null) {
            assertNotEmptyTrimmed(tournament.getName());
            match.setTournament(createTournament(tournament.getName()));
        }

        CommandStat awayCommand = match.getAwayCommand();
        CommandStat homeCommand = match.getHomeCommand();

        assertNotNull(awayCommand);
        assertNotNull(awayCommand.getCommand());

        assertNotNull(homeCommand);
        assertNotNull(homeCommand.getCommand());

        if (awayCommand.getCommand().getId() == null) {
            assertNotEmptyTrimmed(awayCommand.getCommand().getName());
            Command command = createCommand(awayCommand.getCommand().getName());
            match.getAwayCommand().setCommand(command);
        }

        if (homeCommand.getCommand().getId() == null) {
            assertNotEmptyTrimmed(homeCommand.getCommand().getName());
            Command command = createCommand(homeCommand.getCommand().getName());
            match.getHomeCommand().setCommand(command);
        }

        return matchRepository.save(match.toBuilder().id(null).build());
    }

    public List<Match> findByCommandInTournamentAndSeason(String tournId, String season, String commandId, int records) {
        assertNumOfRecords(records);
        return matchRepository.findByCommandInTournamentAndSeason(tournId, season, commandId, records);
    }

    public List<Match> findByCommandsInTournamentAndSeason(String tournId, String season, String awayCommandId, String homeCommandId, int records) {
        assertNumOfRecords(records);
        return matchRepository.findMatchesByCommands(tournId, season, awayCommandId, homeCommandId, records);
    }

    private static void assertNumOfRecords(int records) {
        if (records <= 0)
            throw new IllegalArgumentException("Num of requested records couldn't be zero or negative: " + records);
        if (records > MAX_NUM_OF_RECORDS)
            throw new IllegalArgumentException("Num of records per request should be maximum " + MAX_NUM_OF_RECORDS + " and not " + records);
    }

}
