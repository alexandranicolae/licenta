package com.example.baniimei.clase;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class DAOUser {

    private final DatabaseReference databaseReference;
    //String scorCheie = null;

    public DAOUser() {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        databaseReference = db.getReference(User.class.getSimpleName());
    }

    public Task<Void> updateScor(String cheie, String modif) {
        return databaseReference.child(cheie).child("scor").setValue(modif);
    }

    public String getScor(String cheie) {
        //CountDownLatch done= new CountDownLatch(1);

        final String[] scorCheie = {null};
        DatabaseReference dbref = databaseReference.child(cheie).child("scor");
        dbref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                scorCheie[0] = snapshot.getValue(String.class);
                System.out.println("E OK!!");
                //done.countDown();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println("failed" + error.getMessage());
            }
        });
//        try{
//            done.await();
//        }catch (InterruptedException e){
//            e.printStackTrace();
//        }
        return scorCheie[0];
    }

    public DatabaseReference getDatabaseReference() {
        return databaseReference;
    }

}
