package com.aricilingiroglu.interimproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.EventListener;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private RecyclerView recyclerView;
    private ChatRoomAdapter chatRoomAdapter;
    String uid;
    private FirebaseAuth mAuth;
    private String userRole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        mAuth = FirebaseAuth.getInstance();
        getUserRole();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            uid = user.getUid();
        }

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Button btnCreateChatRoom = findViewById(R.id.btnCreateChatRoom);
        btnCreateChatRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChatActivity.this, AddUserChat.class);
                intent.putExtra("uid", uid);
                startActivity(intent);
            }
        });

        chatRoomAdapter = new ChatRoomAdapter(new ArrayList<>(), new ChatRoomAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                Intent intent = new Intent(ChatActivity.this, ChatRoomActivity.class);
                intent.putExtra("chatRoomID", documentSnapshot.getId());
                startActivity(intent);
            }
        });
        recyclerView.setAdapter(chatRoomAdapter);

        fetchChatRooms();
    }


    public void fetchChatRooms() {
        db.collection("chatRooms")
                .whereArrayContains("userIDs", uid)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w("MainActivity", "Listen failed.", e);
                            return;
                        }

                        List<DocumentSnapshot> chatRooms = queryDocumentSnapshots.getDocuments();

                        // Filter the chat rooms to include only those where current user is a member
                        List<DocumentSnapshot> filteredChatRooms = new ArrayList<>();
                        for (DocumentSnapshot chatRoom : chatRooms) {
                            List<String> users = (List<String>) chatRoom.get("userIDs");
                            if (users != null && users.contains(uid)) {
                                filteredChatRooms.add(chatRoom);
                            }
                        }

                        chatRoomAdapter.setChatRooms(filteredChatRooms);
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
        } else if (userRole.equals("gestionnaire")) {
            menuInflater.inflate(R.menu.gestionnaire_menu, menu);
        }else if (userRole.equals("chercheur d'emploi")) {
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
            Intent intent = new Intent(ChatActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        } else if (item.getItemId() == R.id.addjobOffer) {
            Intent intent = new Intent(ChatActivity.this, AddJobOffer.class);
            startActivity(intent);
            finish();

        } else if (item.getItemId() == R.id.modifiProfil) {
            Intent intent = new Intent(ChatActivity.this, AddPropertyActivity.class);
            startActivity(intent);
            finish();
        } else if (item.getItemId() == R.id.checkOffers) {
            Intent intent = new Intent(ChatActivity.this, CheckOffersActivity.class);
            startActivity(intent);
            finish();
        }else if (item.getItemId() == R.id.spontane) {
            Intent intent = new Intent(ChatActivity.this, SpontaneousCandidatureActivity.class);
            startActivity(intent);
            finish();
        }else if (item.getItemId() == R.id.checkCandiate) {
            Intent intent = new Intent(ChatActivity.this, UserApplicationsActivity.class);
            startActivity(intent);
            finish();
        }else if (item.getItemId() == R.id.evaluate) {
            Intent intent = new Intent(ChatActivity.this, evaluateCandidature.class);
            startActivity(intent);
            finish();
        }else if (item.getItemId() == R.id.profil) {
            Intent intent = new Intent(ChatActivity.this, Profil.class);
            startActivity(intent);
            finish();
        }else if (item.getItemId() == R.id.chat) {
            Intent intent = new Intent(ChatActivity.this, ChatActivity.class);
            startActivity(intent);
            finish();
        }else if (item.getItemId() == R.id.onay) {
            Intent intent = new Intent(ChatActivity.this, PendingUsersActivity.class);
            startActivity(intent);
            finish();
        }else if (item.getItemId() == R.id.login) {
            Intent intent = new Intent(ChatActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }else if (item.getItemId() == R.id.reportedUser) {
            Intent intent = new Intent(ChatActivity.this, ReportedUsersActivity.class);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }


}


