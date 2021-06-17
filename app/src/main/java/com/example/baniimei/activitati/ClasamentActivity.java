package com.example.baniimei.activitati;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.example.baniimei.R;
import com.example.baniimei.adaptoare.ListaAdaptorClasament;
import com.example.baniimei.adaptoare.ListaAdaptorIntrebare;
import com.example.baniimei.clase.DAOUser;
import com.example.baniimei.clase.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ClasamentActivity extends AppCompatActivity {

    private ListView listView;
    private Button btnBack;
    private ListaAdaptorClasament adapter;

    private List<User> userList;
    private DAOUser daoUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_BaniiMei);
        setContentView(R.layout.activity_clasament);

        listView = findViewById(R.id.listaClasament);
        btnBack = findViewById(R.id.btnBackClasament);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        daoUser = new DAOUser();

        userList = new ArrayList<>();
        //userList=daoUser.selectAll();

        daoUser.getDatabaseReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    User user = dataSnapshot.getValue(User.class);
                    userList.add(user);
                }
                Collections.sort(userList);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        adapter = new ListaAdaptorClasament(ClasamentActivity.this, R.layout.forma_adaptor_clasament, userList);
        listView.setAdapter(adapter);
    }
}