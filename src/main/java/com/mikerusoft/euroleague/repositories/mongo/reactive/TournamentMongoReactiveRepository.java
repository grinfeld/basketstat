package com.mikerusoft.euroleague.repositories.mongo.reactive;

import com.mikerusoft.euroleague.entities.mongo.Tournament;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface TournamentMongoReactiveRepository extends ReactiveCrudRepository<Tournament, String> {
    Flux<Tournament> findAllByName(String name);
}
