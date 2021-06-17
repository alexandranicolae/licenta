package com.example.baniimei.activitati;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
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
import com.example.baniimei.adaptoare.ListaAdaptorIntrebare;
import com.example.baniimei.clase.DAOUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class CapitoleJocActivity extends AppCompatActivity {

    private static final String DB_URL_INTREBARE = "http://192.168.0.216/DB_licenta/SelectIntrebare.php";
    private static final int NR_INTR_RANDOM = 5;
    private static final int REQUEST_CODE_OK = 300;
    private static final int REQUEST_CODE_OK_ROATA = 400;
    static final String INTENT_INTREBARE = "INTREBARE";
    static final String INTENT_CATEGORIE = "CATEGORIE";

    private ListView listView;
    private TextView scor;
    private ImageButton btnRoata, btnMagazin, btnClasament;

    private ArrayList<Capitol> listaCapitole;
    private ArrayList<Chestionar> listaIntrebari;
    private ListaAdaptorIntrebare adapter;

    SharedPreferences prefScor;
    SharedPreferences.Editor sharedPrefsEditor;

    private int pozUltimAccesat = -1;

    private DAOUser daoUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_BaniiMei);
        setContentView(R.layout.activity_capitole_joc);

        listView = findViewById(R.id.listViewJoc);
        scor = findViewById(R.id.tvScorCapitoleJoc);
        btnRoata = findViewById(R.id.ibRoata);
        btnMagazin = findViewById(R.id.ibMagazin);
        btnClasament = findViewById(R.id.ibClasament);

        listaIntrebari = new ArrayList<>();
        Intent intent = getIntent();
        listaCapitole = (ArrayList<Capitol>) intent.getSerializableExtra(MainActivity.INTENT_LIST);

        Capitol capitol = new Capitol(0, "Random");
        capitol.setCategorie(Categorie.RANDOM);
        listaCapitole.add(0, capitol);

        prefScor = getSharedPreferences(getString(R.string.shprefs_scor_numefis), MODE_PRIVATE);

        daoUser = new DAOUser();
        // init adaptor
        listView.setOnItemClickListener(adapterItemClick());
        adapter = new ListaAdaptorIntrebare(CapitoleJocActivity.this, R.layout.forma_adaptor_joc, listaCapitole);
        listView.setAdapter(adapter);

        btnRoata.setOnClickListener(clickRoata());
        btnMagazin.setOnClickListener(clickMagazin());
        btnClasament.setOnClickListener(clickClasament());
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

                Collections.shuffle(listaIntrebari);

                switch (capitol.getCategorie()) {
                    case CHESTIONAR: {
                        int i = 0;
                        for (Chestionar c : listaIntrebari) {
                            if (i == NR_INTR_RANDOM) {
                                break;
                            }
                            if (c.getIdCapitol() == capitol.getId()) {
                                temp.add(c);
                                i++;
                            }
                        }
                        break;
                    }
                    case RANDOM: {
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

    private View.OnClickListener clickClasament() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CapitoleJocActivity.this, ClasamentActivity.class);
                startActivity(intent);
            }
        };
    }

    private View.OnClickListener clickMagazin() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        };
    }

    private View.OnClickListener clickRoata() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CapitoleJocActivity.this, RoataActivity.class);
                startActivityForResult(intent, REQUEST_CODE_OK_ROATA);
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
        if (requestCode == REQUEST_CODE_OK && resultCode == RESULT_OK && data != null) {
            Bundle b = data.getExtras();
            int nrCompletate = (int) b.getSerializable(JocActivity.CODE_COMPLETATE);
            int nrTotale = (int) b.getSerializable(JocActivity.CODE_TOTALE);

            listaCapitole.get(pozUltimAccesat).setNrChCompletate(nrCompletate);
            listaCapitole.get(pozUltimAccesat).setNrChTotale(nrTotale);

            scor.setText(prefScor.getString(getString(R.string.shprefs_scor), "15"));
        }

        if (requestCode == REQUEST_CODE_OK_ROATA && resultCode == RESULT_OK && data != null) {
            Bundle b = data.getExtras();
            int puncteCastigate = (int) b.getSerializable(RoataActivity.INTENT_PUNCTE);

            int puncteBefore = Integer.parseInt(String.valueOf(scor.getText()));
            int scorNou = puncteCastigate + puncteBefore;
            scor.setText(String.valueOf(scorNou));

            sharedPrefsEditor = getSharedPreferences(getString(R.string.shprefs_scor_numefis), MODE_PRIVATE).edit();
            sharedPrefsEditor.putString(getString(R.string.shprefs_scor), String.valueOf(scor.getText()));
            sharedPrefsEditor.apply();
        }
    }
}