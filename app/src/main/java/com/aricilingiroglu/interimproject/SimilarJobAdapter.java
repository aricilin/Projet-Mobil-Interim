package com.aricilingiroglu.interimproject;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SimilarJobAdapter extends RecyclerView.Adapter<SimilarJobAdapter.JobOfferViewHolder> {
    private List<JobOffer> jobOffers;
    private Context context;

    public SimilarJobAdapter(List<JobOffer> jobOffers, Context context) {
        this.jobOffers = jobOffers;
        this.context = context;
    }

    @NonNull
    @Override
    public JobOfferViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.job_offer_item, parent, false);
        return new JobOfferViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull JobOfferViewHolder holder, int position) {
        JobOffer jobOffer = jobOffers.get(position);
        holder.bind(jobOffer);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Start JobDetailActivity and pass the job offer details
                Intent intent = new Intent(context, JobDetailActivity.class);
                intent.putExtra("jobId", jobOffer.getJobId());
                // Add any additional data you want to pass to the JobDetailActivity
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return jobOffers.size();
    }

    class JobOfferViewHolder extends RecyclerView.ViewHolder {
        private TextView titleTextView;

        public JobOfferViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.title_text_view);
        }

        public void bind(JobOffer jobOffer) {
            titleTextView.setText(jobOffer.getJobTitle());
        }
    }
}
