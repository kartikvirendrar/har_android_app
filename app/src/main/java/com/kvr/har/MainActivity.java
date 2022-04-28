package com.kvr.har;

import androidx.appcompat.app.AppCompatActivity;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private static final int TIME_STAMP=100;
    private static List<Float> ax,ay,az;
    private static List<Float> gx,gy,gz;

    private SensorManager mSensorManager;
    private Sensor mAccelerometer, mGyroscope;

    private float[] results;
    private ActivityClassifier classifier;

    private TextView sittingTV, standingTV, lyingTV;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initLayoutItems();

        ax=new ArrayList<>(); ay=new ArrayList<>(); az=new ArrayList<>();
        gx=new ArrayList<>(); gy=new ArrayList<>(); gz=new ArrayList<>();

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mGyroscope = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_FASTEST);
        mSensorManager.registerListener(this, mGyroscope, SensorManager.SENSOR_DELAY_FASTEST);

    }

    private void initLayoutItems(){
        sittingTV = findViewById(R.id.sitting_TextView);
        standingTV = findViewById(R.id.standing_TextView);
        lyingTV = findViewById(R.id.lying_TextView);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor sensor = event.sensor;
        if (sensor.getType() == Sensor.TYPE_ACCELEROMETER){
            ax.add(event.values[0]);
            ay.add(event.values[1]);
            az.add(event.values[2]);
        }
        if (sensor.getType() == Sensor.TYPE_GYROSCOPE){
            gx.add(event.values[0]);
            gy.add(event.values[1]);
            gz.add(event.values[2]);
        }
        predictActivity();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private void predictActivity(){
        List<Float> data=new ArrayList<>();
        if (ax.size() >= TIME_STAMP && ay.size() >= TIME_STAMP && az.size() >= TIME_STAMP
            && gx.size() >= TIME_STAMP && gy.size() >= TIME_STAMP && gz.size() >= TIME_STAMP){
            data.addAll(ax.subList(0,TIME_STAMP));
            data.addAll(ay.subList(0,TIME_STAMP));
            data.addAll(az.subList(0,TIME_STAMP));
            data.addAll(gx.subList(0,TIME_STAMP));
            data.addAll(gy.subList(0,TIME_STAMP));
            data.addAll(gz.subList(0,TIME_STAMP));

            results = classifier.predictProbablities(toFloatArray(data));
            Log.i(TAG, "predictActivity: "+ Arrays.toString(results));

            sittingTV.setText("SITTING: \t"+ results[0]);
            standingTV.setText("STANDING: \t"+ results[1]);
            lyingTV.setText("LYING: \t"+ results[2]);

            data.clear();
            ax.clear(); ay.clear(); az.clear();
            gx.clear(); gy.clear(); gz.clear();
        }
    }

    private float[] toFloatArray(List<Float> data){
        int i=0;
        float[] array=new float[data.size()];
        for (Float f:data){
            array[i++] = (f!=null ? f : Float.NaN);
        }
        return array;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_FASTEST);
        mSensorManager.registerListener(this, mGyroscope, SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSensorManager.unregisterListener(this);
    }
}