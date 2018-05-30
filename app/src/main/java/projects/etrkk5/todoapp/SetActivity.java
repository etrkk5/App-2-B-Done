package projects.etrkk5.todoapp;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.icu.util.Calendar;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
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
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import projects.etrkk5.todoapp.util.myAlarm;

public class SetActivity extends AppCompatActivity implements View.OnClickListener{
    EditText editTextTitle, editTextDescription, editTextDate, editTextTime;
    Button buttonSave;
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    String position = "", mTitle="", mDescription="", mDate="", mTime="";
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        editTextTitle = findViewById(R.id.editTextTitle);
        editTextDescription = findViewById(R.id.editTextDescription);
        editTextDate = findViewById(R.id.editTextDate);
        editTextTime = findViewById(R.id.editTextTime);
        buttonSave = findViewById(R.id.buttonSave);
        progressBar = findViewById(R.id.progressbar);

        Bundle extras = getIntent().getExtras();
        if(extras != null){
            mTitle = extras.getString("title");
            mDescription = extras.getString("description");
            mDate = extras.getString("date");
            mTime = extras.getString("time");
            position = extras.getString("position");
            Log.e("inside ", mTitle + mDescription + mDate + mTime);
        }

        buttonSave.setOnClickListener(this);
        editTextDate.setOnClickListener(this);
        editTextTime.setOnClickListener(this);

        getReminder();
    }

    public void setDate(){
        Calendar mcurrentTime = Calendar.getInstance();
        mcurrentTime.setTimeInMillis(System.currentTimeMillis());
        int year = mcurrentTime.get(Calendar.YEAR);
        int month = mcurrentTime.get(Calendar.MONTH);
        final int day = mcurrentTime.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePicker;
        sleep();
        datePicker = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                mDate = Integer.toString(dayOfMonth) + "/" + Integer.toString(month+1) + "/" + Integer.toString(year);
                editTextDate.setText(mDate);
            }
        }, year, month, day);
        datePicker.setTitle("Set Date");
        datePicker.setButton(DatePickerDialog.BUTTON_POSITIVE, "Save", datePicker);
        datePicker.setButton(DatePickerDialog.BUTTON_NEGATIVE, "Cancel", datePicker);
        datePicker.show();
    }

    public void setTime(){
        Calendar mcurrentTime = Calendar.getInstance();
        mcurrentTime.setTimeInMillis(System.currentTimeMillis());
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);

        TimePickerDialog timePicker;
        sleep();
        timePicker = new TimePickerDialog(SetActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                mTime = Integer.toString(hourOfDay) + ":" + Integer.toString(minute);
                editTextTime.setText(mTime);
            }
        }, hour, minute, true);
        timePicker.setTitle("Set time");
        timePicker.setButton(TimePickerDialog.BUTTON_POSITIVE, "Save", timePicker);
        timePicker.setButton(TimePickerDialog.BUTTON_NEGATIVE, "Cancel", timePicker);
        timePicker.show();
    }

    public void saveReminder(){
        final String uid = mAuth.getCurrentUser().getUid();

        mTitle = editTextTitle.getText().toString();
        mDescription = editTextDescription.getText().toString();
        mDate = editTextDate.getText().toString();
        mTime = editTextTime.getText().toString();

        if(mTitle.isEmpty()){
            editTextTitle.setError("Title is required");
            editTextTitle.requestFocus();
            return;
        }
        if(mDate.isEmpty()){
            editTextDate.setError("Date is required");
            editTextDate.requestFocus();
            return;
        }
        if(mTime.isEmpty()){
            editTextTime.setError("Time is required");
            editTextTime.requestFocus();
            return;
        }

        String[] date = mDate.split("/");
        String[] time = mTime.split(":");

        sleep();

        Map<String, String> map = new HashMap<>();
        map.put("title", mTitle);
        map.put("description", mDescription);
        map.put("date", mDate);
        map.put("time", mTime);

        db.collection("records").document(uid).collection("reminders").document("reminder" + position).set(map);

        try{
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(System.currentTimeMillis());
            cal.clear();

            cal.set(Calendar.YEAR, Integer.parseInt(date[2]));
            cal.set(Calendar.MONTH, Integer.parseInt(date[1]));
            cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(date[0]));
            cal.set(Calendar.HOUR, Integer.parseInt(time[0]));
            cal.set(Calendar.MINUTE, Integer.parseInt(time[1]));
            cal.set(Calendar.SECOND, 1);

            Intent activate = new Intent(this, myAlarm.class);
            PendingIntent alarmIntent = PendingIntent.getBroadcast(this, 0, activate, 0);
            AlarmManager aM = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            aM.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), alarmIntent);
        }catch (Exception e){
            Log.e("SetActivity ", e.getMessage());
        }
        progressBar.setVisibility(View.VISIBLE);
        Intent i = new Intent(SetActivity.this, MainActivity.class);
        startActivity(i);
    }

    public void getReminder(){
        sleep();
        editTextTitle.setText(mTitle);
        editTextDescription.setText(mDescription);
        editTextDate.setText(mDate);
        editTextTime.setText(mTime);
    }

    public void sleep(){
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == buttonSave.getId()){
            saveReminder();
        }
        if(v.getId() == editTextDate.getId()) {
            setDate();
        }
        if(v.getId() == editTextTime.getId()){
            setTime();
        }
    }
}
