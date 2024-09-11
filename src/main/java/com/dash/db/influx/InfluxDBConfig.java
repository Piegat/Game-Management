package com.dash.db.influx;

import com.dash.db.influx.repositories.InfluxdbRepository;
import com.dash.db.influx.repositories.TimeSeriesRepository;
import okhttp3.OkHttpClient;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import io.jsonwebtoken.lang.Collections;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableConfigurationProperties(InfluxDBProperties.class)
public class InfluxDBConfig {

    @Bean
    @ConditionalOnProperty(name = "platform.influxdb.enabled", havingValue = "true")
    public TimeSeriesRepository influxDB(InfluxDBProperties p) {
        OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient().newBuilder()
                .connectTimeout(90, TimeUnit.SECONDS)
                .readTimeout(120, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS);
        InfluxDB connection = InfluxDBFactory.connect(p.getUrl(), p.getUser(), p.getPasswd(), okHttpClientBuilder);
        boolean databaseExists = connection.describeDatabases().contains(p.getDatabase());
        if (!databaseExists) {
            connection.createDatabase(p.getDatabase());
        }
        return new InfluxdbRepository(connection);

    }

}

