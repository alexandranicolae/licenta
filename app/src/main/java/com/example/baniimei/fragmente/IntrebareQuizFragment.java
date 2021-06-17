package com.example.baniimei.fragmente;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;

import androidx.appcompat.content.res.AppCompatResources;
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

public class IntrebareQuizFragment extends Fragment {

    public IntrebareQuizFragment() {
        // Required empty public constructor
    }

    TextView intrebare;
    RadioGroup rgRasp;
    Chestionar chestionar;
    List<String> listarasp;
    int nrRasp;

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
            throw new ClassCastException(activity.toString() + "must implement OnRadioGroupSelectedListener");
        }
    }

    View view;
    int corectCheckedId = -1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_intrebare_quiz, container, false);

        intrebare = view.findViewById(R.id.tvIntrebareQuiz);
        rgRasp = view.findViewById(R.id.rgRaspunsuri);

        Bundle bundle = getArguments();
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

        for (int i=0;i<nrRasp;i++) {
            RadioButton raspuns = new RadioButton(view.getContext());
            raspuns.setChecked(false);
            raspuns.setId(i + 100);
            raspuns.setText(listarasp.get(i));
            if (listarasp.get(i).equals(chestionar.getRaspunsCorect())) {
                corectCheckedId = i + 100;
            }
            rgRasp.addView(raspuns);
        }

        rgRasp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @SuppressLint("ResourceAsColor")
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton = group.findViewById(checkedId);
                if (!chestionar.getRaspunsCorect().equals(radioButton.getText())) {
                    radioButton.setBackground(AppCompatResources.getDrawable(view.getContext(), R.color.design_default_color_error));
                    RadioButton rbCorect = group.findViewById(corectCheckedId);
                    rbCorect.setBackground(AppCompatResources.getDrawable(view.getContext(), R.color.grn));
                } else {
                    radioButton.setBackground(AppCompatResources.getDrawable(view.getContext(), R.color.grn));
                }

                mCallback.onButtonSelected((String) radioButton.getText());
            }
        });

        return view;
    }
}