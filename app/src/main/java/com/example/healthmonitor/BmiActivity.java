package com.example.healthmonitor;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.widget.Button;

import android.widget.TextView;
import android.widget.Toast;

import com.example.healthmonitor.custom.MyMarkerView;
import com.example.healthmonitor.object.BMI;
import com.example.healthmonitor.object.User;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IFillFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.Utils;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class BmiActivity extends AppCompatActivity implements View.OnClickListener, OnChartValueSelectedListener {

    TextView btnDu_Lieu;
    TextView tvBMI, tvStatus;
    private FirebaseUser user;
    private DatabaseReference mDatabase;
//    private DatabaseReference reference;
    private String userID, status;
//    private FirebaseUser bmi;
    private int height;
    private float weight, value;
    private TextView tvBmiDate;
    private ArrayList<BMI> mBmiList;

    private LineChart mLineChart;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bmi);

        btnDu_Lieu = findViewById(R.id.btnDuLieu);
        tvBmiDate= findViewById(R.id.tvBmiTime);

        tvBMI = findViewById(R.id.text_chi_so_BMI);

        tvStatus = findViewById(R.id.text_chi_so_status);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();
        userID = user.getUid();
        mLineChart = findViewById(R.id.ChartBMI);

        btnDu_Lieu.setOnClickListener(this);

        mBmiList = new ArrayList<>();
//        bmi = FirebaseAuth.getInstance().getCurrentUser();
//        reference = FirebaseDatabase.getInstance().getReference("Users");
//
//        reference.child(userID);

//        changeBMI();
        showBMI();

    }

    private void showBMI() {
        mDatabase.child("BMIs").child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot iWeight : snapshot.getChildren()) {
                    mBmiList.add(iWeight.getValue(BMI.class));
                    mLineChart.notifyDataSetChanged();

                    if (mBmiList.size() != 0) {
                        BMI bmi = mBmiList.get(mBmiList.size() - 1);
                        if (bmi != null) {
                            tvBMI.setText(String.valueOf(bmi.getBmi()));
                            tvBmiDate.setText(bmi.getDate());
                        }
                    }

                    showLineChart();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void showLineChart() {
        {
            {   // // Chart Style // //
                // background color
                mLineChart.setBackgroundColor(Color.WHITE);

                // disable description text
                mLineChart.getDescription().setEnabled(false);

                // enable touch gestures
                mLineChart.setTouchEnabled(true);

                // set listeners
                mLineChart.setOnChartValueSelectedListener(this);
                mLineChart.setDrawGridBackground(false);

                // create marker to display box when values are selected
                MyMarkerView mv = new MyMarkerView(this, R.layout.custom_maker_view);

                // Set the marker to the chart
                mv.setChartView(mLineChart);
                mLineChart.setMarker(mv);

                // enable scaling and dragging
                mLineChart.setDragEnabled(true);
                mLineChart.setScaleEnabled(true);

                // force pinch zoom along both axis
                mLineChart.setPinchZoom(true);
            }

            {   // // Create Limit Lines // //
                LimitLine llXAxis = new LimitLine(9f, "Index 10");
                llXAxis.setLineWidth(4f);
                llXAxis.enableDashedLine(10f, 10f, 0f);
                llXAxis.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
                llXAxis.setTextSize(10f);

                LimitLine ll1 = new LimitLine(150f, "Upper Limit");
                ll1.setLineWidth(4f);
                ll1.enableDashedLine(10f, 10f, 0f);
                ll1.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
                ll1.setTextSize(10f);

                LimitLine ll2 = new LimitLine(-30f, "Lower Limit");
                ll2.setLineWidth(4f);
                ll2.enableDashedLine(10f, 10f, 0f);
                ll2.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
                ll2.setTextSize(10f);
            }

            // add data
            setData();

            // draw points over time
            mLineChart.animateX(1500);

            // get the legend (only possible after setting data)
            Legend l = mLineChart.getLegend();

            // draw legend entries as lines
            l.setForm(Legend.LegendForm.LINE);
        }
    }

    private void setData() {


            ArrayList<Entry> values = new ArrayList<>();

            for (int i = 0; i < mBmiList.size(); i++) {
                float val = (float) mBmiList.get(i).getBmi();
                values.add(new Entry(i, val));
            }


//        for (int i = 0; i < count; i++) {
//
//            float val = (float) (Math.random() * range) - 30;
//            values.add(new Entry(i, val, getResources().getDrawable(R.drawable.star)));
//        }

            LineDataSet set1;

            if (mLineChart.getData() != null &&
                    mLineChart.getData().getDataSetCount() > 0) {
                set1 = (LineDataSet) mLineChart.getData().getDataSetByIndex(0);
                set1.setValues(values);
                set1.notifyDataSetChanged();
                mLineChart.getData().notifyDataChanged();
                mLineChart.notifyDataSetChanged();
            } else {
                // create a dataset and give it a type
                set1 = new LineDataSet(values, "DataSet 1");

                set1.setDrawIcons(false);

                // draw dashed line
                set1.enableDashedLine(10f, 5f, 0f);

                // black lines and points
                set1.setColor(Color.rgb(0, 188, 212));
                set1.setCircleColor(Color.rgb(0, 188, 212));

                // line thickness and point size
                set1.setLineWidth(1f);
                set1.setCircleRadius(3f);

                // draw points as solid circles
                set1.setDrawCircleHole(false);

                // customize legend entry
                set1.setFormLineWidth(1f);
                set1.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
                set1.setFormSize(15.f);

                // text size of values
                set1.setValueTextSize(9f);

                // draw selection line as dashed
                set1.enableDashedHighlightLine(10f, 5f, 0f);

                // set the filled area
                set1.setDrawFilled(true);
                set1.setFillFormatter(new IFillFormatter() {
                    @Override
                    public float getFillLinePosition(ILineDataSet dataSet, LineDataProvider dataProvider) {
                        return mLineChart.getAxisLeft().getAxisMinimum();
                    }
                });

                // set color of filled area
                if (Utils.getSDKInt() >= 18) {
                    // drawables only supported on api level 18 and above
                    Drawable drawable = ContextCompat.getDrawable(this, R.drawable.fade_blue);
                    set1.setFillDrawable(drawable);
                } else {
                    set1.setFillColor(Color.BLACK);
                }

                ArrayList<ILineDataSet> dataSets = new ArrayList<>();
                dataSets.add(set1); // add the data sets

                // create a data object with the data sets
                LineData data = new LineData(dataSets);

                // set data
                mLineChart.setData(data);
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

    @Override
    public void onClick(View view) {
        startActivity(new Intent(this, ProfileActivity.class));
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        Log.i("Entry selected", e.toString());
        Log.i("LOW HIGH", "low: " + mLineChart.getLowestVisibleX() + ", high: " + mLineChart.getHighestVisibleX());
        Log.i("MIN MAX", "xMin: " + mLineChart.getXChartMin() + ", xMax: " + mLineChart.getXChartMax() + ", yMin: " + mLineChart.getYChartMin() + ", yMax: " + mLineChart.getYChartMax());

    }

    @Override
    public void onNothingSelected() {

    }
}