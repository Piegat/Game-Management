package com.dash.db.influx;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Setter
@Getter
@ConfigurationProperties(prefix = "platform.influxdb")
public class InfluxDBProperties {

    private String url;
    private String token;  // Adicione o campo do token
    private String database;

}
