package com.dash.db.influx.repositories;

import org.influxdb.InfluxDB;
import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.Point;
import java.util.concurrent.TimeUnit;

public class InfluxdbRepository implements TimeSeriesRepository {

    private final InfluxDB influxDB;


    public InfluxdbRepository(InfluxDB influxDB) {
        this.influxDB = influxDB;
    }


    @Override
    public void write(String playerLicense, boolean isOnline, String serverName, String name) {


        BatchPoints.Builder batchPointsBuilder = BatchPoints.database("gaming");

        Point point = Point.measurement("player_online")
                .tag("server", serverName)
                .time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
                .addField("name", name)
                .addField("player_license", playerLicense)
                .addField("status", isOnline ? "online" : "offline")
                .build();

        batchPointsBuilder.point(point);




        influxDB.write(batchPointsBuilder.build());
    }






}
