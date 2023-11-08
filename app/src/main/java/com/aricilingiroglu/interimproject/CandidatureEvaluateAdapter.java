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
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class CandidatureEvaluateAdapter extends RecyclerView.Adapter<CandidatureEvaluateAdapter.CandidatureViewHolder> {

    private final List<Candidature> candidatures;
    private final Context context;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();


    public CandidatureEvaluateAdapter(List<Candidature> candidatures, Context context) {
        this.candidatures = candidatures;
        this.context = context;
    }

    @NonNull
    @Override
    public CandidatureViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.evaluate_candidature_item, parent, false);
        return new CandidatureViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CandidatureViewHolder holder, int position) {
        Candidature candidature = candidatures.get(position);
        holder.titleTextView.setText(candidature.getJobTitle());

        String candidatureId = candidature.getCandidatureId(); // Assuming your Candidature class has getId()


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CandidatureDetailActivity.class);
                intent.putExtra("candidatureId", candidatureId);
                context.startActivity(intent);
            }
        });

        holder.refuser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.collection("candidatures").document(candidatureId)
                        .update("etat", "refused")
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(context, "Candidature refused successfully!", Toast.LENGTH_SHORT).show();
                                candidature.setEtat("Refused"); // Update the local object too
                                //notifyItemChanged(position); // Notify the adapter to redraw the item with the new data
                                removeCandidatureAt(position);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(context, "Error updating document", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        holder.accepter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.collection("candidatures").document(candidatureId)
                        .update("etat", "accepted")
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(context, "Candidature accepted successfully!", Toast.LENGTH_SHORT).show();
                                candidature.setEtat("Accepted"); // Update the local object too
                                //notifyItemChanged(position); // Notify the adapter to redraw the item with the new data
                                removeCandidatureAt(position);

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(context, "Error updating document", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }

    private void removeCandidatureAt(int position) {
        candidatures.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public int getItemCount() {
        return candidatures.size();
    }

    static class CandidatureViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        Button refuser;
        Button accepter;

        public CandidatureViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.candidatureTitleTextView);
            refuser = itemView.findViewById(R.id.refuseButton);
            accepter = itemView.findViewById(R.id.acceptButton);
        }
    }
}
