package com.mikerusoft.euroleague.repositories.mongo;

import com.mikerusoft.euroleague.entities.mongo.Match;
import org.springframework.data.domain.Pageable;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface MatchRepository extends CrudRepository<Match, String>, QuerydslPredicateExecutor<Match> {
    List<Match> findAllBySeason(String season);
    List<Match> findBySeasonAndTournamentIdOrderByDateDesc(String season, String tournId, Pageable pageable);
}
