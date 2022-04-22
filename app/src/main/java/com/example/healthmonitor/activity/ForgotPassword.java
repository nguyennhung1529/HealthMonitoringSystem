package com.example.healthmonitor.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.healthmonitor.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassword extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private EditText edtEmail;
    private Button btnResetPassword;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        getSupportActionBar().hide(); // hide the title bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN); //enable full screen

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        // Anh xa cac bien
        mAuth = FirebaseAuth.getInstance();

        edtEmail = findViewById(R.id.edtEmail);
        progressBar = findViewById(R.id.progressBar);

        btnResetPassword = findViewById(R.id.btnResetPassword);
        btnResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetPassword();
            }
        });
    }

    private void resetPassword() {
        String email = edtEmail.getText().toString().trim();

        if (email.isEmpty()) {
            edtEmail.setError("Email không được để trống!");
            edtEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            edtEmail.setError("Email không hợp lệ!");
            edtEmail.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    displayToast("Kiểm tra email để đặt lại mật khẩu!");
                    progressBar.setVisibility(View.GONE);
                } else {
                    displayToast("Lỗi! Hãy thử lại!");
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }
    public void displayToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }
}