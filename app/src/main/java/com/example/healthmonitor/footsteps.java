//package com.example.healthmonitor;
//
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.content.ContextCompat;
//
//import android.Manifest;
//import android.content.pm.PackageManager;
//import android.hardware.Sensor;
//import android.hardware.SensorEvent;
//import android.hardware.SensorEventListener;
//import android.hardware.SensorManager;
//import android.os.Bundle;
//import android.view.WindowManager;
//import android.widget.TextView;
//
//import com.github.mikephil.charting.charts.BarChart;
//
//public class footsteps extends AppCompatActivity implements SensorEventListener {
//    private TextView textViewStepCounter, textViewStepDetector;
//    private SensorManager sensorManager;
//    private Sensor mStepCounter;
//    private boolean isSensorCounterPresent;
//    int stepCount = 0;
//
//    BarChart barChart;
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_footsteps);
//
//        if(ContextCompat.checkSelfPermission(this,
//                Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_DENIED){ //ask for permission
//            requestPermissions(new String[]{Manifest.permission.ACTIVITY_RECOGNITION}, 0);
//        }
//
//
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//        textViewStepCounter = findViewById(R.id.textViewStepCounter);
//        textViewStepDetector = findViewById(R.id.textViewStepDetector);
//
//        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
//
//        if(sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)!=null){
//            mStepCounter =sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
//            isSensorCounterPresent = true;
//        }
//        else {
//            textViewStepCounter.setText("Counter Sensor id not Present");
//            isSensorCounterPresent = false;
//        }
//
//        barChart = findViewById(R.id.barChart);
//
//
//    }
//
//    @Override
//    public void onSensorChanged(SensorEvent sensorEvent) {
//    if (SensorEvent.sensor == mStepCounter){
//        stepCount = (int) SensorEvent.values[0];
//        textViewStepCounter.setText(String.valueOf(stepCount));
//    }
//    }
//
//    @Override
//    public void onAccuracyChanged(Sensor sensor, int i) {
//
//    }
//
//    @Override
//    protected void onResume(){
//        super.onResume();
//        if(sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)!=null)
//                sensorManager.registerListener(this, mStepCounter, sensorManager.SENSOR_DELAY_NORMAL);
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        if (sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) != null)
//            sensorManager.unregisterListener(this, mStepCounter);
//    }
//}