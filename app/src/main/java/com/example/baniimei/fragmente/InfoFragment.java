package com.example.baniimei.fragmente;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.baniimei.R;
import com.example.baniimei.activitati.ChestionarActivity;
import com.example.baniimei.clase.Chestionar;

public class InfoFragment extends Fragment {

    public InfoFragment() {
        // Required empty public constructor
    }

    TextView tvInfo, tvEx1, tvEx2, tvTitlu;
    Chestionar chestionar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_info, container, false);

        tvTitlu=view.findViewById(R.id.tvInfoTitlu);
        tvInfo=view.findViewById(R.id.tvInformatie);
        tvEx1=view.findViewById(R.id.tvExemplu1);
        tvEx2=view.findViewById(R.id.tvExemplu2);

        Bundle bundle=getArguments();
        chestionar=(Chestionar) bundle.getSerializable(ChestionarActivity.TAG_CHESTIONAR);

        tvTitlu.setText(chestionar.getTitluSubnivel());
        tvInfo.setText(chestionar.getInformatie());
        tvEx1.setText(chestionar.getExemplu1());
        tvEx2.setText(chestionar.getExemplu2());

        return view;
    }
}