package com.mikerusoft.euroleague.repositories.mongo;

import com.mikerusoft.euroleague.entities.mongo.Command;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CommandMongoRepository extends CrudRepository<Command, String>, QuerydslPredicateExecutor<Command> {
    List<Command> findAllByName(String name);
}
