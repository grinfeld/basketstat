package com.mikerusoft.euroleague.repositories;

import com.mikerusoft.euroleague.entities.Result;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResultRepository extends JpaRepository<Result, Integer> {

    @Query(value = "select r from Result r where r.command.id = ?1 order by r.date desc")
    List<Result> findResultsByCommandId(int commandId, Pageable pageable);

    @Query(value = "select r from Result r where r.tournament.id = ?1 AND r.command.id = ?2 order by r.date desc")
    List<Result> findResultsByCommandIdAndTournId(int torunId, int commandId, Pageable pageable);

    @Query(value = "select r from Result r where r.command.id = ?1 AND r.season = ?2 order by r.date desc")
    List<Result> findResultsByCommandIdAndSeason(int commandId, String season);

    @Query(value = "select r from Result r where r.tournament.id = ?1 AND r.command.id = ?2 AND r.season = ?3 order by r.date desc")
    List<Result> findResultsByCommandIdAndTournIdAndSeason(int torunId, int commandId, String season);

    @Query(value = "delete from Result r where r.command.id = ?1")
    @Modifying
    void deleteByCommandId(int cmdId);

    @Query(value = "delete from Result r where r.tournament.id = ?1")
    @Modifying
    void deleteByTournamentId(int torunId);
}
