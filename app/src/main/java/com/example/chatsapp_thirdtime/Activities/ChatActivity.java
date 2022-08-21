package com.example.chatsapp_thirdtime.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.view.View;

import com.example.chatsapp_thirdtime.Adapters.MessagesRecyclerAdapter;
import com.example.chatsapp_thirdtime.Models.Message;
import com.example.chatsapp_thirdtime.R;
import com.example.chatsapp_thirdtime.databinding.ActivityChatBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class ChatActivity extends AppCompatActivity {

    ActivityChatBinding binding;
    MessagesRecyclerAdapter adapter;
    ArrayList<Message> messageArrayList;
    FirebaseDatabase database;

    String senderRoom, receiverRoom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        database = FirebaseDatabase.getInstance();

        messageArrayList = new ArrayList<>();

        // get values from intent
        String name = getIntent().getStringExtra("userName");
        String profilePic = getIntent().getStringExtra("profilePic");

        //set values
        binding.userName.setText(name);
        Picasso.get()
                .load(profilePic)
                .placeholder(R.drawable.user)
                .into(binding.profilePic);

        // currentUserId
        String senderUserId = FirebaseAuth.getInstance().getUid();
        String receiverUserId = getIntent().getStringExtra("userId");

        senderRoom = senderUserId + receiverUserId;
        receiverRoom = receiverUserId + senderUserId;


        adapter = new MessagesRecyclerAdapter(this, messageArrayList, senderRoom, receiverRoom);
        binding.chattingRecyclerview.setLayoutManager(new LinearLayoutManager(this));
        binding.chattingRecyclerview.setAdapter(adapter);


        database.getReference().child("chats")
                .child(senderRoom)
                .child("messages")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        messageArrayList.clear();
                        for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                            Message message = dataSnapshot.getValue(Message.class);
                            message.setMessageId(dataSnapshot.getKey());
                            messageArrayList.add(message);
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });





        binding.send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String messageText = binding.messageEditText.getText().toString();

                Date date = new Date();
                Message message = new Message(messageText, senderUserId, date.getTime());

                binding.messageEditText.setText("");

                String randomKey = database.getReference().push().getKey();


                HashMap<String, Object> lastMsgObj = new HashMap<>();
                lastMsgObj.put("lastMsg", message.getMessage());
                lastMsgObj.put("lastMsgTime", date.getTime());

                database.getReference().child("chats").child(senderRoom).updateChildren(lastMsgObj);
                database.getReference().child("chats").child(receiverRoom).updateChildren(lastMsgObj);

                database.getReference().child("chats")
                        .child(senderRoom)
                        .child("messages")
                        .child(randomKey)
                        .setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                database.getReference().child("chats")
                                        .child(receiverRoom)
                                        .child("messages")
                                        .child(randomKey)
                                        .setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {

                                            }
                                        });
                            }
                        });
            }
        });



        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }
}