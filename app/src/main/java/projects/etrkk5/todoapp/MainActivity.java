package projects.etrkk5.todoapp;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.icu.util.Calendar;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TimePicker;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import projects.etrkk5.todoapp.adapter.add_reminder_adapter;
import projects.etrkk5.todoapp.item.item;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    ImageView buttonAdd;
    RecyclerView recyclerView;
    FirebaseDatabase fbDb;
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    Context context = this;

    private add_reminder_adapter adapter;
    private List<item> itemList;
    private List list;
    RecyclerView.LayoutManager mLayoutManager;
    public String mTitle = "";
    public String mDate = "";
    public String mTime = "";

    private static final String TAG = "MainActivity: ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        fbDb = FirebaseDatabase.getInstance();
        db = FirebaseFirestore.getInstance();
        itemList = new ArrayList<>();
        list = new ArrayList();

        buttonAdd = findViewById(R.id.buttonAdd);
        recyclerView = findViewById(R.id.recycler_view);
        adapter = new add_reminder_adapter(itemList, context, list);
        mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        getReminder();

        buttonAdd.setOnClickListener(this);
    }

    public void addReminder() {
        final String uid = mAuth.getCurrentUser().getUid();
        db.collection("records").document(uid).collection("reminders").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for(DocumentSnapshot doc : task.getResult()){
                    list.add(doc.getId());
                }
                String listSize = Integer.toString(list.size()+1);
                Intent i = new Intent(MainActivity.this, SetActivity.class);
                i.putExtra("position", listSize);
                startActivity(i);
            }
        });
    }

    public void getReminder(){
        final String uid = mAuth.getCurrentUser().getUid();

        db.collection("records").document(uid).collection("reminders").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for(DocumentSnapshot doc : task.getResult()){
                    list.add(doc.getId());
                }
                for(int i=0; i<list.size(); i++){
                    sleep();
                    db.collection("records").document(uid).collection("reminders").document("reminder" + Integer.toString(i+1)).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            sleep();
                            mTitle = task.getResult().get("title").toString();
                            mDate = task.getResult().get("date").toString();
                            mTime = task.getResult().get("time").toString();
                            recyclerView.setAdapter(adapter);
                            itemList.add(new item(mTitle, mDate, mTime, uid));
                        }
                    });
                }
                list.clear();
            }
        });
    }

    public void sleep(){
        try {
            Thread.sleep(250);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == buttonAdd.getId()){
            addReminder();
        }
    }
}
