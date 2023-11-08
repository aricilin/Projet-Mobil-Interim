package com.aricilingiroglu.interimproject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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

public class CandidatureAdapter extends RecyclerView.Adapter<CandidatureAdapter.CandidatureViewHolder> {

    private final List<Candidature> candidatures;
    private final Context context;
    Candidature candidature;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();


    public CandidatureAdapter(List<Candidature> candidatures, Context context) {
        this.candidatures = candidatures;
        this.context = context;
    }

    @NonNull
    @Override
    public CandidatureViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.candidature_item, parent, false);
        return new CandidatureViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CandidatureViewHolder holder, int position) {
        candidature = candidatures.get(position);
        holder.titleTextView.setText(candidature.getJobTitle());

        String candidatureId = candidature.getCandidatureId();

        switch (candidature.getEtat()) {
            case "accepted":
                holder.itemView.setBackgroundColor(Color.GREEN);
                break;
            case "refused":
                holder.itemView.setBackgroundColor(Color.RED);
                break;
            default:
                holder.itemView.setBackgroundColor(Color.YELLOW);
                break;
        }
        if (candidature.getEtat().equals("pending")) {
            holder.modifyButton.setEnabled(true);
            holder.deleteButton.setEnabled(true);
        } else {
            holder.modifyButton.setEnabled(false);
            holder.deleteButton.setEnabled(true);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CandidatureDetailActivity.class);
                intent.putExtra("candidatureId", candidatureId);
                context.startActivity(intent);
            }
        });

        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.collection("candidatures").document(candidature.getCandidatureId()).delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                int currentPosition = holder.getAdapterPosition();
                                if (currentPosition != RecyclerView.NO_POSITION) {
                                    candidatures.remove(currentPosition);
                                    notifyItemRemoved(currentPosition);
                                }
                                Toast.makeText(context, "Candidature deleted successfully", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(context, "Failed to delete candidature", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        holder.modifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CandidatureModifyActivity.class);
                intent.putExtra("candidatureId", candidatureId);
                context.startActivity(intent);
            }
        });
    }


    @Override
    public int getItemCount() {
        return candidatures.size();
    }

    static class CandidatureViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        Button deleteButton;
        Button modifyButton;

        public CandidatureViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.candidatureTitleTextView);
            deleteButton = itemView.findViewById(R.id.delete_button);
            modifyButton = itemView.findViewById(R.id.modify_button);
        }
    }
}
