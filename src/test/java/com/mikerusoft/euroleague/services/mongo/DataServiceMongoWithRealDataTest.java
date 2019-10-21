package com.mikerusoft.euroleague.services.mongo;

import com.mikerusoft.euroleague.entities.mongo.Command;
import com.mikerusoft.euroleague.entities.mongo.CommandMatchStat;
import com.mikerusoft.euroleague.entities.mongo.Match;
import com.mikerusoft.euroleague.entities.mongo.Tournament;
import com.mikerusoft.euroleague.model.Quarter;
import com.mikerusoft.euroleague.modelToEntityConvertor.Converter;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

@SpringBootTest
@Profile("test")
@TestInstance(PER_CLASS)
class DataServiceMongoWithRealDataTest {

    @Autowired
    private DataServiceMongo service;

    private Command team1;
    private Command team2;
    private Command team3;
    private Command team4;
    private Command team5;
    private Tournament tournament;
    private Match match1;
    private Match match2;

    @BeforeAll
    void setup() {
        Date firstMatchDate = new Date(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(7));
        Date secondMatchDate = new Date(System.currentTimeMillis());
        team1 = Converter.convertM(service.insertCommand("Team1"));
        team2 = Converter.convertM(service.insertCommand("Team2"));
        team3 = Converter.convertM(service.insertCommand("Team3"));
        team4 = Converter.convertM(service.insertCommand("Team4"));
        team5 = Converter.convertM(service.insertCommand("Team5"));
        tournament = Converter.convertM(service.insertTournament("Tournament"));

        match1 = Match.builder()
                .date(firstMatchDate)
                .season("201920120")
                .tournament(tournament)
                .homeCommand(CommandMatchStat.builder().assists(5).maxLead(5).maxLeadQuarter(Quarter.FIRST).command(team1).build())
                .awayCommand(CommandMatchStat.builder().assists(10).maxLead(1).maxLeadQuarter(Quarter.OT).command(team2).build())
            .build();
        match1 = service.createMatch(match1);
        match2 = Match.builder()
                .date(secondMatchDate)
                .season("201920120")
                .tournament(tournament)
                .homeCommand(CommandMatchStat.builder().assists(4).maxLead(3).maxLeadQuarter(Quarter.THIRD).command(team3).build())
                .awayCommand(CommandMatchStat.builder().assists(2).maxLead(2).maxLeadQuarter(Quarter.SECOND).command(team1).build())
            .build();
        match2 = service.createMatch(match2);
    }

    @Nested
    class FindByCommandInTournamentAndSeasonTest {

        @Test
        @DisplayName("when team appears both in home and away in 2 matches, expected 2 matches returned where this team appears")
        void when2Matches_SearchingTeamAppearedIn2Matches() {
            List<Match> matches = service.findByCommandInTournamentAndSeason(tournament.getId().toHexString(),
                    "201920120", team1.getId().toHexString(), 3);
            assertThat(matches).isNotNull().isNotEmpty().hasSize(2)
                    .containsExactly(match2, match1);
        }

        @Test
        @DisplayName("when team appears as away command in 1 match, expected 1 match returned where this team appears")
        void when2Matches_SearchingAwayTeamAppearedIn1Match() {
            List<Match> matches = service.findByCommandInTournamentAndSeason(tournament.getId().toHexString(),
                    "201920120", team2.getId().toHexString(), 3);
            assertThat(matches).isNotNull().isNotEmpty().hasSize(1)
                    .containsExactly(match1);
        }

        @Test
        @DisplayName("when team appears as home command in 1 match, expected 1 match returned where this team appears")
        void when2Matches_SearchingHomeTeamAppearedIn1Matches() {
            List<Match> matches = service.findByCommandInTournamentAndSeason(tournament.getId().toHexString(),
                    "201920120", team3.getId().toHexString(), 3);
            assertThat(matches).isNotNull().isNotEmpty().hasSize(1)
                    .containsExactly(match2);
        }

        @Test
        @DisplayName("when team does not appear in any match, expected no matches to be returned")
        void when2Matches_SearchingTeamInNoOneMatch() {
            List<Match> matches = service.findByCommandInTournamentAndSeason(tournament.getId().toHexString(),
                    "201920120", team4.getId().toHexString(), 3);
            assertThat(matches).isNotNull().isEmpty();
        }

        @Test
        @DisplayName("when records parameter is negative, expected IllegalArgumentException")
        void when2MatchesAndRecordsIsNegative_expectingIllegalArgumentException() {
            assertThrows(
                    IllegalArgumentException.class,
                    () -> service.findByCommandInTournamentAndSeason(tournament.getId().toHexString(),
                            "201920120", team4.getId().toHexString(), -1)
            );
        }
    }

    @Nested
    class FindByCommandsInTournamentAndSeason {

        @Test
        @DisplayName("when exists 1 match with 2 teams we searth with, expected 1 match returned")
        void when2Matches_SearchingMatchWith2Teams() {
            List<Match> matches = service.findByCommandsInTournamentAndSeason(tournament.getId().toHexString(),
                    "201920120", team2.getId().toHexString(), team1.getId().toHexString(), 3);
            assertThat(matches).isNotNull().isNotEmpty().hasSize(1)
                        .containsExactly(match1);
        }

        @Test
        @DisplayName("when both teams in different matches, expected returned empty list")
        void when2Matches_TeamsInDifferentMatches_ButNotFound() {
            List<Match> matches = service.findByCommandsInTournamentAndSeason(tournament.getId().toHexString(),
                    "201920120", team2.getId().toHexString(), team3.getId().toHexString(), 3);
            assertThat(matches).isNotNull().isEmpty();
        }

        @Test
        @DisplayName("when one teams is part of some match, but other team not in any match, expected returned empty list")
        void when2Matches_OneTeamNotInAnyMatch_ButNotFound() {
            List<Match> matches = service.findByCommandsInTournamentAndSeason(tournament.getId().toHexString(),
                    "201920120", team2.getId().toHexString(), team3.getId().toHexString(), 3);
            assertThat(matches).isNotNull().isEmpty();
        }

        @Test
        @DisplayName("when one teams are not part of any match, expected returned empty list")
        void when2Matches_BothTeamsNotInAnyMatch_ButNotFound() {
            List<Match> matches = service.findByCommandsInTournamentAndSeason(tournament.getId().toHexString(),
                    "201920120", team5.getId().toHexString(), team4.getId().toHexString(), 3);
            assertThat(matches).isNotNull().isEmpty();
        }
    }

}
