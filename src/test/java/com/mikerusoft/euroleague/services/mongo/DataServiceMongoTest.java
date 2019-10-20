package com.mikerusoft.euroleague.services.mongo;

import com.mikerusoft.euroleague.entities.mongo.Command;
import com.mikerusoft.euroleague.entities.mongo.CommandStat;
import com.mikerusoft.euroleague.entities.mongo.Match;
import com.mikerusoft.euroleague.entities.mongo.Tournament;
import com.mikerusoft.euroleague.model.Quarter;
import com.wix.mysql.EmbeddedMysql;
import com.wix.mysql.ScriptResolver;
import com.wix.mysql.config.MysqldConfig;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;
import java.util.Date;

import static com.wix.mysql.EmbeddedMysql.anEmbeddedMysql;
import static com.wix.mysql.config.Charset.UTF8;
import static com.wix.mysql.config.MysqldConfig.aMysqldConfig;
import static com.wix.mysql.distribution.Version.v5_6_23;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Profile("test")
class DataServiceMongoTest {

    @Autowired
    private DataServiceMongo service;

    @Nested
    class CreateMatch {

        @Test
        void whenHomeCommandNull_expectedNullPointerException() {
            Match match = Match.builder()
                    .date(new Date(System.currentTimeMillis()))
                    .season("201920120")
                    .tournament(Tournament.builder().name("tourn").build())
                    .awayCommand(CommandStat.builder().command(Command.builder().name("name").build()).build())
                .build();
            assertThrows(NullPointerException.class, () -> service.createMatch(match));
        }

        @Test
        void whenHomeCommandNameIsEmpty_expectedNullPointerException() {
            Match match = Match.builder()
                    .date(new Date(System.currentTimeMillis()))
                    .season("201920120")
                    .tournament(Tournament.builder().name("tourn").build())
                    .awayCommand(CommandStat.builder().command(Command.builder().name("name").build()).build())
                    .homeCommand(CommandStat.builder().command(Command.builder().name("").build()).build())
                .build();
            assertThrows(NullPointerException.class, () -> service.createMatch(match));
        }

        @Test
        void whenAwayCommandNull_expectedNullPointerException() {
            Match match = Match.builder()
                    .date(new Date(System.currentTimeMillis()))
                    .season("201920120")
                    .tournament(Tournament.builder().name("tourn").build())
                    .homeCommand(CommandStat.builder().command(Command.builder().name("name").build()).build())
                .build();
            assertThrows(NullPointerException.class, () -> service.createMatch(match));
        }

        @Test
        void whenAwayCommandNameIsEmpty_expectedNullPointerException() {
            Match match = Match.builder()
                    .date(new Date(System.currentTimeMillis()))
                    .season("201920120")
                    .tournament(Tournament.builder().name("tourn").build())
                    .homeCommand(CommandStat.builder().command(Command.builder().name("name").build()).build())
                    .awayCommand(CommandStat.builder().command(Command.builder().name("").build()).build())
                .build();
            assertThrows(NullPointerException.class, () -> service.createMatch(match));
        }

        @Test
        void whenTournamentIsNull_expectedNullPointerException() {
            Match match = Match.builder()
                    .date(new Date(System.currentTimeMillis()))
                    .season("201920120")
                    .homeCommand(CommandStat.builder().command(Command.builder().name("name").build()).build())
                    .awayCommand(CommandStat.builder().command(Command.builder().name("name").build()).build())
                .build();
            assertThrows(NullPointerException.class, () -> service.createMatch(match));
        }

        @Test
        void whenTournamentNameIsEmpty_expectedNullPointerException() {
            Match match = Match.builder()
                    .date(new Date(System.currentTimeMillis()))
                    .season("201920120")
                    .tournament(Tournament.builder().name("").build())
                    .homeCommand(CommandStat.builder().command(Command.builder().name("name").build()).build())
                    .awayCommand(CommandStat.builder().command(Command.builder().name("name").build()).build())
                .build();
            assertThrows(NullPointerException.class, () -> service.createMatch(match));
        }

        @Test
        void whenNewTournament_expectedCreatedNewTournamentAndNewMatchWithExistedHomeAndAwayCommands() {
            Date now = new Date(System.currentTimeMillis());
            Command homeTeam = service.createCommand("HomeTeam");
            Command awayTeam = service.createCommand("AwayTeam");
            Match match = Match.builder()
                    .date(now)
                    .season("201920120")
                    .tournament(Tournament.builder().name("Tourn").build())
                    .homeCommand(CommandStat.builder().assists(5).maxLead(5).maxLeadQuarter(Quarter.FIRST).command(homeTeam).build())
                    .awayCommand(CommandStat.builder().assists(10).maxLead(1).maxLeadQuarter(Quarter.OT).command(awayTeam).build())
                .build();
            Match createdMatch = service.createMatch(match);
            assertThat(createdMatch).isNotNull()
                    .hasFieldOrPropertyWithValue("date", now)
                    .hasFieldOrPropertyWithValue("season", "201920120")
                    .hasNoNullFieldsOrProperties();
            assertThat(createdMatch.getHomeCommand()).isNotNull()
                    .hasFieldOrPropertyWithValue("assists", 5)
                    .hasFieldOrPropertyWithValue("maxLead", 5)
                    .hasFieldOrPropertyWithValue("maxLeadQuarter", Quarter.FIRST);
            assertThat(createdMatch.getAwayCommand()).isNotNull()
                    .hasFieldOrPropertyWithValue("assists", 10)
                    .hasFieldOrPropertyWithValue("maxLead", 1)
                    .hasFieldOrPropertyWithValue("maxLeadQuarter", Quarter.OT);
            assertThat(createdMatch.getTournament()).isNotNull()
                .hasFieldOrPropertyWithValue("name", "Tourn")
                .hasNoNullFieldsOrProperties();
        }

    }

    @Configuration
    @AutoConfigureBefore(DataSource.class)
    private static class TestConf implements AutoCloseable {

        private EmbeddedMysql mysqld;

        public TestConf() {
            MysqldConfig config = aMysqldConfig(v5_6_23)
                    .withCharset(UTF8)
                    .withUser("test", "test")
                    .withPort(2215).build();

            mysqld = anEmbeddedMysql(config)
                    .addSchema("euroleague", ScriptResolver.classPathScript("euroleague.sql"))
                    .start();
        }

        @Bean
        public DataSource getDataSource() {
            DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
            dataSourceBuilder.driverClassName("com.mysql.cj.jdbc.Driver");
            dataSourceBuilder.url("jdbc:mysql://localhost:2215/euroleague");
            dataSourceBuilder.username("test");
            dataSourceBuilder.password("test");
            return dataSourceBuilder.build();
        }

        @Override
        public void close() throws Exception {
            try {
                mysqld.stop();
            } catch (Exception ignore){}
        }
    }
}