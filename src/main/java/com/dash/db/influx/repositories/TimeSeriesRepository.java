package com.dash.db.influx.repositories;


public interface TimeSeriesRepository {

    void write(String playerLicense, boolean isOnline, String serverName, String name);


}
