package app.aacr.studypoint.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import app.aacr.studypoint.R;

public class LoginActivity extends AppCompatActivity{

    private EditText mobile_number;
    private TextInputLayout error;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ((TextView)findViewById(R.id.error_text)).setText("Enter a valid mobile number");
        error = findViewById(R.id.error);
        mobile_number = findViewById(R.id.inputMobile);

        findViewById(R.id.buttongetOTP).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                error.setErrorEnabled(false);
                String mobile = mobile_number.getText().toString().trim();
                Pattern p = Pattern.compile("^[6-9]\\d{9}$");
                Matcher m = p.matcher(mobile);
                if(!m.find()){
                    error.setError("Enter a valid mobile number");
                    mobile_number.requestFocus();
                    return;
                }
                Intent intent = new Intent(LoginActivity.this, VerifyPhoneActivity.class);
                intent.putExtra("mobile", mobile);
                startActivity(intent);
                finish();
            }
        });
        findViewById(R.id.sequritypass).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                error.setErrorEnabled(false);
                String mobile = mobile_number.getText().toString().trim();
                Pattern p = Pattern.compile("^[6-9]\\d{9}$");
                Matcher m = p.matcher(mobile);
                if(!m.find()){
                    error.setError("Enter a valid mobile number");
                    mobile_number.requestFocus();
                    return;
                }
                FirebaseDatabase.getInstance().getReference("Users").child(mobile).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()) {
                            Intent intent = new Intent(LoginActivity.this, SecurityCodeVerify.class);
                            intent.putExtra("mobile", mobile);
                            startActivity(intent);
                            finish();
                        }
                        else{
                            Snackbar.make(findViewById(R.id.parent), "User Not Registered", Snackbar.LENGTH_LONG).show();
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