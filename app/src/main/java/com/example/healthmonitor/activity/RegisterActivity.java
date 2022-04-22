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
import com.example.healthmonitor.object.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;

    private TextView tvBanner, tvLogin;
    private Button btnRegister;
    private EditText edtName, edtEmail, edtPassword, edtConfirmPassword;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        getSupportActionBar().hide(); // hide the title bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN); //enable full screen

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Anh xa cac bien
        mAuth = FirebaseAuth.getInstance();

        edtName = findViewById(R.id.edtName);
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        edtConfirmPassword = findViewById(R.id.edtConfirmPassword);

        progressBar = findViewById(R.id.progressBar);

        tvBanner = findViewById(R.id.tvBanner);
        tvBanner.setOnClickListener(this);

        tvLogin = findViewById(R.id.tvLogin);
        tvLogin.setOnClickListener(this);

        btnRegister = findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tvBanner:
            case R.id.tvLogin:
                startActivity(new Intent(this, LoginActivity.class));
                break;
            case R.id.btnRegister:
                registerUser();
                break;

        }
    }

    private void registerUser() {
        String name = edtName.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();
        String confirmPassword = edtConfirmPassword.getText().toString().trim();

        if (name.isEmpty()) {
            edtName.setError("Họ tên không được để trống!");
            edtName.requestFocus();
            return;
        }

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

        if (password.isEmpty()) {
            edtPassword.setError("Mật khẩu không được để trống!");
            edtPassword.requestFocus();
            return;
        }

        if (password.length() < 6) {
            edtPassword.setError("Mật khẩu có độ dài trên 6 ký tự!");
            edtPassword.requestFocus();
            return;
        }

        if (confirmPassword.isEmpty() || !confirmPassword.equals(password)) {
            edtConfirmPassword.setError("Mật khẩu nhập lại không hợp lệ!");
            edtConfirmPassword.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        // 1 - register user and 2 - send user obj to real-time database
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            User user = new User(name, email);

                            // send user obj to real-time database
                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        displayToast("Đăng ký tài khoản thành công!");
                                        progressBar.setVisibility(View.GONE);

                                        // redirect to login layout
                                        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                                    } else {
                                        displayToast("Đăng ký không thành công! Hãy kiểm tra lại thông tin!");
                                        progressBar.setVisibility(View.GONE);
                                    }
                                }
                            });
                        } else {
                            displayToast("Đăng ký không thành công! Hãy kiểm tra lại thông tin!");
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
    }

    public void displayToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }

}