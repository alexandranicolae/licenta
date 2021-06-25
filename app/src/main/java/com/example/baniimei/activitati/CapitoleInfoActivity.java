package com.example.baniimei.activitati;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
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
import java.util.List;

public class CapitoleInfoActivity extends AppCompatActivity {

    private static final String DB_URL_INFO = "http://alexandral.bestconstruct.ro/SelectInformatie.php";
    private static final int REQUEST_CODE_OK = 200;
    static final String INTENT_INFORMATIE = "Info";
    static final String INTENT_CAPITOL = "POZITIE";

    private ListView listView;

    private List<Capitol> listaCapitole;
    private List<Capitol> listaCapitoleInitiala;
    private List<Informatie> listaInformatii;
    private ListaAdaptorInfo adapter;
    private String query = "";
    private List<Informatie> listaInfoFiltrata;
    SharedPreferences.Editor sharedPrefs;

    int indexActivare = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_BaniiMei_ActionBar);
        setContentView(R.layout.activity_capitole_info);

        // start initializari
        listView = findViewById(R.id.listViewInfo);

        listaInformatii = new ArrayList<>();
        //listaInfoFiltrata = new ArrayList<>();
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
        listaCapitoleInitiala = new ArrayList<>(listaCapitole);
        getInfoDB();
    }

    private void updateListCapitole() {
        listaCapitole.clear();
        listaInfoFiltrata.clear();
        for (Capitol c : listaCapitoleInitiala) {
            Informatie infoCapitol = null;
            for (Informatie i : listaInformatii) {
                if (c.getId() == i.getIdCapitol()) {
                    infoCapitol = i;

                    if (infoCapitol != null && infoCapitol.getInformatie().toUpperCase().contains(query.toUpperCase())) {
                        listaInfoFiltrata.add(infoCapitol);
                        if (!listaCapitole.contains(c)) {
                            listaCapitole.add(c);
                        }
                    }
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    private AdapterView.OnItemClickListener adapterItemClick() {
        return new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // start InfoActivity
                // trimite lista info corespunzatoare capitolului
                if (listaCapitole.get(position).isActiv()) {
                    ArrayList<Informatie> temp = new ArrayList<>();
                    for (Informatie c : listaInfoFiltrata) {
                        if (c.getIdCapitol() == listaCapitole.get(position).getId())
                            temp.add(c);
                    }

                    Intent intent = new Intent(CapitoleInfoActivity.this, InfoActivity.class);
                    intent.putExtra(INTENT_INFORMATIE, temp);
                    intent.putExtra(INTENT_CAPITOL, position);
                    startActivityForResult(intent, REQUEST_CODE_OK);

                    indexActivare = position + 1;
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
                            listaInfoFiltrata = new ArrayList<>(listaInformatii);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println("EROARE AICI");
                        Toast.makeText(CapitoleInfoActivity.this, "Eroare baze de date: " + error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
        Volley.newRequestQueue(this).add(request);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            //Bundle b = data.getExtras();
            //int indexActivare = (int) b.getSerializable(InfoActivity.INTENT_CAPITOL_INCHEIAT) +1;
            //int indexActivare = Capitol.getNrCapitoleActive();
            if (listaCapitole.get(indexActivare) != null && !listaCapitole.get(indexActivare).isActiv()) {

                listaCapitole.get(indexActivare).setActiv(true);
                adapter.notifyDataSetChanged();

                sharedPrefs = getSharedPreferences("Capitole active", MODE_PRIVATE).edit();
                sharedPrefs.putBoolean(String.valueOf(listaCapitole.get(indexActivare).getId()), listaCapitole.get(indexActivare).isActiv());
                sharedPrefs.apply();
            } else {
                //todo mesaj felicitari
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.search, menu);
        MenuItem item = menu.findItem(R.id.searchId);

        SearchView searchView = (SearchView) item.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                query = newText;
                updateListCapitole();
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }
}