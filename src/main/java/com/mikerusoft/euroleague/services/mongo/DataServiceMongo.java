package com.mikerusoft.euroleague.services.mongo;

import com.mikerusoft.euroleague.entities.mongo.Command;
import com.mikerusoft.euroleague.entities.mongo.CommandStat;
import com.mikerusoft.euroleague.entities.mongo.Match;
import com.mikerusoft.euroleague.entities.mongo.Tournament;
import com.mikerusoft.euroleague.repositories.mongo.CommandMongoRepository;
import com.mikerusoft.euroleague.repositories.mongo.MatchRepository;
import com.mikerusoft.euroleague.repositories.mongo.TournamentMongoRepository;
import org.springframework.stereotype.Service;

import static com.mikerusoft.euroleague.utils.Utils.*;

@Service
public class DataServiceMongo {

    private CommandMongoRepository commandRepository;
    private TournamentMongoRepository tournamentRepository;
    private MatchRepository matchRepository;

    public DataServiceMongo(CommandMongoRepository commandRepository, TournamentMongoRepository tournamentRepository,
                            MatchRepository matchRepository) {
        this.commandRepository = commandRepository;
        this.tournamentRepository = tournamentRepository;
        this.matchRepository = matchRepository;
    }

    public Command createCommand(String command) {
        return commandRepository.save(Command.builder().name(command).build());
    }

    public Command findCommand(String id) {
        return commandRepository.findById(id).orElse(null);
    }

    public Tournament createTournament(String command) {
        return tournamentRepository.save(Tournament.builder().name(command).build());
    }

    public Tournament findTournament(String id) {
        return tournamentRepository.findById(id).orElse(null);
    }

    public Match createMatch(Match match) {

        match = match.toBuilder().build();

        Tournament tournament = match.getTournament();
        assertNotNull(tournament);
        if (tournament.getId() == null) {
            assertNotEmptyTrimmed(tournament.getName());
            match.setTournament(tournamentRepository.save(tournament));
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



}
