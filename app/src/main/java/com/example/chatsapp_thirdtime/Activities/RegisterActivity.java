package com.example.chatsapp_thirdtime.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.chatsapp_thirdtime.databinding.ActivityRegisterBinding;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {

    ActivityRegisterBinding binding;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // setup binding
        binding =   ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.phoneNumberEditText.requestFocus();

        auth = FirebaseAuth.getInstance();
        if(auth.getCurrentUser() != null){

            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }


        binding.continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String phoneNumber = binding.phoneNumberEditText.getText().toString().trim();
                if(phoneNumber.equals(""))
                {
                    Toast.makeText(RegisterActivity.this, "Type a phone Number", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Intent intent = new Intent(RegisterActivity.this, OTPActivity.class);
                    intent.putExtra("phoneNumber", phoneNumber);
                    startActivity(intent);
                }


            }
        });



    }
}