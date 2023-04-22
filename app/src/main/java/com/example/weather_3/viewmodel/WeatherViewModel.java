package com.example.weather_3.viewmodel;

import static android.content.Context.MODE_PRIVATE;

import android.app.Application;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.weather_3.model.Repository;
import com.example.weather_3.model.json.Record;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WeatherViewModel extends AndroidViewModel {
    private static final String PREFERENCE_NAME = "SharedPreferences";
    private static final String PREFERENCE_KEY = "location";
    private static final String TAG = "WeatherViewModel";

    public MutableLiveData<Record> record = new MutableLiveData<>();
    public MutableLiveData<String> location = new MutableLiveData<>();

    public WeatherViewModel(@NonNull Application application) {
        super(application);
        fetchLocation();
    }

    public void fetchWeather(String location) {
        Repository.getInstance().fetchWeather(location).enqueue(new Callback<Record>() {
            @Override
            public void onResponse(Call<Record> call, Response<Record> response) {
                Log.d(TAG, "onResponse " + response);
                if (response.body() == null) {
                    Log.w(TAG, "Invalid response from openweathermap.org");
                }
                record.setValue(response.body());
            }

            @Override
            public void onFailure(Call<Record> call, Throwable t) {
                Log.e(TAG, "onFailure " + t);
            }
        });
    }

    public void saveLocation(String city) {
        SharedPreferences.Editor preferences = getApplication().getApplicationContext().getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE).edit();
        preferences.putString(PREFERENCE_KEY, city);
        preferences.apply();
        location.setValue(city);
    }

    private void fetchLocation() {
        SharedPreferences preferences = getApplication().getApplicationContext().getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE);
        location.setValue(preferences.getString(PREFERENCE_KEY, null));
    }
}
