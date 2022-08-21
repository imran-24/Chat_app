package com.example.chatsapp_thirdtime.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.chatsapp_thirdtime.databinding.ActivityOtpBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class OTPActivity extends AppCompatActivity {

    ActivityOtpBinding binding;
    FirebaseAuth auth;

    String verificationId = "";
    String otp = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOtpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.one.requestFocus();


        auth = FirebaseAuth.getInstance();

        // get intent value
        String phoneNumber = getIntent().getStringExtra("phoneNumber");

        // set value
        binding.numberLabel.setText("Verify " + phoneNumber);

       PhoneAuthOptions options = PhoneAuthOptions.newBuilder(auth)
               .setPhoneNumber(phoneNumber)
               .setTimeout(60L, TimeUnit.SECONDS)
               .setActivity(OTPActivity.this)
               .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                   @Override
                   public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

                   }

                   @Override
                   public void onVerificationFailed(@NonNull FirebaseException e) {

                   }

                   @Override
                   public void onCodeSent(@NonNull String verifyId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                       super.onCodeSent(verifyId, forceResendingToken);
                       verificationId = verifyId;
                   }
               }).build();

       PhoneAuthProvider.verifyPhoneNumber(options);




        binding.continueBtn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {

               // collect OTP from input
               otp =   binding.one.getText().toString() +
                       binding.two.getText().toString() +
                       binding.three.getText().toString() +
                       binding.four.getText().toString() +
                       binding.five.getText().toString() +
                       binding.six.getText().toString();



               PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, otp);
               auth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                   @Override
                   public void onComplete(@NonNull Task<AuthResult> task) {
                       if(task.isSuccessful()){
                           Toast.makeText(OTPActivity.this, "Logged In", Toast.LENGTH_SHORT).show();
                           Intent intent = new Intent(OTPActivity.this, SetupProfileActivity.class);
                           startActivity(intent);
                           finishAffinity();
                       }
                       else{
                           Toast.makeText(OTPActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                       }
                   }
               });
           }
       });


    }
}