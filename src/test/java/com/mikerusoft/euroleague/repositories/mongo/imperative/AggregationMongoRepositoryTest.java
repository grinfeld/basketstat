package com.mikerusoft.euroleague.repositories.mongo.imperative;

import com.mikerusoft.euroleague.model.Aggregation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.BsonDocument;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapreduce.MapReduceResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.StreamSupport;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Profile("test")
@TestInstance(PER_CLASS)
class AggregationMongoRepositoryTest {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private AggregationRepository<String> aggregationRepository;

    @BeforeAll
    void setup() throws Exception {
        Path commands = Paths.get(ClassLoader.getSystemResource("commands.json").getFile());
        Path matches = Paths.get(ClassLoader.getSystemResource("matches.json").getFile());
        Path tourns = Paths.get(ClassLoader.getSystemResource("tournaments.json").getFile());

        Files.lines(tourns).map(BsonDocument::parse).forEach(d -> mongoTemplate.insert(d, "tournaments"));
        Files.lines(commands).map(BsonDocument::parse).forEach(d -> mongoTemplate.insert(d, "commands"));
        Files.lines(matches).map(BsonDocument::parse).forEach(d -> mongoTemplate.insert(d, "matches"));
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Result {
        private String _id;
        private Map<String, List<Object>> value;
    }

    @Test
    void whenInputTournamentDoesNotExist_expectedEmptyList() {
        List<Aggregation> score = aggregationRepository.aggregate("20192020", "5db2c90bfa7b6d37ff27a512", 5, "score", 2);
        assertThat(score).isNotNull().isEmpty();
    }

    @Test
    void whenInputSeasonDoesNotExist_expectedEmptyList() {
        List<Aggregation> score = aggregationRepository.aggregate("20112020", "5db2c90bfa7b6d37ff27a587", 5, "score", 2);
        System.out.println(score);
    }

    @Test
    void testSomething() {
        List<Aggregation> score = aggregationRepository.aggregate("20192020", "5db2c90bfa7b6d37ff27a587", 5, "score", 2);
        System.out.println(score);
    }

    @Test
    void testme() {
//        GroupOperation home = group("homeCommand.command.name").sum("homeCommand.score").as("homeScore");
//        //GroupOperation away = group("awayCommand.command.name").sum("awayCommand.score").as("awayScore");
//        SortOperation sort = sort(Sort.by(Sort.Direction.DESC, "homeScore"));
//        //MatchOperation filterStates = match(new Criteria("season").is("20192020"));//.and("tournament.name").is("Eurolegue"));
//        Aggregation aggregation = newAggregation(home, sort);
//        AggregationResults<Document> matches = mongoTemplate.aggregate(aggregation, "matches", Document.class);
//        StreamSupport.stream(matches.spliterator(), false).forEach(t -> System.out.println(t));

        String season = "20192020";
        int count = 2;

        MapReduceResults<Result> results = mongoTemplate.mapReduce(
                Query.query(new Criteria("season").is(season).and("tournament._id").is(new ObjectId("5db2c90bfa7b6d37ff27a587"))), "matches",
    "function() {" +
                    "var result1 = {'date': this.date, 'timestamp': this.date.getTime(), 'name': this.homeCommand.command.name, 'score': this.homeCommand.score, 'reboundsDefense': this.homeCommand.reboundsDefense, 'reboundsOffense': this.homeCommand.reboundsOffense, 'assists': this.homeCommand.assists, 'count': 1, 'home': true};" +
                    "var result2 = {'date': this.date, 'timestamp': this.date.getTime(), 'name': this.awayCommand.command.name, 'score': this.awayCommand.score, 'reboundsDefense': this.awayCommand.reboundsDefense, 'reboundsOffense': this.awayCommand.reboundsOffense, 'assists': this.awayCommand.assists, 'count': 1, 'home': false};" +
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

        String str = "function() {" +
                "var result1 = {'date': this.date, 'timestamp': this.date.getTime(), 'command': this.homeCommand.command.name, 'field': 'score', 'value': this.homeCommand.score, 'count': 1, 'home': true};" +
                "var result2 = {'date': this.date, 'timestamp': this.date.getTime(), 'command': this.awayCommand.command.name, 'field': 'score', 'value': this.awayCommand.score, 'count': 1, 'home': false};" +
                "emit(this.season + '_' + this.tournament._id + '_' + this.homeCommand.command.name, result1);" +
                "emit(this.season + '_' + this.tournament._id + '_' + this.awayCommand.command.name, result2);" +
                "emit(this.season + '_' + this.tournament._id + '_' + this.homeCommand.command.name, null);" +
                "emit(this.season + '_' + this.tournament._id + '_' + this.awayCommand.command.name, null);" +
                "}";

        StreamSupport.stream(results.spliterator(), false).map(Result::getValue).map(v -> v.get("results")).filter(Objects::nonNull)
                .forEach(t -> System.out.println(t));
    }
}