package com.example.baniimei.activitati;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.baniimei.R;

public class MainActivity extends AppCompatActivity {

    private Button btnSetari;
    private Button btnStart;
    private Button btnExit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_BaniiMei);
        setContentView(R.layout.activity_main);

        init();

        btnSetari.setOnClickListener(setariClick());
        btnExit.setOnClickListener(exitClick());
        btnStart.setOnClickListener(startClick());
    }

    private void init(){
        btnSetari= findViewById(R.id.btnSettings);
        btnStart=findViewById(R.id.btnStart);
        btnExit=findViewById(R.id.btnExit);
    }

    private View.OnClickListener startClick() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(MainActivity.this, ChestionarCapitoleActivity.class);
                startActivity(intent);
            }
        };
    }

    private View.OnClickListener setariClick() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SetariActivity.class);
                startActivity(intent);
            }
        };
    }

    private View.OnClickListener exitClick() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);
                builder.setMessage(R.string.exit_message);
                builder.setCancelable(true);
                builder.setNegativeButton(R.string.exit_da, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
                builder.setPositiveButton(R.string.exit_nu, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                AlertDialog alertDialog=builder.create();
                alertDialog.show();
            }

        };
    }

}