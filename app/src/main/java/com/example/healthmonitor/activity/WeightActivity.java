package com.example.healthmonitor.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.example.healthmonitor.R;
import com.google.firebase.auth.FirebaseAuth;

public class WeightActivity extends AppCompatActivity {

    private NumberPicker npWeightHead;
    private NumberPicker npWeightTail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weight);

        npWeightHead = findViewById(R.id.npWeightHead);
        npWeightTail = findViewById(R.id.npWeightTail);

        String[] nums = new String[700];
        for(int i=2; i<nums.length; i++)
            nums[i] = Integer.toString(i);

        npWeightHead.setMinValue(2);
        npWeightHead.setMaxValue(700);
        npWeightHead.setWrapSelectorWheel(false);
//        npWeightHead.setDisplayedValues(nums);
        npWeightHead.setValue(50);

        npWeightTail.setMinValue(0);
        npWeightTail.setMaxValue(9);
        npWeightTail.setValue(0);
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
                startActivity(new Intent(this, WeightStatisticActivity.class));
                return true;
            case R.id.action_sleep:
                startActivity(new Intent(this, SleepActivity.class));
                return true;
            case R.id.action_logout:
                AlertDialog.Builder myAlertBuilder = new AlertDialog.Builder(WeightActivity.this);
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
}