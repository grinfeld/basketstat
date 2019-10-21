package com.mikerusoft.euroleague.repositories.mongo.imperative;

import com.mikerusoft.euroleague.entities.mongo.Tournament;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface TournamentMongoRepository extends CrudRepository<Tournament, String> {
    List<Tournament> findAllByName(String name);
}
