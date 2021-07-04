package app.aacr.studypoint.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.chaos.view.PinView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.net.URI;
import java.util.HashMap;

import app.aacr.studypoint.R;

public class SecurityCodeVerify extends AppCompatActivity {
    private PinView editTextCode;
    private FirebaseAuth mAuth;
    private String mobile;
    private TextView mobile_number;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_security_code_verify);

        mobile = getIntent().getStringExtra("mobile");
        mAuth = FirebaseAuth.getInstance();

        editTextCode = findViewById(R.id.code);
        mobile_number = findViewById(R.id.mobile_number);

        mobile_number.setText(mobile);

        findViewById(R.id.wrong_number).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SecurityCodeVerify.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        findViewById(R.id.verify_code).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = editTextCode.getText().toString().trim();
                if (code.isEmpty() || code.length() < 6) {
                    Snackbar.make(findViewById(R.id.parent), "Enter valid code", Snackbar.LENGTH_LONG).show();
                    return;
                }
                FirebaseDatabase.getInstance().getReference("Users").child(mobile).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        HashMap info = (HashMap) snapshot.getValue();
                        if(info.get("security_code").equals(code)) {
                            getApplicationContext().getSharedPreferences("User_mobile", Context.MODE_PRIVATE).edit().putString("mobile",mobile).apply();
                            getApplicationContext().getSharedPreferences("User_name",Context.MODE_PRIVATE).edit().putString("name",info.get("name").toString()).apply();
                            if(info.get("profile")!=null) getApplicationContext().getSharedPreferences("User_profile",Context.MODE_PRIVATE).edit().putString("profile",info.get("profile").toString()).apply();
                            startActivity(new Intent(SecurityCodeVerify.this,MainActivity.class));
                            finish();
                        }else{
                            Snackbar.make(findViewById(R.id.parent), "Wrong Security Code", Snackbar.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Snackbar.make(findViewById(R.id.parent), "Try Again Later!", Snackbar.LENGTH_LONG).show();
                    }
                });
            }
        });
    }
}