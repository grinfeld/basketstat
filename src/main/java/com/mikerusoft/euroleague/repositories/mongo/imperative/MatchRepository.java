package com.mikerusoft.euroleague.repositories.mongo.imperative;

import com.mikerusoft.euroleague.entities.mongo.Match;
import com.mikerusoft.euroleague.model.Place;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.ArrayList;
import java.util.Date;
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
        value = "{'tournament.id': ?0, 'season': ?1, 'date': ?2, 'homeCommand.command.id': ?3, 'awayCommand.command.id': ?4}"
    )
    Match findByMatchInTournamentAndSeasonWithDate(String tournId, String season, Date date, String commandId1, String commandId2);

    @Query(
        value = "{'tournament.id': ?0, 'season': ?1, 'awayCommand.command.id': ?2}",
        sort = "{'date': -1}"
    )
    List<Match> findByAwayCommandInTournamentAndSeason(String tournId, String season, String commandId, Pageable pageable);

    default List<Match> findByCommandInTournamentAndSeason(String tournId, String season, String commandId, Place place, int records) {
        PageRequest pageable = PageRequest.of(0, records);
        List<Match> awayMatches = place == Place.home ? new ArrayList<>(0) :
                findByAwayCommandInTournamentAndSeason(tournId, season, commandId, pageable);
        List<Match> homeMatches = place == Place.away ? new ArrayList<>(0) :
                findByHomeCommandInTournamentAndSeason(tournId, season, commandId, pageable);

        List<Match> showMatches = new ArrayList<>();
        showMatches.addAll(homeMatches);
        showMatches.addAll(awayMatches);

                                    // we need reverse order - a.k.a. DESC
        return showMatches.stream().sorted((o1, o2) -> o2.getDate().compareTo(o1.getDate()))
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
