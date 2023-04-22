package com.example.weather_3.view;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.weather_3.R;
import com.example.weather_3.databinding.ActivityMainBinding;
import com.example.weather_3.model.json.Day;
import com.example.weather_3.viewmodel.DaysAdapter;
import com.example.weather_3.viewmodel.WeatherViewModel;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private ActivityMainBinding binding;
    private List<Day> days;
    private DaysAdapter adapter;
//    private String location;
    private WeatherViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        days = new ArrayList<>();
        adapter = new DaysAdapter(days);
        binding.recycler.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        binding.recycler.setLayoutManager(layoutManager);

        viewModel = new ViewModelProvider(this).get(WeatherViewModel.class);
        viewModel.record.observe(this, record -> {
            if (record == null) {
                Toast.makeText(getApplicationContext(), "Please enter a well-known location, e.g. London", Toast.LENGTH_LONG).show();
            } else {
                // display the temperature of today
                binding.location.setText(record.city.name);
                Day today = record.list.get(0);
                binding.condition.setText(today.weather.get(0).main);
                binding.temperature.setText(String.valueOf(Math.round(today.temp.max)));
                int max = Math.round(today.temp.max);
                int min = Math.round(today.temp.min);
                binding.highLow.setText("H:" + max + "\u00B0" + " L:" + min + "\u00B0");

                // display the temperature of the next 16 days in a RecyclerView
                days.clear();
                days.addAll(record.list);
                adapter.notifyDataSetChanged();

                // save the queried city
//                location = record.city.name;
                viewModel.location.setValue(record.city.name);
            }
        });

        // try to retrieve last saved location
        viewModel.location.observe(this, location -> {
            if (location != null) {
                viewModel.fetchWeather(location);
            } else {
                binding.getRoot().setVisibility(View.INVISIBLE);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
//        viewModel.saveLocation(location);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // get a hold of the search menu item
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView)searchItem.getActionView();

        // listen for the search query
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String location) {
                // reset the search menu item
                searchView.clearFocus();
                searchView.setQuery("", false);
                searchView.setIconified(true);
                searchItem.collapseActionView();

                viewModel.fetchWeather(location);
                hideKeyboard();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
        return true;
    }

    private void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}