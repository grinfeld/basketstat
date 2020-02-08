package com.mikerusoft.euroleague.repositories.mongo.imperative;

import com.mikerusoft.euroleague.entities.mongo.CommandMatchStat;
import com.mikerusoft.euroleague.model.Aggregation;
import com.mikerusoft.euroleague.utils.Utils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapreduce.MapReduceResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.lang.reflect.Field;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Repository
public class AggregationMongoRepository implements AggregationRepository<String> {

    private static Set<? extends Class<?>> supportedFieldTypes = Stream.of(Integer.class, Integer.TYPE, Long.class,
                Long.TYPE, Short.class, Short.TYPE, Byte.class, Byte.TYPE, Float.class, Float.TYPE, Double.class, Double.TYPE)
            .collect(Collectors.toMap(Function.identity(), Function.identity(), (k1, k2) -> k1)).keySet();

    private MongoTemplate mongoTemplate;
    private Set<String> supportedFields;

    public AggregationMongoRepository(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
        fillSupportedFields();
    }

    private void fillSupportedFields() {
        supportedFields = new HashSet<>();
        List<String> fields = Stream.of(CommandMatchStat.class.getFields())
                .filter(f -> supportedFieldTypes.contains(f.getType()))
                .map(Field::getName).collect(Collectors.toList());
        List<String> declaredFields = Stream.of(CommandMatchStat.class.getDeclaredFields())
                .filter(f -> supportedFieldTypes.contains(f.getType()))
                .map(Field::getName).collect(Collectors.toList());
        supportedFields.addAll(fields);
        supportedFields.addAll(declaredFields);
    }

    @Override
    public List<Aggregation> aggregate(String season, String tournId, int games, String field, int top) {
        if (top <= 0)
            return new ArrayList<>(0);
        if (games <=0 )
            return new ArrayList<>(0);
        if (!supportedFields.contains(field))
            return new ArrayList<>(0);

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
            .sorted(doubleToComparatorReverse(Aggregation::getAggregatedValue))
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

    private static <C> Comparator<C> doubleToComparatorReverse(Function<C, Double> convert) {
        return (o1, o2) -> new Double(convert.apply(o2) - convert.apply(o1)).intValue();
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
            // this "if" is useless, since map reduce made by command and field, but it makes more clear code when we aggregate
            if (belongsToAggregation(aggr, singleResult)) {
                if (Utils.isEmptyTrimmed(aggr.getField()))
                    aggr.setField(singleResult.getField());
                if (Utils.isEmptyTrimmed(aggr.getCommand()))
                    aggr.setCommand(singleResult.getCommand());
                aggr.setAggregatedValue(aggr.getAggregatedValue() + singleResult.value);
            }
        }

        private static boolean belongsToAggregation(Aggregation aggr, SingleResult singleResult) {
            // the first aggregation, so values are empty in aggregation object
            if (Utils.isEmptyTrimmed(aggr.getCommand()) && Utils.isEmptyTrimmed(aggr.getField())) return true;
            return singleResult.getCommand().equals(aggr.getCommand()) && singleResult.getField().equals(aggr.getField());
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
