package com.example.baniimei.fragmente;

import android.app.Activity;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.baniimei.R;
import com.example.baniimei.activitati.JocActivity;
import com.example.baniimei.clase.Chestionar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class IntrebareFragment extends Fragment {

    public IntrebareFragment() {
        // Required empty public constructor
    }

    TextView intrebare;
    RadioGroup rgRasp;
    Chestionar chestionar;
    List<String> listarasp;
    int nrRasp;

//    public void resetRgRasp() {
//        this.rgRasp.setSelected(false);
//    }

    public interface OnRadioGroupSelectedListener {
        void onButtonSelected(String value);
    }

    OnRadioGroupSelectedListener mCallback;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (OnRadioGroupSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnRadioGroupSelectedListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_intrebare, container, false);

        intrebare=view.findViewById(R.id.tvIntrebare);
        rgRasp= view.findViewById(R.id.rgRaspunsuri);

        Bundle bundle=getArguments();
        if (bundle != null) {
            chestionar = (Chestionar) bundle.getSerializable(JocActivity.TAG_CHESTIONAR);
        }
        else {
            throw new Error("Eroare transfer chestionar");
        }

        nrRasp= chestionar.getRaspunsuri().size()+1;
        intrebare.setText(chestionar.getIntrebare());

        listarasp=new ArrayList<>();
        listarasp.add(chestionar.getRaspunsCorect());
        listarasp.addAll(chestionar.getRaspunsuri());
        Collections.shuffle(listarasp);


//        RadioButton rbInv=new RadioButton(view.getContext());
//        rbInv.setChecked(true);
//        rbInv.setVisibility(View.INVISIBLE);
//        rgRasp.addView(rbInv);
        for (int i=0;i<nrRasp;i++) {
            RadioButton raspuns = new RadioButton(view.getContext());
            raspuns.setChecked(false);
            raspuns.setId(i+100);
            raspuns.setText(listarasp.get(i));
            rgRasp.addView(raspuns);
        }
        rgRasp.clearCheck();

        rgRasp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton = group.findViewById(checkedId);
                mCallback.onButtonSelected((String) radioButton.getText());
            }
        });

        return view;
    }
}