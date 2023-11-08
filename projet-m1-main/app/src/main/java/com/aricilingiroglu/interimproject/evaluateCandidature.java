package com.aricilingiroglu.interimproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class evaluateCandidature extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private List<Candidature> userCandidatures;
    private EditText candidatureSearchEditText;
    private Button candidatureSearchButton;
    private String userRole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evaluate_candidature);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        candidatureSearchEditText = findViewById(R.id.candidatureSearchEditText);
        candidatureSearchButton = findViewById(R.id.candidatureSearchButton);
        getUserRole();

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();
        userCandidatures = new ArrayList<>();

        if (currentUser != null) {
            //getUserCandidatures();
            getJobOffersMadeByUser();
            //getUserSpontaneousCandidatures();
        }

        candidatureSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchQuery = candidatureSearchEditText.getText().toString().trim();
                filterCandidatures(searchQuery);
            }
        });
    }
    private void filterCandidatures(String searchQuery) {
        List<Candidature> filteredCandidatures = new ArrayList<>();

        for (Candidature candidature : userCandidatures) {
            String title = candidature.getJobTitle();
            if (title.toLowerCase().contains(searchQuery.toLowerCase())) {
                filteredCandidatures.add(candidature);
            }
        }

        displayCandidatures(filteredCandidatures);
    }

    private void getJobOffersMadeByUser() {
        db.collection("jobOffers")
                .whereEqualTo("userEmail", currentUser.getEmail())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<String> jobIds = new ArrayList<>();
                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                            jobIds.add(documentSnapshot.getId());
                        }
                        getCandidaturesForJobs(jobIds);
                    }
                });
    }


    private void getCandidaturesForJobs(List<String> jobIds) {
        if (!jobIds.isEmpty()) {
            db.collection("candidatures")
                    .whereIn("jobId", jobIds)
                    .whereEqualTo("etat", "pending")
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                                Candidature candidature = documentSnapshot.toObject(Candidature.class);
                                if(candidature != null) {
                                    candidature.setCandidatureId(documentSnapshot.getId());
                                    userCandidatures.add(candidature);
                                }
                            }
                            displayCandidatures();
                        }
                    });
        } else {
        }
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
            menuInflater.inflate(R.menu.anonyme_menu, menu);}
        else if (userRole.equals("chercheur d'emploi")) {
            menuInflater.inflate(R.menu.option_menu_basic, menu);
        } else if (userRole.equals("Employeur")) {
            menuInflater.inflate(R.menu.employeur_menu, menu);
        }else {
            menuInflater.inflate(R.menu.option_menu, menu);
        }


        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.logout) {
            mAuth.signOut();
            Intent intent = new Intent(evaluateCandidature.this, LoginActivity.class);
            startActivity(intent);
            finish();
        } else if (item.getItemId() == R.id.addjobOffer) {
            Intent intent = new Intent(evaluateCandidature.this, AddJobOffer.class);
            startActivity(intent);
            finish();

        } else if (item.getItemId() == R.id.modifiProfil) {
            Intent intent = new Intent(evaluateCandidature.this, AddPropertyActivity.class);
            startActivity(intent);
            finish();
        } else if (item.getItemId() == R.id.checkOffers) {
            Intent intent = new Intent(evaluateCandidature.this, CheckOffersActivity.class);
            startActivity(intent);
            finish();
        }else if (item.getItemId() == R.id.spontane) {
            Intent intent = new Intent(evaluateCandidature.this, SpontaneousCandidatureActivity.class);
            startActivity(intent);
            finish();
        }else if (item.getItemId() == R.id.checkCandiate) {
            Intent intent = new Intent(evaluateCandidature.this, UserApplicationsActivity.class);
            startActivity(intent);
            finish();
        }else if (item.getItemId() == R.id.evaluate) {
            Intent intent = new Intent(evaluateCandidature.this, evaluateCandidature.class);
            startActivity(intent);
            finish();
        }else if (item.getItemId() == R.id.profil) {
            Intent intent = new Intent(evaluateCandidature.this, Profil.class);
            startActivity(intent);
            finish();
        }else if (item.getItemId() == R.id.chat) {
            Intent intent = new Intent(evaluateCandidature.this, ChatActivity.class);
            startActivity(intent);
            finish();
        }else if (item.getItemId() == R.id.onay) {
            Intent intent = new Intent(evaluateCandidature.this, PendingUsersActivity.class);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
    private void displayCandidatures(List<Candidature> candidatures) {
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        CandidatureEvaluateAdapter adapter = new CandidatureEvaluateAdapter(userCandidatures, this);
        adapter = new CandidatureEvaluateAdapter(candidatures, this);
        recyclerView.setAdapter(adapter);
    }

    private void getUserCandidatures() {
        db.collection("candidatures")
                .whereEqualTo("userId", currentUser.getUid())
                .whereEqualTo("userMail", currentUser.getEmail())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                            Candidature candidature = documentSnapshot.toObject(Candidature.class);
                            if(candidature != null) {
                                candidature.setCandidatureId(documentSnapshot.getId());
                                userCandidatures.add(candidature);
                            }
                        }
                        displayCandidatures();
                    }
                });
    }

    private void getUserSpontaneousCandidatures() {
        db.collection("candidatures2")
                .whereEqualTo("userId", currentUser.getUid())
                .whereEqualTo("etat", "pending")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                            Candidature candidature = documentSnapshot.toObject(Candidature.class);
                            userCandidatures.add(candidature);
                        }
                        displayCandidatures();
                    }
                });
    }

    private void displayCandidatures() {
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        CandidatureEvaluateAdapter adapter = new CandidatureEvaluateAdapter(userCandidatures, this);
        recyclerView.setAdapter(adapter);
    }

}
