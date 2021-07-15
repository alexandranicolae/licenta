package com.example.baniimei.activitati;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bluehomestudio.luckywheel.LuckyWheel;
import com.bluehomestudio.luckywheel.OnLuckyWheelReachTheTarget;
import com.bluehomestudio.luckywheel.WheelItem;
import com.example.baniimei.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RuletaActivity extends AppCompatActivity {

    static final String INTENT_PUNCTE = "puncte_roata";

    LuckyWheel roata;
    Button btnSpin;
    Dialog templatePopup;

    List<WheelItem> itemList = new ArrayList<>();
    int pct;
    String puncteCastigate;

    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_BaniiMei);
        setContentView(R.layout.activity_roata);

        roata = findViewById(R.id.roata);
        btnSpin = findViewById(R.id.btnRoata);
        intent = getIntent();

        populeazaRoata();

        roata.setLuckyWheelReachTheTarget(new OnLuckyWheelReachTheTarget() {
            @Override
            public void onReachTarget() {
                WheelItem win = itemList.get(pct - 1);
                puncteCastigate = win.text;

                if (puncteCastigate.equals("0")) {
                    showPopUp("Poate data viitoare!", "Se pare ca nu ai avut noroc astazi. Mai incearca data viitoare!");
                } else {
                    showPopUp("Felicitari!", "Ai castigat " + puncteCastigate + " monede!");
                }
            }
        });

        btnSpin.setOnClickListener(clickInvarte());
    }

    private void showPopUp(String titluPopup, String mesaj) {
        TextView btnX;
        TextView tvMesaj;
        TextView tvTitlu;
        Button btn;

        templatePopup = new Dialog(RuletaActivity.this);
        templatePopup.setCanceledOnTouchOutside(false);

        templatePopup.setContentView(R.layout.template_popup);
        btnX = templatePopup.findViewById(R.id.tvX);
        tvMesaj = templatePopup.findViewById(R.id.tvMesaj);
        btn = templatePopup.findViewById(R.id.btnMaiDeparte);
        tvTitlu = templatePopup.findViewById(R.id.tvTitlu);

        tvTitlu.setText(titluPopup);
        tvMesaj.setText(mesaj);
        btn.setText("OK");

        btnX.setOnClickListener(clickPopUp());
        btn.setOnClickListener(clickPopUp());

        MainActivity.isNetworkAvailable(this);
        templatePopup.show();
    }

    private View.OnClickListener clickPopUp() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(RuletaActivity.this, puncteCastigate, Toast.LENGTH_LONG).show();
                intent.putExtra(INTENT_PUNCTE, Integer.parseInt(puncteCastigate));
                setResult(RESULT_OK, intent);

                templatePopup.dismiss();
                finish();
            }
        };
    }

    private void populeazaRoata() {
        WheelItem item = new WheelItem(ResourcesCompat.getColor(getResources(), R.color.portocaliu, null),
                BitmapFactory.decodeResource(getResources(), R.drawable.icon_roata), "0");
        itemList.add(item);
        WheelItem item1 = new WheelItem(ResourcesCompat.getColor(getResources(), R.color.purple_finchis, null),
                BitmapFactory.decodeResource(getResources(), R.drawable.icon_roata), "15");
        itemList.add(item1);
        WheelItem item2 = new WheelItem(ResourcesCompat.getColor(getResources(), R.color.portocaliu, null),
                BitmapFactory.decodeResource(getResources(), R.drawable.icon_roata), "3");
        itemList.add(item2);
        WheelItem item3 = new WheelItem(ResourcesCompat.getColor(getResources(), R.color.purple_finchis, null),
                BitmapFactory.decodeResource(getResources(), R.drawable.icon_roata), "0");
        itemList.add(item3);
        WheelItem item4 = new WheelItem(ResourcesCompat.getColor(getResources(), R.color.portocaliu, null),
                BitmapFactory.decodeResource(getResources(), R.drawable.icon_roata), "50");
        itemList.add(item4);
        WheelItem item5 = new WheelItem(ResourcesCompat.getColor(getResources(), R.color.purple_finchis, null),
                BitmapFactory.decodeResource(getResources(), R.drawable.icon_roata), "5");
        itemList.add(item5);
        WheelItem item6 = new WheelItem(ResourcesCompat.getColor(getResources(), R.color.portocaliu, null),
                BitmapFactory.decodeResource(getResources(), R.drawable.icon_roata), "10");
        itemList.add(item6);
        WheelItem item7 = new WheelItem(ResourcesCompat.getColor(getResources(), R.color.purple_finchis, null),
                BitmapFactory.decodeResource(getResources(), R.drawable.icon_roata), "5");
        itemList.add(item7);

        roata.addWheelItems(itemList);
    }

    private View.OnClickListener clickInvarte() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Random random = new Random();
                pct = random.nextInt(itemList.size());
                if (pct == 0) {
                    pct = 1;
                }
                roata.rotateWheelTo(pct);
            }
        };
    }
}