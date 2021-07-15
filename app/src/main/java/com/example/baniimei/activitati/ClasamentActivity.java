package com.example.baniimei.activitati;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.baniimei.R;
import com.example.baniimei.adaptoare.ListaAdaptorClasament;
import com.example.baniimei.clase.DAOUser;
import com.example.baniimei.clase.FormatGrafic;
import com.example.baniimei.clase.User;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ClasamentActivity extends AppCompatActivity {

    private static final int NR_CLASAMENT = 3;
    private ListView listView;
    private Button btnBack;
    private TextView tvRang;
    private BarChart barChart;
    private ListaAdaptorClasament adapter;

    private List<User> userList;
    private List<User> userBarChartList;
    private List<String> userLabels;
    private DAOUser daoUser;
    SharedPreferences prefUser;
    private String userKey = "";
    private User myUser;

    List<BarEntry> barEntryList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_BaniiMei);
        setContentView(R.layout.activity_clasament);

        listView = findViewById(R.id.listaClasament);
        btnBack = findViewById(R.id.btnBackClasament);
        tvRang = findViewById(R.id.tvStareClasament);
        barChart = findViewById(R.id.barChart);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        MainActivity.isNetworkAvailable(this);

        daoUser = new DAOUser();
        prefUser = getSharedPreferences("DATE_USER", MODE_PRIVATE);
        userKey = prefUser.getString("ID_USER", "");

        userList = new ArrayList<>();
        barEntryList = new ArrayList<>();
        userBarChartList = new ArrayList<>();
        userLabels = new ArrayList<>();

        myUser = new User();

        daoUser.getDatabaseReference().addValueEventListener(getUsersDB());

        adapter = new ListaAdaptorClasament(ClasamentActivity.this, R.layout.forma_adaptor_clasament, userList);
        listView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MainActivity.isNetworkAvailable(this);
    }

    private void showBarChart() {
        barEntryList.clear();
        userLabels.clear();

        for (int i = 0; i < NR_CLASAMENT; i++) {
            userBarChartList.add(userList.get(i));
        }

        for (int i = 0; i < userBarChartList.size(); i++) {
            int poz;
            switch (i) {
                case 0:
                    poz = 1;
                    break;
                case 1:
                    poz = 0;
                    break;
                default:
                    poz = i;
            }
            int rang = poz + 1;
            userLabels.add(rang + ". " + userBarChartList.get(poz).getNume());
            barEntryList.add(new BarEntry(poz, Integer.parseInt(userBarChartList.get(i).getScor())));
        }

        BarDataSet barDataSet = new BarDataSet(barEntryList, " ");
        barDataSet.setValueTextSize(18);
        barDataSet.setValueFormatter(new FormatGrafic());
        barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);

        Description description = new Description();
        description.setEnabled(false);
        barChart.setDescription(description);

        BarData barData = new BarData(barDataSet);
        barChart.setData(barData);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(userLabels));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(false);
        xAxis.setGranularity(1f);
        xAxis.setTextSize(18);
        xAxis.setLabelCount(userLabels.size());
        barChart.setExtraBottomOffset(20);
        //xAxis.setLabelRotationAngle(270);

        YAxis leftAxis = barChart.getAxisLeft();
        //hiding the left y-axis line, default true if not set
        leftAxis.setDrawAxisLine(false);
        YAxis rightAxis = barChart.getAxisRight();
        //hiding the right y-axis line, default true if not set
        rightAxis.setDrawAxisLine(false);

        Legend legend = barChart.getLegend();
        legend.setEnabled(false);
        //setting the shape of the legend form to line, default square shape

        barChart.animateY(2000);
        barChart.invalidate();
    }

    private ValueEventListener getUsersDB() {
        return new ValueEventListener() {
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

                for (int i = 0; i < userList.size(); i++) {
                    if (userList.get(i).equals(myUser)) {
                        tvRang.setText(String.valueOf(i + 1));
                        break;
                    }
                }

                adapter.notifyDataSetChanged();
                showBarChart();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //Toast.makeText(MainActivity.this, "EROARE BAZA DE DATE!", Toast.LENGTH_SHORT).show();
            }
        };
    }
}