package com.example.weather_3.model;

import com.example.weather_3.model.json.Record;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherService {
    @GET("daily")
    Call<Record> fetchWeather(@Query("q") String zipcode, @Query("units") String units, @Query("cnt") int numDays, @Query("appid") String apiKey);
}
