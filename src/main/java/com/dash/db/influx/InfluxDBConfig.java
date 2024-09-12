package com.dash.db.influx;

import com.dash.db.influx.repositories.InfluxdbRepository;
import com.dash.db.influx.repositories.TimeSeriesRepository;
import okhttp3.OkHttpClient;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
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
    public InfluxDB influxDB(InfluxDBProperties p) {
        OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient().newBuilder()
                .connectTimeout(90, TimeUnit.SECONDS)
                .readTimeout(120, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .addInterceptor(chain -> chain.proceed(chain.request().newBuilder()
                        .addHeader("Authorization", "Token " + p.getToken())  // Usando o token de API
                        .build()));

        return InfluxDBFactory.connect(p.getUrl(), okHttpClientBuilder);
    }
}

