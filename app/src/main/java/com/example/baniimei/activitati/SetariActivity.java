package com.example.baniimei.activitati;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.baniimei.R;

public class SetariActivity extends AppCompatActivity {

    SwitchCompat swSunet, swMuzica, swNotificari;
    TextView tvDespre, tvEvalueaza, tvDescarca;
    Dialog desprePopup, evalueazaPopup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_BaniiMei);
        setContentView(R.layout.activity_setari);

        init();

        swSunet.setOnCheckedChangeListener(sunetClick());
        swMuzica.setOnCheckedChangeListener(muzicaClick());
        swNotificari.setOnCheckedChangeListener(notificariClick());

        tvDespre.setOnClickListener(despreClick());
        tvEvalueaza.setOnClickListener(evalueazaClick());
        tvDescarca.setOnClickListener(descarcaClick());
    }

    private void init() {
        swSunet=findViewById(R.id.setareSunet);
        swMuzica=findViewById(R.id.setareMuzica);
        swNotificari=findViewById(R.id.setareNotificari);
        tvDespre=findViewById(R.id.setareDespre);
        tvEvalueaza=findViewById(R.id.setareEvalueaza);
        tvDescarca=findViewById(R.id.setareDescarca);
        desprePopup=new Dialog(SetariActivity.this);
        evalueazaPopup=new Dialog(SetariActivity.this);
    }

    private View.OnClickListener despreClick() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView btnX;

                desprePopup.setContentView(R.layout.despre_popup);

                btnX=desprePopup.findViewById(R.id.tvX);

                btnX.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        desprePopup.dismiss();
                    }
                });

                desprePopup.show();
            }
        };
    }

    private View.OnClickListener evalueazaClick() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView btnX;
                RatingBar rating;
                EditText mesaj;
                Button btnEvalueaza;

                evalueazaPopup.setContentView(R.layout.evalueaza_popup);

                btnX=evalueazaPopup.findViewById(R.id.tvX);
                rating=evalueazaPopup.findViewById(R.id.ratingBar);
                mesaj=evalueazaPopup.findViewById(R.id.etEvaluare);
                btnEvalueaza=evalueazaPopup.findViewById(R.id.btnEvalueaza);

                btnX.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        evalueazaPopup.dismiss();
                    }
                });

                btnEvalueaza.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //todo
                        //sa se retina evaluarea in bd externa
                        // sa se salveze starea cu sharepref?
                        // doar prima data?
                        Toast.makeText(getApplicationContext(), Float.toString(rating.getRating())+" "+mesaj.getText(), Toast.LENGTH_LONG).show();
                        //de retinut ca rating bar-ul e de la 0 la 5!
                        evalueazaPopup.dismiss();
                    }
                });

                evalueazaPopup.show();
            }
        };
    }

    private View.OnClickListener descarcaClick() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO
                //descarca materiale
            }
        };
    }

    private CompoundButton.OnCheckedChangeListener notificariClick() {
        return new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    //TODO
                    // porneste notificari
                } else {
                    //TODO
                    // opreste notificari
                }
            }
        };
    }

    private CompoundButton.OnCheckedChangeListener muzicaClick() {
        return new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    //TODO
                    // porneste muzica
                } else {
                    //TODO
                    // opreste muzica
                }
            }
        };
    }

    private CompoundButton.OnCheckedChangeListener sunetClick() {
        return new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    //TODO
                    // porneste sunet
                } else {
                    //TODO
                    // opreste sunet
                }
            }
        };
    }
}