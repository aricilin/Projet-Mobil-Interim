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
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class Profil extends AppCompatActivity {
    FirebaseAuth mauth;
    private FirebaseFirestore db;
    private FirebaseAuth Auth;

    private String userRole;
    private TextView loggedInUserTextView;
    private Button addJobOfferButton, modifyProfileButton, checkOffersButton, spontaneousCandidatureButton, checkApplicationsButton, evaluateCandidatureButton, logoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mauth = FirebaseAuth.getInstance();
        Auth = FirebaseAuth.getInstance();

        setContentView(R.layout.activity_profil);

         addJobOfferButton = findViewById(R.id.add_job_offer_button);
         modifyProfileButton = findViewById(R.id.modify_profile_button);
         checkOffersButton = findViewById(R.id.check_offers_button);
         spontaneousCandidatureButton = findViewById(R.id.spontaneous_candidature_button);
         checkApplicationsButton = findViewById(R.id.check_applications_button);
         evaluateCandidatureButton = findViewById(R.id.evaluate_candidature_button);
         loggedInUserTextView = findViewById(R.id.logged_in_user_text_view);
         logoutButton=findViewById(R.id.logout);
        getUserRole();
        getUserName();

        addJobOfferButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle Add Job Offer button click
                Intent intent = new Intent(Profil.this, AddJobOffer.class);
                startActivity(intent);
                finish();
            }
        });
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(Profil.this,LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        modifyProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle Modify Profile button click
                Intent intent = new Intent(Profil.this, AddPropertyActivity.class);
                startActivity(intent);
                finish();
            }
        });

        checkOffersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle Check Offers button click
                Intent intent = new Intent(Profil.this, CheckOffersActivity.class);
                startActivity(intent);
                finish();
            }
        });

        spontaneousCandidatureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle Spontaneous Candidature button click
                Intent intent = new Intent(Profil.this, SpontaneousCandidatureActivity.class);
                startActivity(intent);
                finish();
            }
        });

        checkApplicationsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle Check Applications button click
                Intent intent = new Intent(Profil.this, UserApplicationsActivity.class);
                startActivity(intent);
                finish();
            }
        });

        evaluateCandidatureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle Evaluate Candidature button click
                Intent intent = new Intent(Profil.this, evaluateCandidature.class);
                startActivity(intent);
                finish();
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
                    if (userRole == null) {
                        addJobOfferButton.setVisibility(View.GONE);
                        evaluateCandidatureButton.setVisibility(View.GONE);
                        modifyProfileButton.setVisibility(View.GONE);
                        checkApplicationsButton.setVisibility(View.GONE);
                        spontaneousCandidatureButton.setVisibility(View.GONE);
                        logoutButton.setVisibility(View.GONE);
                    } else if (userRole.equals("chercheur d'emploi")){
                        addJobOfferButton.setVisibility(View.GONE);
                        evaluateCandidatureButton.setVisibility(View.GONE);
                    } else if (userRole.equals("Employeur")) {
                        spontaneousCandidatureButton.setVisibility(View.GONE);
                        checkApplicationsButton.setVisibility(View.GONE);



                    }

                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        //menuInflater.inflate(R.menu.option_menu, menu);
        if (userRole == null) {
            menuInflater.inflate(R.menu.anonyme_menu, menu);
        } else if (userRole.equals("gestionnaire")) {
            menuInflater.inflate(R.menu.gestionnaire_menu, menu);
        }else if (userRole.equals("chercheur d'emploi")) {
            menuInflater.inflate(R.menu.option_menu_basic, menu);
        } else if (userRole.equals("Employeur")) {
            String userId = Auth.getCurrentUser().getUid();
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
    private void getUserName() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            db = FirebaseFirestore.getInstance();
            db.collection("users").document(userId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    String userName = documentSnapshot.getString("name");
                    if (userName == null || userName.isEmpty()) {
                        loggedInUserTextView.setText("Veillez mettre à jour votre profil");
                    } else {
                        loggedInUserTextView.setText("Bonjour : " + userName);
                    }
                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.logout) {
            mauth.signOut();
            Intent intent = new Intent(Profil.this, LoginActivity.class);
            startActivity(intent);
            finish();
        } else if (item.getItemId() == R.id.addjobOffer) {
            Intent intent = new Intent(Profil.this, AddJobOffer.class);
            startActivity(intent);
            finish();

        } else if (item.getItemId() == R.id.modifiProfil) {
            Intent intent = new Intent(Profil.this, AddPropertyActivity.class);
            startActivity(intent);
            finish();
        } else if (item.getItemId() == R.id.checkOffers) {
            Intent intent = new Intent(Profil.this, CheckOffersActivity.class);
            startActivity(intent);
            finish();
        }else if (item.getItemId() == R.id.spontane) {
            Intent intent = new Intent(Profil.this, SpontaneousCandidatureActivity.class);
            startActivity(intent);
            finish();
        }else if (item.getItemId() == R.id.checkCandiate) {
            Intent intent = new Intent(Profil.this, UserApplicationsActivity.class);
            startActivity(intent);
            finish();
        }else if (item.getItemId() == R.id.evaluate) {
            Intent intent = new Intent(Profil.this, evaluateCandidature.class);
            startActivity(intent);
            finish();
        }else if (item.getItemId() == R.id.profil) {
            Intent intent = new Intent(Profil.this, Profil.class);
            startActivity(intent);
            finish();
        }else if (item.getItemId() == R.id.onay) {
            Intent intent = new Intent(Profil.this, PendingUsersActivity.class);
            startActivity(intent);
            finish();
        }else if (item.getItemId() == R.id.chat) {
            Intent intent = new Intent(Profil.this, ChatActivity.class);
            startActivity(intent);
            finish();
        }else if (item.getItemId() == R.id.login) {
            Intent intent = new Intent(Profil.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }else if (item.getItemId() == R.id.reportedUser) {
            Intent intent = new Intent(Profil.this, ReportedUsersActivity.class);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
