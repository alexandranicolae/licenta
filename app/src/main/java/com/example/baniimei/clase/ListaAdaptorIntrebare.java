package com.example.baniimei.clase;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.baniimei.R;

import java.util.ArrayList;
import java.util.List;

public class ListaAdaptorIntrebare extends ArrayAdapter<Capitol> {

    private static final String DETALII_RANDOM = "Intrebari din toate categoriile";
    private final Context context;
    private final int resource;

    private TextView textViewTitlu, textViewDetalii;
    private ProgressBar pgBar;

    private final List<Capitol> capitols;

    public ListaAdaptorIntrebare(Context context, int resource, ArrayList<Capitol> objects) {
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
            textViewTitlu = convertView.findViewById(R.id.tvCategorie);
            textViewTitlu.setText(capitol.getNumeCapitol());

            textViewDetalii = convertView.findViewById(R.id.tvDetalii);
            pgBar = convertView.findViewById(R.id.pgBar);

            switch (capitol.getCategorie()) {
                case RANDOM: {
                    textViewDetalii.setVisibility(View.VISIBLE);
                    pgBar.setVisibility(View.INVISIBLE);

                    textViewDetalii.setText(DETALII_RANDOM);
                    break;
                }
                default: {
                    textViewDetalii.setVisibility(View.INVISIBLE);
                    pgBar.setVisibility(View.VISIBLE);

                    if (capitol.getNrChTotale() != 0)
                        pgBar.setProgress(capitol.getNrChCompletate() / capitol.getNrChTotale());
                    else
                        pgBar.setProgress(0);
                }
            }
        }

        return convertView;
    }
}
