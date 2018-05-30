package projects.etrkk5.todoapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import projects.etrkk5.todoapp.dao.FirebaseDao;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener{
    FirebaseDao firebaseDao = FirebaseDao.getInstance();

    EditText editTextName, editTextSurname, editTextEmail, editTextPassword;
    Button buttonSignUp;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        editTextName = findViewById(R.id.editTextName);
        editTextSurname = findViewById(R.id.editTextSurname);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonSignUp = findViewById(R.id.buttonSignUp);
        progressBar = findViewById(R.id.progressbar);

        buttonSignUp.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == buttonSignUp.getId()){
            String name = editTextName.getText().toString();
            String surname = editTextSurname.getText().toString();
            String email = editTextEmail.getText().toString();
            String password = editTextPassword.getText().toString();

            if(name.isEmpty()){
                editTextName.setError("Name is required");
                editTextName.requestFocus();
                return;
            }
            if(surname.isEmpty()){
                editTextSurname.setError("Surname is required");
                editTextSurname.requestFocus();
                return;
            }
            if(email.isEmpty()){
                editTextEmail.setError("Email is required");
                editTextEmail.requestFocus();
                return;
            }
            if(password.isEmpty()){
                editTextPassword.setError("Password is required");
                editTextPassword.requestFocus();
                return;
            }

            firebaseDao.createUser(name, surname, email, password);
            progressBar.setVisibility(View.VISIBLE);
            Intent i = new Intent(SignUpActivity.this, MainActivity.class);
            startActivity(i);
        }
    }
}
