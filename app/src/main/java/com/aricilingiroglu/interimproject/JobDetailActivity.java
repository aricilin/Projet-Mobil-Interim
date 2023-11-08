package com.aricilingiroglu.interimproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
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
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;import android.widget.CheckBox;import android.Manifest;



public class JobDetailActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private Button shareFacebookButton,shareTwitterButton,shareEmailButton,shareWhatsAppButton,appeler,candidatureButton;


    private TextView titleTextView;
    private static final int CALL_PERMISSION_REQUEST_CODE = 1;
    private TextView descriptionTextView;
    private TextView locationTextView;
    private TextView salaryTextView;
    private TextView enterpriseNameTextView;
    private TextView sourceLinkTextView;
    private TextView jobType;
    private TextView dateDebut,dateFin;
    FirebaseAuth mAuth;
    private Button searchSimilarJobsButton,onPartagerButtonClick;
    private Button emailJobOwnerButton;
    private String jobOwnerEmail;

    private String userRole;
    private String jobTitle,jobDescription;
    private CheckBox sameEmployerCheckBox, sameTypeCheckBox, sameLocationCheckBox;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_detail);
        mAuth = FirebaseAuth.getInstance();
        getUserRole();
        onPartagerButtonClick = findViewById(R.id.Partager);
        emailJobOwnerButton = findViewById(R.id.envoyer_mail);
//        shareWhatsAppButton = findViewById(R.id.partagerWhatsApp);
//        shareTwitterButton = findViewById(R.id.partagerTwitter);
//        shareEmailButton = findViewById(R.id.PartagerMAil);
        appeler=findViewById(R.id.Appeler);
        searchSimilarJobsButton = findViewById(R.id.searchSimilarJob);
        sameEmployerCheckBox = findViewById(R.id.same_employer_check_box);
        sameTypeCheckBox = findViewById(R.id.same_type_check_box);
        sameLocationCheckBox = findViewById(R.id.same_location_check_box);




        db = FirebaseFirestore.getInstance();
        TextView sourceLinkTextView = findViewById(R.id.source_link_text_view);
        titleTextView = findViewById(R.id.title_text_view);
        descriptionTextView = findViewById(R.id.description_text_view);
        locationTextView = findViewById(R.id.location_text_view);
        salaryTextView = findViewById(R.id.salary_text_view);
        enterpriseNameTextView = findViewById(R.id.enterprise_name_text_view);
        dateDebut=findViewById(R.id.date_debut);
        dateFin=findViewById(R.id.date_fin);
        candidatureButton = findViewById(R.id.candidater_button);
        jobType=findViewById(R.id.job_type);
        searchSimilarJobsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent similarJobsIntent = new Intent(JobDetailActivity.this, SimilarJobsActivity.class);
                similarJobsIntent.putExtra("sameEmployer", enterpriseNameTextView.getText().toString());
                similarJobsIntent.putExtra("sameType", jobType.getText().toString());
                similarJobsIntent.putExtra("sameLocation", locationTextView.getText().toString());
                startActivity(similarJobsIntent);
            }
        });


        String jobId = getIntent().getStringExtra("jobId");
        if (jobId != null) {
            db.collection("jobOffers").document(jobId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    JobOffer jobOffer = documentSnapshot.toObject(JobOffer.class);
                    if (jobOffer != null) {
                        titleTextView.setText(jobOffer.getJobTitle());
                        descriptionTextView.setText(jobOffer.getJobDescription());
                        locationTextView.setText(jobOffer.getLocation());
                        salaryTextView.setText(jobOffer.getEstimatedSalary());
                        enterpriseNameTextView.setText(jobOffer.getEnterpriseName());
                        jobType.setText(jobOffer.getJobType());
                        dateDebut.setText((jobOffer.getDateDebut()));
                        dateFin.setText((jobOffer.getDateFin()));
                        jobOwnerEmail = jobOffer.getUserEmail();

                        String sourceLink = jobOffer.getSourceLink();
                        if (sourceLink != null && !sourceLink.isEmpty()) {

                            sourceLinkTextView.setVisibility(View.VISIBLE);
                            sourceLinkTextView.setText(sourceLink);
                            sourceLinkTextView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    openSourceLink(sourceLink);
                                }
                            });
                        } else {

                            sourceLinkTextView.setVisibility(View.GONE);
                        }
                        jobTitle = jobOffer.getJobTitle();
                        jobDescription = jobOffer.getJobDescription();
                    }
                }
            });
        }
        onPartagerButtonClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String jobId = getIntent().getStringExtra("jobId");
                if (jobId != null) {
                    db.collection("jobOffers").document(jobId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            JobOffer jobOffer = documentSnapshot.toObject(JobOffer.class);
                            if (jobOffer != null) {
                                String jobTitle = jobOffer.getJobTitle();
                                String jobDescription = jobOffer.getJobDescription();
                                String location = jobOffer.getLocation();
                                String enterpriseName = jobOffer.getEnterpriseName();

                                String shareText = "Job Title: " + jobTitle + "\n" +
                                        "Job Description: " + jobDescription + "\n" +
                                        "Location: " + location + "\n" +
                                        "Enterprise Name: " + enterpriseName;

                                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                                shareIntent.setType("text/plain");
                                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Job Details");
                                shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);

                                Intent chooser = Intent.createChooser(shareIntent, "Share via");
                                if (shareIntent.resolveActivity(getPackageManager()) != null) {
                                    startActivity(chooser);
                                }
                            }
                        }
                    });
                }
            }
        });
        appeler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                                                if (ContextCompat.checkSelfPermission(JobDetailActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                                    ActivityCompat.requestPermissions(JobDetailActivity.this, new String[]{Manifest.permission.CALL_PHONE}, CALL_PERMISSION_REQUEST_CODE);
                                                } else {
                                                    initiatePhoneCall(phoneNumber);
                                                }
                                            } else {
                                                showToast("no phone number");
                                            }
                                        } else {
                                            showToast("User not found");
                                        }
                                    }
                                });
                            }
                        }
                    });
                }
            }
        });
