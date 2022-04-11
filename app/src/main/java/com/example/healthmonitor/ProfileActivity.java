package com.example.healthmonitor;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.healthmonitor.object.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseUser user;
    private DatabaseReference mDatabase;
    private String userID;

    private EditText edtName, edtEmail, edtAge, edtHeight, edtWeight;
    private RadioButton radMale, radFemale;
    private RadioGroup radGrpGender;
    private Button btnUpdateProfile;
    private TextView tvSkip;

    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();
        userID = user.getUid();

        initUI();
        initListener();

        // Read user profile
        showUserProfile();
    }

    public void showUserProfile() {
        progressBar.setVisibility(View.VISIBLE);
        if (user == null) {
            return;
        }

        mDatabase.child("Users").child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userProfile = snapshot.getValue(User.class);

                if (userProfile != null) {
                    edtName.setText(userProfile.getName().toString());
                    edtEmail.setText(userProfile.getEmail().toString());

                    if (userProfile.getAge() != 0) {
                        edtAge.setText(String.valueOf(userProfile.getAge()));
                    }
                    if (userProfile.getHeight() != 0) {
                        edtHeight.setText(String.valueOf(userProfile.getHeight()));
                    }
                    if (userProfile.getWeight() != 0) {
                        edtWeight.setText(String.valueOf(userProfile.getWeight()));
                    }
                    if (userProfile.getGender() == 1) {
                        radMale.setChecked(true);
                    } else {
                        radFemale.setChecked(true);
                    }
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "loadUser:onCancelled", error.toException());
            }
        });
    }

    public void initUI() {
        edtName = findViewById(R.id.edtName);
        edtEmail = findViewById(R.id.edtEmail);
        edtAge = findViewById(R.id.edtAge);
        edtHeight = findViewById(R.id.edtHeight);
        edtWeight = findViewById(R.id.edtWeight);
        radGrpGender = findViewById(R.id.radGrpGender);
        radMale = findViewById(R.id.radMale);
        radFemale = findViewById(R.id.radFemale);
        btnUpdateProfile = findViewById(R.id.btnUpdateProfile);
        progressBar = findViewById(R.id.progressBar);
        tvSkip = findViewById(R.id.tvSkip);

        edtEmail.setEnabled(false);
    }

    public void initListener() {
        btnUpdateProfile.setOnClickListener(this);
        tvSkip.setOnClickListener(this);
    }

    public void displayToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_profile:
                startActivity(new Intent(this, MainActivity.class));
                return true;
            case R.id.action_home:
                startActivity(new Intent(this, HomeActivity.class));
                return true;
            case R.id.action_bmi:
                startActivity(new Intent(this, BmiActivity.class));
                return true;
            case R.id.action_water:
                startActivity(new Intent(this, WaterActivity.class));
                return true;
            case R.id.action_weight:
                startActivity(new Intent(this, WeightActivity.class));
                return true;
            case R.id.action_sleep:
                startActivity(new Intent(this, SleepActivity.class));
                return true;
            case R.id.action_logout:
                AlertDialog.Builder myAlertBuilder = new AlertDialog.Builder(ProfileActivity.this);
                myAlertBuilder.setTitle("Thông báo!");
                myAlertBuilder.setMessage("Bạn có chắc chắn muốn đăng xuất khỏi hệ thống?");
                myAlertBuilder.setPositiveButton("OK", (dialog, which) -> {
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(this, LoginActivity.class));
                    finishAffinity();
                });
                myAlertBuilder.setNegativeButton("Cancel", null);

                myAlertBuilder.show();
                return true;
            default:
                // Do nothing
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tvSkip:
                startActivity(new Intent(this, HomeActivity.class));
                break;
            case R.id.btnUpdateProfile:
                updateProfileUser();
                break;
        }
    }

    private void updateProfileUser() {
        String name = edtName.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();

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

        if (edtAge.getText().toString().trim().isEmpty()) {
            edtAge.setError("Tuổi không được để trống!");
            edtAge.requestFocus();
            return;
        }

        if (Integer.parseInt(edtAge.getText().toString().trim()) <= 0) {
            edtAge.setError("Tuổi không hợp lệ!");
            edtAge.requestFocus();
            return;
        }

        if (edtHeight.getText().toString().trim().isEmpty()) {
            edtHeight.setError("Chiều cao không được để trống!");
            edtHeight.requestFocus();
            return;
        }

        if (Integer.parseInt(edtHeight.getText().toString().trim()) <= 0) {
            edtHeight.setError("Chiều cao không hợp lệ!");
            edtHeight.requestFocus();
            return;
        }

        if (edtWeight.getText().toString().trim().isEmpty()) {
            edtWeight.setError("Cân nặng không được để trống!");
            edtWeight.requestFocus();
            return;
        }

        if (Float.parseFloat(edtWeight.getText().toString().trim()) <= 0) {
            edtWeight.setError("Cân nặng không hợp lệ!");
            edtWeight.requestFocus();
            return;
        }


        int age = Integer.parseInt(edtAge.getText().toString().trim());
        int height = Integer.parseInt(edtHeight.getText().toString().trim());
        float weight = Float.parseFloat(edtWeight.getText().toString().trim());
        int gender = (radMale.isChecked()) ? 1 : 0;

        progressBar.setVisibility(View.VISIBLE);
        // update user profile
        User user = new User(name, email, age, height, weight, gender);
        mDatabase.child("Users").child(userID).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    displayToast("Cập nhật tài khoản thành công!");
                    // redirect to profile activity
                    startActivity(new Intent(ProfileActivity.this, MainActivity.class));
                } else {
                    displayToast("Cập nhật không thành công! Hãy kiểm tra lại thông tin!");
                }
                progressBar.setVisibility(View.GONE);
            }
        });
    }
}