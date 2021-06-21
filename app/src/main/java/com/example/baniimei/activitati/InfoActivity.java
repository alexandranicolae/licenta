package com.example.baniimei.activitati;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.baniimei.R;
import com.example.baniimei.clase.Informatie;
import com.example.baniimei.clase.SunetFundalService;

import java.util.ArrayList;
import java.util.List;

public class InfoActivity extends AppCompatActivity {

    static final String INTENT_CAPITOL_INCHEIAT = "capitol incheiat";
    TextView tvInfo, tvEx1, tvEx2, tvTitlu;
    List<Informatie> listaInformatii;
    Button btnNext, btnBack;
    int indexCurent;
    Intent intent;
    SharedPreferences preferinteMuzica;
    int nrPozitie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        tvTitlu = findViewById(R.id.tvInfoTitlu);
        tvInfo = findViewById(R.id.tvInformatie);
        tvEx1 = findViewById(R.id.tvExemplu1);
        tvEx2 = findViewById(R.id.tvExemplu2);
        btnNext = findViewById(R.id.btnNext);
        btnBack = findViewById(R.id.btnBack);

        preferinteMuzica = getSharedPreferences(getString(R.string.shprefs_numefisier), MODE_PRIVATE);

        indexCurent = 0;

        intent = getIntent();
        listaInformatii = (ArrayList<Informatie>) intent.getSerializableExtra(CapitoleInfoActivity.INTENT_INFORMATIE);
        nrPozitie = (int) intent.getSerializableExtra(CapitoleInfoActivity.INTENT_CAPITOL);

        setTextViews();

        btnNext.setOnClickListener(clickBtnNext());
        btnBack.setOnClickListener(clickBtnBack());
        btnBack.setVisibility(View.INVISIBLE);
    }

    private void setTextViews() {
        tvTitlu.setText(listaInformatii.get(indexCurent).getTitluSubnivel());
        tvInfo.setText(listaInformatii.get(indexCurent).getInformatie());
        tvEx1.setText(listaInformatii.get(indexCurent).getExemplu1());
        tvEx2.setText(listaInformatii.get(indexCurent).getExemplu2());
    }

    private View.OnClickListener clickBtnBack() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                indexCurent--;
                if (indexCurent == 0) {
                    btnBack.setVisibility(View.INVISIBLE);
                }
                setTextViews();
            }
        };
    }

    private View.OnClickListener clickBtnNext() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (indexCurent == listaInformatii.size() - 1) {
                    //trimite inapoi la capitole + mesaj
                    //todo mesaj
                    intent.putExtra(INTENT_CAPITOL_INCHEIAT, nrPozitie);
                    setResult(RESULT_OK, intent);
                    finish();
                    return;
                }
                if (indexCurent < listaInformatii.size() - 1) {
                    indexCurent++;
                    setTextViews();
                    btnBack.setVisibility(View.VISIBLE);
                }
            }
        };
    }
}