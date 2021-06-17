package com.example.baniimei.activitati;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.baniimei.R;
import com.example.baniimei.clase.Capitol;
import com.example.baniimei.adaptoare.ListaAdaptorInfo;
import com.example.baniimei.clase.Informatie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class CapitoleInfoActivity extends AppCompatActivity {

    private static final String DB_URL_INFO = "http://192.168.0.216/DB_licenta/SelectInformatie.php";
    private static final int REQUEST_CODE_OK = 200;
    static final String INTENT_INFORMATIE = "Info";

    private ListView listView;

    private ArrayList<Capitol> listaCapitole;
    private ArrayList<Informatie> listaInformatii;
    private ListaAdaptorInfo adapter;

    SharedPreferences.Editor sharedPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_BaniiMei);
        setContentView(R.layout.activity_capitole_info);

        // start initializari
        listView = findViewById(R.id.listViewInfo);

        listaInformatii = new ArrayList<>();
        Intent intent = getIntent();
        listaCapitole = (ArrayList<Capitol>) intent.getSerializableExtra(MainActivity.INTENT_LIST);

        // init item clickEvent pt adaptor
        listView.setOnItemClickListener(adapterItemClick());

        SharedPreferences preferinte = getSharedPreferences("Capitole active", MODE_PRIVATE);
        for (int i = 1; i < listaCapitole.size(); i++) {
            listaCapitole.get(i).setActiv(preferinte.getBoolean(String.valueOf(listaCapitole.get(i).getId()), false));
        }

        // init adaptor
        adapter = new ListaAdaptorInfo(CapitoleInfoActivity.this, R.layout.forma_adaptor_info, listaCapitole);
        listView.setAdapter(adapter);

        getInfoDB();
    }

    private AdapterView.OnItemClickListener adapterItemClick() {
        return new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // start InfoActivity
                // trimite lista info corespunzatoare capitolului
                if (listaCapitole.get(position).isActiv()) {
                    ArrayList<Informatie> temp = new ArrayList<>();
                    for (Informatie c : listaInformatii) {
                        if (c.getIdCapitol() == listaCapitole.get(position).getId())
                            temp.add(c);
                    }

                    Intent intent = new Intent(CapitoleInfoActivity.this, InfoActivity.class);
                    intent.putExtra(INTENT_INFORMATIE, temp);
                    startActivityForResult(intent, REQUEST_CODE_OK);
                } else {
                    //todo mesaj sau plateste
                }
            }
        };
    }

    private void getInfoDB() {
        StringRequest request = new StringRequest(Request.Method.GET, DB_URL_INFO,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray chestionareJSON = new JSONArray(response);

                            for (int i = 0; i < chestionareJSON.length(); i++) {
                                JSONObject obiect = chestionareJSON.getJSONObject(i);

                                int id = obiect.getInt("idInfo");
                                String titlu = obiect.getString("titluSubnivel");
                                String informatie = obiect.getString("informatie");
                                String exemplu1 = obiect.getString("exemplu1");
                                String exemplu2 = obiect.getString("exemplu2");
                                int idCapitol = obiect.getInt("idCapitol");

                                Informatie info = new Informatie(id, titlu, informatie, exemplu1, exemplu2, idCapitol);
                                listaInformatii.add(info);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(CapitoleInfoActivity.this, "Eroare baze de date: " + error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
        Volley.newRequestQueue(this).add(request);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            int indexActivare = Capitol.getNrCapitoleActive();
            if (listaCapitole.get(indexActivare) != null) {

                listaCapitole.get(indexActivare).activeaza();
                adapter.notifyDataSetChanged();

                sharedPrefs = getSharedPreferences("Capitole active", MODE_PRIVATE).edit();
                sharedPrefs.putBoolean(String.valueOf(listaCapitole.get(indexActivare).getId()), listaCapitole.get(indexActivare).isActiv());
                sharedPrefs.apply();
            } else {
                //todo mesaj felicitari
            }
        }
    }
}