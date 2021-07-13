package com.example.baniimei.adaptoare;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.example.baniimei.R;
import com.example.baniimei.clase.Capitol;
import com.example.baniimei.clase.Dificultate;

import java.util.ArrayList;
import java.util.List;

public class ListaAdaptorIntrebare extends ArrayAdapter<Capitol> {

    private final Context context;
    private final int resource;

    private final List<Capitol> capitols;

    public ListaAdaptorIntrebare(Context context, int resource, ArrayList<Capitol> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        this.capitols = objects;
    }

    @SuppressLint("ViewHolder")
    @RequiresApi(api = Build.VERSION_CODES.N)
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        Capitol capitol = capitols.get(position);
        LayoutInflater inflater = LayoutInflater.from(context);
        convertView = inflater.inflate(resource, parent, false);

        if (capitol != null) {
            TextView textViewTitlu = convertView.findViewById(R.id.tvCategorie);
            textViewTitlu.setText(capitol.getNumeCapitol());

            ProgressBar pgBar = convertView.findViewById(R.id.pgBar);
            int start = 100 / Dificultate.values().length;

            switch (capitol.getDificultate()) {
                case USOARA: {
                    pgBar.setProgress(1 * start, true);
                    //pgBar.setProgressTintList(ColorStateList.valueOf(R.color.grn));
                    break;
                }
                case MEDIE:
                    pgBar.setProgress(2 * start, true);
                    //pgBar.setProgressTintList(ColorStateList.valueOf(R.color.portocaliu));
                    break;
                case GREA:
                    pgBar.setProgress(3 * start, true);
                    //pgBar.setProgressTintList(ColorStateList.valueOf(R.color.red));
                    break;
            }
        }

        return convertView;
    }
}
