package com.example.baniimei.adaptoare;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.baniimei.R;
import com.example.baniimei.clase.User;

import java.util.ArrayList;
import java.util.List;

public class ListaAdaptorClasament extends ArrayAdapter<User> {

    private final Context context;
    private final int resource;

    private TextView tvNume, tvPuncte, tvClasament;
    private static int nrClasament;

    private final List<User> capitols;

    public ListaAdaptorClasament(Context context, int resource, List<User> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        this.capitols = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        User user = capitols.get(position);
        LayoutInflater inflater = LayoutInflater.from(context);
        convertView = inflater.inflate(resource, parent, false);

        if (user != null) {
            tvNume = convertView.findViewById(R.id.tvNume);
            tvNume.setText(user.getNume());
            tvPuncte = convertView.findViewById(R.id.tvPuncte);
            tvPuncte.setText(user.getScor());
            tvClasament = convertView.findViewById(R.id.tvPozitie);
            int poz = position + 1;
            tvClasament.setText("#" + poz);
        }

        return convertView;
    }
}
