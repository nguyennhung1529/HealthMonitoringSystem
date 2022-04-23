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
import com.example.healthmonitor.object.Weight;
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

        tvWeightDate.setText(currentDate);

        mDatabase.child("Weights").child(userID).child(expectedKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Weight weight = snapshot.getValue(Weight.class);
                if (weight != null) {
                    //tvWeightDate.setText(weight.getDate());
                    edtNote.setText(weight.getNote() != null ? weight.getNote() : null);

                    String[] arrWeight = String.valueOf(weight.getWeight()).split("\\.");
                    npWeightHead.setValue(Integer.parseInt(arrWeight[0]));
                    npWeightTail.setValue(Integer.parseInt(arrWeight[1]));
//                        displayToast(weight.toString());
                }
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

    private void saveWeight() {
        int weightHead = npWeightHead.getValue();
        int weightTail = npWeightTail.getValue();
        float currentWeight = Float.valueOf(String.valueOf(weightHead) + "."  + String.valueOf(weightTail));
        String note = edtNote.getText().toString();
        String date = tvWeightDate.getText().toString();

//        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
//        String date = df.format(Calendar.getInstance().getTime());
//
////        displayToast(String.valueOf(date));
        DateFormat dfKey = new SimpleDateFormat("yyyyMMdd");
        String key = dfKey.format(Calendar.getInstance().getTime());

        Weight weight = new Weight(currentWeight, date, note);
        mDatabase.child("Weights").child(userID).child(key).setValue(weight).addOnCompleteListener(new OnCompleteListener<Void>() {
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