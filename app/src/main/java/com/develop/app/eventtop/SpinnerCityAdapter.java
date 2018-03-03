package com.develop.app.eventtop;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class SpinnerCityAdapter extends ArrayAdapter<City> {
    private Context context;
    private List<City> cityList;

    public SpinnerCityAdapter(@NonNull Context context, int resource, @NonNull List<City> cityList) {
        super(context, resource, cityList);
        this.context = context;
        this.cityList = cityList;
    }

    @Override
    public int getCount() {
        return cityList.size();
    }

    @Override
    public City getItem(int position) {
        return cityList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        TextView label = new TextView(context);
        label.setTextColor(Color.BLACK);
        label.setPadding(15, 15, 15, 15);
        Typeface typeface = Typeface.createFromAsset(context.getAssets(), "STV_0.ttf");
        label.setTypeface(typeface);
        label.setText(cityList.get(position).getName());
        return label;
    }
}
