package com.mikerusoft.euroleague.repositories.mysql;

import com.mikerusoft.euroleague.entities.mysql.Tournament;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TournamentRepository extends JpaRepository<Tournament, Integer> {
}
