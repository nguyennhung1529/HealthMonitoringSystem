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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
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

        npWeightHead.setMinValue(2);
        npWeightHead.setMaxValue(700);
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
        String currentDate = dateTypeToDateString(Calendar.getInstance().getTime());
        String expectedKey = dateToKey(currentDate);
        
        tvWeightDate.setText(currentDate);

        // get lastest user details
        mDatabase.child("UserDetails").child(userID).orderByKey().limitToLast(1).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Data userDetailsLastest = null;
                for (DataSnapshot iData : snapshot.getChildren()) {
                    userDetailsLastest = iData.getValue(Data.class);
                }

                if (userDetailsLastest != null) {
                    String[] arrWeight = String.valueOf(userDetailsLastest.getWeight()).split("\\.");
                    npWeightHead.setValue(Integer.parseInt(arrWeight[0]));
                    npWeightTail.setValue(Integer.parseInt(arrWeight[1]));
                    
                    if (currentDate.equals(userDetailsLastest.getDate())) {
                        edtNote.setText(userDetailsLastest.getNote() != null ? userDetailsLastest.getNote() : null);
                    }
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
                // startActivity(new Intent(this, WeightStatisticActivity.class));
                finish();
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
        String currentNote = edtNote.getText().toString();
        String currentDate = tvWeightDate.getText().toString();
        String key = dateToKey(currentDate);

        mDatabase.child("UserDetails").child(userID).orderByKey().limitToLast(1).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot iData : snapshot.getChildren()) {
                    Data userDetailsLastest = iData.getValue(Data.class);
                    if (userDetailsLastest != null) {
                        // save weight
                        Data dataUser = new Data(userDetailsLastest.getHeight(), currentWeight, currentNote, currentDate);
                        mDatabase.child("UserDetails").child(userID).child(key).setValue(dataUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    // redirect to profile activity
                                    // startActivity(new Intent(WeightActivity.this, WeightStatisticActivity.class));
                                    finish();
                                } else {
                                    displayToast("Cập nhật không thành công!");
                                }
                            }
                        });
                    }
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

    public String keyToDate(String key) {
        String date = "";

        DateFormat formatDate = new SimpleDateFormat("dd/MM/yyyy");
        DateFormat formatKey = new SimpleDateFormat("yyyyMMdd");

        Date d = null;
        try {
            d = formatKey.parse(key);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (d != null) {
            date = formatDate.format(d);
        }

        return date;
    }

    public String dateToKey(String date) {
        String key = "";

        DateFormat formatDate = new SimpleDateFormat("dd/MM/yyyy");
        DateFormat formatKey = new SimpleDateFormat("yyyyMMdd");

        Date d = null;
        try {
            d = formatDate.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (d != null) {
            key = formatKey.format(d);
        }

        return key;
    }

    public String dateTypeToDateString(Date date) {
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        return df.format(date);
    }

}