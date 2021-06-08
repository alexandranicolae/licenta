package com.example.baniimei.activitati;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.baniimei.R;
import com.example.baniimei.clase.Capitol;
import com.example.baniimei.clase.CapitolListaAdaptor;
import com.example.baniimei.clase.Chestionar;
import com.example.baniimei.clase.SunetFundalService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ChestionarCapitoleActivity extends AppCompatActivity {

    private static final String DB_URL_CAPITOL = "http://192.168.0.216/DB_licenta/SelectCapitol.php";
    private static final String DB_URL_CHESTIONAR = "http://192.168.0.216/DB_licenta/SelectChestionar.php";

    private ListView listView;
    private TextView scor;

    private ArrayList<String> listaAdaptor;
    private ArrayList<Capitol> listaCapitole;
    private ArrayList<Chestionar> listaChestionare;

    static final String TAG_SCOR = "scor";
    static final String TAG_CHESTIONARE = "chestionare";
    static int REQUEST_CODE_OK = 300;

    private int nrActive = 0;

    private static MediaPlayer sunetJocIncheiat;

    SharedPreferences preferinteMuzica, prefScor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_BaniiMei);
        setContentView(R.layout.activity_chestionarcapitole);

        // start initializari
        listView = findViewById(R.id.listView);
        scor = findViewById(R.id.tvPuncte);
        sunetJocIncheiat = MediaPlayer.create(this, R.raw.success);

        listaAdaptor = new ArrayList<>();
        listaCapitole = new ArrayList<>();
        listaChestionare= new ArrayList<>();

        preferinteMuzica = getSharedPreferences(getString(R.string.shprefs_numefisier), MODE_PRIVATE);
        handleSunetFundal();

        prefScor=getSharedPreferences(getString(R.string.shprefs_scor_numefis),MODE_PRIVATE);
        scor.setText(prefScor.getString(getString(R.string.shprefs_scor), "15"));

        SharedPreferences preferinte= getSharedPreferences(getString(R.string.shprefs_scor_numefis), MODE_PRIVATE);
        scor.setText(preferinte.getString(getString(R.string.shprefs_scor), "15"));

        getCaptioleDB();
        getChestionareDB();
        //initListaQuizModel(); //daca db e inactiva
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handleSunetFundal();
    }

    @Override
    protected void onResume() {
        super.onResume();
        scor.setText(prefScor.getString(getString(R.string.shprefs_scor), "15"));
    }

    private void initAfterDB(){
        // init lista de strings pt adaptor
        int i = 0;
        for (Capitol quiz : listaCapitole) {
            StringBuilder builder = new StringBuilder("LvL");
            builder.append(i + 1);
            i++;
            builder.append("-");
            builder.append(quiz.getNumeCapitol());
            listaAdaptor.add(builder.toString());
        }

        // init adaptor
        CapitolListaAdaptor adapter = new CapitolListaAdaptor(ChestionarCapitoleActivity.this, R.layout.forma_adaptor, listaAdaptor);
        listView.setAdapter(adapter);

        // init item clickEvent pt adaptor
        listView.setOnItemClickListener(adapterItemClick());
    }

    private AdapterView.OnItemClickListener adapterItemClick() {
        return new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                if (listaCapitole.get(position).isActiv()) {
                    // start ChestionarActivity
                    // trimite scor si lista chestionare corespunzatoare capitolului

                    Intent intent = new Intent(getApplicationContext(), ChestionarActivity.class);
                    intent.putExtra(TAG_SCOR, scor.getText());

                    Bundle bundle = new Bundle();

                    List<Chestionar> temp=new ArrayList<>();
                    for (Chestionar c: listaChestionare) {
                        if(c.getIdCapitol() == listaCapitole.get(position).getId())
                            temp.add(c);
                    }
                    bundle.putSerializable(TAG_CHESTIONARE, (Serializable) temp);
                    intent.putExtras(bundle);

                    startActivityForResult(intent, REQUEST_CODE_OK);
                } else {
                    // ALERTA nivel inactiv

                    AlertDialog.Builder builder = new AlertDialog.Builder(ChestionarCapitoleActivity.this);
                    builder.setMessage(R.string.msg_nivelinactiv);
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
            }
        };
    }

    // TODO preia din bd
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
                            listaCapitole.get(0).setActiv(true);
                        }
                        initAfterDB();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ChestionarCapitoleActivity.this, "Eroare baze de date: " + error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
        Volley.newRequestQueue(this).add(request);

    }

    private void getChestionareDB() {
        StringRequest request = new StringRequest(Request.Method.GET, DB_URL_CHESTIONAR,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray chestionareJSON = new JSONArray(response);

                            for (int i = 0; i < chestionareJSON.length(); i++) {
                                JSONObject obiect = chestionareJSON.getJSONObject(i);

                                int id = obiect.getInt("idChestionar");
                                String titlu = obiect.getString("titluSubnivel");
                                String informatie = obiect.getString("informatie");
                                String exemplu1 = obiect.getString("exemplu1");
                                String exemplu2 = obiect.getString("exemplu2");
                                String intrebare = obiect.getString("intrebare");
                                String raspunsCorect = obiect.getString("raspunsCorect");
                                String raspunsuriString = obiect.getString("raspunsuri");
                                List<String> raspunsuri = Arrays.asList(raspunsuriString.split(" , "));
                                String indiciu = obiect.getString("indiciu");
                                int idCapitol= obiect.getInt("idCapitol");

                                Chestionar chestionar = new Chestionar(id, titlu,informatie,exemplu1,exemplu2,intrebare,raspunsCorect,raspunsuri,indiciu,idCapitol);
                                listaChestionare.add(chestionar);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ChestionarCapitoleActivity.this, "Eroare baze de date: " + error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
        Volley.newRequestQueue(this).add(request);
    }

    private void initListaQuizModel() {
        ArrayList<String> rasp = new ArrayList<>();
        rasp.add("rasp b");
        rasp.add("rasp c");
        rasp.add("rasp d");

        Capitol c0 = new Capitol(1, "Capitol proba1");
        c0.setActiv(true);
        listaCapitole.add(c0);
        listaCapitole.add(new Capitol(2, "Capitol proba2"));
        listaCapitole.add(new Capitol(3, "Capitol proba3"));

        listaChestionare.add(new Chestionar(10,"Titlu1", "Info1", "ex1.1", "ex2.2", "Intrebare1?", "rasp a", rasp, "mesaj1",1));
        listaChestionare.add(new Chestionar(20,"Titlu2", "Info2", "ex1.2", "ex2.2", "Intrebare2?", "rasp a", rasp, "mesaj2",1));
        listaChestionare.add(new Chestionar(30,"Titlu3", "Info3", "ex1.3", "ex2.3", "Intrebare3?", "rasp a", rasp, "mesaj3",1));
        listaChestionare.add(new Chestionar(10,"Titlu1", "Info1", "ex1.1", "ex2.2", "Intrebare1?", "rasp a", rasp, "mesaj1",2));
        listaChestionare.add(new Chestionar(20,"Titlu2", "Info2", "ex1.2", "ex2.2", "Intrebare2?", "rasp a", rasp, "mesaj2",2));
        listaChestionare.add(new Chestionar(30,"Titlu3", "Info3", "ex1.3", "ex2.3", "Intrebare3?", "rasp a", rasp, "mesaj3",2));
        listaChestionare.add(new Chestionar(10,"Titlu1", "Info1", "ex1.1", "ex2.2", "Intrebare1?", "rasp a", rasp, "mesaj1",3));
        listaChestionare.add(new Chestionar(20,"Titlu2", "Info2", "ex1.2", "ex2.2", "Intrebare2?", "rasp a", rasp, "mesaj2",3));
        listaChestionare.add(new Chestionar(30,"Titlu3", "Info3", "ex1.3", "ex2.3", "Intrebare3?", "rasp a", rasp, "mesaj3",3));
    }

    // preluare scor din chestionarActivity, activeaza nivel urmator
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null) {
            // requestCode == REQUEST_CODE_OK &&
//            Bundle b = data.getExtras();
//            int s = (int) b.getSerializable(ChestionarActivity.CODE_SCOR);
//            scor.setText(String.valueOf(s));
            scor.setText(prefScor.getString(getString(R.string.shprefs_scor), "15"));

            activeazaNivelUrmator();
        }
    }

    // activeaza nivel urmator; daca exista, daca nu -> felicitari, joc incheiat
    private void activeazaNivelUrmator(){
        nrActive++;
        if (nrActive < listaCapitole.size()) {
            listaCapitole.get(nrActive).setActiv(true);
        } else {
            //mesaj felicitari joc incheiat
            sunetJocIncheiat.start();
            ChestionarActivity c = new ChestionarActivity();
            c.showMesajPopup(ChestionarCapitoleActivity.this, getString(R.string.joc_incheiat), getString(R.string.titlu_joc_incheiat), getString(R.string.ok_finish));
        }
    }

    private void handleSunetFundal() {
        if(preferinteMuzica.getBoolean(getString(R.string.shprefs_muzica_key), true)) {
            if (isMyServiceRunning(SunetFundalService.class)) {
                stopService(new Intent(ChestionarCapitoleActivity.this, SunetFundalService.class));
            } else {
                startService(new Intent(ChestionarCapitoleActivity.this, SunetFundalService.class));
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
}