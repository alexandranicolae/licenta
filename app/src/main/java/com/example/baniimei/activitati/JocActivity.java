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
import com.example.baniimei.clase.Intrebare;
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

    private static final int minusHint = 5;
    private static final int plusPct = 5;

    //private static MediaPlayer sunetJocIncheiat = null;
    private static MediaPlayer sunetRspGresit = null;
    private static MediaPlayer sunetRspCorect = null;

    Fragment fragment = null;

    int index;

    TextView scor;
    int punctaj;

    ImageButton btnHint;
    TextView tvNrIntrebare;
    Intent intent;
    Dialog templatePopup;

    private DAOUser daoUser;

    SharedPreferences preferinteSunet, prefUser;
    private String userKey = "";

    private ArrayList<Intrebare> listaIntrebari;

    private Boolean aCerutHint = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_BaniiMei);
        setContentView(R.layout.activity_joc);

        btnHint = findViewById(R.id.btnHint);
        scor = findViewById(R.id.tvScorJoc);
        tvNrIntrebare = findViewById(R.id.tvNrIntrebare);

        preferinteSunet = getSharedPreferences(getString(R.string.shprefs_numefisier), MODE_PRIVATE);
        if (preferinteSunet.getBoolean(getString(R.string.shprefs_sunet_key), true)) {
            sunetRspGresit = MediaPlayer.create(this, R.raw.wrong);
            sunetRspCorect = MediaPlayer.create(this, R.raw.corect);
            //sunetJocIncheiat = MediaPlayer.create(this, R.raw.success);
        }

        prefUser = getSharedPreferences("DATE_USER", MODE_PRIVATE);
        userKey = prefUser.getString("ID_USER", "");
        daoUser = new DAOUser();

        intent = getIntent();
        listaIntrebari = (ArrayList<Intrebare>) intent.getSerializableExtra(CapitoleJocActivity.INTENT_INTREBARE);

        index = 0;

        btnHint.setOnClickListener(clickBtnHint());
        setScorDB();
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
                    builder.setPositiveButton(R.string.da, (dialog, which) -> {
                        if (punctaj >= minusHint) {
                            showMesajPopup(JocActivity.this, listaIntrebari.get(index).getIndiciu(), getString(R.string.hint_title));
                            scadePuncte();
                            aCerutHint = true;
                        } else {
                            Toast.makeText(JocActivity.this, "Fonduri insuficiente!", Toast.LENGTH_LONG).show();
                        }
                        dialog.cancel();
                    });
                    builder.setNegativeButton(R.string.nu, (dialog, which) -> dialog.cancel());
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                } else {
                    showMesajPopup(JocActivity.this, listaIntrebari.get(index).getIndiciu(), getString(R.string.hint_title));
                }
            }
        };
    }

    private void schimbaFragment() {
        if (fragment != null)
            getSupportFragmentManager().beginTransaction().remove(fragment).commit();

        if (listaIntrebari.get(index).getRaspunsuri().isEmpty()) {
            fragment = new IntrebareLiberaFragment();
        } else {
            fragment = new IntrebareQuizFragment();
        }

        Bundle b = new Bundle();
        b.putSerializable(TAG_CHESTIONAR, listaIntrebari.get(index));
        fragment.setArguments(b);


        getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();

        seteazaTextNrIntrebare();
    }

    private void seteazaTextNrIntrebare() {
        String builder = (index + 1) + " / " + listaIntrebari.size();
        tvNrIntrebare.setText(builder);
    }

    private void adaugaPuncte() {
        punctaj += plusPct;
        String puncte = String.valueOf(punctaj);

        daoUser.updateScor(userKey, puncte);
        setScorDB();
    }

    private void scadePuncte() {
        punctaj -= minusHint;
        String puncte = String.valueOf(punctaj);

        daoUser.updateScor(userKey, puncte);
        setScorDB();
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

        if ("Joc incheiat".equals(mesaj)) {
            tvTitlu.setText(mesaj);
            tvMesaj.setText("Nivelul s-a incheiat! Poti alege alta categorie, sau tot pe aceasta pentru a accesa intrebari noi!");
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
                setResult(RESULT_OK, intent);
                finish();
            }
        };
    }

    public void handleRaspuns(String raspunsDat) {
        index++;
        //e corect? ne asiguram ca rasp dat de la tastatura e ok si el
        if (listaIntrebari.get(index - 1).getRaspunsCorect().toUpperCase().replaceAll("\\s", "")
                .equals(raspunsDat.toUpperCase().replaceAll("\\s", ""))) {
            if (sunetRspCorect != null) {
                sunetRspCorect.start();
            }
            if (!aCerutHint) {
                adaugaPuncte();
            }
        } else {
            // nu-> sunet + next fara puncte :(
            if (sunetRspGresit != null) {
                sunetRspGresit.start();
            }
        }
        // s-a terminat?
        if (index < listaIntrebari.size()) {
            // nu->next intreb
            schimbaFragment();
        } else {
            // da->mesaj felicitari popup+ inapoi la capitol(+send score si activeaza next)
            showMesajPopup(JocActivity.this, "Joc incheiat");
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