package com.mikerusoft.euroleague.repositories.mongo.imperative;

import com.mikerusoft.euroleague.model.Aggregation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.BsonDocument;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

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
        assertThat(score).isNotNull().isEmpty();
    }

    @Test
    void whenFieldIsScoreAndGames5Top2_expectedTop2ByScore() {
        List<Aggregation> score = aggregationRepository.aggregate("20192020", "5db2c90bfa7b6d37ff27a587", 3, "score", 2);
        assertThat(score).isNotNull().hasSize(2).containsExactlyInAnyOrderElementsOf(Arrays.asList(
           Aggregation.builder().command("Maccabi T-A").field("score").aggregatedValue(227).build(),
           Aggregation.builder().command("Real Madrid").field("score").aggregatedValue(219).build()
        ));
    }

    @Test
    void _expectedTop2By() {
        List<Aggregation> steals = aggregationRepository.aggregate("20192020", "5db2c90bfa7b6d37ff27a587", 4, "steals", 3);
        assertThat(steals).isNotNull().hasSize(3).containsExactlyInAnyOrderElementsOf(Arrays.asList(
                Aggregation.builder().command("Real Madrid").field("steals").aggregatedValue(52).build(),
                Aggregation.builder().command("Maccabi T-A").field("steals").aggregatedValue(49).build(),
                Aggregation.builder().command("Fenerbahce").field("steals").aggregatedValue(34).build()
        ));
    }

}