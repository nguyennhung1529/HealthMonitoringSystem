package com.example.healthmonitor.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.example.healthmonitor.R;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        getSupportActionBar().hide(); // hide the title bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN); //enable full screen

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
    }


    public void navigation(View view) {
        if (view.getId() == R.id.layout_home_bmi) {
            startActivity(new Intent(this, BmiActivity.class));
        } else if (view.getId() == R.id.layout_home_weight) {
            startActivity(new Intent(this, WeightActivity.class));
        } else if (view.getId() == R.id.layout_home_water) {
            startActivity(new Intent(this, WaterActivity.class));
        } else if (view.getId() == R.id.layout_home_sleep) {
            startActivity(new Intent(this, SleepActivity.class));
        } else if (view.getId() == R.id.iv_btn_user) {
            startActivity(new Intent(this, MainActivity.class));
        }
    }
}