package com.mikerusoft.euroleague.repositories.mongo.imperative;

import com.mikerusoft.euroleague.entities.mongo.Command;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CommandMongoRepository extends CrudRepository<Command, String> {
    List<Command> findAllByName(String name);
}
