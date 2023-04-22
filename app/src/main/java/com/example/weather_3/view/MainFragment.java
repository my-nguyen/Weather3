package com.example.weather_3.view;

import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.weather_3.R;
import com.example.weather_3.databinding.FragmentMainBinding;
import com.example.weather_3.model.json.Day;
import com.example.weather_3.viewmodel.DaysAdapter;
import com.example.weather_3.viewmodel.WeatherViewModel;

import java.util.ArrayList;
import java.util.List;

public class MainFragment extends Fragment {
    private static final String TAG = "MainFragment";

    private FragmentMainBinding binding;
    private List<Day> days;
    private DaysAdapter adapter;
    private WeatherViewModel viewModel;

    public MainFragment() {
        super(R.layout.fragment_main);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding = FragmentMainBinding.bind(view);

        days = new ArrayList<>();
        adapter = new DaysAdapter(days);
        binding.recycler.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        binding.recycler.setLayoutManager(layoutManager);

        viewModel = new ViewModelProvider(this).get(WeatherViewModel.class);

        if (viewModel.location != null) {
            viewModel.fetchWeather(viewModel.location);
        } else {
            binding.getRoot().setVisibility(View.INVISIBLE);
        }

        viewModel.record.observe(getViewLifecycleOwner(), record -> {
            binding.getRoot().setVisibility(View.VISIBLE);
            if (record == null) {
                Toast.makeText(getContext(), "Please enter a well-known location, e.g. London", Toast.LENGTH_LONG).show();
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
                viewModel.saveLocation(record.city.name);
            }
        });

        getActivity().addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                // get a hold of the search menu item
                menuInflater.inflate(R.menu.menu_main, menu);
                MenuItem searchItem = menu.findItem(R.id.action_search);
                SearchView searchView = (SearchView) searchItem.getActionView();

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
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                return false;
            }
        });
    }

    private void hideKeyboard() {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}