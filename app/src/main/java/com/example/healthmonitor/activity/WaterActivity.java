package com.example.healthmonitor.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.healthmonitor.R;
import com.example.healthmonitor.custom.MyMarkerView;
import com.example.healthmonitor.object.Water;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class WaterActivity extends AppCompatActivity implements View.OnClickListener, OnChartValueSelectedListener {

    private DatabaseReference mDatabase;
    private String userID;

    private BarChart mBarChart;
    private TextView tvWaterTime, tvWater, tvLuongNuocMl;
    private ImageView iv_add, iv_sub;

    private ArrayList<Water> mWaterList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_water);
        setTitle("Lượng nước");

        mDatabase = FirebaseDatabase.getInstance().getReference();
        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();


        initUI();
        initListener();
        // get list data water => show in diagram
        showWaterList();
    }

    private void initUI() {
        mBarChart = findViewById(R.id.barchartWater);
        tvLuongNuocMl = findViewById(R.id.tvLuongNuocMl);
        tvWater = findViewById(R.id.content_waterDrinked);
        tvWaterTime = findViewById(R.id.tvWaterTime);
        iv_add = findViewById(R.id.iv_add);
        iv_sub = findViewById(R.id.iv_sub);
    }

    private void initListener() {
        iv_add.setOnClickListener(this);
        iv_sub.setOnClickListener(this);
    }

    private void getWater(String key) {
        mDatabase.child("Waters").child(userID).child(key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Water water = snapshot.getValue(Water.class);
                if (water != null) {
                    tvWater.setText(String.valueOf(water.getValue()));
                    String luongNuoc = String.valueOf(Integer.parseInt(tvWater.getText().toString()) * 250);
                    tvLuongNuocMl.setText("(" + luongNuoc + " ml)");

                    if (dateTypeToDateString(Calendar.getInstance().getTime()).equals(water.getDate()))
                        tvWaterTime.setText("Hôm nay, " + water.getDate());
                    else
                        tvWaterTime.setText(water.getDate());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getWaterByIndex(int idx) {
        mDatabase.child("Waters").child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Water water = null;
                int i = 0;
                for (DataSnapshot iWater : snapshot.getChildren()) {
                    if (i == idx) {
                        water = iWater.getValue(Water.class);
                        break;
                    }
                    i++;
                }
                if (water != null) {
                    tvWater.setText(String.valueOf(water.getValue()));
                    String luongNuoc = String.valueOf(Integer.parseInt(tvWater.getText().toString()) * 250);
                    tvLuongNuocMl.setText("(" + luongNuoc + " ml)");

                    if (dateTypeToDateString(Calendar.getInstance().getTime()).equals(water.getDate()))
                        tvWaterTime.setText("Hôm nay, " + water.getDate());
                    else
                        tvWaterTime.setText(water.getDate());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void showWaterList() {
        mDatabase.child("Waters").child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mWaterList = new ArrayList<>();
                for (DataSnapshot iWater : snapshot.getChildren()) {
                    mWaterList.add(iWater.getValue(Water.class));
                }
                mBarChart.notifyDataSetChanged();
                showBarChart();
                // First time access activity Water => get water data of time max
                if (mWaterList.size() > 0) {
                    if (tvWaterTime.getText().toString().equals("")) {
                        String key = dateToKey(mWaterList.get(mWaterList.size() - 1).getDate());
                        getWater(key);
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
        int valueWater = Integer.parseInt(tvWater.getText().toString());
        String luongNuoc = "(0 ml)";
        String strDate = tvWaterTime.getText().toString().trim();
        String[] arrDate = strDate.split(",");
        String date = "";
        if (arrDate.length > 0) {
            date = arrDate[arrDate.length - 1].trim();
        }

        switch (view.getId()) {
            case R.id.iv_add:
                valueWater = valueWater + 1;
                luongNuoc = String.valueOf(valueWater * 250);
                break;
            case R.id.iv_sub:
                valueWater = valueWater - 1;
                if (valueWater >= 0) {
                    luongNuoc = String.valueOf(valueWater * 250);
                }
                break;
            default:
                break;
        }

        tvWater.setText(String.valueOf(valueWater));
        tvLuongNuocMl.setText("(" + luongNuoc + " ml)");
        saveWater(valueWater, date);
    }

    private void saveWater(int valueWater, String date) {
        String key = dateToKey(date);

        Water water = new Water(date, valueWater);
        mDatabase.child("Waters").child(userID).child(key).setValue(water).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (!task.isSuccessful()) {
                    displayToast("Cập nhật không thành công!");
                }
            }
        });
    }

    public void displayToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private ArrayList<BarEntry> getDataSetBarChart() {
        ArrayList<BarEntry> values = new ArrayList<>();

        for (int i = 0; i < mWaterList.size(); i++) {
            int val = mWaterList.get(i).getValue();
            values.add(new BarEntry(i, val));
        }

        return values;
    }

    private ArrayList<String> getLabelsBarChart() {
        ArrayList<String> labels = new ArrayList<>();

        for (int i = 0; i < mWaterList.size(); i++) {
            String val = mWaterList.get(i).getDate();
            String dd = val.split("/")[0];
            labels.add(dd);
        }

        return labels;
    }

    private void showBarChart() {
        BarDataSet barDataSet = new BarDataSet(getDataSetBarChart(), "Cốc nước");
        ArrayList<String> labels = getLabelsBarChart();

//        barDataSet.setColor(Color.rgb(0, 155, 0));
        barDataSet.setValueTextColor(Color.BLACK);
        barDataSet.setValueTextSize(16f);

        BarData barData = new BarData(barDataSet);
        barData.setValueTextSize(10f);
        barData.setBarWidth(0.9f);

        // create marker to display box when values are selected
        MyMarkerView mv = new MyMarkerView(this, R.layout.custom_marker_view);

        // Set the marker to the chart
        mv.setChartView(mBarChart);
        mBarChart.setMarker(mv);

        // enable scaling and dragging
        mBarChart.setDragEnabled(true);
        mBarChart.setScaleEnabled(true);

        // force pinch zoom along both axis
        mBarChart.setPinchZoom(true);

        mBarChart.setData(barData);
        mBarChart.setFitBars(true);
        mBarChart.getDescription().setText("Bar chart example");
        mBarChart.getDescription().setPosition(3f, 3f);
        mBarChart.animateY(1500);

        // set clicked/touckedable
        mBarChart.setClickable(true);
        mBarChart.setTouchEnabled(true);
        mBarChart.setHighlightPerTapEnabled(true);

        mBarChart.getData().notifyDataChanged();
        mBarChart.notifyDataSetChanged();

        Legend l = mBarChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setForm(Legend.LegendForm.SQUARE);
        l.setFormSize(9f);
        l.setTextSize(11f);
        l.setXEntrySpace(4f);

        XAxis xAxis = mBarChart.getXAxis();

        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f); // only intervals of 1 day
        xAxis.setLabelCount(7);

        YAxis leftAxis = mBarChart.getAxisLeft();
        leftAxis.removeAllLimitLines(); // reset all limit lines to avoid overlapping lines
        leftAxis.setAxisMinimum(0f);

        YAxis rightAxis = mBarChart.getAxisRight();
        rightAxis.setAxisMinimum(0f);

        mBarChart.setOnChartValueSelectedListener(this);
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

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        int indexWater = (int) e.getX();
        getWaterByIndex(indexWater);
    }

    @Override
    public void onNothingSelected() {
        Log.i("Chart ", "onNothingSelected");
    }
}