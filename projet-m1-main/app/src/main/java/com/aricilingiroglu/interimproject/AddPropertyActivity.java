package com.aricilingiroglu.interimproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


public class AddPropertyActivity extends AppCompatActivity {

    private EditText nameEditText, surnameEditText, birthdateEditText, addressEditText;
    private EditText nationalityEditText, phoneNumberEditText, cityEditText,  commentsEditText;
    private Button saveButton, uploadCvButton;
    private DatabaseReference databaseReference;
    private RadioGroup profileRadioGroup;
    FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String userRole;
    private static final int PICK_PDF_FILE = 1;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_property);
        mAuth = FirebaseAuth.getInstance();
        getUserRole();


        nameEditText = findViewById(R.id.name_edit_text);
        surnameEditText = findViewById(R.id.surname_edit_text);
        birthdateEditText = findViewById(R.id.birthdate_edit_text);
        addressEditText = findViewById(R.id.location_edit_text);
        saveButton = findViewById(R.id.save_button);
        uploadCvButton = findViewById(R.id.upload_cv_button);
        profileRadioGroup = findViewById(R.id.profile_radio_group);
        nationalityEditText = findViewById(R.id.nationality_edit_text);
        phoneNumberEditText = findViewById(R.id.phone_number_edit_text);
        commentsEditText = findViewById(R.id.comments_edit_text);


        databaseReference = FirebaseDatabase.getInstance().getReference();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            String userId = user.getUid();

            db.collection("users").document(userId).get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.exists()) {
                                String name = documentSnapshot.getString("name");
                                String surname = documentSnapshot.getString("surname");
                                String role = documentSnapshot.getString("role");
                                String nation=documentSnapshot.getString("nationality");
                                String numero=documentSnapshot.getString("phoneNumber");
                                String adress=documentSnapshot.getString("address");
                                String dateNaissance=documentSnapshot.getString("birthdate");
                                String commentaire=documentSnapshot.getString("comments");






                                if (name != null) {
                                    nameEditText.setText(name);
                                }
                                if (surname != null) {
                                    surnameEditText.setText(surname);
                                }
                                if (nation != null) {
                                    nationalityEditText.setText(nation);
                                }
                                if (numero != null) {
                                    phoneNumberEditText.setText(numero);
                                }
                                if (adress != null) {
                                    addressEditText.setText(adress);
                                }
                                if (dateNaissance != null) {
                                    birthdateEditText.setText(dateNaissance);
                                }if (commentaire != null) {
                                    commentsEditText.setText(commentaire);
                                }

                                if (role != null) {
                                    profileRadioGroup.setVisibility(View.GONE);
                                    //setRadioButtonByRole(role);
                                }
                            }
                        }
                    });
        }
        uploadCvButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
                chooseFile.setType("application/pdf");
                chooseFile = Intent.createChooser(chooseFile, "Choose a file");
                startActivityForResult(chooseFile, PICK_PDF_FILE);
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {


                String name = nameEditText.getText().toString().trim();
                String surname = surnameEditText.getText().toString().trim();
                String birthdate = birthdateEditText.getText().toString().trim();
                String address = addressEditText.getText().toString().trim();
                String role = getSelectedRole();
                String nationality = nationalityEditText.getText().toString().trim();
                String phoneNumber = phoneNumberEditText.getText().toString().trim();
                String comments = commentsEditText.getText().toString().trim();


                Map<String, Object> userData = new HashMap<>();

                // Check if not empty
                if (!name.isEmpty()) {
                    userData.put("name", name);
                }
                if (!surname.isEmpty()) {
                    userData.put("surname", surname);
                }
                if (!birthdate.isEmpty()) {
                    userData.put("birthdate", birthdate);
                }
                if (!address.isEmpty()) {
                    userData.put("address", address);
                }
                if (role != null) {
                    userData.put("role",role);

                }
                if (role != null) {
                    userData.put("role",role);
                    if (role.equals("Employeur")) {
                        userData.put("etat", "pending");
                        userData.put("paid","pending");
                    }
                }

                if (!nationality.isEmpty()) {
                    userData.put("nationality", nationality);
                }
                if (!phoneNumber.isEmpty()) {
                    userData.put("phoneNumber", phoneNumber);
                }
                if (!comments.isEmpty()) {
                    userData.put("comments", comments);
                }


                if (userData.isEmpty()) {
                    Toast.makeText(AddPropertyActivity.this, "No data entered", Toast.LENGTH_LONG).show();
                    return;
                }

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    String userId = user.getUid();
                    String email = user.getEmail();
                    userData.put("email", email);

                    db.collection("users").document(userId).set(userData, SetOptions.merge())

                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(AddPropertyActivity.this, "User profile saved successfully", Toast.LENGTH_SHORT).show();

                                    // get back   to Profile
                                    Intent intent;
                                    if (role != null && role.equals("Employeur")) {
                                        intent = new Intent(AddPropertyActivity.this, ImageGalleryActivity.class);
                                    }
                                    else{
                                        intent = new Intent(AddPropertyActivity.this, Profil.class);
                                    }
                                    startActivity(intent);
                                    finish();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(AddPropertyActivity.this, "Failed to save user profile", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        });

    }
    private void setRadioButtonByRole(String role) {
        switch(role){
            case "role1":
                RadioButton rb1 = findViewById(R.id.chercheur);
                rb1.setChecked(true);
                break;
            case "role2":
                RadioButton rb2 = findViewById(R.id.Employeur);
                rb2.setChecked(true);
                break;
            case "role3":
                RadioButton rb3 = findViewById(R.id.Gestionnaire);
                rb3.setChecked(true);
                break;
            default:
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_PDF_FILE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri fileUri = data.getData();

            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageReference = storage.getReference();
            final StorageReference cvReference = storageReference.child("CVs/" + fileUri.getLastPathSegment());

            cvReference.putFile(fileUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            cvReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String fileLink = uri.toString();
                                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                    if (user != null) {
                                        String userId = user.getUid();
                                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                                        db.collection("users").document(userId).update("cvUrl", fileLink);
                                    }
                                    Toast.makeText(AddPropertyActivity.this, "CV uploaded successfully", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            Toast.makeText(AddPropertyActivity.this, "Failed to upload CV", Toast.LENGTH_SHORT).show();
                        }
                    });
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
            menuInflater.inflate(R.menu.anonyme_menu, menu);
        } else if (userRole.equals("chercheur d'emploi")) {
            menuInflater.inflate(R.menu.option_menu_basic, menu);
        } else if (userRole.equals("gestionnaire")) {
            menuInflater.inflate(R.menu.gestionnaire_menu, menu);
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
            Intent intent = new Intent(AddPropertyActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        } else if (item.getItemId() == R.id.addjobOffer) {
            Intent intent = new Intent(AddPropertyActivity.this, AddJobOffer.class);
            startActivity(intent);
            finish();

        } else if (item.getItemId() == R.id.modifiProfil) {
            Intent intent = new Intent(AddPropertyActivity.this, AddPropertyActivity.class);
            startActivity(intent);
            finish();
        } else if (item.getItemId() == R.id.checkOffers) {
            Intent intent = new Intent(AddPropertyActivity.this, CheckOffersActivity.class);
            startActivity(intent);
            finish();
        }else if (item.getItemId() == R.id.spontane) {
            Intent intent = new Intent(AddPropertyActivity.this, SpontaneousCandidatureActivity.class);
            startActivity(intent);
            finish();
        }else if (item.getItemId() == R.id.checkCandiate) {
            Intent intent = new Intent(AddPropertyActivity.this, UserApplicationsActivity.class);
            startActivity(intent);
            finish();
        }else if (item.getItemId() == R.id.evaluate) {
            Intent intent = new Intent(AddPropertyActivity.this, evaluateCandidature.class);
            startActivity(intent);
            finish();
        }else if (item.getItemId() == R.id.profil) {
            Intent intent = new Intent(AddPropertyActivity.this, Profil.class);
            startActivity(intent);
            finish();
        }else if (item.getItemId() == R.id.chat) {
            Intent intent = new Intent(AddPropertyActivity.this, ChatActivity.class);
            startActivity(intent);
            finish();
        }else if (item.getItemId() == R.id.onay) {
            Intent intent = new Intent(AddPropertyActivity.this, PendingUsersActivity.class);
            startActivity(intent);
            finish();
        }else if (item.getItemId() == R.id.login) {
            Intent intent = new Intent(AddPropertyActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }else if (item.getItemId() == R.id.reportedUser) {
            Intent intent = new Intent(AddPropertyActivity.this, ReportedUsersActivity.class);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
    private String getSelectedRole() {
        int selectedId = profileRadioGroup.getCheckedRadioButtonId();
        if (selectedId == -1) {
            // No buton
            return null;
        }
        RadioButton selectedRadioButton = findViewById(selectedId);
        return selectedRadioButton.getText().toString();
    }


}
