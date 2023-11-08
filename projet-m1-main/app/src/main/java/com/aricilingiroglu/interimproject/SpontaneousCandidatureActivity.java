package com.aricilingiroglu.interimproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpontaneousCandidatureActivity extends AppCompatActivity {

    private EditText editTextName, editTextSurname, editTextBirthdate;
    private EditText editTextTargetJobs, editTextTargetEmployers, editTextTargetLocation;
    private Button buttonSubmit;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String cvUrl;

    private FirebaseUser currentUser;
    private String userRole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spontaneous_candidature);
        getUserRole();
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        editTextTargetJobs = findViewById(R.id.target_jobs);
        editTextTargetEmployers = findViewById(R.id.target_employers);
        editTextTargetLocation = findViewById(R.id.Location);

        editTextName = findViewById(R.id.edit_text_name);
        editTextSurname = findViewById(R.id.edit_text_surname);
        editTextBirthdate = findViewById(R.id.edit_text_birthdate);

        buttonSubmit = findViewById(R.id.button_submit);

        currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            db.collection("users").document(currentUser.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    User user = documentSnapshot.toObject(User.class);
                    if (user != null) {
                        editTextName.setText(user.getName());
                        editTextSurname.setText(user.getSurname());
                        editTextBirthdate.setText(user.getBirthdate());
                        cvUrl=user.getCvUrl();

                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // Handle failure
                    Toast.makeText(SpontaneousCandidatureActivity.this, "Failed to load user details", Toast.LENGTH_SHORT).show();
                }
            });
        }

        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = editTextName.getText().toString();
                String surname = editTextSurname.getText().toString();
                String birthdate = editTextBirthdate.getText().toString();
                String targetJobs = editTextTargetJobs.getText().toString();
                String targetLocation= editTextTargetLocation.getText().toString();
                String targetEmployer= editTextTargetEmployers.getText().toString();


                db.collection("jobOffers")
                        .whereEqualTo("jobTitle", targetJobs)
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot querySnapshot) {
                                List<DocumentSnapshot> filteredJobOffers = querySnapshot.getDocuments();
                                if (!targetLocation.isEmpty()) {
                                    filteredJobOffers = filterJobOffersByLocation(filteredJobOffers, targetLocation);
                                }
                                if (!targetEmployer.isEmpty()) {
                                    filteredJobOffers = filterJobOffersByEmployers(filteredJobOffers, targetEmployer);
                                }
                                for (DocumentSnapshot documentSnapshot : filteredJobOffers) {
                                    JobOffer jobOffer = documentSnapshot.toObject(JobOffer.class);
                                    String jobId = documentSnapshot.getId();

                                    Candidature candidature = new Candidature();
                                    candidature.setJobId(jobId);
                                    candidature.setJobTitle(jobOffer.getJobTitle());
                                    candidature.setUserId(currentUser.getUid());
                                    candidature.setName(name);
                                    candidature.setSurname(surname);
                                    candidature.setBirthdate(birthdate);
                                    candidature.setEtat("pending");
                                    candidature.setCvUrl(cvUrl);



                                    db.collection("candidatures")
                                            .add(candidature)
                                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                @Override
                                                public void onSuccess(DocumentReference documentReference) {
                                                    candidature.setCandidatureId(documentReference.getId());
                                                    documentReference.set(candidature);

                                                    Toast.makeText(SpontaneousCandidatureActivity.this, "Candidature Submitted Successfully!", Toast.LENGTH_SHORT).show();

                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    // Something went wrong.
                                                    Toast.makeText(SpontaneousCandidatureActivity.this, "Failed to submit the candidature. Please try again!", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(SpontaneousCandidatureActivity.this, "Failed to filter job offers. Please try again!", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

    }
    private List<DocumentSnapshot> filterJobOffersByLocation(List<DocumentSnapshot> jobOffers, String targetLocation) {
        List<DocumentSnapshot> filteredJobOffers = new ArrayList<>();

        for (DocumentSnapshot documentSnapshot : jobOffers) {
            JobOffer jobOffer = documentSnapshot.toObject(JobOffer.class);
            if (jobOffer != null && jobOffer.getLocation() != null && jobOffer.getLocation().equals(targetLocation)) {
                filteredJobOffers.add(documentSnapshot);
            }
        }

        return filteredJobOffers;
    }
    private List<DocumentSnapshot> filterJobOffersByEmployers(List<DocumentSnapshot> jobOffers, String targetEmployers) {
        List<DocumentSnapshot> filteredJobOffers = new ArrayList<>();

        for (DocumentSnapshot documentSnapshot : jobOffers) {
            JobOffer jobOffer = documentSnapshot.toObject(JobOffer.class);
            if (jobOffer != null && jobOffer.getEnterpriseName() != null && jobOffer.getEnterpriseName().equals(targetEmployers)) {
                filteredJobOffers.add(documentSnapshot);
            }
        }

        return filteredJobOffers;
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
            Intent intent = new Intent(SpontaneousCandidatureActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        } else if (item.getItemId() == R.id.addjobOffer) {
            Intent intent = new Intent(SpontaneousCandidatureActivity.this, AddJobOffer.class);
            startActivity(intent);
            finish();

        } else if (item.getItemId() == R.id.modifiProfil) {
            Intent intent = new Intent(SpontaneousCandidatureActivity.this, AddPropertyActivity.class);
            startActivity(intent);
            finish();
        } else if (item.getItemId() == R.id.checkOffers) {
            Intent intent = new Intent(SpontaneousCandidatureActivity.this, CheckOffersActivity.class);
            startActivity(intent);
            finish();
        }else if (item.getItemId() == R.id.spontane) {
            Intent intent = new Intent(SpontaneousCandidatureActivity.this, SpontaneousCandidatureActivity.class);
            startActivity(intent);
            finish();
        }else if (item.getItemId() == R.id.checkCandiate) {
            Intent intent = new Intent(SpontaneousCandidatureActivity.this, UserApplicationsActivity.class);
            startActivity(intent);
            finish();
        }else if (item.getItemId() == R.id.evaluate) {
            Intent intent = new Intent(SpontaneousCandidatureActivity.this, evaluateCandidature.class);
            startActivity(intent);
            finish();
        }else if (item.getItemId() == R.id.profil) {
            Intent intent = new Intent(SpontaneousCandidatureActivity.this, Profil.class);
            startActivity(intent);
            finish();
        }else if (item.getItemId() == R.id.chat) {
            Intent intent = new Intent(SpontaneousCandidatureActivity.this, ChatActivity.class);
            startActivity(intent);
            finish();
        }else if (item.getItemId() == R.id.onay) {
            Intent intent = new Intent(SpontaneousCandidatureActivity.this, PendingUsersActivity.class);
            startActivity(intent);
            finish();
        }else if (item.getItemId() == R.id.login) {
            Intent intent = new Intent(SpontaneousCandidatureActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }else if (item.getItemId() == R.id.reportedUser) {
            Intent intent = new Intent(SpontaneousCandidatureActivity.this, ReportedUsersActivity.class);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
