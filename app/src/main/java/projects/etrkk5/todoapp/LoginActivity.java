package projects.etrkk5.todoapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import projects.etrkk5.todoapp.dao.FirebaseDao;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{
    FirebaseDao firebaseDao = FirebaseDao.getInstance();
    EditText editTextEmail, editTextPassword;
    Button buttonLogin;
    TextView textViewSignUp;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        isUserAlreadyLoggedIn();

        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        textViewSignUp = findViewById(R.id.textViewSignUp);
        progressBar = findViewById(R.id.progressbar);

        buttonLogin.setOnClickListener(this);
        textViewSignUp.setOnClickListener(this);
    }

    public void isUserAlreadyLoggedIn(){
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser() != null){
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
        }
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == buttonLogin.getId()){
            String email = editTextEmail.getText().toString();
            String password = editTextPassword.getText().toString();

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

            progressBar.setVisibility(View.VISIBLE);
            firebaseDao.connect(email, password);

            Intent i = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(i);
        }
        if(v.getId() == textViewSignUp.getId()){
            Intent i = new Intent(LoginActivity.this, SignUpActivity.class);
            startActivity(i);
        }
    }
}
