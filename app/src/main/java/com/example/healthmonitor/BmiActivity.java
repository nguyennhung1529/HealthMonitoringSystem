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

import com.example.healthmonitor.object.BMI;
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

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class BmiActivity extends AppCompatActivity {

    Button btnDu_Lieu;
    TextView tvBMI, tvStatus;
    private FirebaseUser user;
    private DatabaseReference mDatabase;
//    private DatabaseReference reference;
    private String userID, status;
//    private FirebaseUser bmi;
    private int height;
    private float weight, value;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bmi);

        btnDu_Lieu = findViewById(R.id.btn_dulieu);

        tvBMI = findViewById(R.id.text_chi_so_BMI);

        tvStatus = findViewById(R.id.text_chi_so_status);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();
        userID = user.getUid();
        

//        bmi = FirebaseAuth.getInstance().getCurrentUser();
//        reference = FirebaseDatabase.getInstance().getReference("Users");
//
//        reference.child(userID);

        changeBMI();
        showBMI();

    }

    private void showBMI() {

        mDatabase.child("BMIs").child(userID).limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                BMI bmis = snapshot.getValue(BMI.class);

                if (bmis != null){
                    tvBMI.setText(bmis.getBmi());
                    tvStatus.setText(bmis.getStatus());
                }else{
                    tvBMI.setText("0");
                    tvStatus.setText("");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void changeBMI(){
        mDatabase.child("Users").child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user1 = snapshot.getValue(User.class);

                if (user1.getHeight() != 0){
                    if (user1.getWeight() != 0){
                        height = user1.getHeight();
                        weight = user1.getWeight();
                        value = weight/(height*height/(100*100));
                        if (value <18.5){
                            status ="Thiếu cân";
                        }else if(value<=25){
                            status = "Bình thường";
                        }else {
                            status = "Thừa cân";
                        }
                        Map<String, String> bmi = new HashMap<>();
                        bmi.put("statsus", status);
                        bmi.put("value", String.valueOf(value));

                        mDatabase.child("BMIs").child(userID).push().setValue(bmi);
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
                AlertDialog.Builder myAlertBuilder = new AlertDialog.Builder(BmiActivity.this);
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