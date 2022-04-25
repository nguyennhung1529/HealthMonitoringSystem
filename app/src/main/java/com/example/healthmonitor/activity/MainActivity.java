package com.example.healthmonitor.activity;

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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.healthmonitor.R;
import com.example.healthmonitor.object.Data;
import com.example.healthmonitor.object.User;
import com.example.healthmonitor.object.Water;
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

    private TextView tvName, tvBmiValue, tvWeightValue, tvSleepValue, tvWaterValue, tvStepValue, tvBmiStatus;
    private TextView tvUpdateProfile;
    private LinearLayout layout_bmi, layout_weight, layout_water, layout_sleep, layout_step;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Thông tin cá nhân");

        mDatabase = FirebaseDatabase.getInstance().getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();
        userID = user.getUid();

        initUI();
        initListener();
        // Read user profile
        showUserShortInformation();
        showWaterLastest();

    }

    public void initUI() {
        tvName = findViewById(R.id.tvName);
        tvBmiValue = findViewById(R.id.tvBmiValue);
        tvBmiStatus = findViewById(R.id.tvBmiStatus);
        tvWeightValue = findViewById(R.id.tvWeightValue);
        tvSleepValue = findViewById(R.id.tvSleepValue);
        tvWaterValue = findViewById(R.id.tvWaterValue);
        tvStepValue = findViewById(R.id.tvStepValue);
        tvUpdateProfile = findViewById(R.id.tvUpdateProfile);
        layout_bmi = findViewById(R.id.layout_bmi);
        layout_weight = findViewById(R.id.layout_weight);
        layout_water = findViewById(R.id.layout_water);
        layout_sleep = findViewById(R.id.layout_sleep);
        layout_step = findViewById(R.id.layout_step);
    }

    public void initListener() {
        tvUpdateProfile.setOnClickListener(this);
        layout_bmi.setOnClickListener(this);
        layout_weight.setOnClickListener(this);
        layout_water.setOnClickListener(this);
        layout_sleep.setOnClickListener(this);
        layout_step.setOnClickListener(this);
    }

    public void showUserShortInformation() {
        if (user != null) {
            // get lastest user details
            mDatabase.child("UserDetails").child(userID).orderByKey().limitToLast(1).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Data userDetailsLastest = null;
                    for (DataSnapshot iData : snapshot.getChildren()) {
                        userDetailsLastest = iData.getValue(Data.class);
                    }

                    if (userDetailsLastest != null) {
                        float weight = userDetailsLastest.getWeight();
                        float valueBMI = weight / (((float) userDetailsLastest.getHeight() / 100) * ((float) userDetailsLastest.getHeight() / 100));
                        String statusBMI = (valueBMI < 18) ? "Thiếu cân" : ((valueBMI <= 25) ? "Bình thường" : "Thừa cân");

                        tvWeightValue.setText(String.valueOf(Math.round(weight)));
                        tvBmiValue.setText(String.valueOf(Math.round(valueBMI)));
                        tvBmiStatus.setText(statusBMI);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });

            mDatabase.child("Users").child(userID).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User user = snapshot.getValue(User.class);
                    if (user != null) {
                        tvName.setText(user.getName());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

    }

    public void showWaterLastest() {
        if (user != null) {
            // get lastest water
            mDatabase.child("Waters").child(userID).orderByKey().limitToLast(1).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot iWater : snapshot.getChildren()) {
                        Water water = iWater.getValue(Water.class);
                        if (water != null) {
                            tvWaterValue.setText(String.valueOf(water.getValue()) + "/10");
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }
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
            case R.id.layout_weight:
                startActivity(new Intent(this, WeightStatisticActivity.class));
                break;
            case R.id.layout_bmi:
                startActivity(new Intent(this, BmiActivity.class));
                break;
            case R.id.layout_sleep:
                startActivity(new Intent(this, SleepActivity.class));
                break;
            case R.id.layout_step:
                startActivity(new Intent(this, Footsteps.class));
                break;
            case R.id.layout_water:
                startActivity(new Intent(this, WaterActivity.class));
                break;
            default:
                break;
        }
    }
}