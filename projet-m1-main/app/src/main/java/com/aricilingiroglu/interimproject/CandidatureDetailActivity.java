package com.aricilingiroglu.interimproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import android.Manifest;
import android.content.pm.PackageManager;



public class CandidatureDetailActivity extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String userRole;
    private static final int CALL_PERMISSION_REQUEST_CODE = 1;

    private FirebaseAuth mAuth;
    private String cvUrl;
    private Button appeler,signaler;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_candidature_detail);
        mAuth = FirebaseAuth.getInstance();
        appeler = findViewById(R.id.Appeler);
        getUserRole();

        Intent intent = getIntent();
        String candidatureId = intent.getStringExtra("candidatureId");

        if (candidatureId != null) {
            db.collection("candidatures").document(candidatureId).get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            Candidature candidature = documentSnapshot.toObject(Candidature.class);
                            if (candidature != null) {
                                db.collection("jobOffers").document(candidature.getJobId()).get()
                                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                            @Override
                                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                JobOffer jobOffer = documentSnapshot.toObject(JobOffer.class);
                                                if (jobOffer != null) {
                                                    TextView jobTitleTextView = findViewById(R.id.jobTitleTextView);
                                                    jobTitleTextView.setText(jobOffer.getJobTitle());
                                                    TextView jobDescriptionTextView = findViewById(R.id.jobDescriptionTextView);
                                                    jobDescriptionTextView.setText(jobOffer.getJobDescription());
                                                    TextView jobTypeTextView = findViewById(R.id.jobTypeTextView);
                                                    jobTypeTextView.setText(jobOffer.getJobType());
                                                    TextView locationTextView = findViewById(R.id.locationTextView);
                                                    locationTextView.setText(jobOffer.getLocation());
                                                    TextView enterpriseNameTextView = findViewById(R.id.enterpriseNameTextView);
                                                    enterpriseNameTextView.setText(jobOffer.getEnterpriseName());
                                                }
                                            }
                                        });
                                db.collection("users").document(candidature.getUserId()).get()
                                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                            @Override
                                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                User user = documentSnapshot.toObject(User.class);
                                                if (user != null) {
                                                    TextView userNameTextView = findViewById(R.id.userNameTextView);
                                                    userNameTextView.setText(user.getName());
                                                    TextView userSurnameTextView = findViewById(R.id.userSurnameTextView);
                                                    userSurnameTextView.setText(user.getSurname());
                                                    cvUrl = user.getCvUrl();
                                                    TextView cvTextView = findViewById(R.id.cvTextView);
                                                    cvTextView.setText(cvUrl);
                                                }
                                            }
                                        });
                            } else {
                                Toast.makeText(CandidatureDetailActivity.this, "No candidature found", Toast.LENGTH_SHORT).show();
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(CandidatureDetailActivity.this, "Failed to retrieve candidature", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(this, "No candidature found", Toast.LENGTH_SHORT).show();
        }

        appeler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String candidatureId = getIntent().getStringExtra("candidatureId");
                if (candidatureId != null) {
                    db.collection("candidatures").document(candidatureId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            Candidature candidature = documentSnapshot.toObject(Candidature.class);
                            if (candidature != null) {
                                String userId = candidature.getUserId();
                                db.collection("users").document(userId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        User user = documentSnapshot.toObject(User.class);
                                        if (user != null) {
                                            String phoneNumber = user.getPhoneNumber();
                                            if (phoneNumber != null && !phoneNumber.isEmpty()) {
                                                if (ContextCompat.checkSelfPermission(CandidatureDetailActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                                    ActivityCompat.requestPermissions(CandidatureDetailActivity.this, new String[]{Manifest.permission.CALL_PHONE}, CALL_PERMISSION_REQUEST_CODE);
                                                } else {
                                                    initiatePhoneCall(phoneNumber);
                                                }
                                            } else {
                                                showToast("No numero portable");
                                            }
                                        } else {
                                            showToast(" pas d'utilisateur ");
                                        }
                                    }
                                });
                            } else {
                                showToast("Candidat n'exise pas ");
                            }
                        }
                    });
                }
            }
        });

        signaler = findViewById(R.id.Signaler);

        signaler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String candidatureId = getIntent().getStringExtra("candidatureId");
                if (candidatureId != null) {
                    db.collection("candidatures").document(candidatureId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            Candidature candidature = documentSnapshot.toObject(Candidature.class);
                            if (candidature != null) {
                                String userId = candidature.getUserId();
                                db.collection("users").document(userId).update("accountStatus", "Reported")
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                showToast("User status has been reported successfully");
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                showToast("Failed to report user status");

                                            }
                                        });
                            } else {
                                showToast("No candidature found");
                            }
                        }
                    });
                }
            }
        });

    }
        private void initiatePhoneCall(String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        startActivity(intent);
    }

    private void showToast(String message) {
        Toast.makeText(CandidatureDetailActivity.this, message, Toast.LENGTH_SHORT).show();
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CALL_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                String jobId = getIntent().getStringExtra("jobId");
                if (jobId != null) {
                    db.collection("jobOffers").document(jobId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            JobOffer jobOffer = documentSnapshot.toObject(JobOffer.class);
                            if (jobOffer != null) {
                                String creatorEmail = jobOffer.getUserEmail();
                                db.collection("users").whereEqualTo("email", creatorEmail).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot querySnapshot) {
                                        if (!querySnapshot.isEmpty()) {
                                            DocumentSnapshot userSnapshot = querySnapshot.getDocuments().get(0);
                                            String phoneNumber = userSnapshot.getString("phoneNumber");
                                            if (phoneNumber != null && !phoneNumber.isEmpty()) {
                                                initiatePhoneCall(phoneNumber);
                                            } else {
                                                showToast("Pas de numero portable");
                                            }
                                        } else {
                                            showToast("Pas d'utilisatuer ");
                                        }
                                    }
                                });
                            }
                        }
                    });
                }
            } else {
                showToast("Pas de permission");
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.logout) {
            mAuth.signOut();
            Intent intent = new Intent(CandidatureDetailActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        } else if (item.getItemId() == R.id.addjobOffer) {
            Intent intent = new Intent(CandidatureDetailActivity.this, AddJobOffer.class);
            startActivity(intent);
            finish();

        } else if (item.getItemId() == R.id.modifiProfil) {
            Intent intent = new Intent(CandidatureDetailActivity.this, AddPropertyActivity.class);
            startActivity(intent);
            finish();
        } else if (item.getItemId() == R.id.checkOffers) {
            Intent intent = new Intent(CandidatureDetailActivity.this, CheckOffersActivity.class);
            startActivity(intent);
            finish();
        }else if (item.getItemId() == R.id.spontane) {
            Intent intent = new Intent(CandidatureDetailActivity.this, SpontaneousCandidatureActivity.class);
            startActivity(intent);
            finish();
        }else if (item.getItemId() == R.id.checkCandiate) {
            Intent intent = new Intent(CandidatureDetailActivity.this, UserApplicationsActivity.class);
            startActivity(intent);
            finish();
        }else if (item.getItemId() == R.id.evaluate) {
            Intent intent = new Intent(CandidatureDetailActivity.this, evaluateCandidature.class);
            startActivity(intent);
            finish();
        }else if (item.getItemId() == R.id.profil) {
            Intent intent = new Intent(CandidatureDetailActivity.this, Profil.class);
            startActivity(intent);
            finish();
        }else if (item.getItemId() == R.id.chat) {
            Intent intent = new Intent(CandidatureDetailActivity.this, ChatActivity.class);
            startActivity(intent);
            finish();
        }else if (item.getItemId() == R.id.onay) {
            Intent intent = new Intent(CandidatureDetailActivity.this, PendingUsersActivity.class);
            startActivity(intent);
            finish();
        }else if (item.getItemId() == R.id.login) {
            Intent intent = new Intent(CandidatureDetailActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }else if (item.getItemId() == R.id.reportedUser) {
            Intent intent = new Intent(CandidatureDetailActivity.this, ReportedUsersActivity.class);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
