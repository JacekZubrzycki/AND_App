package com.example.and_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    EditText emailId, passwordId;
    Button buttonSignIn;
    TextView textViewSignUp;
    FirebaseAuth fireBaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        fireBaseAuth = FirebaseAuth.getInstance();
        emailId = findViewById(R.id.emailLogin);
        passwordId = findViewById(R.id.passwordLogin);
        textViewSignUp = findViewById(R.id.textViewSignUp);
        buttonSignIn = findViewById(R.id.signIn);

        authStateListener = new FirebaseAuth.AuthStateListener() {

            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser fireBaseUser = fireBaseAuth.getCurrentUser();
                if (fireBaseUser != null) {
                    Toast.makeText(LoginActivity.this, "Youre are logged in", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(LoginActivity.this, HomeActivity2.class);
                    startActivity(i);
                } else {
                    Toast.makeText(LoginActivity.this, "Please Login", Toast.LENGTH_SHORT).show();
                }
            }

        };
        buttonSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailId.getText().toString();
                String password = passwordId.getText().toString();
                if (email.isEmpty()) {
                    emailId.setError("Please enter email");
                    emailId.requestFocus();
                } else if (password.isEmpty()) {
                    passwordId.setError("Please enter password");
                    passwordId.requestFocus();
                } else {
                    fireBaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()) {
                                Toast.makeText(LoginActivity.this, "Login Error, please try again", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(LoginActivity.this, "Check", Toast.LENGTH_SHORT).show();
                                Intent toHome = new Intent(LoginActivity.this, HomeActivity2.class);
                                startActivity(toHome);
                            }
                        }
                    });
                }

            }
        });

        textViewSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toSignUp = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(toSignUp);
            }
        });
    }

    protected void onStart() {
        super.onStart();
        fireBaseAuth.addAuthStateListener(authStateListener);
    }
}
