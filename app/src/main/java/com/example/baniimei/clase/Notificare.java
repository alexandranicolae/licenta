package com.example.baniimei.clase;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.baniimei.R;

public class Notificare extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationCompat.Builder builder=new NotificationCompat.Builder(context,"notifyJoc")
                .setSmallIcon(R.drawable.ic_icon_100)
                .setContentTitle("Nu uita de noi!")
                .setContentText("Hai sa te joci si sa afli cum functioneaza lumea banilor!");
        NotificationManagerCompat managerCompat= NotificationManagerCompat.from(context);

        managerCompat.notify(100, builder.build());
    }
}
