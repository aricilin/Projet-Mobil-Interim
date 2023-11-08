package com.aricilingiroglu.interimproject;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ChatRoomAdapter extends RecyclerView.Adapter<ChatRoomAdapter.ChatRoomViewHolder> {
    private List<DocumentSnapshot> chatRooms;
    private OnItemClickListener listener;

    public ChatRoomAdapter(List<DocumentSnapshot> chatRooms, OnItemClickListener listener) {
        this.chatRooms = (chatRooms != null) ? chatRooms : new ArrayList<>();
        this.listener = listener;
    }

    @NonNull
    @Override
    public ChatRoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_room, parent, false);
        return new ChatRoomViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatRoomViewHolder holder, int position) {
        DocumentSnapshot chatRoomSnapshot = chatRooms.get(position);
        String chatRoomName = chatRoomSnapshot.getString("name");
        holder.tvChatRoomName.setText(chatRoomName);
    }

    @Override
    public int getItemCount() {
        return chatRooms.size();
    }

    public void setChatRooms(List<DocumentSnapshot> chatRooms) {
        this.chatRooms = (chatRooms != null) ? chatRooms : new ArrayList<>();
        notifyDataSetChanged();
    }

    public interface OnItemClickListener {
        void onItemClick(DocumentSnapshot chatRoomSnapshot, int position);
    }

    public class ChatRoomViewHolder extends RecyclerView.ViewHolder {
        public TextView tvChatRoomName;

        public ChatRoomViewHolder(View itemView) {
            super(itemView);
            tvChatRoomName = itemView.findViewById(R.id.tvChatRoomName);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        DocumentSnapshot chatRoomSnapshot = chatRooms.get(position);
                        listener.onItemClick(chatRoomSnapshot, position);
                    }
                }
            });
        }
    }
}

