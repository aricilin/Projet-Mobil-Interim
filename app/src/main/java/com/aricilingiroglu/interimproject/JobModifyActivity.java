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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class JobModifyActivity extends AppCompatActivity {
    private FirebaseFirestore db;

    private EditText titleEditText;
    private EditText descriptionEditText;
    private EditText locationEditText;
    private EditText salaryEditText;
    private EditText enterpriseNameEditText;
    private EditText jobTypeEditText;
    private EditText dateDebutEditText;
    private EditText dateFinEditText;

    private Button saveChangesButton;
    private FirebaseAuth mAuth;
    private String userRole;

    private String jobId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_modify);
        mAuth = FirebaseAuth.getInstance();

        db = FirebaseFirestore.getInstance();

        titleEditText = findViewById(R.id.job_title_edit_text);
        descriptionEditText = findViewById(R.id.job_description_edit_text);
        locationEditText = findViewById(R.id.location_edit_text);
        salaryEditText = findViewById(R.id.salary_edit_text);
        enterpriseNameEditText = findViewById(R.id.enterprise_name_edit_text);
        jobTypeEditText = findViewById(R.id.job_type_edit_text);
        dateDebutEditText = findViewById(R.id.date_debut_edit_text);
        dateFinEditText = findViewById(R.id.date_fin_edit_text);
        saveChangesButton = findViewById(R.id.update_button);
        getUserRole();

        jobId = getIntent().getStringExtra("jobId");
        if (jobId != null) {
            loadJobDetails();
        }

        saveChangesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateJobOffer();
            }
        });
    }

    private void loadJobDetails() {
        db.collection("jobOffers").document(jobId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                JobOffer jobOffer = documentSnapshot.toObject(JobOffer.class);
                if (jobOffer != null) {
                    titleEditText.setText(jobOffer.getJobTitle());
                    descriptionEditText.setText(jobOffer.getJobDescription());
                    locationEditText.setText(jobOffer.getLocation());
                    salaryEditText.setText(jobOffer.getEstimatedSalary());
                    enterpriseNameEditText.setText(jobOffer.getEnterpriseName());
                    jobTypeEditText.setText(jobOffer.getJobType());
                    dateDebutEditText.setText(jobOffer.getDateDebut());
                    dateFinEditText.setText(jobOffer.getDateFin());
                }
            }
        });
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
            Intent intent = new Intent(JobModifyActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        } else if (item.getItemId() == R.id.addjobOffer) {
            Intent intent = new Intent(JobModifyActivity.this, AddJobOffer.class);
            startActivity(intent);
            finish();

        } else if (item.getItemId() == R.id.modifiProfil) {
            Intent intent = new Intent(JobModifyActivity.this, AddPropertyActivity.class);
            startActivity(intent);
            finish();
        } else if (item.getItemId() == R.id.checkOffers) {
            Intent intent = new Intent(JobModifyActivity.this, CheckOffersActivity.class);
            startActivity(intent);
            finish();
        }else if (item.getItemId() == R.id.spontane) {
            Intent intent = new Intent(JobModifyActivity.this, SpontaneousCandidatureActivity.class);
            startActivity(intent);
            finish();
        }else if (item.getItemId() == R.id.checkCandiate) {
            Intent intent = new Intent(JobModifyActivity.this, UserApplicationsActivity.class);
            startActivity(intent);
            finish();
        }else if (item.getItemId() == R.id.evaluate) {
            Intent intent = new Intent(JobModifyActivity.this, evaluateCandidature.class);
            startActivity(intent);
            finish();
        }else if (item.getItemId() == R.id.profil) {
            Intent intent = new Intent(JobModifyActivity.this, Profil.class);
            startActivity(intent);
            finish();
        }else if (item.getItemId() == R.id.chat) {
            Intent intent = new Intent(JobModifyActivity.this, ChatActivity.class);
            startActivity(intent);
            finish();
        }else if (item.getItemId() == R.id.onay) {
            Intent intent = new Intent(JobModifyActivity.this, PendingUsersActivity.class);
            startActivity(intent);
            finish();
        }else if (item.getItemId() == R.id.login) {
            Intent intent = new Intent(JobModifyActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }else if (item.getItemId() == R.id.reportedUser) {
            Intent intent = new Intent(JobModifyActivity.this, ReportedUsersActivity.class);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateJobOffer() {
        String title = titleEditText.getText().toString().trim();
        String description = descriptionEditText.getText().toString().trim();
        String location = locationEditText.getText().toString().trim();
        String salary = salaryEditText.getText().toString().trim();
        String enterpriseName = enterpriseNameEditText.getText().toString().trim();
        String jobType = jobTypeEditText.getText().toString().trim();
        String dateDebut = dateDebutEditText.getText().toString().trim();
        String dateFin = dateFinEditText.getText().toString().trim();
        String userEmail = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getEmail() : null;
        if (userEmail == null) {
            Toast.makeText(this, "User is not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }

        JobOffer updatedJobOffer = new JobOffer(title, description, location, salary, enterpriseName, userEmail, jobType, dateDebut, dateFin);

        db.collection("jobOffers").document(jobId).set(updatedJobOffer)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(JobModifyActivity.this, "Job offer updated successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(JobModifyActivity.this, "Failed to update job offer", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
