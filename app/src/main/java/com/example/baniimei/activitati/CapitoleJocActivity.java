package com.example.baniimei.activitati;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
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
import com.example.baniimei.clase.Intrebare;
import com.example.baniimei.adaptoare.ListaAdaptorIntrebare;
import com.example.baniimei.clase.DAOUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CapitoleJocActivity extends AppCompatActivity {

    private static final String DB_URL_INTREBARE = "http://alexandral.bestconstruct.ro/SelectIntrebare.php";
    private static final int NR_INTREBARI = 5;
    private static final int REQUEST_CODE_OK = 300;
    private static final int REQUEST_CODE_OK_ROATA = 400;
    static final String INTENT_INTREBARE = "INTREBARE";

    private TextView scor;

    private ArrayList<Capitol> listaCapitole;
    private ArrayList<Intrebare> listaIntrebari;

    SharedPreferences prefUser;
    private String userKey = "";

    private DAOUser daoUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_BaniiMei);
        setContentView(R.layout.activity_capitole_joc);

        ListView listView = findViewById(R.id.listViewJoc);
        scor = findViewById(R.id.tvScorCapitoleJoc);
        ImageButton btnRoata = findViewById(R.id.ibRoata);
        Button btnRandom = findViewById(R.id.btnRandom);
        ImageButton btnClasament = findViewById(R.id.ibClasament);

        listaIntrebari = new ArrayList<>();
        Intent intent = getIntent();
        listaCapitole = (ArrayList<Capitol>) intent.getSerializableExtra(MainActivity.INTENT_LIST);

        //prefScor = getSharedPreferences(getString(R.string.shprefs_scor_numefis), MODE_PRIVATE);
        prefUser = getSharedPreferences("DATE_USER", MODE_PRIVATE);
        userKey = prefUser.getString("ID_USER", "");
        daoUser = new DAOUser();

        setScorDB();
        //scor.setText(daoUser.getScor(userKey));

        //init adaptor
        listView.setOnItemClickListener(adapterItemClick());
        ListaAdaptorIntrebare adapter = new ListaAdaptorIntrebare(CapitoleJocActivity.this, R.layout.forma_adaptor_joc, listaCapitole);
        listView.setAdapter(adapter);

        btnRoata.setOnClickListener(clickRoata());
        btnClasament.setOnClickListener(clickClasament());
        btnRandom.setOnClickListener(clickRandom());
        getIntrebariDB();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setScorDB();
        //scor.setText(daoUser.getScor(userKey));
    }

    public void setScorDB() {
        DatabaseReference dbref = daoUser.getDatabaseReference().child(userKey).child("scor");
        dbref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                scor.setText(snapshot.getValue(String.class));
                System.out.println("E OK!!");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println("failed" + error.getMessage());
            }
        });
    }

    private AdapterView.OnItemClickListener adapterItemClick() {
        return new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // start JocActivity
                // trimite lista intrebari corespunzatoare capitolului
                Capitol capitol = listaCapitole.get(position);

                Intent intent = new Intent(CapitoleJocActivity.this, JocActivity.class);

                Collections.shuffle(listaIntrebari);
                ArrayList<Intrebare> temp = new ArrayList<>();
                int i = 0;
                for (Intrebare c : listaIntrebari) {
                    if (i == NR_INTREBARI) {
                        break;
                    }
                    if (c.getIdCapitol() == capitol.getId()) {
                        temp.add(c);
                        i++;
                    }
                }

                intent.putExtra(INTENT_INTREBARE, temp);
                startActivityForResult(intent, REQUEST_CODE_OK);
            }
        };
    }

    private View.OnClickListener clickRandom() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CapitoleJocActivity.this, JocActivity.class);
                ArrayList<Intrebare> temp = new ArrayList<>();

                Collections.shuffle(listaIntrebari);
                for (int i = 0; i < NR_INTREBARI; i++) {
                    temp.add(listaIntrebari.get(i));
                }

                intent.putExtra(INTENT_INTREBARE, temp);
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

    private View.OnClickListener clickRoata() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CapitoleJocActivity.this, RuletaActivity.class);
                startActivityForResult(intent, REQUEST_CODE_OK_ROATA);
            }
        };
    }

    private void getIntrebariDB() {
        StringRequest request = new StringRequest(Request.Method.GET, DB_URL_INTREBARE,
                response -> {
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

                            Intrebare intr = new Intrebare(id, intrebare, raspunsCorect, raspunsuri, indiciu, idCapitol);
                            listaIntrebari.add(intr);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
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
            setScorDB();
        }

        if (requestCode == REQUEST_CODE_OK_ROATA && resultCode == RESULT_OK && data != null) {
            Bundle b = data.getExtras();
            int puncteCastigate = (int) b.getSerializable(RuletaActivity.INTENT_PUNCTE);

            int scorDB = Integer.parseInt((String) scor.getText());
            int scorNou = puncteCastigate + scorDB;
            daoUser.updateScor(userKey, String.valueOf(scorNou));
            setScorDB();
        }
    }
}