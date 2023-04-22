package com.example.weather_3.model;

import com.example.weather_3.model.json.Record;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Repository {
    private static final String BASE_URL = "https://api.openweathermap.org/data/2.5/forecast/";
    private static final String API_KEY = "a0e4b2727858f8dc3bdc0428ef7e3712";
    private static final String IMPERIAL_UNITS = "imperial";

    private static Repository instance;
    private WeatherService service;

    private Repository() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        service = retrofit.create(WeatherService.class);
    }

    public static Repository getInstance() {
        if (instance == null) {
            instance = new Repository();
        }
        return instance;
    }

    public Call<Record> fetchWeather(String location) {
        return service.fetchWeather(location, IMPERIAL_UNITS, 16, API_KEY);
    }
}
