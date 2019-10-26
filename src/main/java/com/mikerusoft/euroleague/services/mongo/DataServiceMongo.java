package com.mikerusoft.euroleague.services.mongo;

import com.mikerusoft.euroleague.entities.mongo.Command;
import com.mikerusoft.euroleague.entities.mongo.CommandMatchStat;
import com.mikerusoft.euroleague.entities.mongo.Match;
import com.mikerusoft.euroleague.entities.mongo.Tournament;
import com.mikerusoft.euroleague.modelToEntityConvertor.ConverterI;
import com.mikerusoft.euroleague.repositories.mongo.imperative.CommandMongoRepository;
import com.mikerusoft.euroleague.repositories.mongo.imperative.MatchRepository;
import com.mikerusoft.euroleague.repositories.mongo.imperative.TournamentMongoRepository;
import com.mikerusoft.euroleague.services.DataService;
import com.mikerusoft.euroleague.utils.Utils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static com.mikerusoft.euroleague.utils.Utils.*;

@Service
public class DataServiceMongo implements DataService<String> {

    private static final int MAX_NUM_OF_RECORDS = 1000;

    private static final Class<com.mikerusoft.euroleague.model.Command> COMMAND_MODEL_CLASS = com.mikerusoft.euroleague.model.Command.class;
    private static final Class<com.mikerusoft.euroleague.model.Tournament> TOURN_MODEL_CLASS = com.mikerusoft.euroleague.model.Tournament.class;
    private static final Class<com.mikerusoft.euroleague.model.Match> MATCH_MODEL_CLASS = com.mikerusoft.euroleague.model.Match.class;

    private static final Class<com.mikerusoft.euroleague.entities.mongo.Match> MATCH_MONGO_CLASS = com.mikerusoft.euroleague.entities.mongo.Match.class;

    private CommandMongoRepository commandRepository;
    private TournamentMongoRepository tournamentRepository;
    private MatchRepository matchRepository;
    private ConverterI converter;

    public DataServiceMongo(CommandMongoRepository commandRepository, TournamentMongoRepository tournamentRepository,
                            MatchRepository matchRepository, ConverterI converter) {
        this.commandRepository = commandRepository;
        this.tournamentRepository = tournamentRepository;
        this.matchRepository = matchRepository;
        this.converter = converter;
    }

    private Command createCommand(String command) {
        return commandRepository.save(Command.builder().name(command).build());
    }

    @Override
    public com.mikerusoft.euroleague.model.Command insertCommand(String command) {
        return converter.convert(createCommand(command), COMMAND_MODEL_CLASS);
    }

    @Override
    public com.mikerusoft.euroleague.model.Command getCommand(String id) {
        return converter.convert(commandRepository.findById(id).orElse(null), COMMAND_MODEL_CLASS);
    }

    private Tournament createTournament(String tournament) {
        return tournamentRepository.save(Tournament.builder().name(tournament).build());
    }

    @Override
    public com.mikerusoft.euroleague.model.Tournament insertTournament(String tournament) {
        return converter.convert((createTournament(tournament)), TOURN_MODEL_CLASS);
    }

    @Override
    public com.mikerusoft.euroleague.model.Tournament getTournament(String id) {
        return converter.convert((tournamentRepository.findById(id).orElse(null)), TOURN_MODEL_CLASS);
    }

    @Override
    public com.mikerusoft.euroleague.model.Command updateCommand(com.mikerusoft.euroleague.model.Command command) {
        Optional<Command> cmd = commandRepository.findById(command.getId());
        // todo - continue
        return null;
    }

    @Override
    public com.mikerusoft.euroleague.model.Tournament updateTournament(com.mikerusoft.euroleague.model.Tournament tournament) {
        Optional<Tournament> tourn = tournamentRepository.findById(tournament.getId());
        // todo - continue
        return null;
    }

    @Override
    public List<com.mikerusoft.euroleague.model.Command> getCommands() {
        return StreamSupport.stream(commandRepository.findAll().spliterator(), false)
                .map(c -> converter.convert(c, COMMAND_MODEL_CLASS)).collect(Collectors.toList());
    }

    @Override
    public List<com.mikerusoft.euroleague.model.Tournament> getTournaments() {
        return StreamSupport.stream(tournamentRepository.findAll().spliterator(), false)
            .map(t -> converter.convert(t, TOURN_MODEL_CLASS)).collect(Collectors.toList());
    }

    @Override
    public void deleteCommand(String cmdId) {

    }

    @Override
    public void deleteTournament(String tournId) {

    }

    @Override
    public com.mikerusoft.euroleague.model.Match createMatch(com.mikerusoft.euroleague.model.Match match) {
        return converter.convert(createMatch(converter.convert(match.toBuilder().build(), MATCH_MONGO_CLASS)), MATCH_MODEL_CLASS);
    }

    public Match createMatch(Match match) {

        match = match.toBuilder().build();

        Tournament tournament = match.getTournament();
        assertNotNull(tournament);
        if (tournament.getId() == null) {
            assertNotEmptyTrimmed(tournament.getName());
            match.setTournament(createTournament(tournament.getName()));
        } else if (Utils.isEmptyTrimmed(tournament.getName())) {
            Tournament createTourn = tournamentRepository.findById(tournament.getId().toHexString()).orElse(null);
            assertNotNull(createTourn);
            match.setTournament(createTourn);
        }

        CommandMatchStat awayCommand = match.getAwayCommand();
        assertNotNull(awayCommand);
        assertNotNull(awayCommand.getCommand());

        if (awayCommand.getCommand().getId() == null) {
            assertNotEmptyTrimmed(awayCommand.getCommand().getName());
            Command command = createCommand(awayCommand.getCommand().getName());
            match.getAwayCommand().setCommand(command);
        } else if (Utils.isEmptyTrimmed(awayCommand.getCommand().getName())) {
            Command command = commandRepository.findById(awayCommand.getCommand().getId().toHexString()).orElse(null);
            assertNotNull(command);
            match.getHomeCommand().setCommand(command);
        }


        CommandMatchStat homeCommand = match.getHomeCommand();
        assertNotNull(homeCommand);
        assertNotNull(homeCommand.getCommand());

        if (homeCommand.getCommand().getId() == null) {
            assertNotEmptyTrimmed(homeCommand.getCommand().getName());
            Command command = createCommand(homeCommand.getCommand().getName());
            match.getHomeCommand().setCommand(command);
        } else if (Utils.isEmptyTrimmed(homeCommand.getCommand().getName())) {
            Command command = commandRepository.findById(homeCommand.getCommand().getId().toHexString()).orElse(null);
            assertNotNull(command);
            match.getHomeCommand().setCommand(command);
        }

        return matchRepository.save(match.toBuilder().id(null).build());
    }

    @Override
    public com.mikerusoft.euroleague.model.Match getMatch(String matchId) {
        return matchRepository.findById(matchId).map(m -> converter.convert(m, MATCH_MODEL_CLASS)).orElse(null);
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
