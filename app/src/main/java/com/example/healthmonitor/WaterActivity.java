package com.example.healthmonitor;

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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.healthmonitor.custom.MyMarkerView;
import com.example.healthmonitor.object.BMI;
import com.example.healthmonitor.object.Water;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IFillFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.Utils;
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
import java.util.ArrayList;
import java.util.Calendar;

public class WaterActivity extends AppCompatActivity implements View.OnClickListener, OnChartValueSelectedListener {

    private DatabaseReference mDatabase;
    private String userID;
    private ArrayList<Water> mWaterList;

    private BarChart mBarChart;
    private TextView tvTime, tvWater;
    private ImageView ivAdd, ivSub;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_water);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mWaterList = new ArrayList<>();

        initUI();
        initListener();
//        showWaterDetail();

        mDatabase.child("Waters").child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot iWeight : snapshot.getChildren()) {
                    mWaterList.add(iWeight.getValue(Water.class));
                    mBarChart.notifyDataSetChanged();
                    if (mWaterList.size() != 0) {
                        Water water = mWaterList.get(mWaterList.size() - 1);
                        if (water != null) {
                            tvWater.setText(String.valueOf(water.getValue()));
                            tvTime.setText(water.getDate());
                        }
                    }

                    showBarChart();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }

//    private void showWaterDetail() {
//        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
//        String currentDate = df.format(Calendar.getInstance().getTime());
//        DateFormat dfKey = new SimpleDateFormat("yyyyMMdd");
//        String expectedKey = dfKey.format(Calendar.getInstance().getTime());
//
//        tvTime.setText(currentDate);
//
//        mDatabase.child("Weights").child(userID).child(expectedKey).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                Water water = snapshot.getValue(Water.class);
//                if (water != null) {
//                    tvWater.setText(String.valueOf(water.getValue()));
//                    tvTime.setText(": " + water.getDate());
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//    }

    private void showBarChart() {
        {   // // Chart Style // //
            // background color
            mBarChart.setBackgroundColor(Color.WHITE);

            // disable description text
            mBarChart.getDescription().setEnabled(false);

            // enable touch gestures
            mBarChart.setTouchEnabled(true);

            // set listeners
            mBarChart.setOnChartValueSelectedListener(this);
            mBarChart.setDrawGridBackground(false);

            // create marker to display box when values are selected
            MyMarkerView mv = new MyMarkerView(this, R.layout.custom_maker_view);

            // Set the marker to the chart
            mv.setChartView(mBarChart);
            mBarChart.setMarker(mv);

            // enable scaling and dragging
            mBarChart.setDragEnabled(true);
            mBarChart.setScaleEnabled(true);

            // force pinch zoom along both axis
            mBarChart.setPinchZoom(true);
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

//

        // add data
        setData();

        // draw points over time
        mBarChart.animateX(1500);

        // get the legend (only possible after setting data)
        Legend l = mBarChart.getLegend();

        // draw legend entries as lines
        l.setForm(Legend.LegendForm.LINE);
    }

    private void setData() {

        ArrayList<BarEntry> values = new ArrayList<>();
        float spaceForBar = 10f;

        for (int i = 0; i < mWaterList.size(); i++) {
            int val = (int) mWaterList.get(i).getValue();
            values.add(new BarEntry(i*spaceForBar, val,getResources().getDrawable(R.drawable.star)));
        }


//        for (int i = 0; i < count; i++) {
//
//            float val = (float) (Math.random() * range) - 30;
//            values.add(new Entry(i, val, getResources().getDrawable(R.drawable.star)));
//        }

        BarDataSet set1;

        if (mBarChart.getData() != null &&
                mBarChart.getData().getDataSetCount() > 0) {
            set1 = (BarDataSet) mBarChart.getData().getDataSetByIndex(0);
            set1.setValues(values);
            set1.notifyDataSetChanged();
            mBarChart.getData().notifyDataChanged();
            mBarChart.notifyDataSetChanged();
        } else {
            // create a dataset and give it a type
            set1 = new BarDataSet(values, "DataSet 1");

            set1.setDrawIcons(false);

            // black lines and points
            set1.setColor(Color.rgb(0, 188, 212));


            // customize legend entry
//            set1.setFormLineWidth(1f);
//            set1.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
//            set1.setFormSize(15.f);

            // text size of values
            set1.setValueTextSize(5f);

            // draw selection line as dashed
//            set1.enableDashedHighlightLine(10f, 5f, 0f);
//
//            // set the filled area
//            set1.setDrawFilled(true);
//            set1.setFillFormatter(new IFillFormatter() {
//                @Override
//                public float getFillLinePosition(ILineDataSet dataSet, LineDataProvider dataProvider) {
//                    return mBarChart.getAxisLeft().getAxisMinimum();
//                }
//            });

            // set color of filled area
//            if (Utils.getSDKInt() >= 18) {
//                // drawables only supported on api level 18 and above
//                Drawable drawable = ContextCompat.getDrawable(this, R.drawable.fade_blue);
//                set1.setFillDrawable(drawable);
//            } else {
//                set1.setFillColor(Color.BLACK);
//            }

            ArrayList<IBarDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1); // add the data sets

            // create a data object with the data sets
            BarData data = new BarData(dataSets);
            data.setBarWidth(10f);

            // set data
            mBarChart.setData(data);
        }
    }


    private void initListener() {
        ivSub.setOnClickListener(this);
        ivAdd.setOnClickListener(this);
    }



    private void initUI() {
        tvTime = findViewById(R.id.tvWaterTime);
        tvWater = findViewById(R.id.content_waterDrinked);
        ivAdd = findViewById(R.id.iv_add);
        ivSub = findViewById(R.id.iv_sub);
        mBarChart = findViewById(R.id.barchart);
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
                AlertDialog.Builder myAlertBuilder = new AlertDialog.Builder(WaterActivity.this);
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
            case R.id.iv_add:
                int valueadd = Integer.parseInt(tvWater.getText().toString()) +1;
                tvWater.setText(String.valueOf(valueadd));
                saveWater();
                break;
            case R.id.iv_sub:
                int valuesub = Integer.parseInt(tvWater.getText().toString());
                if (valuesub>0){
                    tvWater.setText(String.valueOf(valuesub-1));
                }
                break;
        }
    }

    private void saveWater() {
        int value = Integer.parseInt(tvWater.getText().toString());
        String date = tvTime.getText().toString();

        DateFormat dfKey = new SimpleDateFormat("yyyyMMdd");
        String key = dfKey.format(Calendar.getInstance().getTime());

        Water water = new Water(date, value);
        mDatabase.child("Waters").child(userID).child(key).setValue(water).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

            }
        });

    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        Log.i("Entry selected", e.toString());
        Log.i("LOW HIGH", "low: " + mBarChart.getLowestVisibleX() + ", high: " + mBarChart.getHighestVisibleX());
        Log.i("MIN MAX", "xMin: " + mBarChart.getXChartMin() + ", xMax: " + mBarChart.getXChartMax() + ", yMin: " + mBarChart.getYChartMin() + ", yMax: " + mBarChart.getYChartMax());

    }

    @Override
    public void onNothingSelected() {

    }
}