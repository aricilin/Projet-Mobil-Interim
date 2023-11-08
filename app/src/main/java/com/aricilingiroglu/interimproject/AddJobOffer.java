package com.aricilingiroglu.interimproject;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultCaller;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.app.DatePickerDialog;


import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AddJobOffer extends AppCompatActivity {

    Uri imageData;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    private EditText locationEditText, estimatedSalaryEditText, enterpriseNameEditText,linkEditText;
    private EditText jobTypeEditText, applicationDeadlineEditText,titleEditText,descriptionEditText;
    private EditText dateDebutEditText,dateFinEditText;




    ActivityResultLauncher<Intent> activityResultLauncher;
    ActivityResultLauncher<String> permissionLauncher;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_job_offer);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();


        titleEditText = findViewById(R.id.titletext);
        descriptionEditText = findViewById(R.id.descriptionText);
        locationEditText = findViewById(R.id.location_edit_text);
        estimatedSalaryEditText = findViewById(R.id.estimated_salary_edit_text);
        enterpriseNameEditText = findViewById(R.id.enterprise_name_edit_text);
        jobTypeEditText = findViewById(R.id.job_type_edit_text);
        dateDebutEditText=findViewById(R.id.date_debut_edit_text);
        applicationDeadlineEditText = findViewById(R.id.application_deadline_edit_text);
        linkEditText=findViewById(R.id.link);
        /*
        dateDebutEditText.setOnClickListener(this::showDateDebutPickerDialog);
        applicationDeadlineEditText.setOnClickListener(this::showDateFinPickerDialog);
*/



        //setContentView(R.layout.activity_add_job_offer);
        registerLauncher();
    }

    public void addOffer(View view) {
        String jobTitle =titleEditText.getText().toString().trim();
        String jobDescription = descriptionEditText.getText().toString().trim();
        String location = locationEditText.getText().toString().trim();
        String estimatedSalary = estimatedSalaryEditText.getText().toString().trim();
        String enterpriseName = enterpriseNameEditText.getText().toString().trim();
        String jobType = jobTypeEditText.getText().toString().trim();
        String sourceLink= linkEditText.getText().toString().trim();
        String dateDebut = dateDebutEditText.getText().toString().trim();
        String dateFin = applicationDeadlineEditText.getText().toString().trim();

        if (jobTitle.isEmpty() || jobDescription.isEmpty()) {
            Toast.makeText(this, "Entrez un titre et une description", Toast.LENGTH_SHORT).show();
            return;
        }

        String userEmail = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getEmail() : null;
        if (userEmail == null) {
            Toast.makeText(this, "User is not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!isValidDateFormat(dateDebut) || !isValidDateFormat(dateFin)) {
            Toast.makeText(this, "Attention au dates", Toast.LENGTH_SHORT).show();
            return;
        }

        Calendar debutCalendar = parseDate(dateDebut);
        Calendar finCalendar = parseDate(dateFin);

        if (debutCalendar == null || finCalendar == null || !isValidDateRange(debutCalendar, finCalendar)) {
            Toast.makeText(this, "Le début doit être avant la fin.", Toast.LENGTH_SHORT).show();
            return;
        }




        long timestamp = System.currentTimeMillis();
        Date date = new Date(timestamp);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.FRANCE);
        String formattedDate = formatter.format(date);

        Map<String, Object> jobOffer = new HashMap<>();
        jobOffer.put("jobTitle", jobTitle);
        jobOffer.put("jobDescription", jobDescription);
        jobOffer.put("imageUri", imageData != null ? imageData.toString() : null);
        jobOffer.put("userEmail", userEmail);
        jobOffer.put("timestamp", timestamp);
        jobOffer.put("location", location);
        jobOffer.put("estimatedSalary", estimatedSalary);
        jobOffer.put("enterpriseName", enterpriseName);
        jobOffer.put("jobType", jobType);
        jobOffer.put("dateCreation", formattedDate);
        jobOffer.put("dateDebut", dateDebut);
        jobOffer.put("dateFin", dateFin);
        jobOffer.put("sourceLink",sourceLink);

        db.collection("jobOffers")
                .add(jobOffer)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Job offer added successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error adding job offer: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.option_menu, menu);
        /*
        if (userRole != null && !userRole.equals("chercheur d'emploi")) {
            menuInflater.inflate(R.menu.option_menu, menu);
        } else {
            menuInflater.inflate(R.menu.option_menu_basic, menu);
        }
        */
        return super.onCreateOptionsMenu(menu);
    }


    private boolean isValidDateFormat(String date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE);
        dateFormat.setLenient(false);
        try {
            dateFormat.parse(date);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    private Calendar parseDate(String date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE);
        try {
            Date parsedDate = dateFormat.parse(date);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(parsedDate);
            return calendar;
        } catch (ParseException e) {
            return null;
        }
    }
    private boolean isValidDateRange(Calendar debut, Calendar fin) {
        return debut.before(fin);
    }
