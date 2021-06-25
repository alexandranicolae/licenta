package com.example.baniimei.clase;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DAOUser {

    private final DatabaseReference databaseReference;

    public DAOUser() {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        databaseReference = db.getReference(User.class.getSimpleName());
    }

    public Task<Void> updateScor(String cheie, String modif) {
        return databaseReference.child(cheie).child("scor").setValue(modif);
    }

    public DatabaseReference getDatabaseReference() {
        return databaseReference;
    }

}
