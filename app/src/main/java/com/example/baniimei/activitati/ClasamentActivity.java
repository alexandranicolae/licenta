package com.example.baniimei.activitati;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

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
    private TextView tvRang;
    private ListaAdaptorClasament adapter;

    private List<User> userList;
    private DAOUser daoUser;
    SharedPreferences prefUser;
    private String userKey = "";
    private User myUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_BaniiMei);
        setContentView(R.layout.activity_clasament);

        listView = findViewById(R.id.listaClasament);
        btnBack = findViewById(R.id.btnBackClasament);
        tvRang = findViewById(R.id.tvStareClasament);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        daoUser = new DAOUser();
        prefUser = getSharedPreferences("DATE_USER", MODE_PRIVATE);
        userKey = prefUser.getString("ID_USER", "");

        userList = new ArrayList<>();
        //userList=daoUser.selectAll();

        myUser = new User();

        daoUser.getDatabaseReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    User user = dataSnapshot.getValue(User.class);
                    userList.add(user);
                    if (dataSnapshot.getKey().equals(userKey)) {
                        myUser = user;
                    }
                }
                Collections.sort(userList);
                adapter.notifyDataSetChanged();

                for (int i = 0; i < userList.size(); i++) {
                    if (userList.get(i).equals(myUser)) {
                        tvRang.setText(String.valueOf(i + 1));
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        adapter = new ListaAdaptorClasament(ClasamentActivity.this, R.layout.forma_adaptor_clasament, userList);
        listView.setAdapter(adapter);
    }
}