/*
    public void showDateDebutPickerDialog(View v) {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year1, monthOfYear, dayOfMonth) -> {
                    String date = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year1;
                    dateDebutEditText.setText(date);
                }, year, month, day);
        datePickerDialog.show();
    }

    public void showDateFinPickerDialog(View v) {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year1, monthOfYear, dayOfMonth) -> {
                    String date = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year1;
                    applicationDeadlineEditText.setText(date);
                }, year, month, day);
        datePickerDialog.show();
    }*/

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.logout) {
            mAuth.signOut();
            Intent intent = new Intent(AddJobOffer.this, LoginActivity.class);
            startActivity(intent);
            finish();
        } else if (item.getItemId() == R.id.addjobOffer) {
            Intent intent = new Intent(AddJobOffer.this, AddJobOffer.class);
            startActivity(intent);
            finish();

        } else if (item.getItemId() == R.id.modifiProfil) {
            Intent intent = new Intent(AddJobOffer.this, AddPropertyActivity.class);
            startActivity(intent);
            finish();
        } else if (item.getItemId() == R.id.checkOffers) {
            Intent intent = new Intent(AddJobOffer.this, CheckOffersActivity.class);
            startActivity(intent);
            finish();
        }else if (item.getItemId() == R.id.spontane) {
            Intent intent = new Intent(AddJobOffer.this, SpontaneousCandidatureActivity.class);
            startActivity(intent);
            finish();
        }else if (item.getItemId() == R.id.checkCandiate) {
            Intent intent = new Intent(AddJobOffer.this, UserApplicationsActivity.class);
            startActivity(intent);
            finish();
        }else if (item.getItemId() == R.id.evaluate) {
            Intent intent = new Intent(AddJobOffer.this, evaluateCandidature.class);
            startActivity(intent);
            finish();
        }else if (item.getItemId() == R.id.profil) {
            Intent intent = new Intent(AddJobOffer.this, Profil.class);
            startActivity(intent);
            finish();
        }else if (item.getItemId() == R.id.onay) {
            Intent intent = new Intent(AddJobOffer.this, PendingUsersActivity.class);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }





    public void selectLogo(View view){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_EXTERNAL_STORAGE)){
                Snackbar.make(view,"Permission needed for gallery",Snackbar.LENGTH_INDEFINITE).setAction("Give Permissin", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
                    }
                }).show();
            }else{
                //ask
                permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);

            }
        }else{
            Intent intentGallery=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            activityResultLauncher.launch(intentGallery);
        }
    }
    private void registerLauncher(){
        activityResultLauncher=registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if(result.getResultCode()==RESULT_OK){
                    Intent intentFromResult=result.getData();
                    if(intentFromResult != null){
                        imageData=intentFromResult.getData();
                        ImageView imageView = findViewById(R.id.imageView);
                        imageView.setImageURI(imageData);
                    }
                }
            }
        });
        permissionLauncher=registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean result) {
                if(result){
                    Intent intentToGallery=new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    activityResultLauncher.launch(intentToGallery);

                }else{
                    Toast.makeText(AddJobOffer.this,"permission needed!",Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}