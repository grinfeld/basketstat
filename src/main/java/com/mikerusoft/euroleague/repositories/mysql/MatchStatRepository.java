package com.mikerusoft.euroleague.repositories.mysql;

import com.mikerusoft.euroleague.entities.mysql.CommandMatchStat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MatchStatRepository extends JpaRepository<CommandMatchStat, Integer> {

    @Query(value = "select c from CommandMatchStat c WHERE c.match.tournament.id = ?1")
    List<CommandMatchStat> findByTournament(int tournId);

}
