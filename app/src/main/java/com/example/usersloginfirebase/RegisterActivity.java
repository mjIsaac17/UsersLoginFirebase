package com.example.usersloginfirebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {
    EditText etEmail, etPassword;
    Button btnRegister;
    ProgressBar progressBar;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        etEmail = (EditText)findViewById(R.id.tvNewEmail);
        etPassword = (EditText)findViewById(R.id.tvNewPassword);
        mAuth = FirebaseAuth.getInstance();
        btnRegister = (Button)findViewById(R.id.btnRegister);
        progressBar = (ProgressBar)findViewById(R.id.progressBarRegister);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RegisterUser();
            }
        });

    }

    public void RegisterUser(){
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        if(email.isEmpty()){
            etEmail.setError("Correo requerido");
            etEmail.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            etEmail.setError("Ingresa una cuenta de correo válido");
            etEmail.requestFocus();
            return;
        }

        if(password.isEmpty()){
            etPassword.setError("Contraseña requerida");
            etPassword.requestFocus();
            return;
        }

        if(password.length()<6){
            etPassword.setError("La contraseña debe tener al menos 6 caracteres");
            etPassword.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressBar.setVisibility(View.GONE);
                View view = findViewById(R.id.viewRegister);
                if(task.isSuccessful()){
                    Snackbar.make(view, "Usuario registrado", Snackbar.LENGTH_SHORT).show();
                }else{
                    Snackbar.make(view, "Error al registrarse", Snackbar.LENGTH_SHORT).show();
                }
            }
        });
    }
}
