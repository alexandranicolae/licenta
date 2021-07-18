package com.example.baniimei.activitati;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.app.Dialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.baniimei.R;
import com.example.baniimei.clase.Evaluare;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import static android.os.Environment.DIRECTORY_DOWNLOADS;

public class SetariActivity extends AppCompatActivity {

    SwitchCompat swSunet, swMuzica, swNotificari;
    TextView tvDespre, tvEvalueaza, tvDescarca;
    Dialog desprePopup, evalueazaPopup;
    SharedPreferences.Editor sharedPrefs;

    StorageReference storageReference;
    StorageReference reference;
    private static final String PDF_REF = "Fișe de lucru.pdf";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_BaniiMei);
        setContentView(R.layout.activity_setari);

        init();
        initPrefs();

        swSunet.setOnCheckedChangeListener(sunetClick());
        swMuzica.setOnCheckedChangeListener(muzicaClick());
        swNotificari.setOnCheckedChangeListener(notificariClick());

        tvDespre.setOnClickListener(despreClick());
        tvEvalueaza.setOnClickListener(evalueazaClick());
        tvDescarca.setOnClickListener(descarcaClick());
    }

    private void initPrefs() {
        SharedPreferences preferinte= getSharedPreferences(getString(R.string.shprefs_numefisier), MODE_PRIVATE);
        swSunet.setChecked(preferinte.getBoolean(getString(R.string.shprefs_sunet_key), true));
        swMuzica.setChecked(preferinte.getBoolean(getString(R.string.shprefs_muzica_key), true));
        swNotificari.setChecked(preferinte.getBoolean(getString(R.string.shprefs_notificari_key), true));
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
                        Evaluare evaluare = new Evaluare(rating.getRating(), mesaj.getText().toString());
                        trimiteEvaluare(evaluare);
                        Toast.makeText(getApplicationContext(), rating.getRating() + " " + mesaj.getText(), Toast.LENGTH_LONG).show();
                        //de retinut ca rating bar-ul e de la 0 la 5!
                        evalueazaPopup.dismiss();
                    }
                });

                evalueazaPopup.show();
            }
        };
    }

    private void trimiteEvaluare(Evaluare evaluare) {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference dbref = db.getReference(Evaluare.class.getSimpleName());
        dbref.push().setValue(evaluare);
    }

    private View.OnClickListener descarcaClick() {
        return v -> {
            storageReference = FirebaseStorage.getInstance().getReference();
            reference = storageReference.child(PDF_REF);
            reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    DownloadManager manager = (DownloadManager) SetariActivity.this.getSystemService(Context.DOWNLOAD_SERVICE);
                    DownloadManager.Request request = new DownloadManager.Request(uri);
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                    request.setDestinationInExternalFilesDir(SetariActivity.this, DIRECTORY_DOWNLOADS, PDF_REF);

                    manager.enqueue(request);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(SetariActivity.this, "Serviciu indisponibil! Va rugam incercati mai tarziu!", Toast.LENGTH_LONG).show();
                }
            });
        };
    }

    private CompoundButton.OnCheckedChangeListener notificariClick() {
        return (buttonView, isChecked) -> {
            sharedPrefs = getSharedPreferences(getString(R.string.shprefs_numefisier), MODE_PRIVATE).edit();
            sharedPrefs.putBoolean(getString(R.string.shprefs_notificari_key), isChecked);
            sharedPrefs.apply();
        };
    }

    private CompoundButton.OnCheckedChangeListener muzicaClick() {
        return (buttonView, isChecked) -> {
            sharedPrefs = getSharedPreferences(getString(R.string.shprefs_numefisier), MODE_PRIVATE).edit();
            sharedPrefs.putBoolean(getString(R.string.shprefs_muzica_key), isChecked);
            sharedPrefs.apply();

            MainActivity.handleSunetFundal(this);
        };
    }

    private CompoundButton.OnCheckedChangeListener sunetClick() {
        return (buttonView, isChecked) -> {
            sharedPrefs = getSharedPreferences(getString(R.string.shprefs_numefisier), MODE_PRIVATE).edit();
            sharedPrefs.putBoolean(getString(R.string.shprefs_sunet_key), isChecked);
            sharedPrefs.apply();
        };
    }
}