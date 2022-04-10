package com.example.healthmonitor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseUser user;
    private DatabaseReference reference;
    private String userID;

    private EditText edtName, edtEmail, edtAge, edtHeight, edtWeight;
    private RadioButton radMale, radFemale;
    private RadioGroup radGrpGender;
    private Button btnUpdateProfile;
    private TextView tvSkip;

    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        getSupportActionBar().hide(); // hide the title bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN); //enable full screen

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        edtName = findViewById(R.id.edtName);
        edtEmail = findViewById(R.id.edtEmail);
        edtAge = findViewById(R.id.edtAge);
        edtHeight = findViewById(R.id.edtHeight);
        radGrpGender = findViewById(R.id.radGrpGender);

        btnUpdateProfile = findViewById(R.id.btnUpdateProfile);
        btnUpdateProfile.setOnClickListener(this);

        progressBar = findViewById(R.id.progressBar);

        tvSkip = findViewById(R.id.tvSkip);
        tvSkip.setOnClickListener(this);

        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        userID = user.getUid();


        edtEmail.setEnabled(false);


        progressBar.setVisibility(View.VISIBLE);
        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userProfile = snapshot.getValue(User.class);

                if (userProfile != null) {
                    edtName.setText(userProfile.getName().toString());
                    edtEmail.setText(userProfile.getEmail().toString());
                    progressBar.setVisibility(View.GONE);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


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
        int age = Integer.parseInt(edtAge.getText().toString());
        int height = Integer.parseInt(edtHeight.getText().toString());
        float weight = Float.parseFloat(edtWeight.getText().toString());

    }
}