package com.example.chatsapp_thirdtime.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.chatsapp_thirdtime.R;
import com.example.chatsapp_thirdtime.Models.User;
import com.example.chatsapp_thirdtime.Adapters.UserRecyclerviewAdapter;
import com.example.chatsapp_thirdtime.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    Toolbar toolbar;
    ActivityMainBinding binding;
    FirebaseDatabase database;
    ArrayList<User> userArrayList;
    UserRecyclerviewAdapter userRecyclerviewAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initializing variables
        database = FirebaseDatabase.getInstance();
        userArrayList = new ArrayList<>();
        userRecyclerviewAdapter = new UserRecyclerviewAdapter(MainActivity.this, userArrayList);
        binding.recyclerView.setAdapter(userRecyclerviewAdapter);

        // set up toolbar
        toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        database.getReference().child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                userArrayList.clear();
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    User user = dataSnapshot.getValue(User.class);
                    if(!user.getUserId().equals(FirebaseAuth.getInstance().getUid())){
                        userArrayList.add(user);

                    }
                }
                userRecyclerviewAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }




    // attach top menu
    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.top_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // selected option

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId())
        {
            case R.id.search:
                Toast.makeText(this, "Searched Clicked", Toast.LENGTH_SHORT).show();
                break;
            case R.id.settings:
                Toast.makeText(this, "Setting Clicked", Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}