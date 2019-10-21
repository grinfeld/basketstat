package com.mikerusoft.euroleague.repositories.mongo.reactive;

import com.mikerusoft.euroleague.entities.mongo.Match;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface MatchReactiveRepository extends ReactiveCrudRepository<Match, String> {

    Flux<Match> findAllBySeason(String season);

    @Query("{'awayCommand.command.id': ?0}")
    Flux<Match> findAllByAwayCommand(String commandId);

    @Query("{'homeCommand.command.id': ?0}")
    Flux<Match> findAllByHomeCommand(String commandId);

    @Query("{'tournament.id': ?0}")
    Flux<Match> findAllByTournamentId(String tournId);
}
