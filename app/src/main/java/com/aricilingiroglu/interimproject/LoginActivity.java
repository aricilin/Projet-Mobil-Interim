package com.aricilingiroglu.interimproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {


    private FirebaseAuth mAuth;


    private EditText usernameEditText, passwordEditText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        usernameEditText = findViewById(R.id.username_edit_text);
        passwordEditText = findViewById(R.id.password_edit_text);
        FirebaseUser user=mAuth.getCurrentUser();
        if(user != null){
            Intent intent = new Intent(LoginActivity.this, Profil.class);
            startActivity(intent);
            finish();
        }
    }

    public void login(View view) {
        String email = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        if (email.equals("") || password.equals("")) {
            Toast.makeText(this, "Enter email and password", Toast.LENGTH_LONG).show();
        } else {
            mAuth.signInWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                    Intent intent = new Intent(LoginActivity.this, Profil.class);
                    startActivity(intent);
                    finish();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(LoginActivity.this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }




    public void createAccount(View view){
        String email = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        if(email.equals("")|| password.equals("")){
            Toast.makeText(this,"Enter email and password",Toast.LENGTH_LONG).show();
        }else{
            //return task
        mAuth.createUserWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                Intent intent=new Intent(LoginActivity.this,AddPropertyActivity.class);
                startActivity(intent);
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(LoginActivity.this,e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }
    }
    public void resteAnonyme(View view){
        Intent intent=new Intent(LoginActivity.this,CheckOffersActivity.class);
        startActivity(intent);
        finish();

    }


}
