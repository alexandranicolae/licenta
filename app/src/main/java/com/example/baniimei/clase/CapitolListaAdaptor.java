package com.example.baniimei.clase;

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

import java.util.ArrayList;

public class CapitolListaAdaptor extends ArrayAdapter<String>{

    private Context context;
    private int resource;

    private TextView textView;

    public CapitolListaAdaptor(Context context, int resource, ArrayList<String> objects) {
        super(context, resource, objects);
        this.context=context;
        this.resource=resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        String lvlname=getItem(position);
        LayoutInflater inflater=LayoutInflater.from(context);
        convertView=inflater.inflate(resource,parent,false);

        textView=convertView.findViewById(R.id.tvLvl);
        textView.setText(lvlname);

        return convertView;
    }
}
