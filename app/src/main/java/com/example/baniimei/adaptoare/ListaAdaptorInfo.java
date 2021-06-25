package com.example.baniimei.adaptoare;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.baniimei.R;
import com.example.baniimei.clase.Capitol;

import java.util.ArrayList;
import java.util.List;

public class ListaAdaptorInfo extends ArrayAdapter<Capitol> {

    private final Context context;
    private final int resource;

    private TextView textView;
    private ImageView imageViewLock;

    private final List<Capitol> capitols;

    public ListaAdaptorInfo(Context context, int resource, List<Capitol> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        this.capitols = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        Capitol capitol = capitols.get(position);
        LayoutInflater inflater = LayoutInflater.from(context);
        convertView = inflater.inflate(resource, parent, false);
        imageViewLock = convertView.findViewById(R.id.imgLock);

        if (capitol != null) {
            textView = convertView.findViewById(R.id.tvCategorie);
            textView.setText(capitol.getNumeCapitol());

            if (capitol.isActiv()) {
                imageViewLock.setVisibility(View.INVISIBLE);
            }
        }

        return convertView;
    }
}
