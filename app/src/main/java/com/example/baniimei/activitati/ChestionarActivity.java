package com.example.baniimei.activitati;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.baniimei.R;
import com.example.baniimei.clase.Chestionar;
import com.example.baniimei.fragmente.InfoFragment;
import com.example.baniimei.fragmente.IntrebareFragment;

import java.util.ArrayList;

public class ChestionarActivity extends AppCompatActivity implements IntrebareFragment.OnRadioGroupSelectedListener {

    InfoFragment fragmentInfo=new InfoFragment();
    IntrebareFragment fragmentIntrb=new IntrebareFragment();

    private ArrayList<Chestionar> listaChestionare;

    static final String CODE_SCOR = "scor";
    public static final String TAG_CHESTIONAR = "chestionar";

    TextView scor;
    int punctaj;
    Button btnHint;
    Button btnNext;
    Button btnBack;
    int index;
    String raspDat=null;
    Intent intent;

    Dialog templatePopup;

    private static MediaPlayer sunetRspGresit=null;
    private static MediaPlayer sunetRspCorect=null;

    SharedPreferences preferinteSunet;
    SharedPreferences.Editor sharedPrefsEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_BaniiMei);
        setContentView(R.layout.activity_chestionar);

        btnHint=findViewById(R.id.btnHint);
        btnNext=findViewById(R.id.btnNext);
        btnBack=findViewById(R.id.btnBack);
        scor=findViewById(R.id.tvPuncte);

        preferinteSunet = getSharedPreferences(getString(R.string.shprefs_numefisier), MODE_PRIVATE);
        if(preferinteSunet.getBoolean(getString(R.string.shprefs_sunet_key), true)) {
            sunetRspGresit = MediaPlayer.create(this, R.raw.wrong);
            sunetRspCorect = MediaPlayer.create(this, R.raw.corect);
        }

        intent=getIntent();
        Bundle bundle =  intent.getExtras();
        listaChestionare = (ArrayList<Chestionar>) bundle.getSerializable(ChestionarCapitoleActivity.TAG_CHESTIONARE);
        scor.setText(getIntent().getStringExtra(ChestionarCapitoleActivity.TAG_SCOR));
        punctaj=Integer.parseInt(scor.getText().toString());

        index=0;
        schimbaFragment(index,fragmentInfo);

        btnNext.setOnClickListener(clickBtnNext());
        btnHint.setOnClickListener(clickBtnHint());
        btnBack.setOnClickListener(clickBtnBack());
    }

    private View.OnClickListener clickBtnHint() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //sigur doriti?
                AlertDialog.Builder builder = new AlertDialog.Builder(ChestionarActivity.this);
                builder.setMessage(R.string.dialog_hint);
                builder.setCancelable(true);
                builder.setPositiveButton(R.string.da, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // da-> are destule pct?
                        if(punctaj>=2){
                            //      da->popup mesaj + -2pct
                            showMesajPopup(ChestionarActivity.this,listaChestionare.get(index).getIndiciu(),getString(R.string.hint_title));
                            scadePuncte();
                        }
                        else {
                            //      nu->toast
                            Toast.makeText(getApplicationContext(), "Fonduri insuficiente! Nu ai destui banuti!", Toast.LENGTH_LONG).show();
                        }
                        dialog.cancel();
                    }
                });
                builder.setNegativeButton(R.string.nu, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();

            }
        };
    }

    private View.OnClickListener clickBtnBack() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // e pe intrebare -> se duce pe info
                if (fragmentIntrb != null && fragmentIntrb.isVisible()) {
                    schimbaFragment(index, fragmentInfo);
                    btnHint.setVisibility(View.INVISIBLE);
                    if (index == 0) {
                        // daca e prima pag de info, back e invizibil
                        btnBack.setVisibility(View.INVISIBLE);
                    }
                }
                else{
                    //e pe info -> se duce pe intrebarea precedenta!
                    // stim ca exista intrb precedenta pt ca btnBack e invizibil pt prima info
                    // deci index nu va fi <0
                    index--;
                    schimbaFragment(index, fragmentIntrb);
                    btnHint.setVisibility(View.VISIBLE);
                }
            }
        };
    }

    private View.OnClickListener clickBtnNext(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (index < listaChestionare.size()) {
                    // e pe info
                    //      -> se duce pe intrebare
                    if (fragmentInfo != null && fragmentInfo.isVisible()) {
                        schimbaFragment(index, fragmentIntrb);
                        btnHint.setVisibility(View.VISIBLE);
                        btnBack.setVisibility(View.VISIBLE);
                    }

                    // e pe intrebare / sau abia incepe
                    else {
                        // ->a raspuns?
                        if (raspDat != null) {
                            //da-> e corect?
                            if (listaChestionare.get(index).getRaspunsCorect().equals(raspDat)) {
                                // da-> felicitari popup msj  +punctaj
                                //TODO verifica daca poate reda sunetul (SharedPrefs)
                                if(sunetRspCorect!=null)
                                    sunetRspCorect.start();
                                adaugaPuncte();
                                // s-a terminat?
                                if (index + 1 < listaChestionare.size()) {
                                    // nu->next info
                                    index++;
                                    schimbaFragment(index, fragmentInfo);
                                    btnHint.setVisibility(View.INVISIBLE);
                                } else {
                                    // da->mesaj felicitari popup+ inapoi la capitol(+send score si activeaza next)
                                    showMesajPopup(ChestionarActivity.this,getString(R.string.msj_nivelincheiat));
                                }
                            } else {
                                // nu-> mesaj try again + sunet
                                //TODO verifica daca poate reda sunetul (SharedPrefs)
                                if(sunetRspGresit!=null)
                                    sunetRspGresit.start();
                                AlertDialog.Builder builder = new AlertDialog.Builder(ChestionarActivity.this);
                                builder.setMessage(R.string.msj_raspmaiincearca);
                                builder.setCancelable(true);
                                builder.setPositiveButton(R.string.alert_ok, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });
                                AlertDialog alertDialog = builder.create();
                                alertDialog.show();
                            }
                        } else {
                            // mai intai raspunde
                            Toast.makeText(getApplicationContext(), "Alege un raspuns!", Toast.LENGTH_LONG).show();
                        }
                    }
                }
            }
        };
    }

    private void schimbaFragment(int index, Fragment fragment) {
        Bundle b = new Bundle();
        b.putSerializable(TAG_CHESTIONAR, listaChestionare.get(index));
        fragment.setArguments(b);

        getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
//        if(fragment instanceof IntrebareFragment){
//            fragmentIntrb.resetRgRasp();
//        }
    }

    private void adaugaPuncte() {
        punctaj+=5;
        String puncte=String.valueOf(punctaj);
        scor.setText(puncte);
        salveazaScor(puncte);
    }

    private void scadePuncte() {
        punctaj-=2;
        String puncte= String.valueOf(punctaj);
        scor.setText(puncte);
        salveazaScor(puncte);
    }

    private void salveazaScor(String puncte){
        sharedPrefsEditor = getSharedPreferences(getString(R.string.shprefs_scor_numefis), MODE_PRIVATE).edit();
        sharedPrefsEditor.putString(getString(R.string.shprefs_scor),puncte);
        sharedPrefsEditor.apply();
    }

    public void showMesajPopup(Context context, String mesaj, String... args){
        TextView btnX;
        TextView tvMesaj;
        TextView tvTitlu;
        Button btn;

        templatePopup =new Dialog(context);

        String titluPopup=null;
        if(args.length == 1) {
            titluPopup = args[0];
        }

        templatePopup.setContentView(R.layout.template_popup);
        btnX= templatePopup.findViewById(R.id.tvX);
        tvMesaj= templatePopup.findViewById(R.id.tvMesaj);
        btn= templatePopup.findViewById(R.id.btnMaiDeparte);
        tvTitlu=templatePopup.findViewById(R.id.tvTitlu);

        if(titluPopup!=null){
            tvTitlu.setText(titluPopup);
        }

        if(args.length == 2) {
            btn.setText(args[1]);
        }

        tvMesaj.setText(mesaj);

        if(context.getString(R.string.msj_nivelincheiat).equals(mesaj)) {
            btnX.setOnClickListener(clickFelicitariPopupFinish());
            btn.setOnClickListener(clickFelicitariPopupFinish());
        }else{
            btnX.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    templatePopup.dismiss();
                }
            });
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    templatePopup.dismiss();
                }
            });
        }
        templatePopup.show();
    }

    private View.OnClickListener clickFelicitariPopupFinish() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                templatePopup.dismiss();
                intent.putExtra(CODE_SCOR, punctaj);
                setResult(RESULT_OK,intent);
                finish();
            }
        };
    }

    @Override
    public void onButtonSelected(String value) {
        raspDat=value;
    }
}