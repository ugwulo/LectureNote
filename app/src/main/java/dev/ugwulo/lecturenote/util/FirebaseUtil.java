package dev.ugwulo.lecturenote.util;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseUtil {
    public static FirebaseDatabase mDatabase;
    public static DatabaseReference mReference;
    public static FirebaseAuth mAuth;
    public static FirebaseAuth.AuthStateListener mAuthStateListener;
    private static  FirebaseUtil mInstance;

    private FirebaseUtil(){}

    public static synchronized FirebaseUtil getInstance(String ref){
        if (mInstance == null){
            mInstance = new FirebaseUtil();

        }
        return mInstance;
    }
}
