package com.example.weather_3.model.json;

import java.io.Serializable;
import java.util.List;

public class Record implements Serializable {
    public City city;
    public List<Day> list;
}
