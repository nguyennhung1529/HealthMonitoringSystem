package com.example.healthmonitor;

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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;

    private TextView tvRegister, tvForgotPassword;
    private EditText edtEmail, edtPassword;
    private Button btnLogin;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        getSupportActionBar().hide(); // hide the title bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN); //enable full screen

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Anh xa cac bien
        mAuth = FirebaseAuth.getInstance();

        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);

        progressBar = findViewById(R.id.progressBar);

        tvRegister = findViewById(R.id.tvRegister);
        tvRegister.setOnClickListener(this);

        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        tvForgotPassword.setOnClickListener(this);

        btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tvRegister:
                startActivity(new Intent(this, RegisterActivity.class));
                break;
            case R.id.btnLogin:
                loginUser();
                break;
            case R.id.tvForgotPassword:
                startActivity(new Intent(this, ForgotPassword.class));
                break;
        }
    }

    private void loginUser() {
        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        if (email.isEmpty()) {
            edtEmail.setError("Email không được để trống!");
            edtEmail.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            edtPassword.setError("Mật khẩu không được để trống!");
            edtPassword.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                            if (user.isEmailVerified()) {
                                // redirect to user profile
                                progressBar.setVisibility(View.GONE);
                                startActivity(new Intent(LoginActivity.this, ProfileActivity.class));
                            } else {
                                user.sendEmailVerification();
                                progressBar.setVisibility(View.GONE);
                                displayToast("Kiểm tra gmail để xác thực tài khoản của bạn!");
                            }
                        } else {
                            displayToast("Đăng nhập thất bại!");
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
    }
    public void displayToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }
}