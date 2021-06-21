package com.example.baniimei.activitati;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.baniimei.R;
import com.example.baniimei.clase.Chestionar;
import com.example.baniimei.clase.DAOUser;
import com.example.baniimei.fragmente.IntrebareLiberaFragment;
import com.example.baniimei.fragmente.IntrebareQuizFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class JocActivity extends AppCompatActivity
        implements IntrebareQuizFragment.OnRadioGroupSelectedListener,
        IntrebareLiberaFragment.OnMyEventListener {

    public static final String TAG_CHESTIONAR = "chestionar";
    static final String CODE_COMPLETATE = "complete";
    static final String CODE_TOTALE = "totale";

    private final int minusHint = 5;
    private final int plusPct = 5;

    private static MediaPlayer sunetJocIncheiat = null;
    private static MediaPlayer sunetRspGresit = null;
    private static MediaPlayer sunetRspCorect = null;

    Fragment fragment = null;

    int index;

    TextView scor;
    int punctaj;

    ImageButton btnHint;
    String raspDat = null;
    Intent intent;
    Dialog templatePopup;

    private DAOUser daoUser;

    SharedPreferences preferinteSunet, prefUser;
    SharedPreferences.Editor sharedPrefsEditor;
    private String userKey = "";

    private ArrayList<Chestionar> listaChestionare;

    private Boolean aCerutHint = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_BaniiMei);
        setContentView(R.layout.activity_joc);

        btnHint = findViewById(R.id.btnHint);
        scor = findViewById(R.id.tvScorJoc);
        sunetJocIncheiat = MediaPlayer.create(this, R.raw.success);

        preferinteSunet = getSharedPreferences(getString(R.string.shprefs_numefisier), MODE_PRIVATE);
        if (preferinteSunet.getBoolean(getString(R.string.shprefs_sunet_key), true)) {
            sunetRspGresit = MediaPlayer.create(this, R.raw.wrong);
            sunetRspCorect = MediaPlayer.create(this, R.raw.corect);
        }

        prefUser = getSharedPreferences("DATE_USER", MODE_PRIVATE);
        userKey = prefUser.getString("ID_USER", "");
        daoUser = new DAOUser();

        intent = getIntent();
        listaChestionare = (ArrayList<Chestionar>) intent.getSerializableExtra(CapitoleJocActivity.INTENT_INTREBARE);

        index = 0;

        btnHint.setOnClickListener(clickBtnHint());

        //prefScor = getSharedPreferences(getString(R.string.shprefs_scor_numefis), MODE_PRIVATE);
        setScorDB();
        //scor.setText(daoUser.getScor(userKey));

        schimbaFragment();
    }

    public void setScorDB() {
        DatabaseReference dbref = daoUser.getDatabaseReference().child(userKey).child("scor");
        dbref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                scor.setText(snapshot.getValue(String.class));
                System.out.println("E OK!!");
                punctaj = Integer.parseInt(String.valueOf(scor.getText()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println("failed" + error.getMessage());
            }
        });
    }

    private View.OnClickListener clickBtnHint() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!aCerutHint) {
                    //sigur doriti?
                    AlertDialog.Builder builder = new AlertDialog.Builder(JocActivity.this);
                    builder.setMessage(R.string.dialog_hint);
                    builder.setCancelable(true);
                    builder.setPositiveButton(R.string.da, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (punctaj >= minusHint) {
                                //      da->popup mesaj + -pct
                                showMesajPopup(JocActivity.this, listaChestionare.get(index).getIndiciu(), getString(R.string.hint_title));
                                scadePuncte();
                            } else {
                                //      nu->toast
                                Toast.makeText(getApplicationContext(), "Fonduri insuficiente!", Toast.LENGTH_LONG).show();
                            }
                            dialog.cancel();
                            aCerutHint = true;
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
                } else {
                    showMesajPopup(JocActivity.this, listaChestionare.get(index).getIndiciu(), getString(R.string.hint_title));
                }
            }
        };
    }

    private View.OnClickListener clickBtnNext() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (index < listaChestionare.size()) {
                    // e pe info
                    //      -> se duce pe intrebare
//                    if (fragmentInfo != null && fragmentInfo.isVisible()) {
//                        schimbaFragment(index, fragmentIntrb);
//                        btnHint.setVisibility(View.VISIBLE);
//                        btnBack.setVisibility(View.VISIBLE);
//                    }

                    // e pe intrebare / sau abia incepe
//                    else {
//                        // ->a raspuns?
//                        if (raspDat != null) {
//                            //da-> e corect?
//                            if (listaChestionare.get(index).getRaspunsCorect().equals(raspDat)) {
//                                // da-> felicitari popup msj  +punctaj
//                                //TODO verifica daca poate reda sunetul (SharedPrefs)
//                                if(sunetRspCorect!=null)
//                                    sunetRspCorect.start();
//                                adaugaPuncte();
//                                // s-a terminat?
//                                if (index + 1 < listaChestionare.size()) {
//                                    // nu->next info
//                                    index++;
//                                    schimbaFragment(index, fragmentInfo);
//                                    btnHint.setVisibility(View.INVISIBLE);
//                                } else {
//                                    // da->mesaj felicitari popup+ inapoi la capitol(+send score si activeaza next)
//                                    showMesajPopup(JocActivity.this,getString(R.string.msj_nivelincheiat));
//                                }
//                            } else {
//                                // nu-> mesaj try again + sunet
//                                //TODO verifica daca poate reda sunetul (SharedPrefs)
//                                if(sunetRspGresit!=null)
//                                    sunetRspGresit.start();
//                                AlertDialog.Builder builder = new AlertDialog.Builder(JocActivity.this);
//                                builder.setMessage(R.string.msj_raspmaiincearca);
//                                builder.setCancelable(true);
//                                builder.setPositiveButton(R.string.alert_ok, new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        dialog.cancel();
//                                    }
//                                });
//                                AlertDialog alertDialog = builder.create();
//                                alertDialog.show();
//                            }
//                        } else {
//                            // mai intai raspunde
//                            Toast.makeText(getApplicationContext(), "Alege un raspuns!", Toast.LENGTH_LONG).show();
//                        }
                    //}
                }
            }
        };
    }

    private void schimbaFragment() {
        if (fragment != null)
            getSupportFragmentManager().beginTransaction().remove(fragment).commit();

        if (listaChestionare.get(index).getRaspunsuri().isEmpty()) {
            fragment = new IntrebareLiberaFragment();
        } else {
            fragment = new IntrebareQuizFragment();
        }
        Bundle b = new Bundle();
        b.putSerializable(TAG_CHESTIONAR, listaChestionare.get(index));
        fragment.setArguments(b);


        getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
    }

    private void adaugaPuncte() {
        punctaj += plusPct;
        String puncte = String.valueOf(punctaj);

        daoUser.updateScor(userKey, puncte);
        setScorDB();
        //scor.setText(daoUser.getScor(userKey));
        //salveazaScor(puncte);
    }

    private void scadePuncte() {
        punctaj -= minusHint;
        String puncte = String.valueOf(punctaj);

        daoUser.updateScor(userKey, puncte);
        setScorDB();
        //scor.setText(daoUser.getScor(userKey));
        //salveazaScor(puncte);
    }

    private void salveazaScor(String puncte) {
        sharedPrefsEditor = getSharedPreferences(getString(R.string.shprefs_scor_numefis), MODE_PRIVATE).edit();
        sharedPrefsEditor.putString(getString(R.string.shprefs_scor), puncte);
        sharedPrefsEditor.apply();
    }

    public void showMesajPopup(Context context, String mesaj, String... args) {
        TextView btnX;
        TextView tvMesaj;
        TextView tvTitlu;
        Button btn;

        templatePopup = new Dialog(context);

        String titluPopup = null;
        if (args.length == 1) {
            titluPopup = args[0];
        }

        templatePopup.setContentView(R.layout.template_popup);
        btnX = templatePopup.findViewById(R.id.tvX);
        tvMesaj = templatePopup.findViewById(R.id.tvMesaj);
        btn = templatePopup.findViewById(R.id.btnMaiDeparte);
        tvTitlu = templatePopup.findViewById(R.id.tvTitlu);

        if (titluPopup != null) {
            tvTitlu.setText(titluPopup);
        }

        if (args.length == 2) {
            btn.setText(args[1]);
        }

        tvMesaj.setText(mesaj);

        if (context.getString(R.string.msj_nivelincheiat).equals(mesaj)) {
            btnX.setOnClickListener(clickFelicitariPopupFinish());
            btn.setOnClickListener(clickFelicitariPopupFinish());
        } else {
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

                //todo pune in sharedprefs pe astea
                intent.putExtra(CODE_TOTALE, listaChestionare.size());
                intent.putExtra(CODE_COMPLETATE, index);

                setResult(RESULT_OK, intent);
                finish();
            }
        };
    }

    public void handleRaspuns(String value) {
        raspDat = value;
        //
        //todo reseteaza timp

        index++;
        //e corect? ne asiguram ca rasp dat de la tastatura e ok si el
        if (listaChestionare.get(index - 1).getRaspunsCorect().toUpperCase().replaceAll("\\s", "")
                .equals(raspDat.toUpperCase().replaceAll("\\s", ""))) {

            if (sunetRspCorect != null) {
                sunetRspCorect.start();
            }
            if (!aCerutHint)
                adaugaPuncte();

        } else {
            // nu-> sunet + next fara puncte :(
            if (sunetRspGresit != null) {
                sunetRspGresit.start();
            }
        }
        // s-a terminat?
        if (index < listaChestionare.size()) {
            // nu->next intreb
            schimbaFragment();
        } else {
            // da->mesaj felicitari popup+ inapoi la capitol(+send score si activeaza next)
            showMesajPopup(JocActivity.this, getString(R.string.msj_nivelincheiat));
        }

        aCerutHint = false;
    }

    @Override
    public void onButtonSelected(String s) {
        handleRaspuns(s);
    }

    @Override
    public void trimiteEventRezultat(String s) {
        handleRaspuns(s);
    }
}