package com.example.healthmonitor.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.example.healthmonitor.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        getSupportActionBar().hide(); // hide the title bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN); //enable full screen

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        mDatabase.child("UserDetails").child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                System.out.println(!snapshot.exists());
                if(!snapshot.exists()) {
                    startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
//                    finishAffinity();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    public void navigation(View view) {
        if (view.getId() == R.id.layout_home_bmi) {
            startActivity(new Intent(this, BmiActivity.class));
        } else if (view.getId() == R.id.layout_home_weight) {
            startActivity(new Intent(this, WeightStatisticActivity.class));
        } else if (view.getId() == R.id.layout_home_water) {
            startActivity(new Intent(this, WaterActivity.class));
        } else if (view.getId() == R.id.layout_home_footsteps) {
            startActivity(new Intent(this, Footsteps.class));
        } else if (view.getId() == R.id.iv_btn_user) {
            startActivity(new Intent(this, MainActivity.class));
        } else if (view.getId() == R.id.iv_btn_notice_post) {
            startActivity(new Intent(this, PostActivity.class));
        }
    }
}