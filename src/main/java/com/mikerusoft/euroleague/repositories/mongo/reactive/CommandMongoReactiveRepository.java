package com.mikerusoft.euroleague.repositories.mongo.reactive;

import com.mikerusoft.euroleague.entities.mongo.Command;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface CommandMongoReactiveRepository extends ReactiveCrudRepository<Command, String> {
    Flux<Command> findAllByName(String name);
}
