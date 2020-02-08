package com.mikerusoft.euroleague.repositories.mongo.imperative;

import com.mikerusoft.euroleague.model.Aggregation;

import java.util.List;

public interface AggregationRepository<T> {
    List<Aggregation> aggregate(String season, T tournId, int games, String field, int top);
}
