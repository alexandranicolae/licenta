package com.example.baniimei.activitati;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.baniimei.R;
import com.example.baniimei.adaptoare.ListaAdaptorInfo;
import com.example.baniimei.clase.Capitol;
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

    private static final String DB_URL_INFO = "https://alexandral.bestconstruct.ro/SelectInformatie.php";
    private static final int REQUEST_CODE_OK = 200;
    static final String INTENT_INFORMATIE = "Info";
    static final int PUNCTE_BONUS = 10;

    private List<Capitol> listaCapitole;
    private List<Capitol> listaCapitoleInitiala;
    private List<Informatie> listaInformatiiInitiala;
    private ListaAdaptorInfo adapter;
    private String filtruCheie = "";
    private List<Informatie> listaInfo;
    SharedPreferences.Editor sharedPrefs;
    //int pretCapitol;
    int punctaj = 0;
    DAOUser daoUser;
    String userKey = "";

    private static MediaPlayer sunetFelicitari = null;
    SharedPreferences preferinteSunet;

    int indexActivare = 0;
    Boolean aIncheiat = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_BaniiMei_ActionBar);
        setContentView(R.layout.activity_capitole_info);

        // start initializari
        ListView listView = findViewById(R.id.listViewInfo);

        listaInformatiiInitiala = new ArrayList<>();
        //listaInfoFiltrata = new ArrayList<>();
        Intent intent = getIntent();
        listaCapitole = (ArrayList<Capitol>) intent.getSerializableExtra(MainActivity.INTENT_LIST);

        // init item clickEvent pt adaptor
        listView.setOnItemClickListener(adapterItemClick());

        SharedPreferences preferinte = getSharedPreferences(getString(R.string.prefid_capitole_active), MODE_PRIVATE);
        for (int i = 1; i < listaCapitole.size(); i++) {
            String id = String.valueOf(listaCapitole.get(i).getId());
            boolean pref = preferinte.getBoolean(id, false);
            listaCapitole.get(i).setActiv(pref);
        }

        SharedPreferences prefAIncheiat = getSharedPreferences("INCHEIAT", MODE_PRIVATE);
        aIncheiat = prefAIncheiat.getBoolean("aIncheiat", false);

        daoUser = new DAOUser();

        // init adaptor
        adapter = new ListaAdaptorInfo(CapitoleInfoActivity.this, R.layout.forma_adaptor_info, listaCapitole);
        listView.setAdapter(adapter);
        listaCapitoleInitiala = new ArrayList<>(listaCapitole);

        preferinteSunet = getSharedPreferences(getString(R.string.shprefs_numefisier), MODE_PRIVATE);
        if (preferinteSunet.getBoolean(getString(R.string.shprefs_sunet_key), true)) {
            sunetFelicitari = MediaPlayer.create(this, R.raw.success);
        }

        MainActivity.isNetworkAvailable(this);
        getInfoDB();
        getScorDB();
    }

    @Override
    protected void onResume() {
        MainActivity.isNetworkAvailable(this);
        super.onResume();
    }

    private void updateListaCapitole() {
        listaCapitole.clear();
        listaInfo.clear();
        for (Capitol c : listaCapitoleInitiala) {
            Informatie infoCapitol;
            for (Informatie i : listaInformatiiInitiala) {
                if (c.getId() == i.getIdCapitol()) {
                    infoCapitol = i;
                    if (infoCapitol != null && infoCapitol.getInformatie().toUpperCase().contains(filtruCheie.toUpperCase())) {
                        listaInfo.add(infoCapitol);
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
        return (adapterView, view, position, id) -> {
            if (listaCapitole.get(position).isActiv()) {
                startInfoActivity(position);
            } else {
                platesteDeblocare(position);
            }
        };
    }

    public void startInfoActivity(int position) {
        ArrayList<Informatie> temp = new ArrayList<>();
        if (listaInfo != null) {
            for (Informatie c : listaInfo) {
                if (c.getIdCapitol() == listaCapitole.get(position).getId())
                    temp.add(c);
            }


            Intent intent = new Intent(CapitoleInfoActivity.this, InfoActivity.class);
            intent.putExtra(INTENT_INFORMATIE, temp);
            startActivityForResult(intent, REQUEST_CODE_OK);

            indexActivare = position + 1;
        }
    }

    public void platesteDeblocare(int position) {
        if (MainActivity.isNetworkAvailable(this)) {
            //sigur doriti?
            AlertDialog.Builder builder = new AlertDialog.Builder(CapitoleInfoActivity.this);

            int pretCapitol = calculeazaPretCapitol(position);

            builder.setMessage("Acest capitol este blocat. El poate fi deblocat daca capitolul de dinaintea lui este completat, sau in schimbul a " + pretCapitol + " monede. \nDoriti sa-l deblocati acum?");
            builder.setCancelable(true);
            builder.setPositiveButton(R.string.da, (dialog, which) -> {
                if (punctaj >= pretCapitol) {
                    activeazaCapitol(position);
                    scadePuncte(pretCapitol);

                } else {
                    // nu->toast fonduri insuficiente
                    Toast.makeText(getApplicationContext(), "Fonduri insuficiente!", Toast.LENGTH_LONG).show();
                }
                dialog.cancel();
            });
            builder.setNegativeButton(R.string.nu, (dialog, which) -> dialog.cancel());
            AlertDialog alertDialog = builder.create();

            alertDialog.show();
        }
    }

    public int calculeazaPretCapitol(int poz) {
        int pretCapitol = 5;
        for (int i = 0; i < poz; i++) {
            if (!listaCapitole.get(i).isActiv()) {
                pretCapitol += 5;
            }
        }
        return pretCapitol;
    }

    public void scadePuncte(int puncteMinus) {
        punctaj -= puncteMinus;
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
                response -> {
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
                            listaInformatiiInitiala.add(info);
                        }
                        listaInfo = new ArrayList<>(listaInformatiiInitiala);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    System.out.println("EROARE baza de date");
                    //Toast.makeText(CapitoleInfoActivity.this, "Eroare baze de date: " + error.getMessage(), Toast.LENGTH_LONG).show();
                });
        Volley.newRequestQueue(this).add(request);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            int nrInfoFiltrateCapitol = getNrInfoPerCapitol(listaInfo);
            int nrInfoNefiltrateCapitol = getNrInfoPerCapitol(listaInformatiiInitiala);
            if (nrInfoFiltrateCapitol == nrInfoNefiltrateCapitol) {
                if (indexActivare < listaCapitoleInitiala.size() && !listaCapitoleInitiala.get(indexActivare).isActiv()) {
                    activeazaCapitol(indexActivare);
                } else {
                    if (indexActivare >= listaCapitoleInitiala.size() && !aIncheiat) {
                        showMesajPopUp();
                        pornesteSunetFelicitari();
                        adaugaPuncte(PUNCTE_BONUS);
                        aIncheiat = true;

                        SharedPreferences.Editor sharedPrefsEditor = getSharedPreferences("INCHEIAT", MODE_PRIVATE).edit();
                        sharedPrefsEditor.putBoolean("aIncheiat", aIncheiat);
                        sharedPrefsEditor.apply();
                    }
                }
            }
        }
    }

    private void pornesteSunetFelicitari() {
        if (sunetFelicitari != null) {
            sunetFelicitari.start();
        }
    }

    private void adaugaPuncte(int puncteBonus) {
        punctaj += puncteBonus;
        daoUser.updateScor(userKey, String.valueOf(punctaj));
    }

    public void showMesajPopUp() {
        TextView btnX;
        TextView tvMesaj;
        TextView tvTitlu;
        Button btn;

        Dialog templatePopup = new Dialog(this);

        templatePopup.setContentView(R.layout.template_popup);
        btnX = templatePopup.findViewById(R.id.tvX);
        tvMesaj = templatePopup.findViewById(R.id.tvMesaj);
        btn = templatePopup.findViewById(R.id.btnMaiDeparte);
        tvTitlu = templatePopup.findViewById(R.id.tvTitlu);

        tvTitlu.setText("Felicitari!");
        btn.setVisibility(View.INVISIBLE);
        tvMesaj.setText("Esti un mastru in finante! Ai incheiat toate capitolele. Pot fi adaugate oricand noi capitole, deci fi pe faza! Pana atunci, succes la gestionarea banilor!");

        btnX.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                templatePopup.dismiss();
            }
        });

        templatePopup.show();
    }

    private int getNrInfoPerCapitol(List<Informatie> lista) {
        int nr = 0;
        Capitol c = listaCapitoleInitiala.get(indexActivare - 1);
        for (Informatie i : lista) {
            if (i.getIdCapitol() == c.getId()) {
                nr++;
            }
        }
        return nr;
    }

    private void activeazaCapitol(int poz) {
        listaCapitoleInitiala.get(poz).setActiv(true);
        adapter.notifyDataSetChanged();

        sharedPrefs = getSharedPreferences(getString(R.string.prefid_capitole_active), MODE_PRIVATE).edit();
        sharedPrefs.putBoolean(String.valueOf(listaCapitoleInitiala.get(poz).getId()), listaCapitoleInitiala.get(poz).isActiv());
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
                filtruCheie = newText;
                updateListaCapitole();
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }
}