/*
        shareFacebookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String jobTitle = titleTextView.getText().toString();
                String jobDescription = descriptionTextView.getText().toString();
                String location = locationTextView.getText().toString();
                String enterpriseName = enterpriseNameTextView.getText().toString();

                String shareText = "Job Title: " + jobTitle + "\n" +
                        "Job Description: " + jobDescription + "\n" +
                        "Location: " + location + "\n" +
                        "Enterprise Name: " + enterpriseName;

                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Job Details");
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);

                Intent chooser = Intent.createChooser(shareIntent, "Share via");
                if (shareIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(chooser);
                }
            }
        });
        shareWhatsAppButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(JobDetailActivity.this, "Partagé sur WhatsApp", Toast.LENGTH_SHORT).show();
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.setPackage("com.whatsapp");
                shareIntent.putExtra(Intent.EXTRA_TEXT, jobTitle + "\n" + jobDescription);
                startActivity(Intent.createChooser(shareIntent, "Partager via"));
            }
        });

        shareTwitterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(JobDetailActivity.this, "Partagé sur Twitter", Toast.LENGTH_SHORT).show();
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.setPackage("com.twitter.android");
                shareIntent.putExtra(Intent.EXTRA_TEXT, jobTitle + "\n" + jobDescription);
                startActivity(Intent.createChooser(shareIntent, "Partager via"));
            }
        });

        shareEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(JobDetailActivity.this, "Partagé par Email", Toast.LENGTH_SHORT).show();
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, jobTitle);
                shareIntent.putExtra(Intent.EXTRA_TEXT, jobDescription);
                startActivity(Intent.createChooser(shareIntent, "Partager via"));
            }
        });*/
        emailJobOwnerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (jobOwnerEmail != null) {
                    sendEmailToJobOwner(jobOwnerEmail);
                }
            }
        });
    }

    private void sendEmailToJobOwner(String jobOwnerEmail) {
        String subject = "Problem avec Job offer "+jobTitle;
        String body = "Madame/Monsieur, \n\n J'ai renconté un probleme avec votre offre...";

        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setData(Uri.parse("mailto:")); //only email apps should handle this
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{jobOwnerEmail});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, body);

        if (emailIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(emailIntent);
        } else {
            Toast.makeText(this, "No email client available", Toast.LENGTH_SHORT).show();
        }
    }
    private void initiatePhoneCall(String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        startActivity(intent);
    }

    private void showToast(String message) {
        Toast.makeText(JobDetailActivity.this, message, Toast.LENGTH_SHORT).show();
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
                                                showToast("No phone number");
                                            }
                                        } else {
                                            showToast("No Employeur wih this mail adress");
                                        }
                                    }
                                });
                            }
                        }
                    });
                }
            } else {
                showToast("Permission denied");
            }
        }
    }
    public void searchSimilarJobsButton(View view) {
        Intent similarJobsIntent = new Intent(this, SimilarJobsActivity.class);

        String enterpriseName = enterpriseNameTextView.getText().toString();
        String jobType2 = jobType.getText().toString();
        String location = locationTextView.getText().toString();

        if (sameEmployerCheckBox.isChecked()) {
            similarJobsIntent.putExtra("sameEmployer", enterpriseName);
        }
        if (sameTypeCheckBox.isChecked()) {
            similarJobsIntent.putExtra("sameType", jobType2);
        }
        if (sameLocationCheckBox.isChecked()) {
            similarJobsIntent.putExtra("sameLocation", location);
        }

        startActivity(similarJobsIntent);
    }


    private void openSourceLink(String url) {
        Uri webpage = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Toast.makeText(this, "cant touch this", Toast.LENGTH_SHORT).show();
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
                    if (userRole == null || userRole.equals("chercheur d'emploi")) {
                        candidatureButton.setVisibility(View.VISIBLE);
                    } else {
                        candidatureButton.setVisibility(View.GONE);
                    }
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

    public void onCandidaterButtonClick(View view) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if(currentUser == null){
            Toast.makeText(this, "cant touch this please log in ", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(JobDetailActivity.this, LoginActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(JobDetailActivity.this, CandidatureActivity.class);
            intent.putExtra("jobId", getIntent().getStringExtra("jobId"));
            intent.putExtra("jobTitle", titleTextView.getText().toString());
            //intent.putExtra("isMultiple", isMultiple);
            startActivity(intent);
        }
    }

    public void onTraduireButtonClick(View view) {
        //TODO
    }

    public void onEnregistrerButtonClick(View view) {
        //TODO
    }

    public void onChercherButtonClick(View view) {
        //TODO
    }

    public void onPartagerButtonClick(View view) {
        //TODO
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.logout) {
            mAuth.signOut();
            Intent intent = new Intent(JobDetailActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        } else if (item.getItemId() == R.id.addjobOffer) {
            Intent intent = new Intent(JobDetailActivity.this, AddJobOffer.class);
            startActivity(intent);
            finish();

        } else if (item.getItemId() == R.id.modifiProfil) {
            Intent intent = new Intent(JobDetailActivity.this, AddPropertyActivity.class);
            startActivity(intent);
            finish();
        } else if (item.getItemId() == R.id.checkOffers) {
            Intent intent = new Intent(JobDetailActivity.this, CheckOffersActivity.class);
            startActivity(intent);
            finish();
        }else if (item.getItemId() == R.id.spontane) {
            Intent intent = new Intent(JobDetailActivity.this, SpontaneousCandidatureActivity.class);
            startActivity(intent);
            finish();
        }else if (item.getItemId() == R.id.checkCandiate) {
            Intent intent = new Intent(JobDetailActivity.this, UserApplicationsActivity.class);
            startActivity(intent);
            finish();
        }else if (item.getItemId() == R.id.evaluate) {
            Intent intent = new Intent(JobDetailActivity.this, evaluateCandidature.class);
            startActivity(intent);
            finish();
        }else if (item.getItemId() == R.id.profil) {
            Intent intent = new Intent(JobDetailActivity.this, Profil.class);
            startActivity(intent);
            finish();
        }else if (item.getItemId() == R.id.chat) {
            Intent intent = new Intent(JobDetailActivity.this, ChatActivity.class);
            startActivity(intent);
            finish();
        }else if (item.getItemId() == R.id.onay) {
            Intent intent = new Intent(JobDetailActivity.this, PendingUsersActivity.class);
            startActivity(intent);
            finish();
        }else if (item.getItemId() == R.id.login) {
            Intent intent = new Intent(JobDetailActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }else if (item.getItemId() == R.id.reportedUser) {
            Intent intent = new Intent(JobDetailActivity.this, ReportedUsersActivity.class);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
