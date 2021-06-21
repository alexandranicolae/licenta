package com.example.baniimei.adaptoare;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
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

    private static final String DETALII_RANDOM = "Intrebari din toate categoriile";
    private final Context context;
    private final int resource;

    private TextView textViewTitlu;
    private ProgressBar pgBar;

    private final List<Capitol> capitols;

    public ListaAdaptorIntrebare(Context context, int resource, ArrayList<Capitol> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        this.capitols = objects;
    }

    @SuppressLint("ResourceAsColor")
    @RequiresApi(api = Build.VERSION_CODES.N)
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        Capitol capitol = capitols.get(position);
        LayoutInflater inflater = LayoutInflater.from(context);
        convertView = inflater.inflate(resource, parent, false);

        if (capitol != null) {
            textViewTitlu = convertView.findViewById(R.id.tvCategorie);
            textViewTitlu.setText(capitol.getNumeCapitol());

            pgBar = convertView.findViewById(R.id.pgBar);

            switch (capitol.getDificultate()) {
                case USOARA: {
                    pgBar.setProgress(33, true);
                    //pgBar.setProgressTintList(ColorStateList.valueOf(R.color.grn));
                    break;
                }
                case MEDIE:
                    pgBar.setProgress(66, true);
                    //pgBar.setProgressTintList(ColorStateList.valueOf(R.color.portocaliu));
                    break;
                case GREA:
                    pgBar.setProgress(100, true);
                    //pgBar.setProgressTintList(ColorStateList.valueOf(R.color.red));
                    break;
            }
//            if (capitol.getNrChTotale() != 0)
//                pgBar.setProgress(capitol.getNrChCompletate() / capitol.getNrChTotale());
//            else
//                pgBar.setProgress(0);

        }

        return convertView;
    }
}
