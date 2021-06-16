package com.example.baniimei.activitati;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.example.baniimei.clase.Categorie;
import com.example.baniimei.clase.Chestionar;
import com.example.baniimei.clase.ListaAdaptorIntrebare;
import com.example.baniimei.clase.SunetFundalService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CapitoleJocActivity extends AppCompatActivity {

    private static final String DB_URL_INTREBARE = "http://192.168.0.216/DB_licenta/SelectIntrebare.php";
    private static final int NR_INTR_RANDOM = 5;
    private static final int REQUEST_CODE_OK = 300;
    static final String INTENT_INTREBARE = "INTREBARE";
    static final String INTENT_CATEGORIE = "CATEGORIE";

    private ListView listView;
    private TextView scor;

    private ArrayList<Capitol> listaCapitole;
    private ArrayList<Chestionar> listaIntrebari;
    private ListaAdaptorIntrebare adapter;

    SharedPreferences prefScor;

    private int pozUltimAccesat = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_BaniiMei);
        setContentView(R.layout.activity_capitole_joc);

        listView = findViewById(R.id.listViewJoc);
        scor = findViewById(R.id.tvScor);

        listaIntrebari = new ArrayList<>();
        Intent intent = getIntent();
        listaCapitole = (ArrayList<Capitol>) intent.getSerializableExtra(MainActivity.INTENT_LIST);

        Capitol capitol = new Capitol(0, "Random");
        capitol.setCategorie(Categorie.RANDOM);
        listaCapitole.add(0, capitol);
        Capitol capitol1 = new Capitol(99, "Puzzle");
        capitol1.setCategorie(Categorie.PUZZLE);
        listaCapitole.add(capitol1);

        prefScor = getSharedPreferences(getString(R.string.shprefs_scor_numefis), MODE_PRIVATE);

        // init item clickEvent pt adaptor
        listView.setOnItemClickListener(adapterItemClick());

        // init adaptor
        adapter = new ListaAdaptorIntrebare(CapitoleJocActivity.this, R.layout.forma_adaptor_joc, listaCapitole);
        listView.setAdapter(adapter);

        getIntrebariDB();
    }

    @Override
    protected void onResume() {
        super.onResume();
        scor.setText(prefScor.getString(getString(R.string.shprefs_scor), "15"));
    }

    private AdapterView.OnItemClickListener adapterItemClick() {
        return new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // start JocActivity
                // trimite lista intrebari corespunzatoare capitolului
                Capitol capitol = listaCapitole.get(position);

                pozUltimAccesat = position;

                Intent intent = new Intent(CapitoleJocActivity.this, JocActivity.class);
                ArrayList<Chestionar> temp = new ArrayList<>();
                switch (capitol.getCategorie()) {
                    case CHESTIONAR: {
                        for (Chestionar c : listaIntrebari) {
                            if (c.getIdCapitol() == capitol.getId())
                                temp.add(c);
                        }
                        break;
                    }
                    case RANDOM: {
                        Collections.shuffle(listaIntrebari);
                        for (int i = 0; i < NR_INTR_RANDOM; i++) {
                            temp.add(listaIntrebari.get(i));
                        }
                        break;
                    }
                }
                intent.putExtra(INTENT_INTREBARE, temp);
                intent.putExtra(INTENT_CATEGORIE, capitol.getCategorie());
                startActivityForResult(intent, REQUEST_CODE_OK);
            }
        };
    }

    private void getIntrebariDB() {
        StringRequest request = new StringRequest(Request.Method.GET, DB_URL_INTREBARE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray chestionareJSON = new JSONArray(response);

                            for (int i = 0; i < chestionareJSON.length(); i++) {
                                JSONObject obiect = chestionareJSON.getJSONObject(i);

                                int id = obiect.getInt("idIntrebare");
                                String intrebare = obiect.getString("intrebare");
                                String raspunsCorect = obiect.getString("raspunsCorect");
                                String raspunsuriString = obiect.getString("raspunsuri"); //da "NULL"

                                List<String> raspunsuri = new ArrayList<>();
                                if (raspunsuriString != null && !(raspunsuriString.toUpperCase().equals("NULL"))) {
                                    raspunsuri = Arrays.asList(raspunsuriString.split(" , "));
                                }

                                String indiciu = obiect.getString("indiciu");
                                int idCapitol = obiect.getInt("idCapitol");

                                Chestionar intr = new Chestionar(id, intrebare, raspunsCorect, raspunsuri, indiciu, idCapitol);
                                listaIntrebari.add(intr);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(CapitoleJocActivity.this, "Eroare baze de date: " + error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
        Volley.newRequestQueue(this).add(request);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            Bundle b = data.getExtras();
            int nrCompletate = (int) b.getSerializable(JocActivity.CODE_COMPLETATE);
            int nrTotale = (int) b.getSerializable(JocActivity.CODE_TOTALE);

            listaCapitole.get(pozUltimAccesat).setNrChCompletate(nrCompletate);
            listaCapitole.get(pozUltimAccesat).setNrChTotale(nrTotale);

            scor.setText(prefScor.getString(getString(R.string.shprefs_scor), "15"));
        }
    }
}