package com.mikerusoft.euroleague.repositories.mongo.imperative;

import com.mikerusoft.euroleague.model.CommandAggregation;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

@SpringBootTest
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

    @Test
    void whenInputTournamentDoesNotExist_expectedEmptyList() {
        List<CommandAggregation> score = aggregationRepository.getTopCommands("20192020", "5db2c90bfa7b6d37ff27a512", 5, "score", 2);
        assertThat(score).isNotNull().isEmpty();
    }

    @Test
    void whenInputSeasonDoesNotExist_expectedEmptyList() {
        List<CommandAggregation> score = aggregationRepository.getTopCommands("20112020", "5db2c90bfa7b6d37ff27a587", 5, "score", 2);
        assertThat(score).isNotNull().isEmpty();
    }

    @Test
    void whenFieldDoesNotExist_expectedEmptyList() {
        List<CommandAggregation> score = aggregationRepository.getTopCommands("20192020", "5db2c90bfa7b6d37ff27a587", 5, "dsgfsdgsd", 2);
        assertThat(score).isNotNull().isEmpty();
    }

    @Test
    void whenTopIsNegative_expectedEmptyList() {
        List<CommandAggregation> score = aggregationRepository.getTopCommands("20192020", "5db2c90bfa7b6d37ff27a587", 3, "score", -4);
        assertThat(score).isNotNull().isEmpty();
    }

    @Test
    void whenTopIsZero_expectedEmptyList() {
        List<CommandAggregation> score = aggregationRepository.getTopCommands("20192020", "5db2c90bfa7b6d37ff27a587", 3, "score", 0);
        assertThat(score).isNotNull().isEmpty();
    }

    @Test
    void whenGamesIsZero_expectedEmptyList() {
        List<CommandAggregation> score = aggregationRepository.getTopCommands("20192020", "5db2c90bfa7b6d37ff27a587", 0, "score", 4);
        assertThat(score).isNotNull().isEmpty();
    }

    @Test
    void whenGamesIsNegative_expectedEmptyList() {
        List<CommandAggregation> score = aggregationRepository.getTopCommands("20192020", "5db2c90bfa7b6d37ff27a587", -10, "score", 4);
        assertThat(score).isNotNull().isEmpty();
    }

    @Test
    void whenFieldIsScoreAndGames5Top2_expectedTop2ByScore() {
        List<CommandAggregation> score = aggregationRepository.getTopCommands("20192020", "5db2c90bfa7b6d37ff27a587", 3, "score", 2);
        assertThat(score).isNotNull().hasSize(2).containsExactlyInAnyOrderElementsOf(Arrays.asList(
           CommandAggregation.builder().command("Maccabi T-A").field("score").aggregatedValue(227d).build(),
           CommandAggregation.builder().command("Real Madrid").field("score").aggregatedValue(219d).build()
        ));
    }

    @Test
    void whenFieldStealsANdGames4AndTop3_expectedTop3BySteals() {
        List<CommandAggregation> steals = aggregationRepository.getTopCommands("20192020", "5db2c90bfa7b6d37ff27a587", 4, "steals", 3);
        assertThat(steals).isNotNull().hasSize(3).containsExactlyInAnyOrderElementsOf(Arrays.asList(
                CommandAggregation.builder().command("Real Madrid").field("steals").aggregatedValue(52d).build(),
                CommandAggregation.builder().command("Maccabi T-A").field("steals").aggregatedValue(49d).build(),
                CommandAggregation.builder().command("Fenerbahce").field("steals").aggregatedValue(34d).build()
        ));
    }

}