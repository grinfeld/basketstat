package com.mikerusoft.euroleague.repositories.mongo.imperative;

import com.mikerusoft.euroleague.entities.mongo.Match;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.stream.Collectors;

public interface MatchRepository extends CrudRepository<Match, String> {

    List<Match> findAllBySeason(String season);

    @Query(
        value = "{'tournament.id': ?0, 'season': ?1, 'homeCommand.command.id': ?2}",
        sort = "{'date': -1}"
    )
    List<Match> findByHomeCommandInTournamentAndSeason(String tournId, String season, String commandId, Pageable pageable);

    @Query(
        value = "{'tournament.id': ?0, 'season': ?1, 'awayCommand.command.id': ?2}",
        sort = "{'date': -1}"
    )
    List<Match> findByAwayCommandInTournamentAndSeason(String tournId, String season, String commandId, Pageable pageable);

    default List<Match> findByCommandInTournamentAndSeason(String tournId, String season, String commandId, int records) {
        PageRequest pageable = PageRequest.of(0, records);
        List<Match> awayMatches = findByAwayCommandInTournamentAndSeason(tournId, season, commandId, pageable);
        List<Match> homeMatches = findByHomeCommandInTournamentAndSeason(tournId, season, commandId, pageable);

        awayMatches.addAll(homeMatches);
                                    // we need reverse order - a.k.a. DESC
        return awayMatches.stream().sorted((o1, o2) -> o2.getDate().compareTo(o1.getDate()))
                .limit(records).collect(Collectors.toList());
    }

    @Query(
            value = "{'tournament.id': ?0, 'season': ?1, 'awayCommand.command.id': ?2, 'homeCommand.command.id': ?3}",
            sort = "{'date': -1}"
    )
    List<Match> findMatchesByCommands(String tournId, String season, String homeCommand, String awayCommand, Pageable pageable);

    default List<Match> findMatchesByCommands(String tournId, String season, String homeCommand, String awayCommand, int records) {
        return findMatchesByCommands(tournId, season, homeCommand, awayCommand, PageRequest.of(0, records));
    }
}
