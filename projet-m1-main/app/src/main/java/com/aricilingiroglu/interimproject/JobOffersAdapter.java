package com.aricilingiroglu.interimproject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class JobOffersAdapter extends RecyclerView.Adapter<JobOffersAdapter.JobOfferViewHolder> {

    private final List<JobOffer> jobOffers;
    private final List<String> jobIds;
    private final Context context;
    private  FirebaseFirestore db;
    private String userRole;
    FirebaseAuth mauth;


    public JobOffersAdapter(List<JobOffer> jobOffers, List<String> jobIds, Context context, String userRole) {
        this.jobOffers = jobOffers;
        this.jobIds = jobIds;
        this.context = context;
        this.db = FirebaseFirestore.getInstance();
        this.userRole = userRole;
    }


    public void updateJobOffers(List<JobOffer> jobOffers, List<String> jobIds) {
        this.jobOffers.clear();
        this.jobOffers.addAll(jobOffers);
        this.jobIds.clear();
        this.jobIds.addAll(jobIds);
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public JobOfferViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.job_offer_item, parent, false);
        return new JobOfferViewHolder(itemView);
    }



    @Override

    public void onBindViewHolder(@NonNull JobOfferViewHolder holder, int position) {
        JobOffer jobOffer = jobOffers.get(position);
        holder.titleTextView.setText(jobOffer.getJobTitle());

        if (userRole != null && !userRole.equals("chercheur d'emploi")) {
            holder.modifyButton.setVisibility(View.VISIBLE);
            holder.deleteButton.setVisibility(View.VISIBLE);
            holder.bindListeners();
        } else {
            holder.modifyButton.setVisibility(View.GONE);
            holder.deleteButton.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String jobId = jobIds.get(position);
                Intent intent = new Intent(context, JobDetailActivity.class);
                intent.putExtra("jobId", jobId);
                context.startActivity(intent);
            }
        });
    }






    @Override
    public int getItemCount() {
        return jobOffers.size();
    }

    class JobOfferViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        Button deleteButton;
        Button modifyButton;

        public JobOfferViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.jobTitleTextView);
            deleteButton = itemView.findViewById(R.id.delete_button);
            modifyButton = itemView.findViewById(R.id.modify_button);
        }

        public void bindListeners() {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String jobId = jobIds.get(getAdapterPosition());
                    Intent intent = new Intent(context, JobDetailActivity.class);
                    intent.putExtra("jobId", jobId);
                    context.startActivity(intent);
                }
            });

            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String jobId = jobIds.get(getAdapterPosition());
                    db.collection("jobOffers").document(jobId).delete()
                            .addOnSuccessListener(aVoid -> {
                                jobOffers.remove(getAdapterPosition());
                                jobIds.remove(getAdapterPosition());
                                notifyItemRemoved(getAdapterPosition());
                            });
                }
            });

            modifyButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String jobId = jobIds.get(getAdapterPosition());
                    Intent intent = new Intent(context, JobModifyActivity.class);
                    intent.putExtra("jobId", jobId);
                    context.startActivity(intent);
                }
            });
        }
    }

}
