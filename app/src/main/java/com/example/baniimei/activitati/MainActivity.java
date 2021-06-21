package com.example.baniimei.activitati;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.baniimei.R;
import com.example.baniimei.clase.Capitol;
import com.example.baniimei.clase.DAOUser;
import com.example.baniimei.clase.Dificultate;
import com.example.baniimei.clase.Notificare;
import com.example.baniimei.clase.SunetFundalService;
import com.example.baniimei.clase.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String DB_URL_CAPITOL = "http://alexandral.bestconstruct.ro//SelectCapitol.php";
    protected static final String INTENT_LIST = "List";

    private Button btnSetari;
    private Button btnStart, btnInformatii;
    private Button btnExit;

    private ArrayList<Capitol> listaCapitole;

    SharedPreferences preferinteMuzica;
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

        preferinteMuzica = getSharedPreferences(getString(R.string.shprefs_numefisier), MODE_PRIVATE);
        handleSunetFundal();

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
        if (idUser.equals("") || idUser == null) {
            requireNume();
        }

        getCaptioleDB();
    }

    private void requireNume() {
        EditText etNume;
        TextView tvEroare;
        Button btn;

        numePopup = new Dialog(MainActivity.this);
        numePopup.setCanceledOnTouchOutside(false);

        numePopup.setContentView(R.layout.nume_popup);
        etNume = numePopup.findViewById(R.id.etNume);
        tvEroare = numePopup.findViewById(R.id.tvEroareNume);
        btn = numePopup.findViewById(R.id.btnOkNume);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String input = etNume.getText().toString();

                if (input.isEmpty() || input.length() < 3) {
                    etNume.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.red, null));
                    tvEroare.setVisibility(View.VISIBLE);
                } else {
                    User user = new User(input);
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

                    SharedPreferences.Editor sharedPrefsEditor = getSharedPreferences("DATE_USER", MODE_PRIVATE).edit();
                    sharedPrefsEditor.putString("ID_USER", cheie);
                    sharedPrefsEditor.apply();

                    numePopup.dismiss();
                }
            }
        });

        numePopup.show();
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
        handleSunetFundal();
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
                AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);
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

    private void handleSunetFundal() {
        if (preferinteMuzica.getBoolean(getString(R.string.shprefs_muzica_key), true)) {
            if (isMyServiceRunning(SunetFundalService.class)) {
                stopService(new Intent(MainActivity.this, SunetFundalService.class));
            } else {
                startService(new Intent(MainActivity.this, SunetFundalService.class));
            }
        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
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
                        listaCapitole.get(0).activeaza();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, "Eroare baze de date Capitole: " + error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
        Volley.newRequestQueue(this).add(request);
    }

}