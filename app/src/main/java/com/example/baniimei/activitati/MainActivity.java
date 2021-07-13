package com.example.baniimei.activitati;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.baniimei.R;
import com.example.baniimei.clase.Capitol;
import com.example.baniimei.clase.DAOUser;
import com.example.baniimei.clase.Dificultate;
import com.example.baniimei.clase.Notificare;
import com.example.baniimei.clase.SunetFundalService;
import com.example.baniimei.clase.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class MainActivity extends AppCompatActivity {

    private static final String DB_URL_CAPITOL = "http://alexandral.bestconstruct.ro//SelectCapitol.php";
    protected static final String INTENT_LIST = "List";

    private Button btnSetari;
    private Button btnStart, btnInformatii;
    private Button btnExit;

    private ArrayList<Capitol> listaCapitole;

    SharedPreferences shNotificari;
    SharedPreferences sharedPrefsNume;

    Dialog numePopup;

    private DAOUser dbUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_BaniiMei);
        setContentView(R.layout.activity_main);

        init();

        btnSetari.setOnClickListener(setariClick());
        btnExit.setOnClickListener(exitClick());
        btnStart.setOnClickListener(startClick());
        btnInformatii.setOnClickListener(informatiiClick());

        listaCapitole = new ArrayList<>();
        dbUser = new DAOUser();

        handleSunetFundal(this);

        shNotificari = getSharedPreferences(getString(R.string.shprefs_numefisier), MODE_PRIVATE);
        if (shNotificari.getBoolean(getString(R.string.shprefs_notificari_key), true)) {
            creareCanalNotificare();
            Intent intentNotificare = new Intent(MainActivity.this, Notificare.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, intentNotificare, 0);

            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            alarmManager.setInexactRepeating(AlarmManager.RTC, System.currentTimeMillis() + 1000 * 10, AlarmManager.INTERVAL_DAY, pendingIntent);
        }

        sharedPrefsNume = getSharedPreferences("DATE_USER", MODE_PRIVATE);
        String idUser = sharedPrefsNume.getString("ID_USER", "");
        if (idUser.equals("")) {
            requireNume();
        }

        getCaptioleDB();
    }

    private void requireNume() {
        TextInputLayout etInputNume;

        Button btn;

        numePopup = new Dialog(MainActivity.this);
        numePopup.setCanceledOnTouchOutside(false);

        numePopup.setContentView(R.layout.nume_popup);
        etInputNume = numePopup.findViewById(R.id.etInputNume);

        btn = numePopup.findViewById(R.id.btnOkNume);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean eCorect = eCorectNumele(etInputNume);
                if (!eCorect) {
                    return;
                }
                String input = etInputNume.getEditText().getText().toString().trim();
                User user = new User(input);

                adaugaInDB(user);
                numePopup.dismiss();
            }
        });

        numePopup.show();
    }

    private void adaugaInDB(User user) {
        String cheie = dbUser.getDatabaseReference().push().getKey();
        dbUser.getDatabaseReference().child(cheie).setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(MainActivity.this, "Bine ai venit, " + user.getNume() + "!", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this, "EROARE BAZA DE DATE!", Toast.LENGTH_SHORT).show();
            }
        });

        salveazaCheie(cheie);
    }

    private void salveazaCheie(String cheie) {
        SharedPreferences.Editor sharedPrefsEditor = getSharedPreferences("DATE_USER", MODE_PRIVATE).edit();
        sharedPrefsEditor.putString("ID_USER", cheie);
        sharedPrefsEditor.apply();
    }

    public boolean eCorectNumele(TextInputLayout etInputNume) {
        String input = etInputNume.getEditText().getText().toString().trim();
        if (input.isEmpty() || input.length() < 3) {
            etInputNume.setError("Numele trebuie sa aiba minim 3 caractere");
            return false;
        } else {
            etInputNume.setError(null);
            //etInputNume.setErrorEnabled(false);
            return true;
        }
    }

    private void init() {
        btnSetari = findViewById(R.id.btnSettings);
        btnStart = findViewById(R.id.btnStart);
        btnInformatii = findViewById(R.id.btnInformatii);
        btnExit = findViewById(R.id.btnExit);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        handleSunetFundal(this);
    }

    private View.OnClickListener informatiiClick() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CapitoleInfoActivity.class);
                intent.putExtra(INTENT_LIST, listaCapitole);
                startActivity(intent);
            }
        };
    }

    private View.OnClickListener startClick() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CapitoleJocActivity.class);
                intent.putExtra(INTENT_LIST, listaCapitole);
                startActivity(intent);
            }
        };
    }

    private View.OnClickListener setariClick() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SetariActivity.class);
                startActivity(intent);
            }
        };
    }

    private View.OnClickListener exitClick() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage(R.string.exit_message);
                builder.setCancelable(true);
                builder.setNegativeButton(R.string.exit_da, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
                builder.setPositiveButton(R.string.exit_nu, new DialogInterface.OnClickListener() {
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

    protected static void handleSunetFundal(Activity activity) {
        if (isMyServiceRunning(activity)) {
            activity.stopService(new Intent(activity, SunetFundalService.class));
        } else {
            activity.startService(new Intent(activity, SunetFundalService.class));
        }
    }

    private static boolean isMyServiceRunning(Activity activity) {
        ActivityManager manager = (ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (SunetFundalService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private void creareCanalNotificare() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String nume = "CanalNotificareHaiLaJoaca";

            NotificationChannel canal = new NotificationChannel("notifyJoc", nume, NotificationManager.IMPORTANCE_DEFAULT);

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(canal);
        }
    }

    private void getCaptioleDB() {
        StringRequest request = new StringRequest(Request.Method.GET, DB_URL_CAPITOL,
                response -> {
                    try {
                        JSONArray capitoleJson = new JSONArray(response);

                        for (int i = 0; i < capitoleJson.length(); i++) {
                            JSONObject obiect = capitoleJson.getJSONObject(i);

                            int id = obiect.getInt("idCapitol");
                            String titlu = obiect.getString("numeCapitol");
                            Dificultate dificultate = Dificultate.valueOf(obiect.getString("dificultate").toUpperCase());
                            Capitol capitol = new Capitol(id, titlu, dificultate);
                            listaCapitole.add(capitol);
                        }
                        sortareLista();
                        listaCapitole.get(0).setActiv(true);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(MainActivity.this, "Eroare baze de date Capitole: " + error.getMessage(), Toast.LENGTH_LONG).show());
        Volley.newRequestQueue(this).add(request);
    }

    public void sortareLista() {
        Collections.sort(listaCapitole, new Comparator<Capitol>() {
            @Override
            public int compare(Capitol c1, Capitol c2) {
                if (c1.getDificultate() == c2.getDificultate()) {
                    return c1.getNumeCapitol().compareTo(c2.getNumeCapitol());
                } else {
                    return c1.getDificultate().compareTo(c2.getDificultate());
                }
            }
        });
    }
}