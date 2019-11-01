package com.mikerusoft.euroleague.services.mongo;

import com.mikerusoft.euroleague.model.Place;
import com.mikerusoft.euroleague.model.Quarter;
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

    private com.mikerusoft.euroleague.model.Command team1;
    private com.mikerusoft.euroleague.model.Command team2;
    private com.mikerusoft.euroleague.model.Command team3;
    private com.mikerusoft.euroleague.model.Command team4;
    private com.mikerusoft.euroleague.model.Command team5;
    private com.mikerusoft.euroleague.model.Tournament tournament;
    private com.mikerusoft.euroleague.model.Match match1;
    private com.mikerusoft.euroleague.model.Match match2;

    @BeforeAll
    void setup() {
        Date firstMatchDate = new Date(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(7));
        Date secondMatchDate = new Date(System.currentTimeMillis());
        team1 = service.insertCommand("Team1");
        team2 = service.insertCommand("Team2");
        team3 = service.insertCommand("Team3");
        team4 = service.insertCommand("Team4");
        team5 = service.insertCommand("Team5");
        tournament = service.insertTournament("Tournament");

        match1 = com.mikerusoft.euroleague.model.Match.builder()
                .date(firstMatchDate)
                .season("201920120")
                .tournament(tournament)
                .homeCommand(com.mikerusoft.euroleague.model.CommandMatchStat.builder().assists(5).maxLead(5).maxLeadQuarter(Quarter.FIRST.name()).command(team1).build())
                .awayCommand(com.mikerusoft.euroleague.model.CommandMatchStat.builder().assists(10).maxLead(1).maxLeadQuarter(Quarter.OT.name()).command(team2).build())
            .build();
        match1 = service.saveMatch(match1);
        match2 = com.mikerusoft.euroleague.model.Match.builder()
                .date(secondMatchDate)
                .season("201920120")
                .tournament(tournament)
                .homeCommand(com.mikerusoft.euroleague.model.CommandMatchStat.builder().assists(4).maxLead(3).maxLeadQuarter(Quarter.THIRD.name()).command(team3).build())
                .awayCommand(com.mikerusoft.euroleague.model.CommandMatchStat.builder().assists(2).maxLead(2).maxLeadQuarter(Quarter.SECOND.name()).command(team1).build())
            .build();
        match2 = service.saveMatch(match2);
    }

    @Nested
    class FindByCommandInTournamentAndSeasonTest {

        @Test
        @DisplayName("when team appears both in home and away in 2 matches, expected 2 matches returned where this team appears")
        void when2Matches_SearchingTeamAppearedIn2Matches() {
            List<com.mikerusoft.euroleague.model.Match> matches = service.findByCommandInTournamentAndSeason(tournament.getId(),
                    "201920120", team1.getId(), Place.all, 3);
            assertThat(matches).isNotNull().isNotEmpty().hasSize(2)
                    .containsExactly(match2, match1);
        }

        @Test
        @DisplayName("when team appears as away command in 1 match, expected 1 match returned where this team appears")
        void when2Matches_SearchingAwayTeamAppearedIn1Match() {
            List<com.mikerusoft.euroleague.model.Match> matches = service.findByCommandInTournamentAndSeason(tournament.getId(),
                    "201920120", team2.getId(), Place.all, 3);
            assertThat(matches).isNotNull().isNotEmpty().hasSize(1)
                    .containsExactly(match1);
        }

        @Test
        @DisplayName("when team appears as home command in 1 match, expected 1 match returned where this team appears")
        void when2Matches_SearchingHomeTeamAppearedIn1Matches() {
            List<com.mikerusoft.euroleague.model.Match> matches = service.findByCommandInTournamentAndSeason(tournament.getId(),
                    "201920120", team3.getId(), Place.all, 3);
            assertThat(matches).isNotNull().isNotEmpty().hasSize(1)
                    .containsExactly(match2);
        }

        @Test
        @DisplayName("when team does not appear in any match, expected no matches to be returned")
        void when2Matches_SearchingTeamInNoOneMatch() {
            List<com.mikerusoft.euroleague.model.Match> matches = service.findByCommandInTournamentAndSeason(tournament.getId(),
                    "201920120", team4.getId(), Place.all, 3);
            assertThat(matches).isNotNull().isEmpty();
        }

        @Test
        @DisplayName("when records parameter is negative, expected IllegalArgumentException")
        void when2MatchesAndRecordsIsNegative_expectingIllegalArgumentException() {
            assertThrows(
                    IllegalArgumentException.class,
                    () -> service.findByCommandInTournamentAndSeason(tournament.getId(),
                            "201920120", team4.getId(), Place.all, -1)
            );
        }
    }

    @Nested
    class FindByCommandsInTournamentAndSeason {

        @Test
        @DisplayName("when exists 1 match with 2 teams we searth with, expected 1 match returned")
        void when2Matches_SearchingMatchWith2Teams() {
            List<com.mikerusoft.euroleague.model.Match> matches = service.findByCommandsInTournamentAndSeason(tournament.getId(),
                    "201920120", team2.getId(), team1.getId(), 3);
            assertThat(matches).isNotNull().isNotEmpty().hasSize(1)
                        .containsExactly(match1);
        }

        @Test
        @DisplayName("when both teams in different matches, expected returned empty list")
        void when2Matches_TeamsInDifferentMatches_ButNotFound() {
            List<com.mikerusoft.euroleague.model.Match> matches = service.findByCommandsInTournamentAndSeason(tournament.getId(),
                    "201920120", team2.getId(), team3.getId(), 3);
            assertThat(matches).isNotNull().isEmpty();
        }

        @Test
        @DisplayName("when one teams is part of some match, but other team not in any match, expected returned empty list")
        void when2Matches_OneTeamNotInAnyMatch_ButNotFound() {
            List<com.mikerusoft.euroleague.model.Match> matches = service.findByCommandsInTournamentAndSeason(tournament.getId(),
                    "201920120", team2.getId(), team3.getId(), 3);
            assertThat(matches).isNotNull().isEmpty();
        }

        @Test
        @DisplayName("when one teams are not part of any match, expected returned empty list")
        void when2Matches_BothTeamsNotInAnyMatch_ButNotFound() {
            List<com.mikerusoft.euroleague.model.Match> matches = service.findByCommandsInTournamentAndSeason(tournament.getId(),
                    "201920120", team5.getId(), team4.getId(), 3);
            assertThat(matches).isNotNull().isEmpty();
        }
    }

}
