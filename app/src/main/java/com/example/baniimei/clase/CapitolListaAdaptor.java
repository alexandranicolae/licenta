package com.example.baniimei.clase;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.baniimei.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CapitolListaAdaptor extends ArrayAdapter<Capitol> {

    private final Context context;
    private final int resource;

    private TextView textView;
    private ImageView lock;

    private final List<Capitol> capitols;

    public CapitolListaAdaptor(Context context, int resource, ArrayList<Capitol> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        this.capitols = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        Capitol capitol = (Capitol) capitols.get(position);
        LayoutInflater inflater = LayoutInflater.from(context);
        convertView = inflater.inflate(resource, parent, false);

        if (capitol != null) {
            textView = convertView.findViewById(R.id.tvLvl);
            textView.setText(capitol.getNumeCapitol());

            lock = convertView.findViewById(R.id.imgLock);
            if (capitol.isActiv()) {
                lock.setVisibility(View.INVISIBLE);
            } else {
                lock.setVisibility(View.VISIBLE);
            }
        }

        return convertView;
    }
}
