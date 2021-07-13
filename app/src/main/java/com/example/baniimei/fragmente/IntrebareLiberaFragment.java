package com.example.baniimei.fragmente;

import android.app.Activity;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.baniimei.R;
import com.example.baniimei.activitati.JocActivity;
import com.example.baniimei.clase.Intrebare;

public class IntrebareLiberaFragment extends Fragment {

    TextView tvIntrebare;
    EditText etRasp;
    ImageButton btnSend;
    Intrebare intrebare;

    OnMyEventListener myEventListener;

    public IntrebareLiberaFragment() {
        // Required empty public constructor
    }

    public interface OnMyEventListener {
        void trimiteEventRezultat(String s);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            myEventListener = (OnMyEventListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnMyEventListener");
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_intrebare_libera, container, false);

        tvIntrebare = view.findViewById(R.id.tvIntrebareLibera);
        etRasp = view.findViewById(R.id.etRaspuns);
        btnSend = view.findViewById(R.id.btnSendRasp);

        Bundle bundle = getArguments();
        if (bundle != null) {
            intrebare = (Intrebare) bundle.getSerializable(JocActivity.TAG_CHESTIONAR);
        } else {
            throw new Error("Eroare transfer intrebare");
        }

        tvIntrebare.setText(intrebare.getIntrebare());

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myEventListener.trimiteEventRezultat(etRasp.getText().toString());
            }
        });

        return view;
    }
}