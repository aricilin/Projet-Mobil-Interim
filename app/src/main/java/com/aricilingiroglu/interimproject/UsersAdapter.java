package com.aricilingiroglu.interimproject;

import static android.service.controls.ControlsProviderService.TAG;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.List;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UserViewHolder> {

    private List<User> userList;
    private Context context;
    private FirebaseFirestore db;

    public UsersAdapter(List<User> userList, Context context) {
        this.userList = userList;
        this.context = context;
        db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);
        holder.bind(user);
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    class UserViewHolder extends RecyclerView.ViewHolder {

        private TextView usernameTextView;
        private Button acceptButton, refuseButton;
        private View itemClickableArea;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameTextView = itemView.findViewById(R.id.usernameTextView);
            acceptButton = itemView.findViewById(R.id.acceptButton);
            refuseButton = itemView.findViewById(R.id.refuseButton);
            itemClickableArea = itemView.findViewById(R.id.itemClickableArea);

            itemClickableArea.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    User clickedUser = userList.get(position);
                    String userEmail = clickedUser.getEmail();

                    db.collection("users")
                            .whereEqualTo("email", userEmail)
                            .get()
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    QuerySnapshot querySnapshot = task.getResult();
                                    if (querySnapshot != null && !querySnapshot.isEmpty()) {
                                        DocumentSnapshot document = querySnapshot.getDocuments().get(0);
                                        String userId = document.getId();

                                        Intent intent = new Intent(context, UserDetailActivity.class);
                                        intent.putExtra("userId", userId);
                                        context.startActivity(intent);
                                    }
                                } else {
                                    Log.d(TAG, "Error getting user document: ", task.getException());
                                }
                            });
                }
            });


            acceptButton.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    User clickedUser = userList.get(position);
                    String userEmail = clickedUser.getEmail();

                    db.collection("users")
                            .whereEqualTo("email", userEmail)
                            .get()
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    QuerySnapshot querySnapshot = task.getResult();
                                    if (querySnapshot != null && !querySnapshot.isEmpty()) {
                                        DocumentSnapshot document = querySnapshot.getDocuments().get(0);
                                        document.getReference().update("etat", "accepted");

                                        userList.remove(position);
                                        notifyDataSetChanged();
                                    }
                                } else {
                                    Log.d(TAG, "Error updating user status to accepted: ", task.getException());
                                }
                            });
                }
            });

            refuseButton.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    User clickedUser = userList.get(position);
                    String userEmail = clickedUser.getEmail();

                    db.collection("users")
                            .whereEqualTo("email", userEmail)
                            .get()
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    QuerySnapshot querySnapshot = task.getResult();
                                    if (querySnapshot != null && !querySnapshot.isEmpty()) {
                                        DocumentSnapshot document = querySnapshot.getDocuments().get(0);
                                        document.getReference().update("etat", "refused");

                                        // Remove user
                                        userList.remove(position);
                                        notifyDataSetChanged();
                                    }
                                } else {
                                    Log.d(TAG, "Error updating user status to refused: ", task.getException());
                                }
                            });
                }
            });

        }

        public void bind(User user) {
            usernameTextView.setText(user.getName());
        }
    }
}
