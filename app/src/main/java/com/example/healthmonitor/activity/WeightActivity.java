package com.example.healthmonitor.activity;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.example.healthmonitor.R;
import com.example.healthmonitor.object.Data;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Objects;

public class WeightActivity extends AppCompatActivity implements View.OnClickListener {

    private DatabaseReference mDatabase;
    private String userID;

    private NumberPicker npWeightHead, npWeightTail;
    private TextView btnExit, btnSaveWeight, tvWeightDate;

    private EditText edtNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weight);
        setTitle("Cân nặng");

        mDatabase = FirebaseDatabase.getInstance().getReference();
        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        initUI();
        initListener();
        showWeightDetail();
    }

    private void initUI() {

        btnExit = findViewById(R.id.btnExit);
        btnSaveWeight = findViewById(R.id.btnSaveWeight);
        edtNote = findViewById(R.id.edt_NoteWeight);
        tvWeightDate = findViewById(R.id.tvWeightDate);

        npWeightHead = findViewById(R.id.npWeightHead);
        npWeightTail = findViewById(R.id.npWeightTail);

//        String[] nums = new String[700];
//        for(int i=2; i<nums.length; i++)
//            nums[i] = Integer.toString(i);

        npWeightHead.setMinValue(2);
        npWeightHead.setMaxValue(700);
//        npWeightHead.setDisplayedValues(nums);
        npWeightHead.setValue(50);

        npWeightTail.setMinValue(0);
        npWeightTail.setMaxValue(9);
        npWeightTail.setValue(0);
    }

    private void initListener() {
        btnSaveWeight.setOnClickListener(this);
        btnExit.setOnClickListener(this);
    }

    private void showWeightDetail() {
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        String currentDate = df.format(Calendar.getInstance().getTime());
        DateFormat dfKey = new SimpleDateFormat("yyyyMMdd");
        String expectedKey = dfKey.format(Calendar.getInstance().getTime());
        Data userDetailsLastest = getUserDetails();

        tvWeightDate.setText(currentDate);

        mDatabase.child("UserDetails").child(userID).child(expectedKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Data userDetails = snapshot.getValue(Data.class);
                String[] arrWeight;
                if (userDetails != null) {
                    //tvWeightDate.setText(weight.getDate());
                    edtNote.setText(userDetails.getNote() != null ? userDetails.getNote() : null);

                    arrWeight = String.valueOf(userDetails.getWeight()).split("\\.");
//                        displayToast(weight.toString());
                } else {
                    arrWeight = String.valueOf(userDetailsLastest.getWeight()).split("\\.");
                }
                npWeightHead.setValue(Integer.parseInt(arrWeight[0]));
                npWeightTail.setValue(Integer.parseInt(arrWeight[1]));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        view.setVisibility(View.GONE);
        switch (view.getId()) {
            case R.id.btnSaveWeight:
                saveWeight();
                break;
            case R.id.btnExit:
                startActivity(new Intent(this, WeightStatisticActivity.class));
                break;
            default:
                break;
        }
        view.setVisibility(View.VISIBLE);
    }

    private Data getUserDetails() {
        // get lastest user details
        Data userDetails = new Data();
        mDatabase.child("UserDetails").child(userID).orderByKey().limitToLast(1).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot iData : snapshot.getChildren()) {
                    if (iData.getValue(Data.class) != null)
                        userDetails.setData(Objects.requireNonNull(iData.getValue(Data.class)));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        return userDetails;
    }

    private void saveWeight() {
        int weightHead = npWeightHead.getValue();
        int weightTail = npWeightTail.getValue();
        float currentWeight = Float.valueOf(String.valueOf(weightHead) + "."  + String.valueOf(weightTail));
        String note = edtNote.getText().toString();
        String date = tvWeightDate.getText().toString();
        Data userDetailsLastest = getUserDetails();

//        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
//        String date = df.format(Calendar.getInstance().getTime());
//
////        displayToast(String.valueOf(date));
        DateFormat dfKey = new SimpleDateFormat("yyyyMMdd");
        String key = dfKey.format(Calendar.getInstance().getTime());

        Data userDetails = new Data(userDetailsLastest.getHeight(), currentWeight, note, date);
        mDatabase.child("UserDetails").child(userID).child(key).setValue(userDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    // redirect to profile activity
                    startActivity(new Intent(WeightActivity.this, WeightStatisticActivity.class));
                } else {
                    displayToast("Cập nhật không thành công!");
                }
            }
        });

//        try {
//            Date date = df.parse(datetime);
//            System.out.println(date);
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
    }

//    private void saveBMI(Float weight, String date, String key) {
//
//        int height = Integer.parseInt(edtHeight.getText().toString().trim());
//        float weight = Float.parseFloat(edtWeight.getText().toString().trim());
//
//        float value = weight/(((float) height/100)*((float) height/100));
//        String status;
//        if (value<18){
//            status = "Thiếu cân";
//        }else if (value<=25){
//            status = "Bình thường";
//        }else {
//            status = "Thừa cân";
//        }
//
//        BMI bmi = new BMI(value,status, date);
//        mDatabase.child("BMIs").child(userID).child(key).setValue(bmi).addOnCompleteListener(new OnCompleteListener<Void>() {
//            @Override
//            public void onComplete(@NonNull Task<Void> task) {
//
//            }
//        });
//    }

//    private void getHeightUser() {
//        mDatabase.child("Users").child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                User userProfile = snapshot.getValue(User.class);
//                if ()
//                int height = userProfile.getHeight();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Log.w(TAG, "loadUser:onCancelled", error.toException());
//            }
//        });
//    }

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