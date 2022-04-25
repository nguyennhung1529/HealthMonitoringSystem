package com.example.healthmonitor.activity;

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
import android.widget.TextView;
import android.widget.Toast;

import com.example.healthmonitor.R;
import com.example.healthmonitor.custom.MyMarkerView;
import com.example.healthmonitor.object.Data;
import com.example.healthmonitor.object.Water;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IFillFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.Utils;
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

public class WeightStatisticActivity extends AppCompatActivity implements View.OnClickListener, OnChartValueSelectedListener {

    private DatabaseReference mDatabase;
    private String userID;
    private ArrayList<Data> mDataUserList;


    private LineChart mLineChart;
    private TextView btnSetWeight, tvWeight, tvTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weight_statistic);
        setTitle("Cân nặng");

        mDatabase = FirebaseDatabase.getInstance().getReference();
        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        initUI();
        initListener();
        showWeightList();

    }

    private void initUI() {
        mLineChart = findViewById(R.id.lineChart);
        btnSetWeight = findViewById(R.id.btnSetWeight);
        tvWeight = findViewById(R.id.tvWeight);
        tvTime = findViewById(R.id.tvTime);
    }

    private void initListener() {
        btnSetWeight.setOnClickListener(this);
    }

    private void showWeightList() {
        mDatabase.child("UserDetails").child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mDataUserList = new ArrayList<>();
                for (DataSnapshot iData : snapshot.getChildren()) {
                    mDataUserList.add(iData.getValue(Data.class));
                }
                showLineChart();
                mLineChart.notifyDataSetChanged();

                // First time access activity Water => get water data of time max
                if (mDataUserList.size() > 0) {
                    if (tvTime.getText().toString().equals("")) {
                        String key = dateToKey(mDataUserList.get(mDataUserList.size() - 1).getDate());
                        getUserDetail(key);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void getUserDetail(String key) {
        mDatabase.child("UserDetails").child(userID).child(key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                 Data userDetail = snapshot.getValue(Data.class);
                if (userDetail != null) {
                    tvWeight.setText(String.valueOf(userDetail.getWeight()));

                    if (dateTypeToDateString(Calendar.getInstance().getTime()).equals(userDetail.getDate()))
                        tvTime.setText("Hôm nay, " + userDetail.getDate());
                    else
                        tvTime.setText(userDetail.getDate());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getUserDetailByIndex(int idx) {
        mDatabase.child("UserDetails").child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Data userDetail = null;
                int i = 0;
                for (DataSnapshot iData : snapshot.getChildren()) {
                    if (i == idx) {
                        userDetail = iData.getValue(Data.class);
                        break;
                    }
                    i++;
                }
                if (userDetail != null) {
                    tvWeight.setText(String.valueOf(userDetail.getWeight()));

                    if (dateTypeToDateString(Calendar.getInstance().getTime()).equals(userDetail.getDate()))
                        tvTime.setText("Hôm nay, " + userDetail.getDate());
                    else
                        tvTime.setText(userDetail.getDate());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private ArrayList<String> getLabelsChart() {
        ArrayList<String> labels = new ArrayList<>();

        for (int i = 0; i < mDataUserList.size(); i++) {
            String val = mDataUserList.get(i).getDate();
            String dd = val.split("/")[0];
            labels.add(dd);
        }

        return labels;
    }

    public void showLineChart() {
        {   // // Chart Style // //

            mLineChart.setBackgroundColor(Color.WHITE); // background color

            // disable description text
            mLineChart.getDescription().setEnabled(false);

            // enable touch gestures
            mLineChart.setTouchEnabled(true);

            // set listeners
            mLineChart.setOnChartValueSelectedListener(this);
            mLineChart.setDrawGridBackground(false);

            // create marker to display box when values are selected
            MyMarkerView mv = new MyMarkerView(this, R.layout.custom_marker_view);

            // Set the marker to the chart
            mv.setChartView(mLineChart);
            mLineChart.setMarker(mv);

            // enable scaling and dragging
            mLineChart.setDragEnabled(true);
            mLineChart.setScaleEnabled(true);

            // force pinch zoom along both axis
            mLineChart.setPinchZoom(true);

            // set clicked/touckedable
            mLineChart.setClickable(true);
            mLineChart.setTouchEnabled(true);
            mLineChart.setHighlightPerTapEnabled(true);
        }

        // add data
        setData();

        // draw points over time
        mLineChart.animateX(1500);

        // get the legend (only possible after setting data)
        Legend l = mLineChart.getLegend();

        // draw legend entries as lines
        l.setForm(Legend.LegendForm.LINE);

        // on selected listener
        mLineChart.setOnChartValueSelectedListener(this);
    }

    private void setData() {

        ArrayList<Entry> values = new ArrayList<>();

        for (int i = 0; i < mDataUserList.size(); i++) {
            float val = (float) mDataUserList.get(i).getWeight();
            values.add(new Entry(i, val, getResources().getDrawable(R.drawable.star)));
        }

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
            set1 = new LineDataSet(values, "Cân nặng");

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
            ArrayList<String> labels = getLabelsChart();

            // set labels
            XAxis xAxis = mLineChart.getXAxis();

            xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setGranularity(1f); // only intervals of 1 day
            xAxis.setLabelCount(7);
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
            case R.id.action_logout:
                AlertDialog.Builder myAlertBuilder = new AlertDialog.Builder(this);
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
            case R.id.btnSetWeight:
                startActivity(new Intent(WeightStatisticActivity.this, WeightActivity.class));
                break;
            default:
                break;
        }
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        int indexUserDetail = (int) e.getX();
        getUserDetailByIndex(indexUserDetail);
    }

    @Override
    public void onNothingSelected() {
        Log.i("Nothing selected", "Nothing selected.");
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