package com.example.healthmonitor.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.healthmonitor.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class SleepActivity extends AppCompatActivity {

    public BarChart mBarChartSleepTime;
    public PieChart mPieChartSleepTime;
    public TextView btnSetSleepTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleep);
        setTitle("Giấc ngủ");

        mBarChartSleepTime = (BarChart) findViewById(R.id.bcSleepStatistics);
        mPieChartSleepTime = (PieChart) findViewById(R.id.pcSleepTime);
        btnSetSleepTime = findViewById((R.id.btnSetSleepTime));

        showBarChart();
        showPieChart();

        btnSetSleepTime.setOnClickListener(view -> {
//                startActivity(new Intent(this, SetAlarm));
        });

    }

    private ArrayList<PieEntry> getDataSetPieChart() {
        ArrayList<PieEntry> sleeps = new ArrayList<>();
        sleeps.add(new PieEntry(20, "Sleep"));
        sleeps.add(new PieEntry(80, ""));
        return sleeps;
    }

    private void showPieChart() {
        PieDataSet pieDataSet = new PieDataSet(getDataSetPieChart(), "Time sleep");
//        pieDataSet.setColors(Color.rgb(0, 188, 212), Color.LTGRAY);
        pieDataSet.setColors(Color.rgb(150, 240, 255), Color.rgb(220, 220, 220));
        pieDataSet.setValueTextColor(Color.DKGRAY);
        pieDataSet.setValueTextSize(10f);

        PieData pieData = new PieData(pieDataSet);

        mPieChartSleepTime.setData(pieData);
        mPieChartSleepTime.getDescription().setEnabled(false);
        mPieChartSleepTime.setCenterText("7 giờ 30 phút");
        mPieChartSleepTime.animate();
        Legend l = mPieChartSleepTime.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
    }

    private ArrayList<BarEntry> getDataSetBarChart() {
        ArrayList<BarEntry> visitors = new ArrayList<>();
        visitors.add(new BarEntry(2014, 420));
        visitors.add(new BarEntry(2015, 460));
        visitors.add(new BarEntry(2016, 457));
        visitors.add(new BarEntry(2017, 500));
        visitors.add(new BarEntry(2018, 600));
        visitors.add(new BarEntry(2019, 420));

        return visitors;
    }

    private void showBarChart() {
        BarDataSet barDataSet = new BarDataSet(getDataSetBarChart(), "visitors");
//        barDataSet.setColor(Color.rgb(0, 155, 0));
        barDataSet.setValueTextColor(Color.BLACK);
        barDataSet.setValueTextSize(16f);

        BarData barData = new BarData(barDataSet);
        barData.setValueTextSize(10f);
        barData.setBarWidth(0.9f);

        mBarChartSleepTime.setData(barData);
        mBarChartSleepTime.setFitBars(true);
        mBarChartSleepTime.getDescription().setText("Bar chart example");
        mBarChartSleepTime.getDescription().setPosition(3f, 3f);
        mBarChartSleepTime.animateY(1500);

        LimitLine ll1 = new LimitLine(150f, "Standard");
        ll1.setLineWidth(4f);
        ll1.enableDashedLine(10f, 10f, 0f);
        ll1.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        ll1.setTextSize(10f);
//        ll1.setTypeface();

        YAxis leftAxis = mBarChartSleepTime.getAxisLeft();
        leftAxis.removeAllLimitLines(); // reset all limit lines to avoid overlapping lines
        leftAxis.addLimitLine(ll1);


//        mBarChartSleepTime.getXAxis().setValueFormatter(new IndexAxisValueFormatter(xAxisLabel));
//        //mBarChartSleepTime.getXAxis().setLabelRotationAngle(20f);
//        mBarChartSleepTime.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
//        mBarChartSleepTime.getXAxis().setTextSize(8f);
//        //mBarChartSleepTime.getXAxis().setAvoidFirstLastClipping(true);
//        mBarChartSleepTime.getXAxis().setGranularityEnabled(true);
//        mBarChartSleepTime.getXAxis().setCenterAxisLabels(false);

        // X-axis
//        mBarChartSleepTime.getXAxis().setValueFormatter(new IndexAxisValueFormatter(xAxisLabel));
//        XAxis xAxis = mBarChartSleepTime.getXAxis();
//        xAxis.setGranularity(1f);
//        xAxis.setGranularityEnabled(true);
//        xAxis.setCenterAxisLabels(true);
//        xAxis.setDrawGridLines(false);
//        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
//        xAxis.setValueFormatter(new IndexAxisValueFormatter(xAxisLabel));
    }

//    private ArrayList<BarDataSet> getDataSet() {
//        ArrayList<BarDataSet> dataSets = null;
//
//        ArrayList<BarEntry> valueSet1 = new ArrayList<>();
//        valueSet1.add(new BarEntry(110.000f, 0)); // Jan
//        valueSet1.add(new BarEntry(40.000f, 1)); // Feb
//        valueSet1.add(new BarEntry(60.000f, 2)); // Mar
//        valueSet1.add(new BarEntry(30.000f, 3)); // Apr
//        valueSet1.add(new BarEntry(90.000f, 4)); // May
//        valueSet1.add(new BarEntry(100.000f, 5)); // Jun
//
//        ArrayList<BarEntry> valueSet2 = new ArrayList<BarEntry>();
//        valueSet2.add(new BarEntry(150.000f, 0)); // Jan
//        valueSet2.add(new BarEntry(90.000f, 1)); // Feb
//        valueSet2.add(new BarEntry(120.000f, 2)); // Mar
//        valueSet2.add(new BarEntry(60.000f, 3)); // Apr
//        valueSet2.add(new BarEntry(20.000f, 4)); // Jun
//        valueSet2.add(new BarEntry(80.000f, 5)); // May
//
//        BarDataSet barDataSet1 = new BarDataSet(valueSet1, "set data 1");
//        barDataSet1.setColor(Color.rgb(0, 155, 0));
//        barDataSet1.setValueTextColor(Color.BLACK);
//        barDataSet1.setValueTextSize(16f);
//
//        BarDataSet barDataSet2 = new BarDataSet(valueSet2, "set data 2");
//        barDataSet2.setColors(ColorTemplate.COLORFUL_COLORS);
//        barDataSet2.setValueTextColor(Color.BLACK);
//        barDataSet2.setValueTextSize(16f);
//
//        dataSets = new ArrayList<>();
//        dataSets.add(barDataSet1);
//        dataSets.add(barDataSet2);
//        return dataSets;
//    }
//
//    private ArrayList<String> getXAxisValues() {
//        ArrayList<String> xAxisLabel = new ArrayList<String>();
//        xAxisLabel.add("2014");
//        xAxisLabel.add("2015");
//        xAxisLabel.add("2016");
//        xAxisLabel.add("2017");
//        xAxisLabel.add("2018");
//        xAxisLabel.add("2019");
//        return xAxisLabel;
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
                AlertDialog.Builder myAlertBuilder = new AlertDialog.Builder(SleepActivity.this);
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