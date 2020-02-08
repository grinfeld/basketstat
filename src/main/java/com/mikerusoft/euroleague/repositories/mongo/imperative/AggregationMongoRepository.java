package com.mikerusoft.euroleague.repositories.mongo.imperative;

import com.mikerusoft.euroleague.model.Aggregation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapreduce.MapReduceResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Repository
public class AggregationMongoRepository implements AggregationRepository<String> {

    private MongoTemplate mongoTemplate;

    public AggregationMongoRepository(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public List<Aggregation> aggregate(String season, String tournId, int games, String field, int top) {

        MapReduceResults<Result> results = mongoTemplate.mapReduce
                (Query.query(new Criteria("season").is(season).and("tournament._id").is(new ObjectId(tournId))),
            "matches",
                "function() {" +
                            "var result1 = {'date': this.date, 'timestamp': this.date.getTime(), 'command': this.homeCommand.command.name, 'field': '" + field + "', 'value': this.homeCommand." + field + ", 'count': 1, 'home': true};" +
                            "var result2 = {'date': this.date, 'timestamp': this.date.getTime(), 'command': this.awayCommand.command.name, 'field': '" + field + "', 'value': this.awayCommand." + field + ", 'count': 1, 'home': false};" +
                            "emit(this.season + '_' + this.tournament._id + '_' + this.homeCommand.command.name, result1);" +
                            "emit(this.season + '_' + this.tournament._id + '_' + this.awayCommand.command.name, result2);" +
                            // since MongoDB reduce doesn't call reduce if _id appears only once - let's do this patch with adding null value always
                            "emit(this.season + '_' + this.tournament._id + '_' + this.homeCommand.command.name, null);" +
                            "emit(this.season + '_' + this.tournament._id + '_' + this.awayCommand.command.name, null);" +
                        "}",
                        "function(id, results) {" +
                            "var filteredResults = [];" +
                            "for (var i=0; i<results.length; i++) { if (results[i]) { filteredResults.push(results[i]); } }" +
                            "return {'results':filteredResults};" +
                        "}",
                Result.class);

        return StreamSupport.stream(results.spliterator(), false)
            .map(Result::getValue).map(map -> map.get("results")).filter(Objects::nonNull)
            .map(list -> aggregateDataByField(list, games, field)).filter(Objects::nonNull)
            .sorted(longToComparatorReverse(Aggregation::getAggregatedValue))
            .limit(top)
        .collect(Collectors.toList());
    }

    private Aggregation aggregateDataByField(List<SingleResult> list, int games, String field) {
        if (list == null || list.isEmpty())
            return null;
        return list.stream().sorted(longToComparatorReverse(SingleResult::getTimestamp))
                .limit(games).filter(r -> r.field.equals(field))
            .collect(AggregationCollector.collector());
    }

    private static <C> Comparator<C> longToComparatorReverse(Function<C, Long> convert) {
        return (o1, o2) -> new Long(convert.apply(o2) - convert.apply(o1)).intValue();
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    static class SingleResult {
        private String command;
        private long timestamp;
        private String date;
        private String field;
        private long value;
        private int count;
        private boolean home;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static class Result {
        private String _id;
        private Map<String, List<SingleResult>> value;
    }

    private static class AggregationCollector implements Collector<SingleResult, Aggregation, Aggregation> {

        public static AggregationCollector collector() { return INSTANCE; }

        private static AggregationCollector INSTANCE = new AggregationCollector();

        private AggregationCollector() {}

        @Override
        public Supplier<Aggregation> supplier() {
            return Aggregation.builder()::build;
        }

        @Override
        public BiConsumer<Aggregation, SingleResult> accumulator() {
            return AggregationCollector::fillAggregation;
        }

        private static void fillAggregation(Aggregation aggr, SingleResult singleResult) {
            aggr.setField(singleResult.getField());
            aggr.setCommand(singleResult.getCommand());
            aggr.setAggregatedValue(singleResult.value);
        }

        @Override
        public BinaryOperator<Aggregation> combiner() {
            return (aggr, aggr1) -> aggr.toBuilder().command(aggr.getCommand()).aggregatedValue(aggr.getAggregatedValue() + aggr1.getAggregatedValue()).build();
        }

        @Override
        public Function<Aggregation, Aggregation> finisher() {
            return Function.identity();
        }

        @Override
        public Set<Characteristics> characteristics() {
            return Collections.unmodifiableSet(EnumSet.of(Collector.Characteristics.IDENTITY_FINISH));
        }
    }
}
