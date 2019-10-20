package com.mikerusoft.euroleague.repositories.mongo;

import com.mikerusoft.euroleague.entities.mongo.Match;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

public interface MatchRepository extends CrudRepository<Match, String>, QuerydslPredicateExecutor<Match> {
}
