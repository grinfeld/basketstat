package com.mikerusoft.euroleague.services.mongo;

import com.wix.mysql.EmbeddedMysql;
import com.wix.mysql.ScriptResolver;
import com.wix.mysql.config.MysqldConfig;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

import static com.wix.mysql.EmbeddedMysql.anEmbeddedMysql;
import static com.wix.mysql.config.Charset.UTF8;
import static com.wix.mysql.config.MysqldConfig.aMysqldConfig;
import static com.wix.mysql.distribution.Version.v5_6_23;

@Configuration
@AutoConfigureBefore(DataSource.class)
class TestConf implements AutoCloseable {

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
