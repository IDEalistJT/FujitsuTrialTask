package com.example.fujitsutrialtask;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Timestamp;

public interface WeatherDataRepository extends JpaRepository<WeatherData, Long> {
    @Query("SELECT COUNT(w) > 0 FROM WeatherData w WHERE w.stationName = :stationName AND w.timestamp = :timestamp")
    boolean existsByStationNameAndTimestamp(@Param("stationName") String stationName, @Param("timestamp") Timestamp timestamp);

    @Query("SELECT wd FROM WeatherData wd WHERE wd.stationName = :city ORDER BY wd.timestamp DESC")
    WeatherData findLatestDataByCity(@Param("city") String city);
}
