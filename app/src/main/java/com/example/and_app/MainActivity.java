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

public class MainActivity extends AppCompatActivity {
    EditText emailId, passwordId;
    Button buttonSignUp;
    TextView textViewSignIn;
    FirebaseAuth fireBaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fireBaseAuth = FirebaseAuth.getInstance();
        emailId = findViewById(R.id.email);
        passwordId = findViewById(R.id.password);
        textViewSignIn = findViewById(R.id.textViewLogin);
        buttonSignUp = findViewById(R.id.signUp);
        buttonSignUp.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Check", Toast.LENGTH_SHORT).show();
                String email = emailId.getText().toString();
                String password = passwordId.getText().toString();
                if (email.isEmpty()) {
                    emailId.setError("Please enter email");
                    emailId.requestFocus();
                } else if (password.isEmpty()) {
                    passwordId.setError("Please enter password");
                    passwordId.requestFocus();

                } else {
                    fireBaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()) {
                                Toast.makeText(MainActivity.this, "Cannot Sign Up, please try again later", Toast.LENGTH_SHORT).show();
                            } else {
                                startActivity(new Intent(MainActivity.this, HomeActivity2.class));
                            }
                        }
                    });


                }

            }

        });
        textViewSignIn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(i);
            }
        });
    }
}