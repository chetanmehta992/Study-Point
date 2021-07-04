package app.aacr.studypoint.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.chaos.view.PinView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import app.aacr.studypoint.R;

public class VerifyPhoneActivity extends AppCompatActivity {
    private String mVerificationId;
    private PinView editTextCode;
    private FirebaseAuth mAuth;
    private TextView time;
    private Button resend_otp;
    private String mobile;
    private TextView mobile_number;
    private String message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_phone);

        mAuth = FirebaseAuth.getInstance();
        mobile = getIntent().getStringExtra("mobile");

        editTextCode = findViewById(R.id.code);
        time = findViewById(R.id.time);
        resend_otp = findViewById(R.id.resend_code);
        mobile_number = findViewById(R.id.mobile_number);

        mobile_number.setText(mobile);

        sendVerificationCode();

        findViewById(R.id.verify_code).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = editTextCode.getText().toString().trim();
                if (code.isEmpty() || code.length() < 6) {
                    Snackbar.make(findViewById(R.id.parent), "Enter valid code", Snackbar.LENGTH_LONG).show();
                    return;
                }
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);
                signInWithPhoneAuthCredential(credential);
            }
        });
        resend_otp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendVerificationCode();
            }
        });
        findViewById(R.id.wrong_number).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(VerifyPhoneActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void sendVerificationCode() {
        resend_otp.setBackground(getDrawable(R.drawable.rounded_disable_button));
        resend_otp.setEnabled(false);
        time.setVisibility(View.VISIBLE);
        new CountDownTimer(60000, 1000) {
            public void onTick(long millisUntilFinished) {
                time.setText("Resend OTP after "+(millisUntilFinished / 1000)+" sec.");
            }
            public void onFinish() {
                time.setVisibility(View.INVISIBLE);
                resend_otp.setEnabled(true);
                resend_otp.setBackground(getDrawable(R.drawable.button_bg));
            }
        }.start();
        PhoneAuthProvider.getInstance().verifyPhoneNumber("+91" + mobile, 60, TimeUnit.SECONDS, this,
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                        String code = phoneAuthCredential.getSmsCode();
                        if (code != null) {
                            editTextCode.setText(code);
                            signInWithPhoneAuthCredential(phoneAuthCredential);
                        }
                    }

                    @Override
                    public void onVerificationFailed(FirebaseException e) {
                        Snackbar.make(findViewById(R.id.parent), e.getMessage(), Snackbar.LENGTH_LONG).show();
                    }

                    @Override
                    public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        mVerificationId = s;
                    }
                });
    }


    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(VerifyPhoneActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Snackbar.make(findViewById(R.id.parent), "Welcome", Snackbar.LENGTH_LONG).show();
                            VerifyPhoneActivity.this.getApplicationContext().getSharedPreferences("User_mobile", Context.MODE_PRIVATE).edit().putString("mobile",mobile).apply();
                            FirebaseDatabase.getInstance().getReference("Users").child(mobile).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if(snapshot.exists())
                                    {
                                        HashMap info = (HashMap) snapshot.getValue();
                                        getApplicationContext().getSharedPreferences("User_name",Context.MODE_PRIVATE).edit().putString("name",info.get("name").toString()).apply();
                                        if(info.get("profile")!=null) getApplicationContext().getSharedPreferences("User_profile",Context.MODE_PRIVATE).edit().putString("profile",info.get("profile").toString()).apply();
                                        startActivity(new Intent(VerifyPhoneActivity.this, MainActivity.class));
                                        finish();
                                    }
                                    else{
                                        startActivity(new Intent(VerifyPhoneActivity.this, Completeyourprofile.class));
                                        finish();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                        } else {
                            message = "Somthing is wrong, we will fix it soon...";
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                message = "Invalid code entered...";
                            }
                            Snackbar.make(findViewById(R.id.parent), message, Snackbar.LENGTH_LONG).show();
                        }
                    }
                });
    }

}