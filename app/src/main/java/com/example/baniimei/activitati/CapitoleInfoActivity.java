package com.example.baniimei.activitati;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
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
import com.example.baniimei.clase.DAOUser;
import com.example.baniimei.clase.Informatie;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

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
    int pretCapitol;
    int punctaj = 0;
    DAOUser daoUser;
    String userKey = "";

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

        daoUser = new DAOUser();

        // init adaptor
        adapter = new ListaAdaptorInfo(CapitoleInfoActivity.this, R.layout.forma_adaptor_info, listaCapitole);
        listView.setAdapter(adapter);
        listaCapitoleInitiala = new ArrayList<>(listaCapitole);
        getInfoDB();
        getScorDB();
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
                if (listaCapitole.get(position).isActiv()) {
                    startInfoActivity(position);
                } else {
                    platesteDeblocare(position);
                }
            }
        };
    }

    public void startInfoActivity(int position) {
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
    }

    public void platesteDeblocare(int position) {
        //sigur doriti?
        AlertDialog.Builder builder = new AlertDialog.Builder(CapitoleInfoActivity.this);

        calculeazaPretCapitol(position);

        builder.setMessage("Acest capitol este blocat. El poate fi deblocat daca capitolul de dinaintea lui este completat, sau in schimbul a " + pretCapitol + " monede. \nDoriti sa-l deblocati acum?");
        builder.setCancelable(true);
        builder.setPositiveButton(R.string.da, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (punctaj >= pretCapitol) {
                    // todo activeaza capitol
                    activeazaCapitol(position);
                    // scade puncte
                    scadePuncte();

                } else {
                    // nu->toast fonduri insuficiente
                    Toast.makeText(getApplicationContext(), "Fonduri insuficiente!", Toast.LENGTH_LONG).show();
                }
                dialog.cancel();
            }
        });
        builder.setNegativeButton(R.string.nu, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void calculeazaPretCapitol(int poz) {
        pretCapitol = 5;
        for (int i = 0; i < poz; i++) {
            if (!listaCapitole.get(i).isActiv()) {
                pretCapitol += 5;
            }
        }
    }

    public void scadePuncte() {
        punctaj -= pretCapitol;
        daoUser.updateScor(userKey, String.valueOf(punctaj));
    }

    public void getScorDB() {
        SharedPreferences prefUser = getSharedPreferences("DATE_USER", MODE_PRIVATE);
        userKey = prefUser.getString("ID_USER", "");

        DatabaseReference dbref = daoUser.getDatabaseReference().child(userKey).child("scor");
        dbref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                punctaj = Integer.parseInt(String.valueOf(snapshot.getValue(String.class)));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println("failed" + error.getMessage());
            }
        });
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
            if (listaCapitole.get(indexActivare) != null && !listaCapitole.get(indexActivare).isActiv()) {
                activeazaCapitol(indexActivare);
            } else {
                //todo mesaj felicitari
            }
        }
    }

    public void activeazaCapitol(int poz) {
        listaCapitole.get(poz).setActiv(true);
        adapter.notifyDataSetChanged();

        sharedPrefs = getSharedPreferences("Capitole active", MODE_PRIVATE).edit();
        sharedPrefs.putBoolean(String.valueOf(listaCapitole.get(poz).getId()), listaCapitole.get(poz).isActiv());
        sharedPrefs.apply();
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