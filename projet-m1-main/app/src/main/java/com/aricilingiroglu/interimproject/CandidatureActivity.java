package com.aricilingiroglu.interimproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class CandidatureActivity extends AppCompatActivity {

    private EditText editTextName, editTextSurname, editTextBirthdate, editTextNationality;
    private Button buttonSubmit;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private String jobId,jobTitle;
    private String userRole;
    private String cvUrl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_candidature);
        jobId = getIntent().getStringExtra("jobId");
        jobTitle = getIntent().getStringExtra("jobTitle");
        getUserRole();


        //boolean isMultiple = getIntent().getBooleanExtra("isMultiple", false);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        editTextName = findViewById(R.id.edit_text_name);
        editTextSurname = findViewById(R.id.edit_text_surname);
        editTextBirthdate = findViewById(R.id.edit_text_birthdate);
        editTextNationality = findViewById(R.id.edit_text_nationality);

        buttonSubmit = findViewById(R.id.button_submit);

        currentUser = mAuth.getCurrentUser();
        /*
        if (isMultiple) {
            AlertDialog.Builder builder = new AlertDialog.Builder(CandidatureActivity.this);
            builder.setTitle("Multiple Job Offers")
                    .setMessage("vous appliquer pour plusieurs ofres. Est-ce que vous etes sur?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // Continue with the submission
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // Cancel submission
                            dialog.dismiss();
                        }
                    })
                    .show();
        }*/

        if (currentUser != null) {
            db.collection("users").document(currentUser.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    User user = documentSnapshot.toObject(User.class);
                    if (user != null) {
                        editTextName.setText(user.getName());
                        editTextSurname.setText(user.getSurname());
                        editTextBirthdate.setText(user.getBirthdate());
                        editTextNationality.setText(user.getNationality());
                        cvUrl=user.getCvUrl();
                    }
                }
            });
        }


        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = editTextName.getText().toString();
                String surname = editTextSurname.getText().toString();
                String birthdate = editTextBirthdate.getText().toString();
                String nationality = editTextNationality.getText().toString();


                final Candidature candidature = new Candidature();
                candidature.setJobId(jobId);
                candidature.setJobTitle(jobTitle);
                candidature.setEtat("pending");
                candidature.setUserId(currentUser.getUid());
                candidature.setName(name);
                candidature.setSurname(surname);
                candidature.setBirthdate(birthdate);
                candidature.setNationality(nationality);
                candidature.setCvUrl(cvUrl);

                // Check if the user has already applied for this job
                db.collection("candidatures")
                        .whereEqualTo("userId", currentUser.getUid())
                        .whereEqualTo("jobId", jobId)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    if (!task.getResult().isEmpty()) {
                                        // The user has already applied for this job
                                        Toast.makeText(CandidatureActivity.this, "Deja candidaté", Toast.LENGTH_SHORT).show();
                                    } else {
                                        // The user has not applied for this job, so submit the application
                                        db.collection("candidatures").add(candidature)
                                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                    @Override
                                                    public void onSuccess(DocumentReference documentReference) {
                                                        candidature.setCandidatureId(documentReference.getId());

                                                        documentReference.set(candidature);

                                                        Toast.makeText(CandidatureActivity.this, "Candidature submitted successfully", Toast.LENGTH_SHORT).show();
                                                        Intent intent = new Intent(CandidatureActivity.this, CheckOffersActivity.class);
                                                        startActivity(intent);
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(CandidatureActivity.this, "Failed to submit candidature", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                    }
                                } else {
                                    Toast.makeText(CandidatureActivity.this, "Failed to check previous applications", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
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
        } else if (userRole.equals("chercheur d'emploi")) {
            menuInflater.inflate(R.menu.option_menu_basic, menu);
        } else if (userRole.equals("gestionnaire")) {
            menuInflater.inflate(R.menu.gestionnaire_menu, menu);
        }else if (userRole.equals("Employeur")) {
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
            Intent intent = new Intent(CandidatureActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        } else if (item.getItemId() == R.id.addjobOffer) {
            Intent intent = new Intent(CandidatureActivity.this, AddJobOffer.class);
            startActivity(intent);
            finish();

        } else if (item.getItemId() == R.id.modifiProfil) {
            Intent intent = new Intent(CandidatureActivity.this, AddPropertyActivity.class);
            startActivity(intent);
            finish();
        } else if (item.getItemId() == R.id.checkOffers) {
            Intent intent = new Intent(CandidatureActivity.this, CheckOffersActivity.class);
            startActivity(intent);
            finish();
        }else if (item.getItemId() == R.id.spontane) {
            Intent intent = new Intent(CandidatureActivity.this, SpontaneousCandidatureActivity.class);
            startActivity(intent);
            finish();
        }else if (item.getItemId() == R.id.checkCandiate) {
            Intent intent = new Intent(CandidatureActivity.this, UserApplicationsActivity.class);
            startActivity(intent);
            finish();
        }else if (item.getItemId() == R.id.evaluate) {
            Intent intent = new Intent(CandidatureActivity.this, evaluateCandidature.class);
            startActivity(intent);
            finish();
        }else if (item.getItemId() == R.id.profil) {
            Intent intent = new Intent(CandidatureActivity.this, Profil.class);
            startActivity(intent);
            finish();
        }else if (item.getItemId() == R.id.chat) {
            Intent intent = new Intent(CandidatureActivity.this, ChatActivity.class);
            startActivity(intent);
            finish();
        }else if (item.getItemId() == R.id.onay) {
            Intent intent = new Intent(CandidatureActivity.this, PendingUsersActivity.class);
            startActivity(intent);
            finish();
        }else if (item.getItemId() == R.id.login) {
            Intent intent = new Intent(CandidatureActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }else if (item.getItemId() == R.id.reportedUser) {
            Intent intent = new Intent(CandidatureActivity.this, ReportedUsersActivity.class);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
