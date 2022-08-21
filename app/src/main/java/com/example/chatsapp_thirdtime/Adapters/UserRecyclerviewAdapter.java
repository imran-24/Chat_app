package com.example.chatsapp_thirdtime.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatsapp_thirdtime.Activities.ChatActivity;
import com.example.chatsapp_thirdtime.R;
import com.example.chatsapp_thirdtime.Models.User;
import com.example.chatsapp_thirdtime.databinding.RowLayoutBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class UserRecyclerviewAdapter extends RecyclerView.Adapter<UserRecyclerviewAdapter.ViewHolder> {



    Context context;
    ArrayList<User> userArrayList;
    public UserRecyclerviewAdapter(Context context, ArrayList<User> userArrayList) {
        this.context = context;
        this.userArrayList = userArrayList;
    }

    @NonNull
    @Override
    public UserRecyclerviewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_layout, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = userArrayList.get(position);

        String senderId = FirebaseAuth.getInstance().getUid();

        String senderRoom = senderId + user.getUserId();

        FirebaseDatabase.getInstance().getReference()
                .child("chats")
                .child(senderRoom)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()) {
                            String lastMsg = snapshot.child("lastMsg").getValue(String.class);
//                            long time = snapshot.child("lastMsgTime").getValue(Long.class);
//                            SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a");
//                            holder.binding.lastMessageTime.setText(dateFormat.format(new Date(time)));
//                            holder.binding.lastMessage.setText(lastMsg);
                            
                        } else {
                            holder.binding.lastMessage.setText("Tap to chat");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        holder.binding.userName.setText(user.getName());

         Picasso.get().load(user.getProfileImage())
                 .placeholder(R.drawable.user)
                 .into(holder.binding.profilePic);


         holder.binding.rootLayout.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 Intent intent = new Intent(context, ChatActivity.class);
                 intent.putExtra("userName", user.getName());
                 intent.putExtra("profilePic", user.getProfileImage());
                 intent.putExtra("userId", user.getUserId());
                 context.startActivity(intent);

             }
         });

    }

    @Override
    public int getItemCount() {
        return userArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        RowLayoutBinding binding;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = RowLayoutBinding.bind(itemView);
        }
    }
}
