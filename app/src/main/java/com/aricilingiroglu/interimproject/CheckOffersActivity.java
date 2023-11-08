package com.aricilingiroglu.interimproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.content.Intent;
import android.widget.EditText;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CheckOffersActivity extends AppCompatActivity {
    private RecyclerView offersRecyclerView;
    private FirebaseFirestore db;
    private JobOffersAdapter jobOffersAdapter;
    private FirebaseAuth mAuth;
    private Button loginButton;
    private String userRole;
    private EditText searchEditText;
    private Button searchButton;
    private List<JobOffer> jobOffers;
    private List<String> jobIds;
    private EditText jobTitleInput;
    private EditText locationInput;
    private EditText datePostedInput;
    private Button advancedSearchButton;
    private EditText entrepriseName;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_offers);
        db = FirebaseFirestore.getInstance();
        searchEditText = findViewById(R.id.search_edit_text);
        searchButton = findViewById(R.id.search_button);

        offersRecyclerView = findViewById(R.id.offers_recycler_view);
        offersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        jobTitleInput = findViewById(R.id.job_title_input);
        locationInput = findViewById(R.id.location_input);
        datePostedInput = findViewById(R.id.date_posted_input);
        advancedSearchButton = findViewById(R.id.advanced_search_button);
        entrepriseName=findViewById(R.id.entreprise_name);

        mAuth = FirebaseAuth.getInstance();
        loginButton = findViewById(R.id.login_button);

        fetchJobOffers();
        getUserRole();
        advancedSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (jobTitleInput.getVisibility() == View.GONE) {
                    jobTitleInput.setVisibility(View.VISIBLE);
                    locationInput.setVisibility(View.VISIBLE);
                    datePostedInput.setVisibility(View.VISIBLE);
                    entrepriseName.setVisibility(View.VISIBLE);
                } else {

                    String jobTitle = jobTitleInput.getText().toString().trim();
                    String location = locationInput.getText().toString().trim();
                    String datePosted = datePostedInput.getText().toString().trim();
                    String enterpriseName = entrepriseName.getText().toString().trim();


                    performAdvancedSearch(jobTitle, location, datePosted,enterpriseName);

                    jobTitleInput.setVisibility(View.GONE);
                    locationInput.setVisibility(View.GONE);
                    datePostedInput.setVisibility(View.GONE);
                    entrepriseName.setVisibility(View.GONE);
                }
            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String query = searchEditText.getText().toString().trim();
                performSearch(query);
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CheckOffersActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }
    private void performSearch(String query) {
        if (query.isEmpty()) {
            // si vide show all
            fetchJobOffers();
            return;
        }

        Query searchQuery = db.collection("jobOffers").whereEqualTo("jobTitle", query);
        searchQuery.get().addOnSuccessListener(queryDocumentSnapshots -> {
            List<JobOffer> filteredJobOffers = new ArrayList<>();
            List<String> filteredJobIds = new ArrayList<>();

            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                JobOffer jobOffer = document.toObject(JobOffer.class);
                filteredJobOffers.add(jobOffer);
                filteredJobIds.add(document.getId());
            }

            jobOffersAdapter.updateJobOffers(filteredJobOffers, filteredJobIds);
        }).addOnFailureListener(e -> {

        });
    }
    private void performAdvancedSearch(String jobTitle, String location, String datePosted,String enterpriseName) {
        Query searchQuery = db.collection("jobOffers");
        if (!jobTitle.isEmpty()) {
            searchQuery = searchQuery.whereEqualTo("jobTitle", jobTitle);
        }
        if (!location.isEmpty()) {
            searchQuery = searchQuery.whereEqualTo("location", location);
        }
        if (!datePosted.isEmpty()) {

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            try {
                Date date = sdf.parse(datePosted);
                if (date != null) {
                    Timestamp timestamp = new Timestamp(date);
                    searchQuery = searchQuery.whereEqualTo("datePosted", timestamp);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        if(!enterpriseName.isEmpty()){
            searchQuery = searchQuery.whereEqualTo("enterpriseName", enterpriseName);
        }

        searchQuery.get().addOnSuccessListener(queryDocumentSnapshots -> {
            List<JobOffer> filteredJobOffers = new ArrayList<>();
            List<String> filteredJobIds = new ArrayList<>();

            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                JobOffer jobOffer = document.toObject(JobOffer.class);
                filteredJobOffers.add(jobOffer);
                filteredJobIds.add(document.getId());
            }

            jobOffersAdapter.updateJobOffers(filteredJobOffers, filteredJobIds);
        }).addOnFailureListener(e -> {

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
                    fetchJobOffers();
                }
            });
        } else {
            userRole = null;
            fetchJobOffers();
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
            Intent intent = new Intent(CheckOffersActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        } else if (item.getItemId() == R.id.addjobOffer) {
            Intent intent = new Intent(CheckOffersActivity.this, AddJobOffer.class);
            startActivity(intent);
            finish();

        } else if (item.getItemId() == R.id.modifiProfil) {
            Intent intent = new Intent(CheckOffersActivity.this, AddPropertyActivity.class);
            startActivity(intent);
            finish();
        } else if (item.getItemId() == R.id.checkOffers) {
            Intent intent = new Intent(CheckOffersActivity.this, CheckOffersActivity.class);
            startActivity(intent);
            finish();
        }else if (item.getItemId() == R.id.spontane) {
            Intent intent = new Intent(CheckOffersActivity.this, SpontaneousCandidatureActivity.class);
            startActivity(intent);
            finish();
        }else if (item.getItemId() == R.id.checkCandiate) {
            Intent intent = new Intent(CheckOffersActivity.this, UserApplicationsActivity.class);
            startActivity(intent);
            finish();
        }else if (item.getItemId() == R.id.evaluate) {
            Intent intent = new Intent(CheckOffersActivity.this, evaluateCandidature.class);
            startActivity(intent);
            finish();
        }else if (item.getItemId() == R.id.profil) {
            Intent intent = new Intent(CheckOffersActivity.this, Profil.class);
            startActivity(intent);
            finish();
        }else if (item.getItemId() == R.id.chat) {
            Intent intent = new Intent(CheckOffersActivity.this, ChatActivity.class);
            startActivity(intent);
            finish();
        }else if (item.getItemId() == R.id.onay) {
            Intent intent = new Intent(CheckOffersActivity.this, PendingUsersActivity.class);
            startActivity(intent);
            finish();
        }else if (item.getItemId() == R.id.login) {
            Intent intent = new Intent(CheckOffersActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }else if (item.getItemId() == R.id.reportedUser) {
            Intent intent = new Intent(CheckOffersActivity.this, ReportedUsersActivity.class);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void onSearchButtonClick(View view) {
        String query = searchEditText.getText().toString().trim();
        performSearch(query);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            loginButton.setVisibility(View.GONE);
        } else {
            loginButton.setVisibility(View.GONE);
        }
    }

    private void fetchJobOffers() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            db.collection("users").document(userId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    userRole = documentSnapshot.getString("role");
                    Query query = db.collection("jobOffers").orderBy("timestamp", Query.Direction.DESCENDING).limit(10);
                    query.get().addOnSuccessListener(queryDocumentSnapshots -> {
                        jobOffers = new ArrayList<>();
                        jobIds = new ArrayList<>();

                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            JobOffer jobOffer = document.toObject(JobOffer.class);
                            jobOffers.add(jobOffer);
                            jobIds.add(document.getId());
                        }

                        jobOffersAdapter = new JobOffersAdapter(jobOffers, jobIds, CheckOffersActivity.this, userRole);
                        offersRecyclerView.setAdapter(jobOffersAdapter);
                    }).addOnFailureListener(e -> {
                    });
                }
            });
        } else {
            userRole = null;
            Query query = db.collection("jobOffers").orderBy("timestamp", Query.Direction.DESCENDING).limit(10);
            query.get().addOnSuccessListener(queryDocumentSnapshots -> {
                jobOffers = new ArrayList<>();
                jobIds = new ArrayList<>();

                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    JobOffer jobOffer = document.toObject(JobOffer.class);
                    jobOffers.add(jobOffer);
                    jobIds.add(document.getId());
                }

                jobOffersAdapter = new JobOffersAdapter(jobOffers, jobIds, CheckOffersActivity.this, userRole);
                offersRecyclerView.setAdapter(jobOffersAdapter);
            }).addOnFailureListener(e -> {
            });
        }
    }


}
