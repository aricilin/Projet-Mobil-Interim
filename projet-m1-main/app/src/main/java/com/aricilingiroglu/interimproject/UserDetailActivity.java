package com.aricilingiroglu.interimproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class UserDetailActivity extends AppCompatActivity {

    private static final String TAG = "UserDetailActivity";

    private TextView nameTextView;
    private TextView surnameTextView;
    private TextView nationalityTextView;
    private TextView paidTextView;
    private String userRole;
    private FirebaseAuth mAuth;

    private FirebaseFirestore db;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);
        getUserRole();
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        if (getIntent().hasExtra("userId")) {
            userId = getIntent().getStringExtra("userId");
        } else {
            Log.e(TAG, "No user ID found in the intent");
            finish();
        }

        nameTextView = findViewById(R.id.nameTextView);
        surnameTextView = findViewById(R.id.surnameTextView);
        nationalityTextView = findViewById(R.id.nationalityTextView);
        paidTextView = findViewById(R.id.paidTextView);

        fetchUserInformation();
    }


    private void getUserRole() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            db = FirebaseFirestore.getInstance();
            db.collection("users").document(userId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    userRole = documentSnapshot.getString("role");
                    invalidateOptionsMenu();
                }
            });
        }
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        //menuInflater.inflate(R.menu.option_menu, menu);
        if (userRole == null) {
            menuInflater.inflate(R.menu.anonyme_menu, menu);
        }else if (userRole.equals("gestionnaire")) {
            menuInflater.inflate(R.menu.gestionnaire_menu, menu);
        } else if (userRole.equals("chercheur d'emploi")) {
            menuInflater.inflate(R.menu.option_menu_basic, menu);
        } else if (userRole.equals("Employeur")) {
            String userId = mAuth.getCurrentUser().getUid();
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("users").document(userId).get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            String etat = documentSnapshot.getString("etat");
                            if (etat != null && etat.equals("pending")) {
                                menuInflater.inflate(R.menu.employeur_menu_basic, menu);
                            } else {
                                menuInflater.inflate(R.menu.employeur_menu, menu);
                            }
                        }
                    });
        } else {
            menuInflater.inflate(R.menu.option_menu, menu);
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.logout) {
            mAuth.signOut();
            Intent intent = new Intent(UserDetailActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        } else if (item.getItemId() == R.id.addjobOffer) {
            Intent intent = new Intent(UserDetailActivity.this, AddJobOffer.class);
            startActivity(intent);
            finish();

        } else if (item.getItemId() == R.id.modifiProfil) {
            Intent intent = new Intent(UserDetailActivity.this, AddPropertyActivity.class);
            startActivity(intent);
            finish();
        } else if (item.getItemId() == R.id.checkOffers) {
            Intent intent = new Intent(UserDetailActivity.this, CheckOffersActivity.class);
            startActivity(intent);
            finish();
        }else if (item.getItemId() == R.id.spontane) {
            Intent intent = new Intent(UserDetailActivity.this, SpontaneousCandidatureActivity.class);
            startActivity(intent);
            finish();
        }else if (item.getItemId() == R.id.checkCandiate) {
            Intent intent = new Intent(UserDetailActivity.this, UserApplicationsActivity.class);
            startActivity(intent);
            finish();
        }else if (item.getItemId() == R.id.evaluate) {
            Intent intent = new Intent(UserDetailActivity.this, evaluateCandidature.class);
            startActivity(intent);
            finish();
        }else if (item.getItemId() == R.id.profil) {
            Intent intent = new Intent(UserDetailActivity.this, Profil.class);
            startActivity(intent);
            finish();
        }else if (item.getItemId() == R.id.chat) {
            Intent intent = new Intent(UserDetailActivity.this, ChatActivity.class);
            startActivity(intent);
            finish();
        }else if (item.getItemId() == R.id.onay) {
            Intent intent = new Intent(UserDetailActivity.this, PendingUsersActivity.class);
            startActivity(intent);
            finish();
        }else if (item.getItemId() == R.id.login) {
            Intent intent = new Intent(UserDetailActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }else if (item.getItemId() == R.id.reportedUser) {
            Intent intent = new Intent(UserDetailActivity.this, ReportedUsersActivity.class);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void fetchUserInformation() {
        db.collection("users")
                .document(userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null && document.exists()) {
                            String name = document.getString("name");
                            String surname = document.getString("surname");
                            String nationality = document.getString("nationality");
                            String paid = document.getString("paid");

                            nameTextView.setText(name);
                            surnameTextView.setText(surname);
                            nationalityTextView.setText(nationality);
                            paidTextView.setText(paid);
                        } else {
                            Log.d(TAG, "No such document");
                        }
                    } else {
                        Log.d(TAG, "Error getting user document: ", task.getException());
                    }
                });
    }
}
