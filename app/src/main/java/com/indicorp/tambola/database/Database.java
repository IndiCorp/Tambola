package com.indicorp.tambola.database;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Database {

    private static Database single_instance = null;
    // Declaring a variable of type String
    DatabaseReference myRef;

    // Constructor
    // Here we will be creating private constructor
    // restricted to this class itself
    private Database()
    {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference ( );
    }

    // Static method
    // Static method to create instance of Singleton class
    public static DatabaseReference getDatabaseReference()
    {
        if (single_instance == null)
            single_instance = new Database();

        return single_instance.myRef;
    }
}
