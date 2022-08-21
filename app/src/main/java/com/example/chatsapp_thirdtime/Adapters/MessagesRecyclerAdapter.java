package com.example.chatsapp_thirdtime.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatsapp_thirdtime.Models.Message;

import com.example.chatsapp_thirdtime.R;
import com.example.chatsapp_thirdtime.databinding.TextReceivedBinding;
import com.example.chatsapp_thirdtime.databinding.TextSentBinding;
import com.github.pgreze.reactions.ReactionPopup;
import com.github.pgreze.reactions.ReactionsConfig;
import com.github.pgreze.reactions.ReactionsConfigBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class MessagesRecyclerAdapter extends RecyclerView.Adapter {

    private Context context;
    private ArrayList<Message> messageArrayList;

    String senderRoom, receiverRoom;
    final int sent = 1;
    final int received = 2;

    public MessagesRecyclerAdapter(Context context, ArrayList<Message> messageArrayList, String senderRoom, String receiverRoom) {
        this.context = context;
        this.messageArrayList = messageArrayList;
        this.senderRoom = senderRoom;
        this.receiverRoom = receiverRoom;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == sent){
            View view = LayoutInflater.from(context).inflate(R.layout.text_sent, parent, false);
            return new SentViewHolder(view);
        }
        else{
            View view = LayoutInflater.from(context).inflate(R.layout.text_received, parent, false);
            return new ReceivedViewHolder(view);
        }

    }

    @Override
    public int getItemViewType(int position) {
        Message message = messageArrayList.get(position);
        if(FirebaseAuth.getInstance().getUid().equals(message.getSenderId())){
            return sent;
        }
        else{
            return received;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        Message message = messageArrayList.get(position);

        int reactions[] = new int[]{
                R.drawable.like,
                R.drawable.love,
                R.drawable.laugh,
                R.drawable.wow,
                R.drawable.sad,
                R.drawable.angry
        };
        // Reaction
        ReactionsConfig config = new ReactionsConfigBuilder(context)
                .withReactions(reactions)
                .build();

        ReactionPopup popup = new ReactionPopup(context, config, (p) -> {
            if(holder.getClass().equals(SentViewHolder.class)){
                SentViewHolder viewHolder = (SentViewHolder) holder;
                viewHolder.binding.feeling.setImageResource(reactions[p]);
                viewHolder.binding.feeling.setVisibility(View.VISIBLE);

            }
            else{
                ReceivedViewHolder viewHolder = (ReceivedViewHolder) holder;
                viewHolder.binding.feeling.setImageResource(reactions[p]);
                viewHolder.binding.feeling.setVisibility(View.VISIBLE);

            }

            message.setFeeling(p);

            //setting feeling for sender
            FirebaseDatabase.getInstance().getReference()
                    .child("chats")
                    .child(senderRoom)
                    .child("messages")
                    .child(message.getMessageId()).setValue(message);

            //setting feeling for receiver
            FirebaseDatabase.getInstance().getReference()
                    .child("chats")
                    .child(receiverRoom)
                    .child("messages")
                    .child(message.getMessageId()).setValue(message);

            return true; // true is closing popup, false is requesting a new selection
        });

        if(holder.getClass().equals(SentViewHolder.class)){
            SentViewHolder viewHolder = (SentViewHolder) holder;
            viewHolder.binding.message.setText(message.getMessage());

            if(message.getFeeling() >= 0 ){
                viewHolder.binding.feeling.setImageResource(reactions[message.getFeeling()]);
                viewHolder.binding.feeling.setVisibility(View.VISIBLE);
            }
            else {
                viewHolder.binding.feeling.setVisibility(View.GONE);
            }

            viewHolder.binding.message.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    popup.onTouch(view, motionEvent);
                    return false;
                }
            });

        }
        else{
            ReceivedViewHolder viewHolder = (ReceivedViewHolder) holder;
            viewHolder.binding.message.setText(message.getMessage());

            if(message.getFeeling() >= 0 ){
                viewHolder.binding.feeling.setImageResource(reactions[message.getFeeling()]);
                viewHolder.binding.feeling.setVisibility(View.VISIBLE);
            }
            else {
                viewHolder.binding.feeling.setVisibility(View.GONE);
            }

            viewHolder.binding.message.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    popup.onTouch(view, motionEvent);
                    return false;
                }
            });

        }
    }

    @Override
    public int getItemCount() {
        return messageArrayList.size();
    }

    public class SentViewHolder extends RecyclerView.ViewHolder{
        TextSentBinding binding;
        public SentViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = TextSentBinding.bind(itemView);
        }
    }

    public class ReceivedViewHolder extends RecyclerView.ViewHolder{
        TextReceivedBinding binding;
        public ReceivedViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = TextReceivedBinding.bind(itemView);
        }
    }
}
