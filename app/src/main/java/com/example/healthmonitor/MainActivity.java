package com.example.healthmonitor;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseUser user;
    private DatabaseReference mDatabase;
    private String userID;

    private TextView tvName, tvBmiValue, tvWeightValue, tvSleepValue, tvWaterValue, tvStepValue;
    private TextView tvUpdateProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();
        userID = user.getUid();

        initUI();
        initListener();

        // Read user profile
        showUserShortInformation();

    }

    public void showUserShortInformation() {
        if (user == null) {
            return;
        }

        mDatabase.child("Users").child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userProfile = snapshot.getValue(User.class);

                if (userProfile != null) {
                    tvName.setText(userProfile.getName().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "loadUser:onCancelled", error.toException());
            }
        });
    }

    public void initUI() {
        tvName = findViewById(R.id.tvName);
        tvBmiValue = findViewById(R.id.tvBmiValue);
        tvWeightValue = findViewById(R.id.tvWeightValue);
        tvSleepValue = findViewById(R.id.tvSleepValue);
        tvWaterValue = findViewById(R.id.tvWaterValue);
        tvStepValue = findViewById(R.id.tvStepValue);
        tvUpdateProfile = findViewById(R.id.tvUpdateProfile);
    }

    public void initListener() {
        tvUpdateProfile.setOnClickListener(this);
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
                AlertDialog.Builder myAlertBuilder = new AlertDialog.Builder(MainActivity.this);
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
            case R.id.action_post:
                startActivity(new Intent(this, PostActivity.class));
                break;
            default:
                // Do nothing
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tvUpdateProfile:
                startActivity(new Intent(this, ProfileActivity.class));
                break;
        }
    }
}