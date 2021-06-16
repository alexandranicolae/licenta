package com.example.baniimei.activitati;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityManager;
import android.app.AlarmManager;
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

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.baniimei.R;
import com.example.baniimei.clase.Capitol;
import com.example.baniimei.clase.Notificare;
import com.example.baniimei.clase.SunetFundalService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String DB_URL_CAPITOL = "http://192.168.0.216/DB_licenta/SelectCapitol.php";
    protected static final String INTENT_LIST = "List";

    private Button btnSetari;
    private Button btnStart, btnInformatii;
    private Button btnExit;

    private ArrayList<Capitol> listaCapitole;

    SharedPreferences preferinteMuzica;
    SharedPreferences shNotificari;

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

        getCaptioleDB();
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
                            Capitol capitol = new Capitol(id, titlu);
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