package com.mikerusoft.euroleague.repositories.mongo.imperative;

import com.mikerusoft.euroleague.model.CommandAggregation;

import java.util.List;

public interface AggregationRepository<T> {
    List<CommandAggregation> getTopCommands(String season, T tournId, int games, String field, int top);
}
