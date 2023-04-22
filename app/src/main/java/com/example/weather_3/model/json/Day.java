package com.example.weather_3.model.json;

import java.io.Serializable;
import java.util.List;

public class Day implements Serializable {
    long dt;
    long sunrise;
    long sunset;
    public Temperature temp;
    int pressure;
    int humidity;
    public List<Weather> weather;
    float speed;
}
