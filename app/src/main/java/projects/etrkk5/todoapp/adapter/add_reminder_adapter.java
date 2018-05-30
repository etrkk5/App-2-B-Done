package projects.etrkk5.todoapp.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import projects.etrkk5.todoapp.MainActivity;
import projects.etrkk5.todoapp.R;
import projects.etrkk5.todoapp.SetActivity;
import projects.etrkk5.todoapp.item.item;

public class add_reminder_adapter extends RecyclerView.Adapter<add_reminder_adapter.add_reminder_holder> {
    private List<item> itemList;
    private List list;
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    Context context;
    Map<String, String> map1 = new HashMap<>();
    public String mTitle = "";
    public String mDescription = "";
    public String mDate = "";
    public String mTime = "";

    public  add_reminder_adapter(List<item> itemList, Context context, List list){
        this.itemList = itemList;
        this.context = context;
        this.list = list;
    }

    @Override
    public add_reminder_adapter.add_reminder_holder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View reminderView = LayoutInflater.from(parent.getContext()).inflate(R.layout.reminder_layout, parent, false);
        final add_reminder_holder holder = new add_reminder_holder(reminderView);
        return new add_reminder_holder(reminderView);
    }

    @Override
    public void onBindViewHolder(@NonNull add_reminder_adapter.add_reminder_holder holder, int position) {
        item item = itemList.get(position);
        holder.cardView.setTag(position);
        holder.textViewTitle.setText(item.getTitle());
        holder.textViewTime.setText(item.getDate() + " " + item.getTime());
        holder.textViewDocsRef.setText(item.getDocsRef());
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class add_reminder_holder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        public CardView cardView;
        public TextView textViewTitle;
        public TextView textViewTime;
        public TextView textViewDocsRef;

        public add_reminder_holder(View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            textViewTime = itemView.findViewById(R.id.textViewTime);
            textViewDocsRef = itemView.findViewById(R.id.textViewDocsRef);
            cardView = itemView.findViewById(R.id.cardView);
            cardView.setOnClickListener(this);
            cardView.setOnLongClickListener(this);
        }

        public void sleep(){
            try {
                Thread.sleep(250);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onClick(final View v) {
            final int pos = (int) v.getTag();
            int position = pos + 1;
            final String posit = Integer.toString(position);
            mAuth = FirebaseAuth.getInstance();
            final String uid = mAuth.getCurrentUser().getUid();
            db = FirebaseFirestore.getInstance();
            sleep();
            db.collection("records").document(uid).collection("reminders").document("reminder" + posit).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    mTitle = task.getResult().get("title").toString();
                    mDescription = task.getResult().get("description").toString();
                    mDate = task.getResult().get("date").toString();
                    mTime = task.getResult().get("time").toString();
                    sleep();

                    Intent i = new Intent(context, SetActivity.class);
                    i.putExtra("title", mTitle);
                    i.putExtra("description", mDescription);
                    i.putExtra("date", mDate);
                    i.putExtra("time", mTime);
                    i.putExtra("position", posit);
                    v.getContext().startActivity(i);
                }
            });
        }
        @Override
        public boolean onLongClick(final View v) {
            int pos = (int) v.getTag();
            final int position = pos + 1;
            mAuth = FirebaseAuth.getInstance();
            db = FirebaseFirestore.getInstance();
            final String uid = mAuth.getCurrentUser().getUid();

            LayoutInflater li = LayoutInflater.from(context);
            View dialogView = li.inflate(R.layout.input_dialog, null);
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
            alertDialogBuilder.setView(dialogView);

            alertDialogBuilder.setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    db.collection("records").document(uid).collection("reminders").document("reminder" + Integer.toString(position)).delete();
                    changeDocumentId();
                    Intent i = new Intent(context, MainActivity.class);
                    v.getContext().startActivity(i);
                }
            }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();

            return false;
        }

        public void changeDocumentId(){
            final String uid = mAuth.getCurrentUser().getUid();
            db.collection("records").document(uid).collection("reminders").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    int i=1;
                    for(DocumentSnapshot doc : task.getResult()){
                        map1.put("title", doc.get("title").toString());
                        map1.put("description", doc.get("description").toString());
                        map1.put("date", doc.get("date").toString());
                        map1.put("time", doc.get("time").toString());
                        sleep();
                        db.collection("records").document(uid).collection("reminders").document("reminder" + Integer.toString(i)).delete();
                        sleep();
                        db.collection("records").document(uid).collection("reminders").document("reminder" + Integer.toString(i)).set(map1);
                        i++;
                        map1.clear();
                    }
                    Log.e("Deleting reminder", Integer.toString(i));
                    db.collection("records").document(uid).collection("reminders").document("reminder" + Integer.toString(i)).delete();
                }
            });
        }
    }

}
