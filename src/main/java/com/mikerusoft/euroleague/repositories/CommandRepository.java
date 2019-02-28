package com.mikerusoft.euroleague.repositories;

import com.mikerusoft.euroleague.entities.Command;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommandRepository extends JpaRepository<Command, Integer> {

}
