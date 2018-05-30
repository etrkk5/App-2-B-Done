package projects.etrkk5.todoapp.dao;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import projects.etrkk5.todoapp.MainActivity;

public class FirebaseDao {
    Context context;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseDatabase fbDb = FirebaseDatabase.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static FirebaseDao instance = new FirebaseDao();

    public FirebaseDao(Context context){
        this.context = context;
    }

    public FirebaseDao(){
    }

    public void connect(String email, String password){
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.e("Result: ", "Succesfully connected !");
            }
        });
    }

    public void createUser(final String name, final String surname, final String email, final String password){
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                String uid = mAuth.getCurrentUser().getUid();
                Map<String, String> map = new HashMap<>();
                map.put("name", name);
                map.put("surname", surname);
                map.put("email", email);
                fbDb.getReference().child("records").child(uid).setValue(map);

                HashMap<String, String> map1 = new HashMap<>();
                map1.put("title", "");
                map1.put("description", "");
                map1.put("date", "");
                map1.put("time", "");
                map1.put("switch" , "true");
                db.collection("records").document(uid).collection("records").document("reminder1").set(map1);
            }
        });
    }

    public static FirebaseDao getInstance(){
        return instance;
    }
}
