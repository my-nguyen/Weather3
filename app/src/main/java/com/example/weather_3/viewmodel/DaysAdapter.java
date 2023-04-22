package com.example.weather_3.viewmodel;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weather_3.R;
import com.example.weather_3.databinding.ItemDayBinding;
import com.example.weather_3.model.json.Day;

import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.List;

public class DaysAdapter extends RecyclerView.Adapter<DaysAdapter.ViewHolder> {
    private List<Day> days;

    public DaysAdapter(List<Day> days) {
        this.days = days;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemDayBinding binding = ItemDayBinding.inflate(inflater, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(days.get(position));
    }

    @Override
    public int getItemCount() {
        return days.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ItemDayBinding binding;

        public ViewHolder(ItemDayBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Day day) {
            String dia = getDay(getAdapterPosition()).split(",")[0];
            binding.time.setText(dia);
            int resource = getResource(day.weather.get(0).id);
            binding.image.setImageResource(resource);
            binding.temperature.setText(Math.round(day.temp.max) + "\u00B0");
        }
    }

    private String getDay(int position) {
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.add(GregorianCalendar.DATE, position);
        SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE");
        String day = dayFormat.format(calendar.getTime());
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd");
        String date = dateFormat.format(calendar.getTime());
        return day + ", " + date;
    }

    private int getResource(int id) {
        if (id >= 200 && id <= 232) return R.drawable.ic_thunderstorm;
        if (id >= 300 && id <= 321) return R.drawable.ic_shower_rain;
        if (id >= 500 && id <= 531) return R.drawable.ic_rain;
        if (id >= 600 && id <= 622) return R.drawable.ic_snow;
        if (id >= 701 && id <= 781) return R.drawable.ic_mist;
        if (id == 800) return R.drawable.ic_clear_sky;
        if (id == 801) return R.drawable.ic_few_clouds;
        if (id == 802) return R.drawable.ic_scattered_clouds;
        if (id == 803 || id == 804) return R.drawable.ic_broken_clouds;
        return -1;
    }
}
