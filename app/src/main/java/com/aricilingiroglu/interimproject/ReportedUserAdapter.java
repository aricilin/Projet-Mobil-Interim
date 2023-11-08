package com.aricilingiroglu.interimproject;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ReportedUserAdapter extends RecyclerView.Adapter<ReportedUserAdapter.ReportedUserViewHolder> {

    private Context context;
    private ArrayList<User> userList;

    public ReportedUserAdapter(Context context, ArrayList<User> userList) {
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public ReportedUserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.reported_user_item, parent, false);
        return new ReportedUserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReportedUserViewHolder holder, int position) {
        User user = userList.get(position);

        // Bind user data to the ViewHolder
        holder.textViewName.setText(user.getName());


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to UserDetailActivity
                Intent intent = new Intent(context, UserDetail2Activity.class);
                intent.putExtra("email", user.getEmail()); // Pass the user ID or any necessary data to the UserDetailActivity
                context.startActivity(intent);
            }
        });
        // Handle delete button click
        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Delete the user from Firestore
                deleteUser(user);
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class ReportedUserViewHolder extends RecyclerView.ViewHolder {
        TextView textViewName;
        Button btnDelete;

        public ReportedUserViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.userNameTextView);
            btnDelete = itemView.findViewById(R.id.deleteButton);
        }
    }


    private void deleteUser(User user) {
        // Delete user from Firestore based on the user's email (unique identifier)
        String userEmail = user.getEmail(); // Replace 'getEmail()' with the correct method to retrieve the user's email from the User class

        FirebaseFirestore.getInstance().collection("users")
                .whereEqualTo("email", userEmail) // Replace 'email' with the correct field name in your Firestore collection
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot querySnapshot) {
                        if (!querySnapshot.isEmpty()) {
                            // Get the document reference
                            DocumentSnapshot documentSnapshot = querySnapshot.getDocuments().get(0);
                            String documentId = documentSnapshot.getId();

                            // Delete the user document
                            FirebaseFirestore.getInstance().collection("users")
                                    .document(documentId)
                                    .delete()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            // User deleted successfully
                                            userList.remove(user);
                                            notifyDataSetChanged();
                                            Toast.makeText(context, "User deleted", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // Failed to delete user
                                            Toast.makeText(context, "Failed to delete user", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } else {
                            // User document not found
                            Toast.makeText(context, "User document not found", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Failed to fetch user document
                        Toast.makeText(context, "Failed to fetch user document", Toast.LENGTH_SHORT).show();
                    }
                });
    }






}
