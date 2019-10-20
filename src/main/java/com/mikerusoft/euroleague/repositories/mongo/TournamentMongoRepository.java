package com.mikerusoft.euroleague.repositories.mongo;

import com.mikerusoft.euroleague.entities.mongo.Tournament;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

public interface TournamentMongoRepository extends CrudRepository<Tournament, String>, QuerydslPredicateExecutor<Tournament> {
}
