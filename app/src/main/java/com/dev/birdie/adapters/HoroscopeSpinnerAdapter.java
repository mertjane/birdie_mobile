package com.dev.birdie.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.dev.birdie.R;

public class HoroscopeSpinnerAdapter extends ArrayAdapter<CharSequence> {

    private Context context;
    private CharSequence[] items;

    public HoroscopeSpinnerAdapter(@NonNull Context context, int resource, @NonNull CharSequence[] items) {
        super(context, resource, items);
        this.context = context;
        this.items = items;
    }

    @Override
    public boolean isEnabled(int position) {
        // Disable the first item (placeholder)
        return true;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            view = inflater.inflate(R.layout.spinner_dropdown_item, parent, false);
        }

        TextView textView = view.findViewById(android.R.id.text1);
        textView.setText(items[position]);

        if (position == 0) {
            // Make placeholder gray and disabled
            textView.setTextColor(Color.parseColor("#999999"));
        } else {
            textView.setTextColor(Color.parseColor("#222222"));
        }

        return view;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            view = inflater.inflate(R.layout.spinner_item, parent, false);
        }

        TextView textView = view.findViewById(android.R.id.text1);
        textView.setText(items[position]);

        if (position == 0) {
            textView.setTextColor(Color.parseColor("#999999")); // gray placeholder
        } else {
            textView.setTextColor(Color.parseColor("#222222"));
        }

        return view;
    }
}